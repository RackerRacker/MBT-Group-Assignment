package com.example.mobiletech_group_assignment;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// ------------------
// Activity 6
// ------------------
public class Activity6 extends AppCompatActivity {

    List<com.example.mobiletech_group_assignment.MLKitResult> mlKitResults = new ArrayList<>();
    com.example.mobiletech_group_assignment.MLKitAdapter MLKitAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_6);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Activity6.this, Activity1.class);
                startActivity(intent);
            }
        });

        MLKitAdapter = new MLKitAdapter(Activity6.this, R.layout.list_item, mlKitResults);
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(MLKitAdapter);

        // Fetch data from Firebase
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mlKitResults.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String filename = postSnapshot.child("filename").getValue(String.class);
                    String reader = postSnapshot.child("reader").getValue(String.class);
                    String text = postSnapshot.child("text").getValue(String.class);

                    if (filename != null) {
                        try {
                            Uri imageUri = loadImageFromGallery(filename + ".png");
                            mlKitResults.add(new MLKitResult(reader, imageUri, text, filename));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                MLKitAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MLKitResult res = mlKitResults.get(position);
                Intent intent = new Intent(view.getContext(), Activity7.class);
                intent.putExtra("reader", res.getReader());
                intent.putExtra("uri", res.getImageUri() != null ? res.getImageUri().toString() : null);
                intent.putExtra("text", res.getText());
                intent.putExtra("filename", res.getFilename());
                startActivity(intent);
            }
        });
    }

    private Uri loadImageFromGallery(String filename) throws IOException {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME};
        String selection = MediaStore.Images.Media.DISPLAY_NAME + "=?";
        String[] selectionArgs = {filename};
        try (Cursor cursor = getContentResolver().query(uri, projection, selection, selectionArgs, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                long imageId = cursor.getLong(idColumn);
                return ContentUris.withAppendedId(uri, imageId);
            }
        }
        return null;
    }
}
