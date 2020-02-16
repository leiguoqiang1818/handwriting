package com.leiguoqiang.handwriting.Test;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.leiguoqiang.handwriting.R;
import com.leiguoqiang.handwriting.constant.StrokeStatusConstant;
import com.leiguoqiang.handwriting.views.HandwritingView;

/**
 * @author leiguoqiang
 * contact: 274764936
 */

public class MainActivity extends AppCompatActivity {

    private HandwritingView handwritingview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handwritingview = findViewById(R.id.handwritingview);

        findViewById(R.id.recover).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handwritingview.recover();
            }
        });

        findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handwritingview.clear();
            }
        });

        findViewById(R.id.revocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handwritingview.revocation();
            }
        });

        findViewById(R.id.eraser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handwritingview.setStrokeStatus(StrokeStatusConstant.STROKE_STATUS_CUSTOM_ERASER);
            }
        });

        findViewById(R.id.pen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handwritingview.setStrokeStatus(StrokeStatusConstant.STROKE_STATUS_PEN);
            }
        });
        findViewById(R.id.imageview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ImageView) v).setImageBitmap(handwritingview.getBitmap());
//                final Bitmap bitmap = handwritingview.getBitmap();
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        String path = ShotUtils.getPathWithBitmap(bitmap, System.currentTimeMillis() + "");
//                        Log.i("11======", "=="+path);
//                    }
//                }).start();
//                handwritingview.resetData(null);

            }
        });

    }
}
