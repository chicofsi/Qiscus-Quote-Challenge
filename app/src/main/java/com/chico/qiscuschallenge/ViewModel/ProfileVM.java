package com.chico.qiscuschallenge.ViewModel;

import android.content.Context;

import com.chico.qiscuschallenge.Utils.UserPrefManager;

public class ProfileVM  {
    Context context;
    UserPrefManager pref;
    View a;

    public ProfileVM(Context ctx, View v){
        context=ctx;
        a=v;
        pref=new UserPrefManager(context);
    }

    public void logout(){
        pref.logout();
        a.logout();
    }

    public interface View {
        void logout();
    }
}
