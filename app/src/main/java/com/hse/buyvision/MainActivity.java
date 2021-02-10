package com.hse.buyvision;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1 ;
    private Button catch_button;
    private TextView analyzed_text;
    private ImageView image_view;
    private Bitmap imageBitmap;
    private Bitmap filteredBitmap;
    private String currentPhotoPath;
    private Uri createdPhotoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(MainActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        catch_button = findViewById(R.id.catch_button);
        analyzed_text  = findViewById(R.id.analyzed_text);
        image_view = findViewById(R.id.image_view);
        catch_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ignored) {
            }
            if (photoFile != null) {
                createdPhotoUri = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, createdPhotoUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    // React on activities complete
    // REQUEST_IMAGE_CAPTURE -> save iamge and display it on screen

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            FirebaseVisionImage firebaseVisionImage = null;
            try {
                firebaseVisionImage = FirebaseVisionImage.fromFilePath(this, createdPhotoUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Preprocessor preprocessor = new Preprocessor(imageBitmap);
            filteredBitmap = preprocessor.preprocess();
            image_view.setImageBitmap(filteredBitmap);
            FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance().getCloudTextRecognizer();
            Task<FirebaseVisionText> result = textRecognizer.
                    processImage(firebaseVisionImage).
                    addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                        @Override
                        public void onSuccess(FirebaseVisionText firebaseVisionText) {
                            parseFirebaseVisionTextBlocks(firebaseVisionText);
                        };
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "Error: can not get text from the photo", Toast.LENGTH_LONG).show();
                };
            });
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }



    private void parseFirebaseVisionTextBlocks(FirebaseVisionText result){
        String resultText = "";
        int blockNumber = 0;
        for (FirebaseVisionText.TextBlock block: result.getTextBlocks()) {
            resultText = resultText + "Block " + Integer.toString(blockNumber) + "\n";
            String blockText = "";
            for (FirebaseVisionText.Line line: block.getLines()) {
                for (FirebaseVisionText.Element element: line.getElements()) {
                    String elementText = element.getText();
                    blockText += elementText + " ";
                }
            }
            resultText += blockText + "\n";
            blockNumber++;
        }
        System.out.println(resultText);
        analyzed_text.setText(resultText);
    }

}