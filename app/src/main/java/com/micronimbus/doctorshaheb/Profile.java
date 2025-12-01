package com.micronimbus.doctorshaheb;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class Profile extends AppCompatActivity {

    private static final int REQUEST_STORAGE_PERMISSION = 102;

    private ImageView profileImage;
    private EditText etName, etAdd, etAge, etHeight, etWeight, etBlood;
    private TextView tvEmail;
    private Button btnUploadImage, btnSaveName;

    private FirebaseUser currentUser;
    private DatabaseReference userRef;
    private ProfileDatabaseHelper dbHelper;

    private Uri selectedImageUri = null;

    ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ImageButton backBtn = findViewById(R.id.back);

        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish(); // optional (removes current activity from stack)
        });

        profileImage = findViewById(R.id.profileImage);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        btnSaveName = findViewById(R.id.btnSaveName);

        etName = findViewById(R.id.etName);
        etAdd = findViewById(R.id.etadd);
        etAge = findViewById(R.id.etage);
        etHeight = findViewById(R.id.etheight);
        etWeight = findViewById(R.id.etweight);
        etBlood = findViewById(R.id.etblood);
        tvEmail = findViewById(R.id.tvEmail);

        dbHelper = new ProfileDatabaseHelper(this);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String uid = currentUser.getUid();
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);

        loadFirebaseData();
        loadLocalProfile();

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            profileImage.setImageURI(selectedImageUri);
                            dbHelper.updateImageUri(selectedImageUri.toString());
                        }
                    }
                }
        );

        btnUploadImage.setOnClickListener(v -> openImageChooser());

        btnSaveName.setOnClickListener(v -> saveProfile());
    }

    private void loadFirebaseData() {
        tvEmail.setText(currentUser.getEmail());

        // Load both name and blood from Firebase database
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue(String.class);
                String blood = snapshot.child("bloodGroup").getValue(String.class);

                if (name != null) etName.setText(name);
                if (blood != null) etBlood.setText(blood);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Profile.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadLocalProfile() {
        Cursor cursor = dbHelper.getProfile();
        if (cursor.moveToFirst()) {
            String imageUri = cursor.getString(cursor.getColumnIndexOrThrow("imageUri"));
            String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
            String age = cursor.getString(cursor.getColumnIndexOrThrow("age"));
            String height = cursor.getString(cursor.getColumnIndexOrThrow("height"));
            String weight = cursor.getString(cursor.getColumnIndexOrThrow("weight"));
            String blood = cursor.getString(cursor.getColumnIndexOrThrow("blood"));

            if (imageUri != null && !imageUri.isEmpty()) {
                selectedImageUri = Uri.parse(imageUri);
                Glide.with(this).load(selectedImageUri).placeholder(R.drawable.patien).into(profileImage);
            }

            etAdd.setText(address);
            etAge.setText(age);
            etHeight.setText(height);
            etWeight.setText(weight);
            if (blood != null && !blood.isEmpty()) etBlood.setText(blood);
        }
        cursor.close();
    }

    private void saveProfile() {
        String name = etName.getText().toString().trim();
        String address = etAdd.getText().toString().trim();
        String age = etAge.getText().toString().trim();
        String height = etHeight.getText().toString().trim();
        String weight = etWeight.getText().toString().trim();
        String blood = etBlood.getText().toString().trim();
        String imageUriStr = selectedImageUri != null ? selectedImageUri.toString() : "";

        if (name.isEmpty()) {
            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save to Firebase
        userRef.child("name").setValue(name);
        userRef.child("bloodGroup").setValue(blood);

        // Save locally
        dbHelper.saveProfile(imageUriStr, address, age, height, weight, blood);

        Toast.makeText(this, "Profile saved successfully", Toast.LENGTH_SHORT).show();
    }

    private void openImageChooser() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImageChooser();
            } else {
                Toast.makeText(this, "Permission denied to access gallery", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
