package com.example.mobiletech_group_assignment;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.media.Image;
import android.net.Uri;
import android.net.wifi.MloLink;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;

// ------------------
// Activity 5
// ------------------

public class Activity5 extends AppCompatActivity {

    String reader = "No reader available";
    String result = "No result available";
    Uri imageFileUri;
    Uri uri = Uri.EMPTY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_5);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            reader = extras.getString("reader");
            result = extras.getString("result");

            EditText textMlItem = (EditText) findViewById(R.id.textMLItem);
            textMlItem.setText(result);

            String uriString = extras.getString("IMAGE_URI");
            if (uriString != null) {
                Uri myUri = Uri.parse(uriString);

                // Display the image in an ImageView
                ImageView imageView = findViewById(R.id.imageUpload);
                imageView.setImageURI(myUri);
            }
        }

    }

    public void saveItem(View view) {
        TextView editItemName = (TextView) findViewById(R.id.editItemName);
        EditText textMlItem = (EditText) findViewById(R.id.textMLItem);

        // Get image from the current imageFileUri
        Bitmap bitmap = getBitmapFromUri(imageFileUri);
        // Create a unique filename from the current date time
        String currentDateTime = LocalDateTime.now().toString();
        String imageFilename = currentDateTime.replaceAll("\\D+", "");
        saveImageToGallery(bitmap, imageFilename, Activity5.this);

        String itemName = editItemName.getText().toString();
        String MLItem = textMlItem.getText().toString();
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference(imageFilename);

        String childFilename = "filename";
        String childReader = "reader";
        String childResult = "text";

        dbref.child(childFilename).setValue(imageFilename);
        dbref.child(childReader).setValue(itemName);
        dbref.child(childResult).setValue(MLItem);
    }

    private Bitmap getBitmapFromUri(Uri uri) {
        try {
            ImageDecoder.Source source =
                    ImageDecoder.createSource(getContentResolver(), uri);
            Bitmap bitmap = ImageDecoder.decodeBitmap(source);
            return bitmap;
        } catch (IOException e) {
            Log.e("URI_TO_BITMAP", "Failed to load image", e);
            return null;
        }
    }

    private void saveImageToGallery(Bitmap bitmap, String fileName, Context
            context) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES);
        Uri imageUri =
                context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        try {
            OutputStream outputStream =
                    context.getContentResolver().openOutputStream(imageUri);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            Log.d("SAVE_GALLERY", "Image saved to gallery: " +
                    imageUri.toString());
        } catch (IOException e) {
            Log.e("SAVE_GALLERY", "Error saving image", e);
        }
    }
}