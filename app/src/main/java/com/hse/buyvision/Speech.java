package com.hse.buyvision;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;
import java.util.Random;

public class Speech {
    private static TextToSpeech mTTS;
    private static int result;
    public static void init(Context context){
        mTTS = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS){
                int result = mTTS.setLanguage(new Locale("ru"));
            }
        });
    }

    public static void vocalise(String string){
        if(mTTS == null || (result == TextToSpeech.LANG_MISSING_DATA
                || result == TextToSpeech.LANG_NOT_SUPPORTED)){
            Log.d("Error", "TextToSpeach manager has not been correctly initialized");
        }
        else{
            mTTS.speak(string, TextToSpeech.QUEUE_FLUSH, null, "vocalize text");
        }
    }

    public static void stop(){
        if(mTTS == null || (result == TextToSpeech.LANG_MISSING_DATA
                || result == TextToSpeech.LANG_NOT_SUPPORTED)){
            Log.d("Error", "TextToSpeach manager has not been correctly initialized");
        }
        else{
            mTTS.stop();
        }
    }
}
