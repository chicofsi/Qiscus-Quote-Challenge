package com.chico.qiscuschallenge.ViewModel;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.chico.qiscuschallenge.Utils.Action;
import com.qiscus.sdk.chat.core.QiscusCore;
import com.qiscus.sdk.chat.core.data.model.QiscusChatRoom;
import com.qiscus.sdk.chat.core.data.remote.QiscusApi;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HomeVM extends ViewModel {
    private View view;

    public HomeVM(View view){
        this.view=view;
    }

    public void getChatList(){
        Observable.from(QiscusCore.getDataStore().getChatRooms(100))
                .filter(chatRoom -> chatRoom.getLastComment() != null)
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(qiscusChatRooms -> view.showChatRooms(qiscusChatRooms),
                        throwable -> view.showErrorMessage(throwable.getMessage()));

        QiscusApi.getInstance()
                .getAllChatRooms(true, false, true, 1, 100)
                .flatMap(Observable::from)
                .doOnNext(qiscusChatRoom -> QiscusCore.getDataStore().addOrUpdate(qiscusChatRoom))
                .filter(chatRoom -> chatRoom.getLastComment().getId() != 0)
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(qiscusChatRooms -> view.showChatRooms(qiscusChatRooms),
                        throwable -> view.showErrorMessage(throwable.getMessage()));
    }

    public void openChat(QiscusChatRoom chatRoom){
        view.showChatRoomPage(chatRoom);
    }

    public interface View {
        void showChatRooms(List<QiscusChatRoom> chatRooms);

        void showChatRoomPage(QiscusChatRoom chatRoom);

        void showErrorMessage(String errorMessage);
    }
}
