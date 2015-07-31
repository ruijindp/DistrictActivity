package com.ljmob.districtactivity.impl;

import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by london on 15/7/28.
 * 内ListView滚动监听器
 */
public class InsiderListScrollListener implements ListView.OnScrollListener {
    int currentFirstItem;
    boolean isDivDPage;
    InsiderListViewListener insiderListViewListener;
    Direction direction;

    public InsiderListScrollListener(InsiderListViewListener insiderListViewListener, Direction direction) {
        this.insiderListViewListener = insiderListViewListener;
        this.direction = direction;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (currentFirstItem == firstVisibleItem) {
            return;
        }
        currentFirstItem = firstVisibleItem;
        switch (direction) {
            case slideUpFromDown:
                isDivDPage = (firstVisibleItem + visibleItemCount == totalItemCount);
                break;
            case slideDownFromUp:
                isDivDPage = firstVisibleItem == 0;
                break;
        }
        if (isDivDPage) {
            insiderListViewListener.slideEnd(view, direction);
        }
    }

    public interface InsiderListViewListener {
        void slideEnd(View v, Direction direction);
    }

    public enum Direction {
        slideUpFromDown,
        slideDownFromUp
    }
}
