package com.hse.buyvision;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
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
    private TextView analyzed_text;
    private ImageView image_view;
    private Bitmap imageBitmap;
    private String currentPhotoPath;
    private Uri createdPhotoUri;
    private String resultText;
    private Button catch_button;
    private File externalFilesDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(MainActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        catch_button = findViewById(R.id.catch_button);
        analyzed_text  = findViewById(R.id.analyzed_text);
        analyzed_text.setMovementMethod(new ScrollingMovementMethod());
        image_view = findViewById(R.id.image_view);
        catch_button.setOnClickListener(v -> dispatchTakePictureIntent());
        externalFilesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            Log.println(Log.INFO,"start takiong pict", "START");
            try {
                photoFile = Preprocessor.getInstance().createImageFile(externalFilesDir);
            } catch (IOException e) {
                Toast.makeText(MainActivity.this, "Error: can not find image", Toast.LENGTH_LONG).show();
                Log.println(Log.ASSERT,"start takiong pict", "START");
            }
            if (photoFile != null) {
                Uri createdPhotoUri = FileProvider.getUriForFile(this,
                        "com.hse.buyvision.fileprovider",
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
        catch_button.setEnabled(false);
        catch_button.setText(R.string.loading_button);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //Preprocess image
            Bitmap imageBitmap = BitmapFactory.decodeFile(currentPhotoPath);
            Bitmap filteredBitmap = Preprocessor.getInstance(imageBitmap).preprocess();
            image_view.setImageBitmap(imageBitmap);
            analyzed_text.setText(R.string.loading_text);
            FirebaseVisionImage firebaseVisionImage = null;
            firebaseVisionImage = FirebaseVisionImage.fromBitmap(filteredBitmap);
            FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance().getCloudTextRecognizer();
            Task<FirebaseVisionText> result = textRecognizer.
                    processImage(firebaseVisionImage).
                    addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                        @Override
                        public void onSuccess(FirebaseVisionText firebaseVisionText) {
                            analyzed_text.setText(Analyzer.getInstance().parseFirebaseVisionTextBlocks(firebaseVisionText));
                        };
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "Error: can not get text from the photo", Toast.LENGTH_LONG).show();
                };
            });
        }
        catch_button.setText(R.string.photo_button);
        catch_button.setEnabled(true);
    }

}