package com.chico.qiscuschallenge.View.Fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chico.qiscuschallenge.R;
import com.chico.qiscuschallenge.View.Adapter.CommentsAdapter;
import com.chico.qiscuschallenge.View.Adapter.QiscusChatScrollListener;
import com.chico.qiscuschallenge.ViewModel.ChatVM;
import com.qiscus.sdk.chat.core.data.local.QiscusCacheManager;
import com.qiscus.sdk.chat.core.data.model.QiscusChatRoom;
import com.qiscus.sdk.chat.core.data.model.QiscusComment;

import java.util.List;

public class ChatRoomFragment extends Fragment implements QiscusChatScrollListener.Listener,ChatVM.View {

    private EditText messageField;
    private ImageView sendButton;
    private ProgressBar progressBar;
    private RecyclerView chat;
    private LinearLayout emptyChat;

    private static final String CHAT_ROOM_KEY = "extra_chat_room";

    private QiscusChatRoom chatRoom;

    private CommentsAdapter commentsAdapter;

    private ChatVM chatVM;

    public static ChatRoomFragment newInstance(QiscusChatRoom chatRoom) {
        ChatRoomFragment chatRoomFragment = new ChatRoomFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(CHAT_ROOM_KEY, chatRoom);
        chatRoomFragment.setArguments(bundle);
        return chatRoomFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_room, container, false);

        messageField = view.findViewById(R.id.field_message);
        sendButton = view.findViewById(R.id.button_send);
        progressBar = view.findViewById(R.id.progress_bar);
        chat = view.findViewById(R.id.chat);
        emptyChat = view.findViewById(R.id.empty_chat);


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        chatRoom = getArguments().getParcelable(CHAT_ROOM_KEY);
        if (chatRoom == null) {
            throw new RuntimeException("Please provide chat room");
        }

        chatVM=new ChatVM(this,chatRoom);
        sendButton.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(messageField.getText())) {
                chatVM.sendComment(messageField.getText().toString());
                messageField.setText("");
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        chat.setLayoutManager(layoutManager);
        chat.setHasFixedSize(true);
        chat.addOnScrollListener(new QiscusChatScrollListener(layoutManager, this));
        commentsAdapter = new CommentsAdapter(getActivity());
        chat.setAdapter(commentsAdapter);

        chatVM.loadComments(100);
    }

    private void loadMoreComments() {
        if (progressBar.getVisibility() == android.view.View.GONE && commentsAdapter.getItemCount() > 0) {
            QiscusComment comment = commentsAdapter.getData().get(commentsAdapter.getItemCount() - 1);
            if (comment.getId() == -1 || comment.getCommentBeforeId() > 0) {
                chatVM.loadOlderCommentThan(comment);
            }
        }
    }

    @Override
    public void onTopOffListMessage() {
        loadMoreComments();
    }

    @Override
    public void onMiddleOffListMessage() {

    }

    @Override
    public void onBottomOffListMessage() {

    }

    @Override
    public void onResume() {
        super.onResume();
        QiscusCacheManager.getInstance().setLastChatActivity(true, chatRoom.getId());
    }

    @Override
    public void onPause() {
        super.onPause();
        QiscusCacheManager.getInstance().setLastChatActivity(false, chatRoom.getId());
    }



    @Override
    public void onRoomChanged(QiscusChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    @Override
    public void showComments(List<QiscusComment> comments) {
        commentsAdapter.addOrUpdate(comments);
    }

    @Override
    public void initRoomData(QiscusChatRoom chatRoom, List<QiscusComment> comments) {
        this.chatRoom = chatRoom;
        commentsAdapter.addOrUpdate(comments);

        if (comments.size() == 0) {
            emptyChat.setVisibility(View.VISIBLE);
            chat.setVisibility(View.GONE);
        } else {
            emptyChat.setVisibility(View.GONE);
            chat.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoadCommentsError(Throwable throwable) {
        throwable.printStackTrace();
        Log.e("ChatRoomFragment", throwable.getMessage());
    }

    @Override
    public void onSendingComment(QiscusComment comment) {
        commentsAdapter.addOrUpdate(comment);
        chat.smoothScrollToPosition(0);
        if (emptyChat.isShown()) {
            emptyChat.setVisibility(View.GONE);
            chat.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSuccessSendComment(QiscusComment comment) {
        commentsAdapter.addOrUpdate(comment);
    }

    @Override
    public void onFailedSendComment(QiscusComment comment) {
        commentsAdapter.addOrUpdate(comment);
    }

    @Override
    public void onNewComment(QiscusComment comment) {
        commentsAdapter.addOrUpdate(comment);
        if (((LinearLayoutManager) chat.getLayoutManager()).findFirstVisibleItemPosition() <= 2) {
            chat.smoothScrollToPosition(0);
        }

        if (emptyChat.isShown()) {
            emptyChat.setVisibility(View.GONE);
            chat.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showCommentsAndScrollToTop(List<QiscusComment> qiscusComments) {

    }

    @Override
    public void showError(String errorMessage) {
        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void dismissLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void notifyDataChanged() {
        commentsAdapter.notifyDataSetChanged();
    }

    @Override
    public void showLoadMoreLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoadMore(List<QiscusComment> comments) {
        commentsAdapter.addOrUpdate(comments);
    }

    @Override
    public void refreshComment(QiscusComment comment) {
        commentsAdapter.addOrUpdate(comment);
    }

    @Override
    public void clearCommentsBefore(long timestamp) {

    }

}
