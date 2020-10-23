package com.chico.qiscuschallenge.View.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chico.qiscuschallenge.AppInit;
import com.chico.qiscuschallenge.R;
import com.chico.qiscuschallenge.Utils.UserPrefManager;
import com.chico.qiscuschallenge.ViewModel.LoginVM;
import com.qiscus.sdk.chat.core.QiscusCore;

public class LoginActivity extends AppCompatActivity implements LoginVM.View{

    private EditText displayName;
    private EditText userId;
    private EditText userKey;
    private Button loginButton;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userId = findViewById(R.id.userId);
        userKey = findViewById(R.id.userKey);
        displayName = findViewById(R.id.displayName);
        loginButton = findViewById(R.id.login);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait!");



        LoginVM vm=new LoginVM(this,this);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(userId.getText().toString())){
                    userId.setError("Must not empty!");
                }else if(TextUtils.isEmpty(userKey.getText().toString())){
                    userKey.setError("Must not empty!");
                }else if(TextUtils.isEmpty(displayName.getText().toString())){
                    displayName.setError("Must not empty!");
                }else{
                    vm.login(userId.getText().toString(),userKey.getText().toString(),displayName.getText().toString());
                }
            }
        });

    }




    @Override
    public void starthome() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    @Override
    public void showLoading() {
        progressDialog.show();
    }

    @Override
    public void dismissLoading() {
        progressDialog.dismiss();
    }

    @Override
    public void showErrorMessage(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }
}
