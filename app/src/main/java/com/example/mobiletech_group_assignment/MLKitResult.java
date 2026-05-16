package com.example.mobiletech_group_assignment;

import android.net.Uri;

public class MLKitResult {
    private String reader;
    private Uri imageUri;

    public MLKitResult(String reader, Uri imageUri) {
        this.reader = reader;
        this.imageUri = imageUri;
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

    public void setImage(Uri imageUri) {
        this.imageUri = imageUri;
    }
}

