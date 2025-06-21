package com.example.filemanager.internalstorage;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.filemanager.R;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

public class PathHistoryAdapter extends AbstractItem<PathHistoryAdapter, PathHistoryAdapter.ViewHolder> {

    String pathName;
    boolean showArrow;

//    public PathHistoryAdapter(String pathName) {
//        this.pathName = pathName;
//    }
//
//    public String getPathName() {
//        return pathName;
//    }


    public PathHistoryAdapter(String pathName, boolean showArrow) {
        this.pathName = pathName;
        this.showArrow = showArrow;
    }

    public String getPathName() {
        return pathName;
    }

    public boolean isShowArrow() {
        return showArrow;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.path_history_item;
    }

    public static class ViewHolder extends FastAdapter.ViewHolder<PathHistoryAdapter> {

        TextView fileHistoryName;
        ImageView imageViewNext;

        public ViewHolder(View itemView) {
            super(itemView);

            fileHistoryName = itemView.findViewById(R.id.pathHistoryName);
            imageViewNext = itemView.findViewById(R.id.ivNext);

        }

        @Override
        public void bindView(PathHistoryAdapter item, List<Object> payloads) {

            fileHistoryName.setText(item.getPathName());
            imageViewNext.setVisibility(item.showArrow ? View.VISIBLE : View.GONE);

        }

        @Override
        public void unbindView(PathHistoryAdapter item) {

            fileHistoryName.setText(null);
            imageViewNext.setVisibility(View.GONE);

        }
    }
}
