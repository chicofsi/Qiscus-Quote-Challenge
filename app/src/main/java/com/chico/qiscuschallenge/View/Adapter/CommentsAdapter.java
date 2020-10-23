package com.chico.qiscuschallenge.View.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.chico.qiscuschallenge.R;
import com.chico.qiscuschallenge.Utils.DateUtils;
import com.qiscus.sdk.chat.core.QiscusCore;
import com.qiscus.sdk.chat.core.data.model.QiscusChatRoom;
import com.qiscus.sdk.chat.core.data.model.QiscusComment;
import com.qiscus.sdk.chat.core.data.remote.QiscusApi;
import com.qiscus.sdk.chat.core.util.QiscusAndroidUtil;
import com.qiscus.sdk.chat.core.util.QiscusDateUtil;

import org.json.JSONObject;

import java.io.File;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CommentsAdapter extends SortedRecyclerViewAdapter<QiscusComment, CommentsAdapter.VH> {

    private static final int TYPE_MY_TEXT = 1;
    private static final int TYPE_OPPONENT_TEXT = 2;


    private Context context;
    private long lastDeliveredCommentId;
    private long lastReadCommentId;

    public CommentsAdapter(Context context) {
        this.context = context;
    }

    public interface RecyclerViewItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    @Override
    protected Class<QiscusComment> getItemClass() {
        return QiscusComment.class;
    }

    @Override
    protected int compare(QiscusComment item1, QiscusComment item2) {
        if (item2.equals(item1)) { //Same comments
            return 0;
        } else if (item2.getId() == -1 && item1.getId() == -1) { //Not completed comments
            return item2.getTime().compareTo(item1.getTime());
        } else if (item2.getId() != -1 && item1.getId() != -1) { //Completed comments
            return QiscusAndroidUtil.compare(item2.getId(), item1.getId());
        } else if (item2.getId() == -1) {
            return 1;
        } else if (item1.getId() == -1) {
            return -1;
        }
        return item2.getTime().compareTo(item1.getTime());
    }

    @Override
    public int getItemViewType(int position) {
        QiscusComment comment = getData().get(position);
        switch (comment.getType()) {
            case TEXT:
                return comment.isMyComment() ? TYPE_MY_TEXT : TYPE_OPPONENT_TEXT;
            default:
                return comment.isMyComment() ? TYPE_MY_TEXT : TYPE_OPPONENT_TEXT;
        }
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_MY_TEXT:
            case TYPE_OPPONENT_TEXT:
                return new TextVH(getView(parent, viewType));
            default:
                return new TextVH(getView(parent, viewType));
        }
    }

    private View getView(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_MY_TEXT:
                return LayoutInflater.from(context).inflate(R.layout.item_text_me, parent, false);
            case TYPE_OPPONENT_TEXT:
                return LayoutInflater.from(context).inflate(R.layout.item_text_opponent, parent, false);
            default:
                return LayoutInflater.from(context).inflate(R.layout.item_text_opponent, parent, false);
        }
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        holder.bind(getData().get(position));
        holder.position = position;

        if (position == getData().size() - 1) {
            holder.setNeedToShowDate(true);
        } else {
            holder.setNeedToShowDate(!QiscusDateUtil.isDateEqualIgnoreTime(getData().get(position).getTime(),
                    getData().get(position + 1).getTime()));
        }


        setOnClickListener(holder.itemView, position);

    }

    public void addOrUpdate(List<QiscusComment> comments) {
        for (QiscusComment comment : comments) {
            int index = findPosition(comment);
            if (index == -1) {
                getData().add(comment);
            } else {
                getData().updateItemAt(index, comment);
            }
        }
        notifyDataSetChanged();
    }

    public void addOrUpdate(QiscusComment comment) {
        int index = findPosition(comment);
        if (index == -1) {
            getData().add(comment);
        } else {
            getData().updateItemAt(index, comment);
        }
        notifyDataSetChanged();
    }

    public void remove(QiscusComment comment) {
        getData().remove(comment);
        notifyDataSetChanged();
    }

    public QiscusComment getLatestSentComment() {
        int size = getData().size();
        for (int i = 0; i < size; i++) {
            QiscusComment comment = getData().get(i);
            if (comment.getState() >= QiscusComment.STATE_ON_QISCUS) {
                return comment;
            }
        }
        return null;
    }

    public void updateLastDeliveredComment(long lastDeliveredCommentId) {
        this.lastDeliveredCommentId = lastDeliveredCommentId;
        updateCommentState();
        notifyDataSetChanged();
    }

    public void updateLastReadComment(long lastReadCommentId) {
        this.lastReadCommentId = lastReadCommentId;
        this.lastDeliveredCommentId = lastReadCommentId;
        updateCommentState();
        notifyDataSetChanged();
    }

    private void updateCommentState() {
        int size = getData().size();
        for (int i = 0; i < size; i++) {
            if (getData().get(i).getState() > QiscusComment.STATE_SENDING) {
                if (getData().get(i).getId() <= lastReadCommentId) {
                    if (getData().get(i).getState() == QiscusComment.STATE_READ) {
                        break;
                    }
                    getData().get(i).setState(QiscusComment.STATE_READ);
                } else if (getData().get(i).getId() <= lastDeliveredCommentId) {
                    if (getData().get(i).getState() == QiscusComment.STATE_DELIVERED) {
                        break;
                    }
                    getData().get(i).setState(QiscusComment.STATE_DELIVERED);
                }
            }
        }
    }

    static class VH extends RecyclerView.ViewHolder {
        private ImageView avatar;
        private TextView sender;
        private TextView date;
        private TextView dateOfMessage;

        private int pendingStateColor;
        private int readStateColor;
        private int failedStateColor;
        public int position = 0;

        VH(View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            sender = itemView.findViewById(R.id.sender);
            date = itemView.findViewById(R.id.date);
            dateOfMessage = itemView.findViewById(R.id.dateOfMessage);

        }

        void bind(QiscusComment comment) {

            if (sender != null) {
                sender.setText(comment.getSender());
            }
            date.setText(DateUtils.getTimeStringFromDate(comment.getTime()));
            if (dateOfMessage != null) {
                dateOfMessage.setText(DateUtils.toFullDate(comment.getTime()));
            }


        }

        void setNeedToShowDate(Boolean showDate) {
            if (showDate == true) {
                if (dateOfMessage != null) {
                    dateOfMessage.setVisibility(View.VISIBLE);
                }
            } else {
                if (dateOfMessage != null) {
                    dateOfMessage.setVisibility(View.GONE);
                }
            }
        }


    }

    static class TextVH extends VH {
        private TextView message;
        private TextView sender;
        private TextView dateOfMessage;

        TextVH(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            sender = itemView.findViewById(R.id.sender);
            dateOfMessage = itemView.findViewById(R.id.dateOfMessage);
        }

        @Override
        void bind(QiscusComment comment) {
            super.bind(comment);
            message.setText(comment.getMessage());
            QiscusChatRoom chatRoom = QiscusCore.getDataStore().getChatRoom(comment.getRoomId());

            if (sender != null && chatRoom != null) {
                if (chatRoom.isGroup() == false) {
                    sender.setVisibility(View.GONE);
                } else {
                    sender.setVisibility(View.VISIBLE);
                }
            }

            if (dateOfMessage != null) {
                dateOfMessage.setText(DateUtils.toFullDate(comment.getTime()));
            }
        }

        @Override
        void setNeedToShowDate(Boolean showDate) {
            if (showDate == true) {
                if (dateOfMessage != null) {
                    dateOfMessage.setVisibility(View.VISIBLE);
                }
            } else {
                if (dateOfMessage != null) {
                    dateOfMessage.setVisibility(View.GONE);
                }
            }
        }
    }


}
