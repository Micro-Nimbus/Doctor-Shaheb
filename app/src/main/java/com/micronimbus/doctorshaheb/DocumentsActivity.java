package com.micronimbus.doctorshaheb;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.micronimbus.doctorshaheb.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DocumentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DocumentAdapter adapter;
    private List<String> fileNames = new ArrayList<>();
    private File docsFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);
        ImageButton backBtn = findViewById(R.id.back);

        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish(); // optional (removes current activity from stack)
        });

        recyclerView = findViewById(R.id.recycler_documents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        docsFolder = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        if (docsFolder != null && !docsFolder.exists()) docsFolder.mkdirs();

        adapter = new DocumentAdapter();
        recyclerView.setAdapter(adapter);

        loadDocuments();

        findViewById(R.id.button_add_doc).setOnClickListener(v -> pickDocument());
    }

    private void loadDocuments() {
        fileNames.clear();
        if (docsFolder != null && docsFolder.exists()) {
            File[] files = docsFolder.listFiles();
            if (files != null) {
                for (File file : files) fileNames.add(file.getName());
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void pickDocument() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{
                "application/pdf",
                "image/*",
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        });
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        documentPickerLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> documentPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri documentUri = result.getData().getData();
                    if (documentUri != null) saveDocument(documentUri);
                } else {
                    Toast.makeText(this, "Document picking cancelled.", Toast.LENGTH_SHORT).show();
                }
            });

    private void saveDocument(Uri documentUri) {
        String originalFileName = getFileName(documentUri);
        if (originalFileName == null) originalFileName = "untitled_file.dat";

        String uniqueFileName = System.currentTimeMillis() + "_" + originalFileName;
        File destinationFile = new File(docsFolder, uniqueFileName);

        try (InputStream inputStream = getContentResolver().openInputStream(documentUri);
             FileOutputStream outputStream = new FileOutputStream(destinationFile)) {

            if (inputStream == null) throw new Exception("Failed to open input stream.");

            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) outputStream.write(buffer, 0, read);

            Toast.makeText(this, "Saved: " + originalFileName, Toast.LENGTH_SHORT).show();
            loadDocuments();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save file.", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) result = cursor.getString(nameIndex);
                }
            } catch (Exception e) { e.printStackTrace(); }
        }
        if (result == null) result = uri.getLastPathSegment();
        return result;
    }

    // ---------------- Adapter ----------------
    private class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder> {

        @Override
        public DocumentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_document, parent, false);
            return new DocumentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(DocumentViewHolder holder, int position) {
            String fileName = fileNames.get(position);
            holder.name.setText(fileName);

            File file = new File(docsFolder, fileName);

            // Thumbnail or icon
            if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png")) {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                holder.icon.setImageBitmap(bitmap);
            } else if (fileName.endsWith(".pdf")) {
                holder.icon.setImageResource(R.drawable.ic_pdf);
            } else if (fileName.endsWith(".doc") || fileName.endsWith(".docx")) {
                holder.icon.setImageResource(R.drawable.ic_word);
            } else {
                holder.icon.setImageResource(R.drawable.ic_file_placeholder);
            }

            // Open document on click
            holder.itemView.setOnClickListener(v -> openDocument(file));

            // Delete document on button click
            holder.deleteButton.setOnClickListener(v -> new AlertDialog.Builder(DocumentsActivity.this)
                    .setTitle("Delete Document")
                    .setMessage("Are you sure you want to delete '" + fileName + "'?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        if (file.exists() && file.delete()) {
                            Toast.makeText(DocumentsActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                            loadDocuments();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .show());
        }

        @Override
        public int getItemCount() { return fileNames.size(); }

        class DocumentViewHolder extends RecyclerView.ViewHolder {
            ImageView icon, deleteButton;
            TextView name;

            DocumentViewHolder(View itemView) {
                super(itemView);
                icon = itemView.findViewById(R.id.document_icon);
                deleteButton = itemView.findViewById(R.id.button_delete);
                name = itemView.findViewById(R.id.document_name);
            }
        }
    }

    private void openDocument(File file) {
        Uri fileUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(fileUri, getMimeType(file.getName()));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "No app to open this file", Toast.LENGTH_SHORT).show();
        }
    }

    private String getMimeType(String fileName) {
        fileName = fileName.toLowerCase();
        if (fileName.endsWith(".pdf")) return "application/pdf";
        if (fileName.endsWith(".doc")) return "application/msword";
        if (fileName.endsWith(".docx")) return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png")) return "image/*";
        return "*/*";
    }
}
