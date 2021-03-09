package com.hse.buyvision;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1 ;
    private TextView analyzed_text;
    private ImageView image_view;
    private Button catch_button;
    private File externalFilesDir;
    private File photoFile;
    private String returnedText;
    private ProgressBar progress_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(MainActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        catch_button = findViewById(R.id.catch_button);
        analyzed_text  = findViewById(R.id.analyzed_text);
        analyzed_text.setMovementMethod(new ScrollingMovementMethod());
        analyzed_text.setOnTouchListener((v, event) -> {
            Speech.stop();
            Speech.vocalise(analyzed_text.getText().toString());
            return false;
        });
        image_view = findViewById(R.id.image_view);
        catch_button.setOnClickListener(v -> {
            Speech.stop();
            dispatchTakePictureIntent();
        });
        progress_bar = findViewById(R.id.progress_bar);

        externalFilesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        Speech.init(this);
    }


    // TODO: MOVE TO NEW ACTIVITY
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                photoFile = FileManager.createImageFile(externalFilesDir);
            } catch (IOException e) {
                Toast.makeText(MainActivity.this, "Error: can not find image", Toast.LENGTH_LONG).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        catch_button.setEnabled(false);
        catch_button.setText(R.string.loading_button);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap imageBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            Bitmap filteredBitmap = Preprocessor.preprocess(imageBitmap);
            image_view.setImageBitmap(imageBitmap);
            try{
                Analyzer.textResult.setValue("Загрузка");
                Analyzer.textResult.observe(this, s -> {
                    analyzed_text.setText(s);
                    Speech.vocalise(s);
                });
                Analyzer.analyzeText(filteredBitmap);
            }
            catch (RuntimeException e){
                analyzed_text.setText(R.string.recgonize_error);
            }
        }
        catch_button.setText(R.string.photo_button);
        catch_button.setEnabled(true);
        progress_bar.setVisibility(View.INVISIBLE);
    }

}
