package com.example.filemanager.imagexview;

import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.filemanager.R;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

public class ImageFA extends AbstractItem<ImageFA, ImageFA.ViewHolder> {

    private Uri imageUri;
    private String imageSize;

    public ImageFA(Uri imageUri, String imageSize) {
        this.imageUri = imageUri;
        this.imageSize = imageSize;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public String getImageSize() {
        return imageSize;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.gridimage_item;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    static class ViewHolder extends FastAdapter.ViewHolder<ImageFA> {
        ImageView imageView;
        TextView imageSizeText;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            imageSizeText = itemView.findViewById(R.id.tvImageSize);
        }

        @Override
        public void bindView(ImageFA item, List<Object> payloads) {
            Glide.with(itemView.getContext())
                    .load(item.getImageUri())
                    .centerCrop()
                    .into(imageView);

            imageSizeText.setText(item.getImageSize());
        }

        @Override
        public void unbindView(ImageFA item) {
            imageView.setImageDrawable(null);
            imageSizeText.setText(null);
        }
    }
}
