package com.foodmaster.activity;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import com.foodmaster.R;

public class SplashActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        ImageView iv = (ImageView)findViewById(R.id.animationImage);
        iv.setVisibility(ImageView.VISIBLE);
        iv.setBackgroundResource(R.layout.loadinganimation);

        AnimationDrawable animation = (AnimationDrawable)iv.getBackground();
        animation.stop();
        animation.start();

    }
}
