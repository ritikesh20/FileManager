package com.example.filemanager;

import static com.example.filemanager.music.MusicActivity.isMusicView;
import static com.example.filemanager.videolist.VideoActivity.isVideoView;

import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
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

    public FileHelperAdapter() {
    }

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

        if (isMusicView || isVideoView) {
            return R.layout.istore_grid_item;
        } else {
            return R.layout.internal_storage_items;
        }

//        return isVideoView ? R.layout.istore_grid_item : R.layout.internal_storage_items;

    }

    public static class ViewHolder extends FastAdapter.ViewHolder<FileHelperAdapter> {

        TextView fileName;
        IconicsImageView fileImage;
        TextView fileDate;
        TextView tvHeaderText;
        TextView fileSize;
        public IconicsImageView btnFileInfo;
        IconicsImageView fileTypeIcon;

        public ViewHolder(View itemView) {

            super(itemView);

            fileName = itemView.findViewById(R.id.file_name_text_view);
            fileImage = itemView.findViewById(R.id.file_icon_image);
            fileDate = itemView.findViewById(R.id.tvModifierData);
            fileSize = itemView.findViewById(R.id.tvSize);
            btnFileInfo = itemView.findViewById(R.id.btnFileDetails);
            tvHeaderText = itemView.findViewById(R.id.textHeader);
            fileTypeIcon = itemView.findViewById(R.id.btnFileType);

        }

        @Override
        public void bindView(FileHelperAdapter item, @NonNull List<Object> payloads) {

            String mime = item.mineTypes;

            if (mime != null) {
                if (mime.startsWith("image/")) {
                    Glide.with(itemView.getContext()).load(item.uri).placeholder(R.drawable.img).error(R.drawable.img).into(fileImage);
                } else if (mime.startsWith("video/")) {
                    fileImage.setIcon(new IconicsDrawable(itemView.getContext(), GoogleMaterial.Icon.gmd_play_circle_filled).colorRes(R.color.white).actionBar());
                    Glide.with(itemView.getContext()).load(item.uri).error(R.drawable.img).into(fileImage);
                } else if (mime.contains("pdf")) {
                    Glide.with(itemView.getContext()).load(item.uri).error(R.drawable.pdf).into(fileImage);
                } else if (mime.contains("msword") || mime.contains("wordprocessingml")) {
//                    fileImage.setIcon(new IconicsDrawable(itemView.getContext(), GoogleMaterial.Icon.gmd_doc));

                } else if (mime.contains("audio/")) {
                    fileImage.setIcon(new IconicsDrawable(itemView.getContext(), GoogleMaterial.Icon.gmd_music_note).colorRes(R.color.purple).sizeDp(20));
                } else {
//                    fileImage.setIcon(new IconicsDrawable(itemView.getContext(), GoogleMaterial.Icon.));
                }

            }

            fileName.setText(item.getName());

            fileDate.setText(item.getDocDate());

            fileSize.setText(item.getSize());

            if (itemView.isSelected()) {
                btnFileInfo.setIcon(new IconicsDrawable(itemView.getContext(), GoogleMaterial.Icon.gmd_check_circle).actionBar());
            } else {
                btnFileInfo.setIcon(new IconicsDrawable(itemView.getContext(), GoogleMaterial.Icon.gmd_more_vert).actionBar());
            }

            if (isVideoView) {
                fileName.setVisibility(View.GONE);
                fileTypeIcon.setIcon(new IconicsDrawable(itemView.getContext(), GoogleMaterial.Icon.gmd_play_circle_outline).actionBar());
                if (itemView.isSelected()) {
                    btnFileInfo.setVisibility(View.VISIBLE);
                    btnFileInfo.setIcon(new IconicsDrawable(itemView.getContext(), GoogleMaterial.Icon.gmd_check_circle).actionBar());

                } else {
                    btnFileInfo.setVisibility(View.GONE);
                }
            }


        }

        @Override
        public void unbindView(@NonNull FileHelperAdapter item) {

            fileImage.setIcon(new IconicsDrawable(itemView.getContext(), GoogleMaterial.Icon.gmd_drafts));
            fileName.setText(null);
            fileDate.setText(null);
            fileSize.setText(null);

        }
    }
}