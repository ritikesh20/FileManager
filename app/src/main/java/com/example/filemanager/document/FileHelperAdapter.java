package com.example.filemanager.document;

import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.filemanager.R;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import java.util.List;

public class FileHelperAdapter extends AbstractItem<FileHelperAdapter, FileHelperAdapter.ViewHolder> {

    Uri uri;
    String name;
    String mineTypes;
    String docDate;
    String size;

    public FileHelperAdapter(Uri uri, String name, String mineTypes, String docDate, String size) {
        this.uri = uri;
        this.name = name;
        this.mineTypes = mineTypes;
        this.docDate = docDate;
        this.size = size;
    }

    public Uri getUri() {
        return uri;
    }

    public String getName() {
        return name;
    }

    public String getMineTypes() {
        return mineTypes;
    }

    public String getDocDate() {
        return docDate;
    }

    public String getSize() {
        return size;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(@NonNull View v) {
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

    public static class ViewHolder extends FastAdapter.ViewHolder<FileHelperAdapter> {

//        private final ImageView fileIcons;

        private final IconicsImageView fileIcons;
        private final IconicsImageView fileIconMusicVideo;
        private final TextView fileName;
        private final TextView fileDate;
        private final TextView fileSize;

        public ViewHolder(View itemView) {

            super(itemView);

            fileIcons = itemView.findViewById(R.id.musicIcons);
            fileName = itemView.findViewById(R.id.tvMusicName);
            fileDate = itemView.findViewById(R.id.tvMusicModifierData);
            fileSize = itemView.findViewById(R.id.tvMusicSize);
            fileIconMusicVideo = itemView.findViewById(R.id.iconMusicVideo);

        }

        @Override
        public void bindView(FileHelperAdapter item, @NonNull List<Object> payloads) {

            String mime = item.mineTypes;

            if (mime != null) {
                if (mime.startsWith("image/")) {
                    Glide.with(itemView.getContext()).load(item.uri).placeholder(R.drawable.img).error(R.drawable.img).into(fileIcons);

                } else if (mime.startsWith("video/")) {
                    fileIconMusicVideo.setVisibility(View.VISIBLE);
                    fileIconMusicVideo.setIcon(new IconicsDrawable(itemView.getContext(), GoogleMaterial.Icon.gmd_play_circle_filled).colorRes(R.color.white));
                    Glide.with(itemView.getContext()).load(item.uri).error(R.drawable.img).into(fileIcons);
                } else if (mime.contains("pdf")) {
                    Glide.with(itemView.getContext()).load(item.uri).error(R.drawable.pdf).into(fileIcons);
                } else if (mime.contains("msword") || mime.contains("wordprocessingml")) {
                    fileIcons.setIcon(new IconicsDrawable(itemView.getContext(), GoogleMaterial.Icon.gmd_work));
                } else if (mime.contains("audio/")) {
                    fileIcons.setIcon(new IconicsDrawable(itemView.getContext(), GoogleMaterial.Icon.gmd_music_note).colorRes(R.color.purple).sizeDp(20));
                } else {
                    fileIcons.setIcon(new IconicsDrawable(itemView.getContext(), GoogleMaterial.Icon.gmd_description));
                }

            }

            fileName.setText(item.getName());

            fileDate.setText(item.getDocDate());

            fileSize.setText(item.getSize());

        }

        @Override
        public void unbindView(@NonNull FileHelperAdapter item) {

            fileIcons.setImageResource(R.drawable.openfolder);
            fileName.setText(null);
            fileDate.setText(null);
            fileSize.setText(null);

        }
    }
}