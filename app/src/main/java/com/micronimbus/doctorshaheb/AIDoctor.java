package com.micronimbus.doctorshaheb;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import android.util.Log;

public class AIDoctor extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText editMessage;
    private Button btnSend;
    private MessageAdapter adapter;
    private List<Message> messages = new ArrayList<>();

    private OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private static final String API_KEY = "AIzaSyB7bku42iZfy9SmbmdJYQyv-HQ9Uc3VaH8";
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + API_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aidoctor);

        recyclerView = findViewById(R.id.recyclerMessages);
        editMessage = findViewById(R.id.editMessage);
        btnSend = findViewById(R.id.btnSend);

        adapter = new MessageAdapter(messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        addMessage("🤖 Hi! Please tell me your symptoms one by one.", false);

        btnSend.setOnClickListener(v -> {
            String userInput = editMessage.getText().toString().trim();
            if (userInput.isEmpty()) return;

            addMessage(userInput, true);
            editMessage.setText("");

            if (isGreeting(userInput)) {
                addMessage("🤖 Hello! Please tell me your symptoms one at a time.", false);
            } else {
                callGeminiAPI(userInput);
            }
        });
    }

    private boolean isGreeting(String input) {
        String msg = input.toLowerCase();
        return msg.equals("hi") || msg.equals("hello") || msg.equals("hey");
    }

    private void callGeminiAPI(String userInput) {
        JSONObject requestBody = new JSONObject();
        try {
            JSONObject content = new JSONObject();
            content.put("parts", new org.json.JSONArray().put(new JSONObject().put("text", userInput)));
            requestBody.put("contents", new org.json.JSONArray().put(content));
        } catch (Exception e) {
            runOnUiThread(() -> addMessage("🤖 Failed to create request.", false));
            return;
        }

        RequestBody body = RequestBody.create(requestBody.toString(), JSON);

        Request request = new Request.Builder()
                .url(GEMINI_API_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        addMessage("🤖 Sorry, something went wrong. Please try again.", false)
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                Log.d("MY_AI_RESPONSE", "Code: " + response.code() + " Body: " + responseBody);

                if (!response.isSuccessful()) {
                    runOnUiThread(() -> addMessage("🤖 Error from AI service. Please try again.", false));
                    return;
                }

                try {
                    JSONObject json = new JSONObject(response.body().string());
                    String reply = json
                            .getJSONArray("candidates")
                            .getJSONObject(0)
                            .getJSONObject("content")
                            .getJSONArray("parts")
                            .getJSONObject(0)
                            .getString("text");

                    runOnUiThread(() -> addMessage("🤖 " + reply, false));
                } catch (Exception e) {
                    runOnUiThread(() -> addMessage("🤖 Failed to understand the AI response.", false));
                }
            }
        });
    }

    private void addMessage(String text, boolean isUser) {
        messages.add(new Message(text, isUser));
        adapter.notifyItemInserted(messages.size() - 1);
        recyclerView.scrollToPosition(messages.size() - 1);
    }
}
