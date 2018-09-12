package com.example.root.resizerectangleviewdemo;

import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private ResizeRectangleView mResizeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mResizeView = findViewById(R.id.resize_rect);

        mResizeView.setmUpCallback(new ResizeRectangleView.CropVisionFinishCallback() {
            @Override
            public void onCropVisionAreaFinish(Rect rect) {
                Toast.makeText(MainActivity.this
                        ,"select rect"+"--width = "+rect.width()+"--height = "+rect.height()
                        ,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
