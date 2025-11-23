package com.micronimbus.doctorshaheb;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyAppointmentsActivity extends AppCompatActivity {

    ListView listView;
    DatabaseReference appointmentRef;
    FirebaseUser currentUser;
    ArrayList<String> appointmentList = new ArrayList<>();
    ArrayList<String> appointmentIDs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_appointments);

        listView = findViewById(R.id.listAppointments);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        appointmentRef = FirebaseDatabase.getInstance().getReference("appointment");

        loadAppointments();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String appID = appointmentIDs.get(position);
            appointmentRef.child(appID).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(MyAppointmentsActivity.this, "Appointment Cancelled", Toast.LENGTH_SHORT).show();
                    loadAppointments();
                }
            });
        });
    }

    private void loadAppointments() {
        appointmentList.clear();
        appointmentIDs.clear();

        if (currentUser == null) return;

        appointmentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String userID = ds.child("userID").getValue(String.class);
                    if (userID != null && userID.equals(currentUser.getUid())) {

                        String date = ds.child("date").getValue(String.class);
                        String doctorName = ds.child("doctorName").getValue(String.class);
                        String specialization = ds.child("specialization").getValue(String.class);

                        String info = "Doctor: " + doctorName +
                                " | Specialization: " + specialization +
                                " | Date: " + date;

                        appointmentList.add(info);
                        appointmentIDs.add(ds.getKey());
                    }
                }
                listView.setAdapter(new ArrayAdapter<>(MyAppointmentsActivity.this,
                        android.R.layout.simple_list_item_1, appointmentList));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

}
//santona

