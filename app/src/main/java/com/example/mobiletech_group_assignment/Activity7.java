package com.example.mobiletech_group_assignment;

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

        // Get data from intent passed from Activity 6
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String reader = extras.getString("reader");
            String text = extras.getString("text");
            String uri = extras.getString("uri");


            if (reader != null) {
                itemName.setText(reader);
            }

            if (itemTextView != null) {
                itemTextView.setText(text);
            }

            if (uri != null) {
                itemImage.setImageURI(Uri.parse(uri));
            }
        }

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Return to the previous screen (Activity 6)
                finish();
            }
        });
    }

    public void deleteItem(View view) {
        if (filename != null && !filename.isEmpty()) {
            // Get reference to the specific item in Firebase using its unique filename/id
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