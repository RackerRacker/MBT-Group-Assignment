package com.example.mobiletech_group_assignment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// ------------------
// Activity 1
// ------------------

public class Activity1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_1);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView image = findViewById(R.id.imageBar);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int imageTwoId = R.drawable.barcode;
                Intent intent = new Intent(Activity1.this, Activity2.class);
                intent.putExtra("image_key", imageTwoId);
                intent.putExtra("mode", "BARCODE");
                startActivity(intent);
            }
        });

        ImageView image2 = findViewById(R.id.imageCon);
        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int imageTwoId = R.drawable.content;
                Intent intent = new Intent(Activity1.this, Activity2.class);
                intent.putExtra("image_key", imageTwoId);
                intent.putExtra("mode", "CONTENT");
                startActivity(intent);
            }
        });

        ImageView image3 = findViewById(R.id.imageTex);
        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int imageTwoId = R.drawable.text;
                Intent intent = new Intent(Activity1.this, Activity2.class);
                intent.putExtra("image_key", imageTwoId);
                intent.putExtra("mode", "TEXT");
                startActivity(intent);
            }
        });

        Button button = findViewById(R.id.button7);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity1.this, Activity7.class);
                startActivity(intent);
            }
        });
    }
}