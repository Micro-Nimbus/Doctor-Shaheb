package com.micronimbus.doctorshaheb;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Feedback extends AppCompatActivity {

    EditText feedbacktext;
    ImageButton feedbacksubmitbutton;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ImageButton backBtn = findViewById(R.id.back);

        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish(); // optional (removes current activity from stack)
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        feedbacktext = findViewById(R.id.feedback_text);
        feedbacksubmitbutton = findViewById(R.id.feedback_send_button);
        database = FirebaseDatabase.getInstance();

        feedbacksubmitbutton.setOnClickListener(v -> {
            String feedback = feedbacktext.getText().toString().trim();

            if (TextUtils.isEmpty(feedback)) {
                Toast.makeText(Feedback.this, "Feedback can't be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            Map<String, Object> feedbackMap = new HashMap<>();
            feedbackMap.put("feedback", feedback);
            feedbackMap.put("timestamp", System.currentTimeMillis());

            database.getReference("Feedbacks")
                    .child(userId)
                    .push()
                    .setValue(feedbackMap)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(Feedback.this, "Feedback submitted successfully", Toast.LENGTH_SHORT).show();
                            feedbacktext.setText("");
                        } else {  Exception e = task.getException();
                            Toast.makeText(Feedback.this, "Failed to submit feedback"+ e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
