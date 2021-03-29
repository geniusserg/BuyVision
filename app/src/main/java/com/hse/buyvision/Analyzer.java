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
    public static FirebaseVisionText resultBlocks;
    public static MutableLiveData<FirebaseVisionText> textResult = new MutableLiveData<FirebaseVisionText>();


    public static void analyzeText(Bitmap filteredBitmap){
        FirebaseVisionImage firebaseVisionImage = null;
        firebaseVisionImage = FirebaseVisionImage.fromBitmap(filteredBitmap);
        FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance().getCloudTextRecognizer();
        Task<FirebaseVisionText> result = textRecognizer.
                processImage(firebaseVisionImage).
                addOnSuccessListener(firebaseVisionText -> {
                    resultBlocks = firebaseVisionText;
                    textResult.setValue(resultBlocks);
                }).addOnFailureListener(e -> Log.d("FB Error", "Cant analyze "));
    }

}
