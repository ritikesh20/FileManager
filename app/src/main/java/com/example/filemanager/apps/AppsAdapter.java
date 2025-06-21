package com.example.filemanager.apps;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.filemanager.R;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.iconics.view.IconicsImageView;

import java.util.List;

public class AppsAdapter extends AbstractItem<AppsAdapter, AppsAdapter.ViewHolder> {

    public Drawable icon;
    public String name;
    public String size;
    public String date;

    public AppsAdapter(Drawable icon, String name, String size, String date) {
        this.icon = icon;
        this.name = name;
        this.size = size;
        this.date = date;
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
        return R.layout.music_item;
    }

    public static class ViewHolder extends FastAdapter.ViewHolder<AppsAdapter> {

        private final IconicsImageView fileIcons;
        private final TextView fileName;
        private final TextView fileDate;
        private final TextView fileSize;


        public ViewHolder(View itemView) {
            super(itemView);

            fileIcons = itemView.findViewById(R.id.musicIcons);
            fileName = itemView.findViewById(R.id.tvMusicName);
            fileDate = itemView.findViewById(R.id.tvMusicModifierData);
            fileSize = itemView.findViewById(R.id.tvMusicSize);

        }

        @Override
        public void bindView(AppsAdapter item, List<Object> payloads) {

            fileIcons.setImageDrawable(item.icon);
            fileName.setText(item.name);
            fileDate.setText(item.date);
            fileSize.setText(item.size);


        }

        @Override
        public void unbindView(AppsAdapter item) {

        }
    }
}
