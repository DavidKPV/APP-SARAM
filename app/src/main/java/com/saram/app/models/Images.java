package com.saram.app.models;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import com.squareup.picasso.Transformation;

import java.io.ByteArrayOutputStream;

public class Images implements Transformation {
    public byte[] transformarImagenAByte(ImageView imagen){
        byte[] byteArray = null;
        if(imagen.getDrawable() != null) {
            // TRANSFORMAMOS LA IMAGEN COMO BITMAP
            Bitmap bitmap = ((BitmapDrawable) imagen.getDrawable()).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byteArray = stream.toByteArray();
        }
        return byteArray;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());
        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;
        Bitmap result = Bitmap.createBitmap(source, x, y, size, size);
        if (result != source) {
            source.recycle();
        }
        return result;
    }

    @Override
    public String key() {
        return "square()";
    }
}
