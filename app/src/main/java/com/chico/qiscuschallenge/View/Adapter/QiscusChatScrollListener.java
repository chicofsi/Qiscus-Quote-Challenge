package com.chico.qiscuschallenge.View.Adapter;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class QiscusChatScrollListener extends RecyclerView.OnScrollListener  {
    private LinearLayoutManager linearLayoutManager;
    private Listener listener;
    private boolean onTop;
    private boolean onBottom = true;
    private boolean onMiddle;

    public QiscusChatScrollListener(LinearLayoutManager linearLayoutManager, Listener listener) {
        this.linearLayoutManager = linearLayoutManager;
        this.listener = listener;
    }


    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (linearLayoutManager.findFirstVisibleItemPosition() <= 0 && !onTop) {
            listener.onBottomOffListMessage();
            onBottom = true;
            onTop = false;
            onMiddle = false;
        } else if (linearLayoutManager.findLastVisibleItemPosition() >= linearLayoutManager.getItemCount() - 1 && !onBottom) {
            listener.onTopOffListMessage();
            onTop = true;
            onBottom = false;
            onMiddle = false;
        } else if (!onMiddle) {
            listener.onMiddleOffListMessage();
            onMiddle = true;
            onTop = false;
            onBottom = false;
        }
    }

    public interface Listener {
        void onTopOffListMessage();

        void onMiddleOffListMessage();

        void onBottomOffListMessage();
    }
}
