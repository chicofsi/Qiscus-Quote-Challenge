package com.chico.qiscuschallenge;

import androidx.multidex.MultiDexApplication;

import com.qiscus.sdk.chat.core.QiscusCore;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.one.EmojiOneProvider;

public class AppInit extends MultiDexApplication {
    private static AppInit instance;


    private static void initEmoji() {
        EmojiManager.install(new EmojiOneProvider());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        QiscusCore.setup(this, "sdksample");

        initEmoji();

    }

    public static AppInit getInstance() {
        return instance;
    }

}
