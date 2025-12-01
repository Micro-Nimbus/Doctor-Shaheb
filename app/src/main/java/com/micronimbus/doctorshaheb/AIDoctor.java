package com.micronimbus.doctorshaheb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.micronimbus.doctorshaheb.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// --- Data Models ---
class Source {
    public final String uri;
    public final String title;

    public Source(String uri, String title) {
        this.uri = uri;
        this.title = title;
    }
}

class ChatMessage {
    public final String role; // "user" or "model"
    public final String text;
    public final List<Source> sources;

    public ChatMessage(String role, String text, List<Source> sources) {
        this.role = role;
        this.text = text;
        this.sources = sources;
    }
}

// --- Configuration Constants ---
final class Config {
    public static final String GEMINI_MODEL = "gemini-2.5-flash-preview-09-2025";
    public static final String SYSTEM_PROMPT = "You are a friendly, helpful, and knowledgeable AI Doctor. You provide general health information and advice, but you MUST remind the user that you are not a substitute for a licensed medical professional and they should consult a real doctor for diagnosis or treatment. Keep your responses empathetic and concise.";
    public static final int MAX_RETRIES = 5;
    public static final long BASE_DELAY_MS = 1000;

   
    public static final String GEMINI_API_KEY = "AIzaSyC3EA4XL-fjlPg0XiKA0-4t0Mr4k-bsqD0";
}

public class AIDoctor extends Activity {

    private static final String TAG = "AIDoctorActivity";
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private List<ChatMessage> chatHistory;

    // UI elements
    private EditText inputMessage;
    private Button sendButton;
    private ListView chatListView;
    private ChatAdapter chatAdapter;

    // Firebase
    private DatabaseReference chatRef;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aidoctor);
        ImageButton backBtn = findViewById(R.id.back);

        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish(); // optional (removes current activity from stack)
        });
        // Firebase setup
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        chatRef = FirebaseDatabase.getInstance().getReference("chats").child(userId);

        // UI Initialization
        inputMessage = findViewById(R.id.input_message);
        sendButton = findViewById(R.id.send_button);
        chatListView = findViewById(R.id.chat_list_view);

        // Chat history list and adapter
        chatHistory = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, chatHistory);
        chatListView.setAdapter(chatAdapter);

        // Load previous chat from Firebase
        loadChatHistoryFromFirebase();

        // Send button click
        sendButton.setOnClickListener(v -> {
            String message = inputMessage.getText().toString().trim();
            if (!message.isEmpty()) {
                sendMessage(message);
                inputMessage.setText("");
            }
        });
    }

    /** Load chat history from Firebase with live updates */
    private void loadChatHistoryFromFirebase() {
        chatRef.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatHistory.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    String role = child.child("role").getValue(String.class);
                    String text = child.child("text").getValue(String.class);
                    if (role != null && text != null) {
                        chatHistory.add(new ChatMessage(role, text, new ArrayList<>()));
                    }
                }

                // If empty, add welcome message
                if (chatHistory.isEmpty()) {
                    ChatMessage welcome = new ChatMessage("model",
                            "Hello! I'm your AI Doctor assistant. I can help with general health questions, but remember to always consult a licensed medical professional for real diagnosis and treatment. What's on your mind today?",
                            new ArrayList<>());
                    chatHistory.add(welcome);
                    saveMessageToFirebase(welcome);
                }

                chatAdapter.notifyDataSetChanged();
                chatListView.setSelection(chatHistory.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to read chat", error.toException());
            }
        });
    }

    /** Save a message to Firebase */
    private void saveMessageToFirebase(ChatMessage message) {
        String messageId = chatRef.push().getKey();
        if (messageId != null) {
            chatRef.child(messageId).child("role").setValue(message.role);
            chatRef.child(messageId).child("text").setValue(message.text);
            chatRef.child(messageId).child("timestamp").setValue(System.currentTimeMillis());
        }
    }

    /** Send user message and call Gemini API */
    private void sendMessage(String userMessage) {
        // Add user message
        ChatMessage userMsg = new ChatMessage("user", userMessage, new ArrayList<>());
        chatHistory.add(userMsg);
        saveMessageToFirebase(userMsg);

        mainHandler.post(() -> {
            chatAdapter.notifyDataSetChanged();
            chatListView.setSelection(chatHistory.size() - 1);
            sendButton.setEnabled(false);
        });

        executorService.execute(() -> {
            String resultText = "Sorry, an API error occurred. Please try again.";
            List<Source> sources = new ArrayList<>();
            int attempt = 0;
            boolean success = false;

            while (attempt < Config.MAX_RETRIES && !success) {
                try {
                    if (attempt > 0) {
                        long delay = (long) (Config.BASE_DELAY_MS * Math.pow(2, attempt) + Math.random() * 100);
                        Thread.sleep(delay);
                    }

                    String payload = createPayload();
                    ApiResponse response = makeApiCall(payload);

                    if (response.isSuccessful) {
                        JSONObject jsonResponse = new JSONObject(response.body);
                        JSONArray candidates = jsonResponse.optJSONArray("candidates");

                        if (candidates != null && candidates.length() > 0) {
                            JSONObject candidate = candidates.getJSONObject(0);

                            JSONArray parts = candidate.optJSONObject("content").optJSONArray("parts");
                            resultText = parts.optJSONObject(0).optString("text", resultText);

                            JSONObject groundingMetadata = candidate.optJSONObject("groundingMetadata");
                            JSONArray attributions = groundingMetadata != null ? groundingMetadata.optJSONArray("groundingAttributions") : null;

                            sources.clear();
                            if (attributions != null) {
                                for (int i = 0; i < attributions.length(); i++) {
                                    JSONObject attr = attributions.getJSONObject(i);
                                    JSONObject web = attr.optJSONObject("web");
                                    String uri = web != null ? web.optString("uri") : null;
                                    String title = web != null ? web.optString("title") : null;
                                    if (uri != null && title != null) {
                                        sources.add(new Source(uri, title));
                                    }
                                }
                            }
                            success = true;
                        }
                    } else if (response.statusCode == 429) {
                        attempt++;
                    } else {
                        throw new Exception("HTTP Error: " + response.statusCode + " - " + response.body);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "API attempt " + (attempt + 1) + " failed.", e);
                    attempt++;
                }
            }

            final String finalResultText = resultText;
            final List<Source> finalSources = sources;
            final boolean finalSuccess = success;

            mainHandler.post(() -> {
                ChatMessage modelMsg = new ChatMessage("model", finalResultText, finalSources);
                chatHistory.add(modelMsg);
                saveMessageToFirebase(modelMsg);
                chatAdapter.notifyDataSetChanged();
                chatListView.setSelection(chatHistory.size() - 1);
                sendButton.setEnabled(true);

                if (!finalSuccess) {
                    Toast.makeText(AIDoctor.this, "Failed to get response after multiple attempts.", Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    /** Build Gemini API payload */
    private String createPayload() {
        try {
            JSONArray contentsArray = new JSONArray();
            for (ChatMessage message : chatHistory) {
                JSONObject part = new JSONObject();
                part.put("text", message.text);

                JSONArray parts = new JSONArray();
                parts.put(part);

                JSONObject content = new JSONObject();
                content.put("role", message.role.equals("user") ? "user" : "model");
                content.put("parts", parts);

                contentsArray.put(content);
            }

            JSONObject payload = new JSONObject();
            payload.put("contents", contentsArray);

            JSONArray toolsArray = new JSONArray();
            toolsArray.put(new JSONObject().put("google_search", new JSONObject()));
            payload.put("tools", toolsArray);

            JSONObject systemInstruction = new JSONObject();
            JSONArray sysParts = new JSONArray();
            sysParts.put(new JSONObject().put("text", Config.SYSTEM_PROMPT));
            systemInstruction.put("parts", sysParts);
            payload.put("systemInstruction", systemInstruction);

            return payload.toString();
        } catch (Exception e) {
            Log.e(TAG, "Error creating JSON payload", e);
            return "{}";
        }
    }

    /** Gemini API Response Wrapper */
    private static class ApiResponse {
        public final boolean isSuccessful;
        public final int statusCode;
        public final String body;

        public ApiResponse(boolean isSuccessful, int statusCode, String body) {
            this.isSuccessful = isSuccessful;
            this.statusCode = statusCode;
            this.body = body;
        }
    }

    /** Make Gemini API call */
    private ApiResponse makeApiCall(String payload) {
        String urlString = String.format(
                "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s",
                Config.GEMINI_MODEL, Config.GEMINI_API_KEY);
        HttpURLConnection connection = null;

        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(payload.getBytes("UTF-8"));
            }

            int responseCode = connection.getResponseCode();
            boolean isSuccessful = responseCode >= 200 && responseCode < 300;

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    isSuccessful ? connection.getInputStream() : connection.getErrorStream()));

            StringBuilder responseBody = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseBody.append(line);
            }
            reader.close();

            return new ApiResponse(isSuccessful, responseCode, responseBody.toString());

        } catch (Exception e) {
            Log.e(TAG, "Network error in makeApiCall", e);
            return new ApiResponse(false, -1, "Network error: " + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /** Chat Adapter */
    private class ChatAdapter extends ArrayAdapter<ChatMessage> {
        private final Context context;

        public ChatAdapter(Context context, List<ChatMessage> messages) {
            super(context, 0, messages);
            this.context = context;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            ChatMessage message = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_chat_message, parent, false);
            }

            TextView tvRole = convertView.findViewById(R.id.tv_role);
            TextView tvMessage = convertView.findViewById(R.id.tv_message);
            TextView tvSources = convertView.findViewById(R.id.tv_sources);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ((ViewGroup) tvMessage.getParent()).getLayoutParams();
            if (message.role.equals("user")) {
                params.gravity = Gravity.END;
                tvMessage.setBackgroundResource(R.drawable.rounded_bubble_user);
                tvRole.setText("You");
            } else {
                params.gravity = Gravity.START;
                tvMessage.setBackgroundResource(R.drawable.rounded_bubble_model);
                tvRole.setText("Dr. AI");
            }
            ((ViewGroup) tvMessage.getParent()).setLayoutParams(params);

            tvMessage.setText(message.text);

            if (message.sources.isEmpty()) {
                tvSources.setVisibility(View.GONE);
            } else {
                tvSources.setVisibility(View.VISIBLE);
                StringBuilder sourceText = new StringBuilder("Sources:\n");
                for (Source source : message.sources) {
                    sourceText.append("- ").append(source.title).append("\n");
                }

                tvSources.setText(sourceText.toString());
            }

            return convertView;
        }
    }
}
//utshho