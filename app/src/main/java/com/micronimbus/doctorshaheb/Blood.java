package com.micronimbus.doctorshaheb;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class Blood extends AppCompatActivity {

    private RadioGroup radioGroupDonate;
    private RadioButton radioYes, radioNo;
    private Button btnSubmitDonate, btnSearchBlood;
    private Spinner searchBloodSpinner;
    private TableLayout tableResults;

    private FirebaseUser currentUser;
    private DatabaseReference usersRef, bloodRef;

    private String[] bloodGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood);

        // Initialize views
        radioGroupDonate = findViewById(R.id.radioGroupDonate);
        radioYes = findViewById(R.id.radioYes);
        radioNo = findViewById(R.id.radioNo);
        btnSubmitDonate = findViewById(R.id.btnSubmitDonate);
        searchBloodSpinner = findViewById(R.id.searchBloodSpinner);
        btnSearchBlood = findViewById(R.id.btnSearchBlood);
        tableResults = findViewById(R.id.tableResults);

        // Firebase setup
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        usersRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        bloodRef = FirebaseDatabase.getInstance().getReference("Blood");

        // Spinner adapter with black text
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, bloodGroups) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                tv.setTextColor(Color.BLACK);
                return tv;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getDropDownView(position, convertView, parent);
                tv.setTextColor(Color.BLACK);
                return tv;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchBloodSpinner.setAdapter(adapter);

        // Submit donation
        btnSubmitDonate.setOnClickListener(v -> submitDonation());

        // Search donors
        btnSearchBlood.setOnClickListener(v -> searchBloodGroup());
    }

    private void submitDonation() {
        int selectedId = radioGroupDonate.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Please select Yes or No", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedId == radioNo.getId()) {
            Toast.makeText(this, "You chose not to donate", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if user already uploaded the blood data
        bloodRef.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(Blood.this, "Your donation data already submitted", Toast.LENGTH_SHORT).show();
                } else {
                    // Getting user info from Users node
                    usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                            String name = userSnapshot.child("name").getValue(String.class);
                            String phone = userSnapshot.child("phone").getValue(String.class);
                            String bloodGroup = userSnapshot.child("bloodGroup").getValue(String.class);

                            if (name == null) name = "Unknown";
                            if (phone == null) phone = "Unknown";
                            if (bloodGroup == null) bloodGroup = "Unknown";

                            BloodUser bloodUser = new BloodUser(name, phone, bloodGroup);

                            bloodRef.child(currentUser.getUid()).setValue(bloodUser)


                                    .addOnSuccessListener(aVoid ->
                                            Toast.makeText(Blood.this, "Donation data submitted!", Toast.LENGTH_SHORT).show()
                                    )
                                    .addOnFailureListener(e ->
                                            Toast.makeText(Blood.this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                    );
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(Blood.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Blood.this, "Error checking existing data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchBloodGroup() {
        tableResults.removeAllViews();
        String selectedBlood = searchBloodSpinner.getSelectedItem().toString();

        bloodRef.orderByChild("bloodGroup").equalTo(selectedBlood)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            Toast.makeText(Blood.this, "No donors found", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Table header
                        TableRow header = new TableRow(Blood.this);
                        header.setBackgroundColor(Color.parseColor("#D3D3D3"));
                        String[] headers = {"Name", "Phone", "Blood Group"};
                        for (String h : headers) {
                            TextView tv = new TextView(Blood.this);
                            tv.setText(h);
                            tv.setTextColor(Color.BLACK);
                            tv.setPadding(16, 16, 16, 16);
                            header.addView(tv);
                        }
                        tableResults.addView(header);

                        // Data rows
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            BloodUser user = ds.getValue(BloodUser.class);
                            if (user != null) {
                                TableRow row = new TableRow(Blood.this);
                                row.setBackgroundColor(Color.parseColor("#FFFFFF"));

                                TextView tvName = new TextView(Blood.this);
                                tvName.setText(user.name);
                                tvName.setTextColor(Color.BLACK);
                                tvName.setPadding(16, 16, 16, 16);

                                TextView tvPhone = new TextView(Blood.this);
                                tvPhone.setText(user.phone);
                                tvPhone.setTextColor(Color.BLACK);
                                tvPhone.setPadding(16, 16, 16, 16);

                                TextView tvBlood = new TextView(Blood.this);
                                tvBlood.setText(user.bloodGroup);
                                tvBlood.setTextColor(Color.BLACK);
                                tvBlood.setPadding(16, 16, 16, 16);

                                row.addView(tvName);
                                row.addView(tvPhone);
                                row.addView(tvBlood);

                                tableResults.addView(row);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Blood.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                    }
                });
    }
//utsho

    public static class BloodUser {
        public String name, phone, bloodGroup;
        public BloodUser() {}
        public BloodUser(String name, String phone, String bloodGroup) {
            this.name = name;
            this.phone = phone;
            this.bloodGroup = bloodGroup;
        }
    }
}
