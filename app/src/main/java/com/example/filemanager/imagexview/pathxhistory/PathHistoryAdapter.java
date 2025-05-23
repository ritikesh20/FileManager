package com.example.filemanager.imagexview.pathxhistory;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.filemanager.R;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.io.File;
import java.util.List;

public class PathHistoryAdapter extends AbstractItem<PathHistoryAdapter, PathHistoryAdapter.ViewHolder> {

    String pathName;

    public PathHistoryAdapter(String pathName) {
        this.pathName = pathName;
    }

    public String getPathName() {
        return pathName;
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

        public ViewHolder(View itemView) {
            super(itemView);

            fileHistoryName = itemView.findViewById(R.id.pathHistoryName);

        }

        @Override
        public void bindView(PathHistoryAdapter item, List<Object> payloads) {

            fileHistoryName.setText(item.getPathName());


        }

        @Override
        public void unbindView(PathHistoryAdapter item) {

            fileHistoryName.setText(null);

        }
    }
}
