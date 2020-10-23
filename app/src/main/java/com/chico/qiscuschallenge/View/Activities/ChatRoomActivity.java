package com.chico.qiscuschallenge.View.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chico.qiscuschallenge.Model.User;
import com.chico.qiscuschallenge.R;
import com.chico.qiscuschallenge.View.Adapter.ContactAdapter;
import com.chico.qiscuschallenge.View.Adapter.OnItemClickListener;
import com.chico.qiscuschallenge.View.Fragment.ChatRoomFragment;
import com.chico.qiscuschallenge.ViewModel.ContactVM;
import com.qiscus.sdk.chat.core.QiscusCore;
import com.qiscus.sdk.chat.core.data.model.QiscusChatRoom;
import com.qiscus.sdk.chat.core.data.model.QiscusRoomMember;
import com.qiscus.sdk.chat.core.data.remote.QiscusPusherApi;

import java.util.List;

import rx.Observable;

public class ChatRoomActivity extends AppCompatActivity {
    private static final String CHAT_ROOM_KEY = "extra_chat_room";

    private ImageButton back;
    private QiscusChatRoom chatRoom;
    private String opponentEmail;

    public static Intent generateIntent(Context context, QiscusChatRoom chatRoom) {
        Intent intent = new Intent(context, ChatRoomActivity.class);
        intent.putExtra(CHAT_ROOM_KEY, chatRoom);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        chatRoom = getIntent().getParcelableExtra(CHAT_ROOM_KEY);
        if (chatRoom == null) {
            finish();
            return;
        }

        back=findViewById(R.id.back);

        back.setOnClickListener(v->{
            this.finish();
        });

        TextView roomName = findViewById(R.id.room_name);

        roomName.setText(chatRoom.getName());

        opponentEmail = Observable.from(chatRoom.getMember())
                .map(QiscusRoomMember::getEmail)
                .filter(email -> !email.equals(QiscusCore.getQiscusAccount().getEmail()))
                .first()
                .toBlocking()
                .single();


        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container,
                        ChatRoomFragment.newInstance(chatRoom),
                        ChatRoomFragment.class.getName())
                .commit();
    }



}
