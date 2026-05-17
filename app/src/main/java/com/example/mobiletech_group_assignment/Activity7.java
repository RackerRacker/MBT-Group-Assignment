package com.example.mobiletech_group_assignment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// ------------------
// Activity 7
// ------------------

public class Activity7 extends AppCompatActivity {

    private String filename;
    private String reader;
    private String text;
    private String uriString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_7);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components
        TextView itemTextView = findViewById(R.id.itemTextView);
        ImageView itemImage = findViewById(R.id.itemImage);
        TextView itemName = findViewById(R.id.itemName);
        Button cancelButton = findViewById(R.id.cancelButton);
        Button editButton = findViewById(R.id.editButton);
        Button deleteButton = findViewById(R.id.deleteButton);

        // Get data from intent passed from Activity 6
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            reader = extras.getString("reader");
            text = extras.getString("text");
            uriString = extras.getString("uri");
            filename = extras.getString("filename");

            if (reader != null) {
                itemName.setText(reader);
            }

            if (itemTextView != null) {
                itemTextView.setText(text);
            }

            if (uriString != null) {
                itemImage.setImageURI(Uri.parse(uriString));
            }
        }

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Activity7.this, Activity5.class);
                intent.putExtra("reader", reader);
                intent.putExtra("result", text);
                intent.putExtra("IMAGE_URI", uriString);
                intent.putExtra("filename", filename); // Pass filename to overwrite the existing record
                startActivity(intent);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteItem(view);
            }
        });
    }

    public void deleteItem(View view) {
        if (filename != null && !filename.isEmpty()) {
            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference(filename);
            
            // Remove the value from Firebase
            dbref.removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(Activity7.this, "Item deleted successfully from Firebase", Toast.LENGTH_SHORT).show();
                    // Close activity and return to Activity 6
                    finish();
                } else {
                    Toast.makeText(Activity7.this, "Failed to delete item from Firebase", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Error: No item identifier found to delete", Toast.LENGTH_SHORT).show();
        }
    }
}
