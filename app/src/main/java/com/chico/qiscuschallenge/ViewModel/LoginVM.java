package com.chico.qiscuschallenge.ViewModel;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.ViewModel;

import com.chico.qiscuschallenge.Utils.UserPrefManager;
import com.qiscus.sdk.chat.core.QiscusCore;
import com.qiscus.sdk.chat.core.data.model.QiscusAccount;

public class LoginVM extends ViewModel {
    Context context;
    UserPrefManager pref;
    View a;

    public LoginVM(Context ctx,View v){
        context=ctx;
        a=v;
        pref=new UserPrefManager(context);
    }

    public void login(String email, String key, String displayName){
        a.showLoading();
        QiscusCore.setUser(email, key)
                .withUsername(displayName)
                .save(new QiscusCore.SetUserListener() {
                    @Override
                    public void onSuccess(QiscusAccount qiscusAccount) {
                        a.dismissLoading();
                        pref.login(qiscusAccount);
                        a.starthome();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        a.dismissLoading();
                        a.showErrorMessage(throwable.getMessage());
                    }
                });

    }

    public interface View{
        void starthome();

        void showLoading();

        void dismissLoading();

        void showErrorMessage(String errorMessage);
    }

}
