package com.chico.qiscuschallenge.View.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chico.qiscuschallenge.Model.User;
import com.chico.qiscuschallenge.R;
import com.chico.qiscuschallenge.Utils.UserPrefManager;
import com.chico.qiscuschallenge.View.Adapter.ContactAdapter;
import com.chico.qiscuschallenge.View.Adapter.OnItemClickListener;
import com.chico.qiscuschallenge.ViewModel.ContactVM;
import com.qiscus.sdk.chat.core.data.model.QiscusChatRoom;

import java.util.List;

public class ContactActivity extends AppCompatActivity implements ContactVM.View,OnItemClickListener {

    private ImageButton back;
    private ContactVM contactVM;
    private RecyclerView recyclerView;
    private ContactAdapter contactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        back=findViewById(R.id.back);

        recyclerView = findViewById(R.id.contact);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        contactAdapter = new ContactAdapter(this);
        contactAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(contactAdapter);

        back.setOnClickListener(v->{
            this.finish();
        });

        contactVM=new ContactVM(this,this);

        contactVM.loadContacts(1,100,"");



    }


    @Override
    public void onItemClick(int position) {
        contactVM.createChatRoom(contactAdapter.getData().get(position));
    }

    @Override
    public void showContacts(List<User> contacts) {
        contactAdapter.clear();
        contactAdapter.addOrUpdate(contacts);
    }

    @Override
    public void showChatRoomPage(QiscusChatRoom chatRoom) {
        startActivity(ChatRoomActivity.generateIntent(this, chatRoom));
        this.finish();

    }

    @Override
    public void showErrorMessage(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }
}
