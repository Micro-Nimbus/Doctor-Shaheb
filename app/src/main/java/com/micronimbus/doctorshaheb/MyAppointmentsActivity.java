package com.micronimbus.doctorshaheb;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyAppointmentsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference appointmentRef;
    FirebaseUser currentUser;

    ArrayList<String> appointmentList = new ArrayList<>();
    ArrayList<String> appointmentIDs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_appointments);

        recyclerView = findViewById(R.id.recyclerAppointments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        appointmentRef = FirebaseDatabase.getInstance().getReference("appointment");

        loadAppointments();
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

                AppointmentAdapter adapter = new AppointmentAdapter();
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {

        @NonNull
        @Override
        public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_appointment, parent, false);
            return new AppointmentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
            String info = appointmentList.get(position);
            String appID = appointmentIDs.get(position);

            holder.info.setText(info);

            holder.deleteButton.setOnClickListener(v -> {
                appointmentRef.child(appID).removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(MyAppointmentsActivity.this, "Appointment Cancelled", Toast.LENGTH_SHORT).show();
                        loadAppointments();
                    }
                });
            });
        }

        @Override
        public int getItemCount() { return appointmentList.size(); }

        class AppointmentViewHolder extends RecyclerView.ViewHolder {
            TextView info;
            ImageView deleteButton;

            AppointmentViewHolder(@NonNull View itemView) {
                super(itemView);
                info = itemView.findViewById(R.id.appointment_info);
                deleteButton = itemView.findViewById(R.id.button_delete);
            }
        }
    }
}
