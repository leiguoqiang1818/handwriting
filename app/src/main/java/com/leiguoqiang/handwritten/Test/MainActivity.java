package com.leiguoqiang.handwritten.Test;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.leiguoqiang.handwritten.R;
import com.leiguoqiang.handwritten.constant.StrokeStatusConstant;
import com.leiguoqiang.handwritten.views.HandwritingView;

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
        findViewById(R.id.pen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handwritingview.i(StrokeStatusConstant.STROKE_STATUS_PEN);
            }
        });
        findViewById(R.id.imageview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ImageView) v).setImageBitmap(handwritingview.n());
            }
        });

    }
}
