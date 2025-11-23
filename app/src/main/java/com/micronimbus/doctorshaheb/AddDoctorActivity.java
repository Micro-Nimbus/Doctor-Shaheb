package com.micronimbus.doctorshaheb;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddDoctorActivity extends AppCompatActivity {

    EditText name, address, degree, maxAppointments, availableTime;
    Spinner specialization;
    Button btnAdd;
    DatabaseReference doctorRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_doctor);

        name = findViewById(R.id.doctorName);
        address = findViewById(R.id.doctorAddress);
        degree = findViewById(R.id.doctorDegree);
        maxAppointments = findViewById(R.id.maxAppointments);
        availableTime = findViewById(R.id.availableTime);
        specialization = findViewById(R.id.spinnerSpecialization);
        btnAdd = findViewById(R.id.btnAddDoctor);


        String[] sectors = {"Cardiologist","Dermatologist","ENT","Neurologist","Pediatrician"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,   // your custom item layout
                sectors
        );

        adapter.setDropDownViewResource(R.layout.spinner_item);  // dropdown layout

        specialization.setAdapter(adapter);

        doctorRef = FirebaseDatabase.getInstance().getReference("doctorinformation");

        btnAdd.setOnClickListener(v -> addDoctor());
    }

    private void addDoctor() {
        String doctorID = UUID.randomUUID().toString();
        Map<String, Object> doctorMap = new HashMap<>();
        doctorMap.put("name", name.getText().toString());
        doctorMap.put("specialization", specialization.getSelectedItem().toString());
        doctorMap.put("address", address.getText().toString());
        doctorMap.put("degree", degree.getText().toString());
        doctorMap.put("maxAppointmentsPerDay", Integer.parseInt(maxAppointments.getText().toString()));
        doctorMap.put("availableTime", availableTime.getText().toString());

        doctorRef.child(doctorID).setValue(doctorMap).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                Toast.makeText(AddDoctorActivity.this, "Doctor Added", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(AddDoctorActivity.this, "Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
