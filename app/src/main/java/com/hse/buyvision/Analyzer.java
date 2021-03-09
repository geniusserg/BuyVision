package com.hse.buyvision;


import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

class Analyzer{
    private static FirebaseVisionText resultBlocks;
    public static MutableLiveData<String> textResult = new MutableLiveData<String>("");

    public static void analyzeText(Bitmap filteredBitmap){
        FirebaseVisionImage firebaseVisionImage = null;
        firebaseVisionImage = FirebaseVisionImage.fromBitmap(filteredBitmap);
        FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance().getCloudTextRecognizer();
        Task<FirebaseVisionText> result = textRecognizer.
                processImage(firebaseVisionImage).
                addOnSuccessListener(firebaseVisionText -> {
                    resultBlocks = firebaseVisionText;
                    textResult.setValue(parseFirebaseVisionTextBlocks());
                }).addOnFailureListener(e -> Log.d("FB Error", "Cant analyze "));
    }

    public static String parseFirebaseVisionTextBlocks(){
        String resultText = "";
        for (FirebaseVisionText.TextBlock block: resultBlocks.getTextBlocks()) {
            String blockText = "";
            for (FirebaseVisionText.Line line: block.getLines()) {
                for (FirebaseVisionText.Element element: line.getElements()) {
                    String elementText = element.getText();
                    blockText += elementText + " ";
                }
            }
            resultText += blockText + "\n";
        }
        System.out.println("TEXT RECOGNITION RESULT");
        System.out.println(resultText);
        return resultText;
    }
}
