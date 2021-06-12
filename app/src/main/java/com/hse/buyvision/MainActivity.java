package com.hse.buyvision;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.biometric.BiometricPrompt;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.FirebaseApp;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1 ;

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private TextView analyzed_text;
    private ImageView image_view;
    private Button catch_button;
    private File externalFilesDir;
    private File photoFile;
    private Button historyButton;
    private DBHelper dbHelper;
    private DBWrapper dbWrapper;
    private ItemModel item;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(MainActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new DBHelper(MainActivity.this);
        dbWrapper = new DBWrapper(dbHelper);
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(MainActivity.this, executor, new BiometricPrompt.AuthenticationCallback(){

            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                MainActivity.this.startActivity(intent);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.auth_dialog_title))
                .setSubtitle(getString(R.string.auth_dialog_description))
                .setNegativeButtonText(getString(R.string.auth_dialog_negative))
                .build();

        catch_button = findViewById(R.id.photo_button);
        historyButton = findViewById(R.id.history_button);
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
        historyButton.setOnTouchListener((v, event) -> {
            biometricPrompt.authenticate(promptInfo);
            return false;
        });
        externalFilesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        Speech.init(this);
        // dispatchTakePictureIntent();
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
            analyzed_text.setText("Загрузка");
            Speech.vocalise("Загрузка");
            Bitmap imageBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            Bitmap filteredBitmap = Preprocessor.preprocess(imageBitmap);
            image_view.setImageBitmap(imageBitmap);
            item = new ItemModel();
            try {
                item.photo = photoFile.getAbsolutePath();
                item.date = new Date();
                Analyzer.analyzeText(filteredBitmap);
                Analyzer.textResult.observe(this, s -> {
                    if (s == null){
                        return;
                    }
                    String analyzeResult ="";
                    
                    analyzeResult=TextParser.parseFirebaseVisionTextBlocks(s);
                    analyzeResult=TextParser.removeTrash(analyzeResult);

                    Translater translater = new Translater();
                    translater.setTranslateString(analyzeResult);
                    translater.start();
                    try {
                        translater.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    analyzeResult=translater.resultedText;
                    analyzeResult=TextParser.removeTrash(analyzeResult);

                    Speech.vocalise(analyzeResult);

                    analyzed_text.setText(analyzeResult);
                    item.text=analyzeResult;
                    dbWrapper.save(item);

                    translater.setTranslateString("");
                    Analyzer.textResult.setValue(null);
                    Analyzer.textResult.removeObservers(this);
                });
            }
            catch (RuntimeException e){
                analyzed_text.setText(R.string.recgonize_error);
            }
            
        }
        catch_button.setText(R.string.photo_button);
        catch_button.setEnabled(true);
    }

}
