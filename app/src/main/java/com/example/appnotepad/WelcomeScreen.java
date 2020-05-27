package com.example.appnotepad;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeScreen extends AppCompatActivity {

    private static int SPLASH_SCREEN = 4000;

    ImageView imageView;
    TextView textView1, textView2;
    Animation top, bottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome_screen);

        imageView = (ImageView) findViewById(R.id.imgWelcome);
        textView1 = (TextView) findViewById(R.id.txtWelcome1);
        textView2 = (TextView) findViewById(R.id.txtWelcome2);


        top = AnimationUtils.loadAnimation(WelcomeScreen.this, R.anim.top);
        bottom = AnimationUtils.loadAnimation(WelcomeScreen.this, R.anim.bottom);
        imageView.setAnimation(top);
        textView1.setAnimation(bottom);
        textView2.setAnimation(bottom);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(WelcomeScreen.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_SCREEN);

    }
}
