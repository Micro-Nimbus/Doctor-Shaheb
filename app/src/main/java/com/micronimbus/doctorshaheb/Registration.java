package com.micronimbus.doctorshaheb;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;
import com.micronimbus.doctorshaheb.doc.UserData;

public class Registration extends AppCompatActivity {

    Button signUp;
    EditText name, email, password, dob, phone, country;
    TextView SignIn;
    FirebaseAuth auth;
    FirebaseDatabase database;
    ProgressBar progressBar;
    CountryCodePicker ccp;
    Spinner bloodSpinner;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        progressBar = findViewById(R.id.progress_signup);
        progressBar.setVisibility(View.GONE);

        signUp = findViewById(R.id.button_signup);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email_reg);
        password = findViewById(R.id.password_reg);
        dob = findViewById(R.id.dob);
        phone = findViewById(R.id.phone);
        country = findViewById(R.id.country);
        ccp = findViewById(R.id.country_code_picker);
        ccp.registerCarrierNumberEditText(phone);

        bloodSpinner = findViewById(R.id.bloodSpinner);
        String[] bloodGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, bloodGroups);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bloodSpinner.setAdapter(adapter);

        SignIn = findViewById(R.id.sign_in);
        SignIn.setOnClickListener(v -> startActivity(new Intent(Registration.this, Login.class)));

        signUp.setOnClickListener(v -> {
            if (!ccp.isValidFullNumber()) {
                Toast.makeText(Registration.this, "Invalid phone number!", Toast.LENGTH_SHORT).show();
                return;
            }

            createUser();
        });
    }

    private void createUser() {
        String userName = name.getText().toString().trim();
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();
        String userDob = dob.getText().toString().trim();
        String userPhone = ccp.getFullNumberWithPlus();
        String userCountry = country.getText().toString().trim();
        String userBloodGroup = bloodSpinner.getSelectedItem().toString(); // Get selected blood group

        // Validation
        if (TextUtils.isEmpty(userName)) { Toast.makeText(this,"Name is empty",Toast.LENGTH_SHORT).show(); return; }
        if (TextUtils.isEmpty(userEmail)) { Toast.makeText(this,"Email is empty",Toast.LENGTH_SHORT).show(); return; }
        if (TextUtils.isEmpty(userPassword)) { Toast.makeText(this,"Password is empty",Toast.LENGTH_SHORT).show(); return; }
        if (TextUtils.isEmpty(userDob)) { Toast.makeText(this,"Date of birth is empty",Toast.LENGTH_SHORT).show(); return; }
        if (TextUtils.isEmpty(userPhone)) { Toast.makeText(this,"Phone number is empty",Toast.LENGTH_SHORT).show(); return; }
        if (TextUtils.isEmpty(userCountry)) { Toast.makeText(this,"Country is empty",Toast.LENGTH_SHORT).show(); return; }
        if (userPassword.length() < 8) { Toast.makeText(this,"Password length must be >= 8",Toast.LENGTH_SHORT).show(); return; }

        progressBar.setVisibility(View.VISIBLE);

        auth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        String id = task.getResult().getUser().getUid();
                        UserData userObj = new UserData(userName, userEmail, userPassword, userDob, userCountry, userPhone, userBloodGroup);
                        database.getReference().child("Users").child(id).setValue(userObj);
                        Toast.makeText(Registration.this,"Registration Successful",Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Registration.this,"Registration failed: " + task.getException(),Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
