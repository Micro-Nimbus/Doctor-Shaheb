package com.micronimbus.doctorshaheb;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class AddMedicineActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView medicineImage;
    private EditText medicineName, medicineDesc, medicinePrice;
    private Button btnAddMedicine;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);

        ImageButton backBtn = findViewById(R.id.back);

        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish(); // optional (removes current activity from stack)
        });
        medicineImage = findViewById(R.id.medicineImage);
        medicineName = findViewById(R.id.medicineName);
        medicineDesc = findViewById(R.id.medicineDesc);
        medicinePrice = findViewById(R.id.medicinePrice);
        btnAddMedicine = findViewById(R.id.btnAddMedicine);

        // Click to select image
        medicineImage.setOnClickListener(v -> openFileChooser());

        // Click to add medicine
        btnAddMedicine.setOnClickListener(v -> uploadMedicine());
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            medicineImage.setImageURI(imageUri);
            Log.d("AddMedicine", "Selected image URI: " + imageUri);
        }
    }

    private void uploadMedicine() {
        String name = medicineName.getText().toString().trim();
        String desc = medicineDesc.getText().toString().trim();
        String priceStr = medicinePrice.getText().toString().trim();

        if (name.isEmpty() || desc.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (imageUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Enter valid price", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                .child("MedicineImages")
                .child(System.currentTimeMillis() + ".jpg");

        UploadTask uploadTask = storageRef.putFile(imageUri);

        uploadTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                task.getResult().getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    Log.d("AddMedicine", "Uploaded image URL: " + imageUrl);

                    saveMedicineToDatabase(name, desc, price, imageUrl);
                }).addOnFailureListener(e ->
                        Toast.makeText(AddMedicineActivity.this, "Failed to get URL: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            } else {
                Toast.makeText(AddMedicineActivity.this, "Image upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveMedicineToDatabase(String name, String desc, double price, String imageUrl) {
        Map<String, Object> medicineMap = new HashMap<>();
        medicineMap.put("name", name);
        medicineMap.put("description", desc);
        medicineMap.put("price", price);
        medicineMap.put("imageUrl", imageUrl);

        // Save directly under Medicine node (no userId)
        DatabaseReference databaseRef = FirebaseDatabase.getInstance()
                .getReference("Medicine");

        databaseRef.push().setValue(medicineMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(AddMedicineActivity.this, "Medicine added successfully", Toast.LENGTH_SHORT).show();
                        medicineName.setText("");
                        medicineDesc.setText("");
                        medicinePrice.setText("");
                        medicineImage.setImageResource(R.drawable.ic_launcher_background);
                        imageUri = null;
                    } else {
                        Toast.makeText(AddMedicineActivity.this, "Database save failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
