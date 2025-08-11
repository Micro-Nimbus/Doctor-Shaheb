package com.micronimbus.doctorshaheb;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Doctor extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView AIDoctor;
    EditText message_text;
    ImageButton sendButton;
    List<MessageDoctor> messageDoctorList;
    MessageDoctorAdapter messageDoctorAdapter;

    private DiseaseDetector detector;
    private boolean collectingSymptoms = true;
    private List<String> collectedSymptoms = new ArrayList<>();


    private Set<String> knownSymptoms = new HashSet<>(List.of(
            "fever", "chills", "nausea", "rash", "headache", "vomiting",
            "fatigue", "joint pain", "muscle pain", "pain behind the eyes",
            "sweating", "night sweats", "weight loss", "bleeding", "dark skin",
            "abdominal swelling", "weakness", "rapid heartbeat", "anemia"
    ));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor);

        messageDoctorList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerview);
        AIDoctor = findViewById(R.id.AIDoctor);
        message_text = findViewById(R.id.message_text);
        sendButton = findViewById(R.id.send_button);

        messageDoctorAdapter = new MessageDoctorAdapter(messageDoctorList);
        recyclerView.setAdapter(messageDoctorAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);

        detector = new DiseaseDetector(this);

        addToChat("🤖 Hi! Please tell me your symptoms one by one.\nType 'no' when you're done.", MessageDoctor.Sent_BY_BOT);

        sendButton.setOnClickListener(v -> {
            String userInput = message_text.getText().toString().trim().toLowerCase();
            if (!userInput.isEmpty()) {
                addToChat(userInput, MessageDoctor.Sent_BY_ME);
                handleUserInput(userInput);
                message_text.setText("");
                AIDoctor.setVisibility(View.GONE);
            } else {
                Toast.makeText(this, "Please enter a symptom or type 'no' if done.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleUserInput(String input) {
        if (collectingSymptoms) {
            if (input.equals("no") || input.equals("done") || input.equals("that's all")) {
                collectingSymptoms = false;
                diagnose();
            } else {

                boolean foundSymptom = false;
                for (String symptom : knownSymptoms) {
                    if (input.contains(symptom) && !collectedSymptoms.contains(symptom)) {
                        collectedSymptoms.add(symptom);
                        foundSymptom = true;
                        break;
                    }
                }
                if (foundSymptom) {
                    addToChat("🤖 Noted. Any more symptoms? Type 'no' if you're done.", MessageDoctor.Sent_BY_BOT);
                } else {
                    addToChat("🤖 I didn't recognize that symptom. Please try again or type 'no' if done.", MessageDoctor.Sent_BY_BOT);
                }
            }
        }
    }

    private void diagnose() {
        if (collectedSymptoms.isEmpty()) {
            addToChat("🤖 No recognizable symptoms were provided. Please try again.", MessageDoctor.Sent_BY_BOT);
            collectingSymptoms = true; // allow to continue
            return;
        }

        String diagnosis = detector.detectDisease(collectedSymptoms);

        if (diagnosis != null) {
            Disease disease = detector.getDiseaseInfo(diagnosis);
            StringBuilder reply = new StringBuilder();
            reply.append("🤖 Based on your symptoms, you might have **").append(diagnosis).append("**.\n\n");
            reply.append("✅ Recommendations:\n");
            for (String rec : disease.recommendations) {
                reply.append("• ").append(rec).append("\n");
            }
            reply.append("\n💊 Suggested Medicines:\n");
            for (String med : disease.medicines) {
                reply.append("• ").append(med).append("\n");
            }
            addToChat(reply.toString(), MessageDoctor.Sent_BY_BOT);
        } else {
            addToChat("🤖 Sorry, I couldn't match your symptoms to any disease. Please consult a doctor.", MessageDoctor.Sent_BY_BOT);
        }
    }

    private void addToChat(String message, String sentBy) {
        runOnUiThread(() -> {
            messageDoctorList.add(new MessageDoctor(message, sentBy));
            messageDoctorAdapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(messageDoctorAdapter.getItemCount() - 1);
        });
    }
}
