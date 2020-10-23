package com.chico.qiscuschallenge.View.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.chico.qiscuschallenge.R;
import com.chico.qiscuschallenge.Utils.UserPrefManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        UserPrefManager pref=new UserPrefManager(this);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                pref.getCurrentUser(user -> {
                    if (user != null) {
                        startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                        finish();
                    }else{
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        finish();
                    }
                }, throwable -> showErrorMessage(throwable.getMessage()));

            }
        }, 2000);

    }

    void showErrorMessage(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }
}
