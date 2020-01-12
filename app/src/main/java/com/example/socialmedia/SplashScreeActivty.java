package com.example.socialmedia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreeActivty extends AppCompatActivity {

    private ImageView logo;
    private static  int splashtimeout=5000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_scree_activty);

        logo=findViewById(R.id.image_splash);


        new Handler().postDelayed( new Runnable() {
            @Override
            public void run() {

                startActivity(new Intent(SplashScreeActivty.this,MainActivity.class));
                finish();
            }
        },splashtimeout);
        Animation myanim= AnimationUtils.loadAnimation(this,R.anim.mysplashanimation);
        logo.startAnimation(myanim);
    }
}
