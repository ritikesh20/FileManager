package com.example.filemanager;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MusicAdapter extends AbstractItem<MusicAdapter, MusicAdapter.ViewHolder> {

    File file;

    public MusicAdapter(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(@NonNull View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.musicCount;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.music_item;
    }

    public static class ViewHolder extends FastAdapter.ViewHolder<MusicAdapter> {

        ImageView musicIcon;
        TextView musicName, musicDate, musicSize;

        public ViewHolder(View itemView) {
            super(itemView);

            musicIcon = itemView.findViewById(R.id.musicIcons);
            musicName = itemView.findViewById(R.id.tvMusicName);
            musicDate = itemView.findViewById(R.id.tvMusicModifierData);
            musicSize = itemView.findViewById(R.id.tvMusicSize);

        }

        @Override
        public void bindView(MusicAdapter item, @NonNull List<Object> payloads) {

            long sizeInBytes = item.file.length();
            String convertedSize;
            long reSize;

            long lastModifiedData = item.file.lastModified();
            SimpleDateFormat date = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
            String setDate = date.format(new Date(lastModifiedData));

            musicIcon.setImageResource(R.drawable.musicicons);
            musicName.setText(item.file.getName());
            musicDate.setText(setDate);

            if (sizeInBytes < 1024) {
                convertedSize = sizeInBytes + " B";
            } else if (sizeInBytes < 1024 * 1024) {
                reSize = sizeInBytes / 1024;
                convertedSize = reSize + " KB";
            } else if (sizeInBytes < 1024 * 1024 * 1014) {
                reSize = sizeInBytes / (1024 * 1024);
                convertedSize = reSize + " MB";
            } else {
                reSize = sizeInBytes / (1024 * 1024 * 1024);
                convertedSize = reSize + " GB";
            }

            musicSize.setText(convertedSize);

        }

        @Override
        public void unbindView(@NonNull MusicAdapter item) {

            musicIcon.setImageResource(R.drawable.musicicons);
            musicName.setText(null);
            musicDate.setText(null);
            musicSize.setText(null);

        }

    }
}
