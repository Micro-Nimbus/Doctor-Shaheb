package com.micronimbus.doctorshaheb;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BookAppointmentActivity extends AppCompatActivity {

    Spinner specializationSpinner, doctorSpinner;
    EditText selectDate;
    Button btnBook;
    DatabaseReference doctorRef, appointmentRef;
    ArrayList<String> doctorList = new ArrayList<>();
    Map<String, String> doctorMap = new HashMap<>();
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        specializationSpinner = findViewById(R.id.spinnerSearchSpecialization);
        doctorSpinner = findViewById(R.id.spinnerSelectDoctor);
        selectDate = findViewById(R.id.selectDate);
        btnBook = findViewById(R.id.btnBook);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        String[] sectors = {"Cardiologist","Dermatologist","ENT","Neurologist","Pediatrician"};
        //specializationSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sectors));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,   // your custom item layout
                sectors
        );
        adapter.setDropDownViewResource(R.layout.spinner_item);  // dropdown layout

        specializationSpinner.setAdapter(adapter);

        doctorRef = FirebaseDatabase.getInstance().getReference("doctorinformation");
        appointmentRef = FirebaseDatabase.getInstance().getReference("appointment");

        specializationSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                loadDoctorsBySpecialization(sectors[position]);
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) { }
        });

        btnBook.setOnClickListener(v -> bookAppointment());

        deletePastAppointments();
    }

    private void loadDoctorsBySpecialization(String specialization) {
        doctorRef.orderByChild("specialization").equalTo(specialization)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        doctorList.clear();
                        doctorMap.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String id = ds.getKey();
                            String name = ds.child("name").getValue(String.class);
                            doctorList.add(name);
                            doctorMap.put(name, id);
                        }
                        doctorSpinner.setAdapter(new ArrayAdapter<>(BookAppointmentActivity.this,
                                android.R.layout.simple_spinner_item, doctorList));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    private void bookAppointment() {
        if (currentUser == null) return;

        String selectedDoctorName = doctorSpinner.getSelectedItem().toString();
        String doctorID = doctorMap.get(selectedDoctorName);
        String date = selectDate.getText().toString();
        String appointmentID = UUID.randomUUID().toString();

        // Fetch doctor specialization from doctorinformation
        doctorRef.child(doctorID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String specialization = snapshot.child("specialization").getValue(String.class);

                Map<String, Object> appointmentMap = new HashMap<>();
                appointmentMap.put("doctorID", doctorID);
                appointmentMap.put("doctorName", selectedDoctorName);
                appointmentMap.put("specialization", specialization);
                appointmentMap.put("userID", currentUser.getUid());
                appointmentMap.put("userName", currentUser.getDisplayName());
                appointmentMap.put("date", date);

                appointmentRef.child(appointmentID).setValue(appointmentMap)
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful()) {
                                Toast.makeText(BookAppointmentActivity.this, "Appointment Booked", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(BookAppointmentActivity.this, "Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BookAppointmentActivity.this, "Failed to fetch doctor info", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void deletePastAppointments() {
        if (currentUser == null) return;

        String today = LocalDate.now().toString(); // YYYY-MM-DD

        appointmentRef.orderByChild("date").endAt(today).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String appointmentDate = ds.child("date").getValue(String.class);
                    String userID = ds.child("userID").getValue(String.class);
                    if (userID != null && userID.equals(currentUser.getUid())
                            && appointmentDate.compareTo(today) < 0) {
                        ds.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}
