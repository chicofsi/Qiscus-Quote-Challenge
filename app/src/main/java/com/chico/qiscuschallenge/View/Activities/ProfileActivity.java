package com.chico.qiscuschallenge.View.Activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.chico.qiscuschallenge.R;
import com.chico.qiscuschallenge.ViewModel.ProfileVM;
import com.qiscus.sdk.chat.core.QiscusCore;
import com.qiscus.sdk.chat.core.data.local.QiscusCacheManager;
import com.qiscus.sdk.chat.core.data.model.QiscusAccount;
import com.qiscus.sdk.chat.core.data.remote.QiscusApi;
import com.qiscus.sdk.chat.core.util.QiscusFileUtil;

import java.io.File;
import java.io.IOException;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ProfileActivity extends AppCompatActivity implements ProfileVM.View {
    protected static final int TAKE_PICTURE_REQUEST = 3;
    protected static final int RC_CAMERA_PERMISSION = 128;
    private static final int REQUEST_PICK_IMAGE = 1;
    private static final int REQUEST_FILE_PERMISSION = 2;
    private static final String[] FILE_PERMISSION = {
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE"
    };
    private static final String[] CAMERA_PERMISSION = {
            "android.permission.CAMERA",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE",
    };
    private LinearLayout logout, llBottom;
    private ImageView ivAvatar, ivEditName, btBack;
    private TextView tvName, tvUniqueID;
    private PopupWindow mPopupWindow;
    private ProfileVM profileVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        tvName = findViewById(R.id.tvName);
        tvUniqueID = findViewById(R.id.tvUniqueID);
        btBack = findViewById(R.id.back);
        llBottom = findViewById(R.id.llBottom);
        logout = findViewById(R.id.llLogout);

        profileVM = new ProfileVM(this,this);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileVM.logout();
            }
        });



        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        loadProfile();

    }



    @Override
    protected void onResume() {
        super.onResume();
        loadProfile();
    }


    private void loadProfile() {
        //load profile from local db
        tvUniqueID.setText(QiscusCore.getQiscusAccount().getEmail());
        tvName.setText(QiscusCore.getQiscusAccount().getUsername());
    }



    @Override
    public void logout() {
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
