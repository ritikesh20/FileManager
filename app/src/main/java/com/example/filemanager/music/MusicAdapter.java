package com.example.filemanager.music;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.filemanager.R;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MusicAdapter extends AbstractItem<MusicAdapter, MusicAdapter.ViewHolder> {

    File file;

    String filePath;

    public MusicAdapter() {
    }

    public MusicAdapter(File file, String filePath) {
        this.file = file;
        this.filePath = filePath;
    }


    public MusicAdapter(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public String getFilePath() {
        return filePath;
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

        //                ImageView musicIcon;
        ImageView musicIcon;
        ImageButton btnMusicInfo;
        TextView musicName, musicDate, musicSize;

        public ViewHolder(View itemView) {
            super(itemView);

            musicIcon = itemView.findViewById(R.id.fileIcons);
            musicName = itemView.findViewById(R.id.tvFileName);
            musicDate = itemView.findViewById(R.id.tvFileModifierData);
            musicSize = itemView.findViewById(R.id.tvFileSize);
//            btnMusicInfo = itemView.findViewById(R.id.btnFileDot);

        }

        @Override
        public void bindView(MusicAdapter item, @NonNull List<Object> payloads) {

            Context context = itemView.getContext();
            long sizeInBytes = item.file.length();
            String convertedSize;
            long reSize;

            long lastModifiedData = item.file.lastModified();
            SimpleDateFormat date = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
            String setDate = date.format(new Date(lastModifiedData));

//            String name = item.getFile().getName().toLowerCase();

            if (item.isSelected()) {
                musicIcon.setImageResource(R.drawable.check);
            } else {
                musicIcon.setImageResource(R.drawable.musicicons);
            }


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

//            btnMusicInfo.setOnClickListener(v -> {

//                if (itemView.getContext() instanceof MusicActivity) {
//                    ((MusicActivity) itemView.getContext()).showBottomSheet(item.getFile());
//                }

//            });

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


/*

         Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(
                 item.getFile().getPath(),
                 MediaStore.Images.Thumbnails.MINI_KIND
        );
        if (thumbnail != null) {
            musicIcon.setImageBitmap(thumbnail);
        }
         else {
           musicIcon.setImageResource(R.drawable.videoicons);
        }

 */