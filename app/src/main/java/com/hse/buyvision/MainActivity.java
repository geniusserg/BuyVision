package com.hse.buyvision;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
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

import java.util.List;


public class MainActivity extends AppCompatActivity {
    private Button catch_button;
    private TextView analyzed_text;
    private ImageView image_view;
    private Bitmap imageBitmap;
    private Bitmap filteredBitmap;
    private String recognizedText;
    static final int REQUEST_IMAGE_CAPTURE = 1;

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
                dispatchTakePictureIntent(); //try to take a photo
            }
        });
    }

    // React on activities complete
    // REQUEST_IMAGE_CAPTURE -> save iamge and display it on screen

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap)extras.get("data");
            Preprocessor preprocessor = new Preprocessor(imageBitmap);
            filteredBitmap = preprocessor.preprocess();
            image_view.setImageBitmap(imageBitmap);
        }
        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(imageBitmap);
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

    // Make intent to create photo by camera
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(MainActivity.this, "Error: can not take a photo", Toast.LENGTH_LONG).show();
        }
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