package com.hse.buyvision;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

public class Preprocessor {
    private Bitmap source;
    public Preprocessor(Bitmap inputBitmap){
        source = inputBitmap;
    }

    public Bitmap preprocess(){
        source = bilinearFilter();
        source = invert();
        return source;
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
}
