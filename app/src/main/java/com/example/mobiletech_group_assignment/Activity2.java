package com.example.mobiletech_group_assignment;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;
import java.util.List;



public class Activity2 extends AppCompatActivity {
    private static final int REQUEST_PERMISSION = 3000;
    private Uri imageFileUri;
    private ImageView imageView;
    private TextView textViewOutput;
    private String scanMode = "BARCODE";
    private Button buttonEdit;
    private TextView textTitle;
    public void openCamera(View view) {
        if (checkPermission() == false)
            return;
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageFileUri =
                getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new
                        ContentValues());
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
        activityResultLauncher.launch(takePhotoIntent);
    }
    public void loadImage(View view) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activityResultLauncher.launch(galleryIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;


        });

        Button button = findViewById(R.id.buttonEdit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity2.this, Activity5.class);
                intent.putExtra("reader", textTitle.getText().toString());
                intent.putExtra("result", textViewOutput.getText().toString());
                intent.putExtra("IMAGE_URI", imageFileUri.toString());
                startActivity(intent);
            }
        });
        imageView = findViewById(R.id.imageTwo);
        textViewOutput = findViewById(R.id.textTwo);

        int receivedImageId = getIntent().getIntExtra("image_key", 0);

        if (receivedImageId != 0) {
            imageView.setImageResource(receivedImageId);
        }
        textTitle = findViewById(R.id.textTitle);
        buttonEdit = findViewById(R.id.buttonEdit);
        buttonEdit.setVisibility(View.GONE);
        scanMode = getIntent().getStringExtra("mode");

        if (scanMode == null) {
            scanMode = "BARCODE";
        }
        switch (scanMode) {

            case "CONTENT":
                textTitle.setText("Content Reader");

                break;
            case "TEXT":
                textTitle.setText("Text Reader");

                break;

            default:
                textTitle.setText("Barcode Reader");

                break;
        }
    }

    private boolean checkPermission() {
        String permission = android.Manifest.permission.CAMERA;
        boolean grantCamera = ContextCompat.checkSelfPermission(this, permission) ==
                PackageManager.PERMISSION_GRANTED;
        if (!grantCamera) {
            ActivityCompat.requestPermissions(this, new String[]{permission},
                    REQUEST_PERMISSION);
        }
        return grantCamera;
    }
    ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == RESULT_OK) {
                                if (result.getData() != null &&
                                        result.getData().getData() != null)
                                    imageFileUri = result.getData().getData();
                                imageView.setImageURI(imageFileUri);
                                textViewOutput.setText("");
                                InputImage image = null;
                                try {
                                    image = InputImage.fromFilePath(getBaseContext(), imageFileUri);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if (image != null) {
                                    switch (scanMode) {

                                        case "CONTENT":
                                            processImageFromContent(image);
                                            buttonEdit.setVisibility(View.VISIBLE);
                                            break;

                                        case "TEXT":
                                            processImageFromText(image);
                                            buttonEdit.setVisibility(View.VISIBLE);
                                            break;

                                        default:
                                            processImageFromBarcodeReader(image);
                                            buttonEdit.setVisibility(View.VISIBLE);
                                            break;
                                    }
                                }
                            }
                        }
                    });

    public void processImageFromBarcodeReader (InputImage image) {
        BarcodeScannerOptions options =
                new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).build();
        BarcodeScanner scanner = BarcodeScanning.getClient(options);
        Task<List<Barcode>> result = scanner.process(image)
                .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                    @Override
                    public void onSuccess(List<Barcode> barcodes) {
                        textViewOutput.append(Html.fromHtml("<font color='black'><b>Detected barcode:</b></font><br> \n", Html.FROM_HTML_MODE_LEGACY));
                        String result = "";
                        for (Barcode barcode : barcodes) {
                            result = barcode.getRawValue();
                            textViewOutput.append(result + "\n");
                        }
                        if (result.length() < 2) {
                            textViewOutput.append(" Barcode not found.\n");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        textViewOutput.setText("Failed");
                    }
                });
    }

    public void processImageFromContent(InputImage image) {
        ImageLabeler labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);

        labeler.process(image)
                .addOnSuccessListener(labels -> {
                    textViewOutput.append(Html.fromHtml("<font color='black'><b>Recognised image content:</b></font><br> \n", Html.FROM_HTML_MODE_LEGACY));
                    for (ImageLabel label : labels) {
                        textViewOutput.append(label.getText() + " - " + label.getConfidence() + "\n");
                    }
                })
                .addOnFailureListener(e -> textViewOutput.setText("Failed"));
    }

    public void processImageFromText(InputImage image) {
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        recognizer.process(image)
                .addOnSuccessListener(result -> {
                    textViewOutput.append(Html.fromHtml("<font color='black'><b>Extracted text:</b></font><br>\n", Html.FROM_HTML_MODE_LEGACY));
                    textViewOutput.append(result.getText());
                })
                .addOnFailureListener(e -> textViewOutput.setText("Failed"));
    }
}