package com.leiguoqiang.handwriting.Test;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.provider.SyncStateContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

public class ShotUtils {

    public static Bitmap getBitmap(@NonNull final View v) {
        Bitmap result = null;
        if (null == v) {
            return result;
        }
        try {
            result = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(result);
            v.draw(c);
        } catch (Exception e) {
            e.printStackTrace();
            result = null;
        }
        return result;
    }

    public static String viewShot(@NonNull final View v, @NonNull String noteName) {
        String savePath = null;
        if (null == v) {
            return savePath;
        }
        try {
            Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.RGB_565);
            Canvas c = new Canvas(bitmap);
            v.draw(c);
            savePath = createImagePath(noteName);
            boolean result = compressAndGenImage(bitmap, savePath);
            if (!result) {
                savePath = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            savePath = null;
        }
        return savePath;
    }

    public static String getPathWithBitmap(@NonNull final Bitmap bitmap, @NonNull String noteName) {
        String savePath = null;
        if (null == bitmap) {
            return savePath;
        }
        try {
            savePath = createImagePath(noteName);
            boolean result = compressAndGenImage(bitmap, savePath);
            if (!result) {
                savePath = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            savePath = null;
        }
        return savePath;
    }

    private static String createImagePath(String noteName) {
        if (TextUtils.isEmpty(noteName)) {
            return "";
        }
        //判断sd卡是否存在
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
           String result =  Environment.getExternalStorageDirectory().getAbsolutePath() + "/dosmono/note/shot/";
            String filePath = result + noteName + ".png";
            File file = new File(result);
            if (!file.exists()) {
                file.mkdirs();
            }
            return filePath;
        }
        return "";
    }

    private static Boolean compressAndGenImage(Bitmap image, String outPath) throws IOException {
        boolean result = false;
        FileOutputStream fos = null;
        try {
            File file = new File(outPath);
            if (file.exists() && file.isFile()) {
                file.delete();
            }
            fos = new FileOutputStream(file);
            int options = 70;
            image.compress(Bitmap.CompressFormat.PNG, options, fos);
            if (fos != null) {
                fos.flush();
                fos.close();
            }
            result = true;
        } catch (Exception e) {
            Log.i("11======", e.toString());
        } finally {
            if (fos != null) {
                fos.flush();
                fos.close();
            }
        }
        return result;
    }

}
