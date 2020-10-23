package com.chico.qiscuschallenge.ViewModel;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.chico.qiscuschallenge.Model.User;
import com.chico.qiscuschallenge.Utils.UserPrefManager;
import com.qiscus.sdk.chat.core.QiscusCore;
import com.qiscus.sdk.chat.core.data.model.QiscusChatRoom;
import com.qiscus.sdk.chat.core.data.remote.QiscusApi;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ContactVM extends ViewModel {

    View view;
    UserPrefManager usr;
    private List<User> users;

    public ContactVM(View view, Context ctx){
        this.view=view;
        usr=new UserPrefManager(ctx);
    }

    public void loadContacts(int page, int limit, String query) {

        QiscusApi.getInstance().getUsers(query, page, limit)
                .flatMap(Observable::from)
                .filter(user -> !user.getUsername().equals(""))
                .map(usr::qiscusAccountData)
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(users -> {
                            view.showContacts(users);
                            this.users = users;
                        }, throwable -> {
                            view.showErrorMessage(throwable.getMessage());
                        });

    }

    public void createChatRoom(User user){
        QiscusChatRoom savedChatRoom = QiscusCore.getDataStore().getChatRoom(user.getId());
        if (savedChatRoom != null) {
            view.showChatRoomPage(savedChatRoom);
            return;
        }

        QiscusApi.getInstance()
                .chatUser(user.getId(), null)
                .doOnNext(chatRoom -> QiscusCore.getDataStore().addOrUpdate(chatRoom))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(chatRoom -> view.showChatRoomPage(chatRoom),
                        throwable -> view.showErrorMessage(throwable.getMessage()));

    }

    public interface View {
        void showContacts(List<User> contacts);

        void showChatRoomPage(QiscusChatRoom chatRoom);

        void showErrorMessage(String errorMessage);
    }
}
