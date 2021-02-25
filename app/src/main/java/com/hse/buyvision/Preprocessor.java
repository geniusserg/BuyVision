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
    private static Preprocessor instance = null;
    private Preprocessor(){
    }

    public Bitmap preprocess(){
        source = bilinearFilter();
        source = invert();
        return source;
    }

    public static Preprocessor getInstance(){
        if (instance == null){
            instance = new Preprocessor();
        }
        return instance;
    }

    public static Preprocessor getInstance(Bitmap inputSource){
        source = inputSource;
        return getInstance();
    }
    public Bitmap getSource() {
        return source;
    }

    //invert input image to make more contrast
    public Bitmap invert()
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

    public Bitmap bilinearFilter(){
        return Bitmap.createScaledBitmap(source, source.getWidth(), source.getHeight(), true);
    }

    public void rotate(){
//rotate image
    }
    public void contrast(){
//add filter with high contrast
    }
    public void catTextBlocks(){
//cat text block
    }

    public File createImageFile(File storageDir) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        Log.println(Log.INFO, "File", "File");
        return image;
    }


}
