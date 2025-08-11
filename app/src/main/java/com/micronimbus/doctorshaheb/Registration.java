package com.micronimbus.doctorshaheb;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.micronimbus.doctorshaheb.doc.UserData;


public class Registration extends AppCompatActivity {

    Button signUp;
    EditText name, email, password, dob, phone, country;
    TextView SignIn;
    FirebaseAuth auth;
    FirebaseDatabase database;
    ProgressBar progressBar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);

        // Initialize UI elements

        auth=FirebaseAuth.getInstance();

        database=FirebaseDatabase.getInstance();
        progressBar=findViewById(R.id.progress_signup);
        progressBar.setVisibility(View.GONE);

        signUp = findViewById(R.id.button_signup);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email_reg);
        password = findViewById(R.id.password_reg);
        dob = findViewById(R.id.dob);
        phone = findViewById(R.id.phone);
        country = findViewById(R.id.country);
        SignIn = findViewById(R.id.sign_in);

        // Sign In click → go to Login screen
        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Registration.this, Login.class));
            }
        });

        // Sign Up click → process input
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
                progressBar.setVisibility(View.VISIBLE);
                // Fetch user input
//                String userName = name.getText().toString().trim();
//                String userEmail = email.getText().toString().trim();
//                String userPassword = password.getText().toString().trim();
//                String userDob = dob.getText().toString().trim();
//                String userPhone = phone.getText().toString().trim();
//                String userCountry = country.getText().toString().trim();

                // TODO: Add validation and store/send data
                // Example:
                // if (userEmail.isEmpty()) { email.setError("Email required"); return; }

            }


        });
    }


    private void createUser() {
        String userName = name.getText().toString().trim();
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();
        String userDob = dob.getText().toString().trim();
        String userPhone = phone.getText().toString().trim();
        String userCountry = country.getText().toString().trim();


        if (TextUtils.isEmpty(userName)){
            Toast.makeText(this,"Name is empty",Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(userEmail)){
            Toast.makeText(this,"Email is empty",Toast.LENGTH_SHORT).show();return;
        }

        if (TextUtils.isEmpty(userPassword)){
            Toast.makeText(this,"Password is empty",Toast.LENGTH_SHORT).show();return;
        }

        if (TextUtils.isEmpty(userDob)){
            Toast.makeText(this,"Date of birth is empty",Toast.LENGTH_SHORT).show();return;
        }

        if (TextUtils.isEmpty(userPhone)){
            Toast.makeText(this,"Phone number is empty",Toast.LENGTH_SHORT).show();return;
        }

        if (TextUtils.isEmpty(userCountry)){
            Toast.makeText(this,"Country number is empty",Toast.LENGTH_SHORT).show();return;
        }


        if (userPassword.length()<8){
            Toast.makeText(this,"Password length must be greater than 8 ",Toast.LENGTH_SHORT).show();return;

        }

        auth.createUserWithEmailAndPassword(userEmail,userPassword)

        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Do something on success

                    UserData userobj=new UserData(userName,userEmail,userPassword,userDob,userCountry,userPhone);
                    String id=task.getResult().getUser().getUid();
                    database.getReference().child("Users").child(id).setValue(userobj);
                    progressBar.setVisibility(View.GONE);

                    Toast.makeText(Registration.this,"registration Successful",Toast.LENGTH_SHORT).show();
                    return;

                } else {
                    // Handle failure
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Registration.this,"registration unsuccessful"+task.getException(),Toast.LENGTH_SHORT).show();
                    return;
                }





            }
        });



    }


}
