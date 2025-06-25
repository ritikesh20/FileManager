package com.example.filemanager.internalstorage;

import static com.example.filemanager.internalstorage.InternalStorageActivity.isGridView;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.filemanager.R;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ISAdapter extends AbstractItem<ISAdapter, ISAdapter.ViewHolder> {

    private File file;
    private final boolean isHeader;
    private final String headerTitle;


    public ISAdapter(File file, boolean isHeader, String headerTitle) {
        this.file = file;
        this.isHeader = isHeader;
        this.headerTitle = headerTitle;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public String getHeaderTitle() {
        return headerTitle;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }


    @NonNull
    @Override
    public ViewHolder getViewHolder(@NonNull View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return 102;
//        return R.id.file_item_id;
    }

    @Override
    public int getLayoutRes() {
        return isGridView ? R.layout.istore_grid_item : R.layout.internal_storage_items;
//        return R.layout.internal_storage_items;
    }

    public static class ViewHolder extends FastAdapter.ViewHolder<ISAdapter> {

        TextView fileName;
        ImageView fileImage;
        TextView fileDate;
        TextView tvHeaderText;
        TextView fileSize;
        IconicsImageView btnFileInfo;

        public ViewHolder(View itemView) {
            super(itemView);

            fileName = itemView.findViewById(R.id.file_name_text_view);
            fileImage = itemView.findViewById(R.id.file_icon_image);
            fileDate = itemView.findViewById(R.id.tvModifierData);
            fileSize = itemView.findViewById(R.id.tvSize);
            btnFileInfo = itemView.findViewById(R.id.btnFileDetails);
            tvHeaderText = itemView.findViewById(R.id.textHeader);

        }

        @Override
        public void bindView(ISAdapter item, @NonNull List<Object> payloads) {

            Glide.with(itemView.getContext()).clear(fileImage);

            if (item.isHeader()) {
                tvHeaderText.setVisibility(View.VISIBLE);
                tvHeaderText.setText(item.getHeaderTitle());
            }
            else {
                tvHeaderText.setVisibility(View.GONE);
            }

            fileName.setText(item.getFile().getName());

            long sizeInBytes = item.file.length();

            String convertedSize;
            long reSize;

            String name = item.getFile().getName().toLowerCase();

            if (item.getFile().isDirectory()) {
                fileImage.setImageResource(R.drawable.folderg);
                fileSize.setVisibility(View.GONE);
            }
            else if (name.endsWith(".mp3") || name.endsWith(".wav")) {
                fileImage.setImageResource(R.drawable.musicicons);
            }
            else if (name.endsWith(".mp4")) {
                Glide.with(itemView.getContext()).load(item.getFile()).error(R.drawable.videoicons).into(fileImage);
            } else if (name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".jpeg")) {
                Glide.with(itemView.getContext()).load(item.getFile()).error(R.drawable.img).into(fileImage);
            } else if (name.endsWith(".apk")) {
                Glide.with(itemView.getContext()).load(item.getFile()).error(R.drawable.apkicons).into(fileImage);
            } else if (name.endsWith(".pdf")) {
                Glide.with(itemView.getContext()).load(item.getFile()).error(R.drawable.pdficons).into(fileImage);
            } else {
                fileImage.setImageResource(R.drawable.newdocument);
            }

            if (itemView.isSelected()) {
                btnFileInfo.setIcon(new IconicsDrawable(itemView.getContext(), GoogleMaterial.Icon.gmd_check_circle).actionBar());

            } else {
                btnFileInfo.setIcon(new IconicsDrawable(itemView.getContext(), GoogleMaterial.Icon.gmd_more_vert).actionBar());

            }

            long lastModifiedData = item.file.lastModified();
            SimpleDateFormat date = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
            String setDate = date.format(new Date(lastModifiedData));

            fileDate.setText(setDate);

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

            fileSize.setText(convertedSize);

            if (isGridView){
                tvHeaderText.setVisibility(View.GONE);
            }

        }

        @Override
        public void unbindView(@NonNull ISAdapter item) {

            fileImage.setImageDrawable(null);
            fileName.setText(null);
            fileDate.setText(null);
            fileSize.setText(null);


        }

    }
}

