package com.hse.buyvision;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Preprocessor {
    private static Bitmap source;

    public static Bitmap preprocess(Bitmap input){
        source = input;
        source = bilinearFilter();
        source = invert();
        return source;
    }

    public static Bitmap getSource() {
        return source;
    }

    //invert input image to make more contrast
    public static Bitmap invert()
    {
        int height = source.getHeight();
        int width = source.getWidth();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        ColorMatrix matrixGrayscale = new ColorMatrix();
        matrixGrayscale.setSaturation(0);
        ColorMatrix matrixInvert = new ColorMatrix();
        matrixInvert.set(new float[]
                {
                        -1.0f, 0.0f, 0.0f, 0.0f, 255.0f,
                        0.0f, -1.0f, 0.0f, 0.0f, 255.0f,
                        0.0f, 0.0f, -1.0f, 0.0f, 255.0f,
                        0.0f, 0.0f, 0.0f, 1.0f, 0.0f
                });
        matrixInvert.preConcat(matrixGrayscale);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrixInvert);
        paint.setColorFilter(filter);
        canvas.drawBitmap(source, 0, 0, paint);
        return bitmap;
    }

    public static Bitmap bilinearFilter(){
        return Bitmap.createScaledBitmap(source, source.getWidth(), source.getHeight(), true);
    }

}
