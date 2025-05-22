package com.example.filemanager.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.filemanager.R;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.io.File;
import java.util.List;


public class ISAdapter extends AbstractItem<ISAdapter, ISAdapter.ViewHolder> {

    File file;

    public ISAdapter(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.file_item_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.internal_storage_items;
    }

    public static class ViewHolder extends FastAdapter.ViewHolder<ISAdapter> {

        TextView fileName;
        ImageView fileImage;

        public ViewHolder(View itemView) {
            super(itemView);

            fileName = itemView.findViewById(R.id.file_name_text_view);
            fileImage = itemView.findViewById(R.id.file_icon_image);

        }

        @Override
        public void bindView(ISAdapter item, List<Object> payloads) {

            fileName.setText(item.getFile().getName());

            if (item.getFile().isDirectory()) {
                fileImage.setImageResource(R.drawable.openfolder);
            } else {
                String name = item.getFile().getName().toLowerCase();
                if (name.endsWith(".mp3") || name.endsWith(".wav")) {
                    fileImage.setImageResource(R.drawable.musicicons);
                } else if (name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".jpeg")) {
//                    fileImage.setImageResource(R.drawable.img);
                    Glide.with(itemView.getContext())
                            .load(item.getFile())
                            .error(R.drawable.img)
                            .into(fileImage);
                } else if (name.endsWith(".apk")) {
                    fileImage.setImageResource(R.drawable.apkicons);
                } else if (name.endsWith(".mp4")) {
                    Glide.with(itemView.getContext())
                            .load(item.getFile())
                            .error(R.drawable.img)
                            .into(fileImage);
//                    fileImage.setImageResource(R.drawable.videoicons);
                } else {
                    fileImage.setImageResource(R.drawable.newdocument);
                }
            }
        }

        @Override
        public void unbindView(ISAdapter item) {
            fileImage.setImageDrawable(null);
            fileName.setText(null);
        }
    }
}
