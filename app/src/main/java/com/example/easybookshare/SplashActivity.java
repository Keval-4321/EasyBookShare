package com.example.easybookshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.easybookshare.helpers.SharedPreferenceManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity
{

    ImageView imgLogo;
    TextView txtAppName;
    Animation topAnimation,bottomAnimation;

    SharedPreferenceManager sharedPreferenceManager;
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sharedPreferenceManager = new SharedPreferenceManager(SplashActivity.this);

        // to hide the actionbar
        getSupportActionBar().hide();

        imgLogo = findViewById(R.id.img_logo);
        txtAppName = findViewById(R.id.txt_app_name);

        firebaseAuth = FirebaseAuth.getInstance();

        // animation
        topAnimation = AnimationUtils.loadAnimation(this,R.anim.top_in);
        bottomAnimation = AnimationUtils.loadAnimation(this,R.anim.bottom_in);

        imgLogo.startAnimation(topAnimation);
        txtAppName.startAnimation(bottomAnimation);


    }

    @Override
    protected void onStart()
    {
        super.onStart();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run()
            {
                SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(SplashActivity.this);
                SharedPreferences.Editor editor = sharedPreferenceManager.sharedPreferencesData.edit();

                firebaseUser = firebaseAuth.getCurrentUser();
                // means user exist or not in firebase auth
                if(firebaseUser!=null)
                {
                    if(!sharedPreferenceManager.sharedPreferencesData.getBoolean("IS_LOGIN",false))
                    {
                        Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                        return;
                    }
                    else
                    {
                        editor.putString("USER_ID",firebaseAuth.getCurrentUser().getUid());
                        editor.putBoolean("IS_LOGIN",true);
                        editor.putBoolean("IS_SIGNUP",true);
                        editor.commit();

                        Intent intent = new Intent(SplashActivity.this,DashBoardActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    // to get delay
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run()
//                {
//                    //boolean isLogin = sharedPreferenceManager.sharedPreferencesData.getBoolean("IS_LOGIN",false);
                    //boolean isSignup = sharedPreferenceManager.sharedPreferencesData.getBoolean("IS_SIGNUP",false);
//                    if(isSignup)
//                    {
//                        Intent intent;
//                        if(isLogin)
//                        {
//                            intent = new Intent(SplashActivity.this, DashBoardActivity.class);
//                        }
//                        else
//                        {
//                            intent = new Intent(SplashActivity.this, LoginActivity.class);
//                        }
//                        startActivity(intent);
//                    }
//                    else
//                    {
//                        Intent intent = new Intent(SplashActivity.this,SignUpActivity.class);
//                        startActivity(intent);
//                    }
                    //                   finish();
//
//                }
//            },3000);
                }
                else  // means user does not exist create account
                {
                    editor.putString("USER_ID",null);
                    editor.putBoolean("IS_LOGIN",false);
                    editor.putBoolean("IS_SIGNUP",false);
                    editor.commit();
                    Intent intent = new Intent(SplashActivity.this,SignUpActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        },3000);
    }
}