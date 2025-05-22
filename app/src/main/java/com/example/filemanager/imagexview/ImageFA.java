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

    final Uri imageUri;
    String imageName;
    final String imageSize;
    String imageDate;

    public ImageFA(Uri imageUri, String imageName, String imageSize, String imageDate) {
        this.imageUri = imageUri;
        this.imageName = imageName;
        this.imageSize = imageSize;
        this.imageDate = imageDate;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageSize() {
        return imageSize;
    }

    public String getImageDate() {
        return imageDate;
    }

    public void setImageDate(String imageDate) {
        this.imageDate = imageDate;
    }

    @Override
    public int getType() {
        return R.id.PhotoCount;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.listimage_item;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    static class ViewHolder extends FastAdapter.ViewHolder<ImageFA> {
        private final ImageView imageView;
        private final TextView imageLvNameTv, imageSizeText, imageLvDateTv;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.lvImage);
            imageLvNameTv = itemView.findViewById(R.id.lvName);
            imageSizeText = itemView.findViewById(R.id.lvSize);
            imageLvDateTv = itemView.findViewById(R.id.lvDOP);

        }

        @Override
        public void bindView(ImageFA item, List<Object> payloads) {

            Glide.with(itemView.getContext())
                    .load(item.getImageUri())
                    .centerCrop()
                    .into(imageView);

            imageLvNameTv.setText(item.getImageName());
            imageSizeText.setText(item.getImageSize());
            imageLvDateTv.setText(item.getImageDate());
        }

        @Override
        public void unbindView(ImageFA item) {

            imageView.setImageDrawable(null);
            imageLvNameTv.setText(null);
            imageSizeText.setText(null);
            imageLvDateTv.setText(null);

        }
    }
}
