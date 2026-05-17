package com.example.mobiletech_group_assignment;

import android.net.Uri;

public class MLKitResult {
    private String reader;
    private Uri imageUri;
    private String text;


    public MLKitResult(String reader, Uri imageUri, String text) {
        this.reader = reader;
        this.imageUri = imageUri;
        this.text = text;
    }

    public String getReader() {
        return reader;
    }

    public void setReader(String reader) {
        this.reader = reader;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

