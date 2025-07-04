package com.example.filemanager.videolist;

import android.net.Uri;
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

import java.util.List;

public class HomeFileAdapter extends AbstractItem<HomeFileAdapter, HomeFileAdapter.ViewHolder> {

    Uri uri;
    String recentFileName;
    String recentFolderName;
    String recentMimeType;


    public HomeFileAdapter(Uri recentFileUri, String recentFileName, String recentFolderName, String recentMimeType) {
        this.recentFileName = recentFileName;
        this.recentFolderName = recentFolderName;
        this.recentMimeType = recentMimeType;
        this.uri = recentFileUri;
    }

    public String getRecentFileName() {
        return recentFileName;
    }

    public String getRecentFolderName() {
        return recentFolderName;
    }

    public Uri getUri() {
        return uri;
    }

    public String getRecentMimeType() {
        return recentMimeType;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(@NonNull View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return 543;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.home_recent;
    }

    public static class ViewHolder extends FastAdapter.ViewHolder<HomeFileAdapter> {

        ImageView recentFileImage;
        TextView recentFileName;
        TextView recentFolderName;
        IconicsImageView recentHomeInfoIcon;

        public ViewHolder(View itemView) {
            super(itemView);

            recentFileImage = itemView.findViewById(R.id.homeRecentImage);
            recentFileName = itemView.findViewById(R.id.homeRecentName);
            recentFolderName = itemView.findViewById(R.id.homeRecentFolderName);
            recentHomeInfoIcon = itemView.findViewById(R.id.recentHomeInfoIcon);
        }

        @Override
        public void bindView(HomeFileAdapter item, @NonNull List<Object> payloads) {

            Uri fileUri = item.uri;

            String mime = item.getRecentMimeType();

            if (mime != null) {

                if (mime.startsWith("image/")) {
                    Glide.with(itemView.getContext())
                            .load(fileUri)
                            .into(recentFileImage);
                } else if (mime.startsWith("video/")) {
                    Glide.with(itemView.getContext())
                            .load(fileUri)
                            .frame(1000000)
                            .error(R.drawable.videoicons)
                            .placeholder(R.drawable.videoicons)
                            .into(recentFileImage);
                } else if (mime.startsWith("audio/")) {
                    recentFileImage.setImageResource(R.drawable.musicicons);
                } else {
                    recentFileImage.setImageResource(R.drawable.pdf);
                }
            }

            recentFileName.setText(item.getRecentFileName());
            recentFolderName.setText(item.getRecentFolderName());
            recentHomeInfoIcon.setIcon(new IconicsDrawable(itemView.getContext(),
                    GoogleMaterial.Icon.gmd_more_vert).colorRes(R.color.white).actionBar());
        }

        @Override
        public void unbindView(@NonNull HomeFileAdapter item) {

            recentFileImage.setImageResource(R.drawable.delete);
            recentFileName.setText(null);
            recentFolderName.setText(null);

        }
    }

}
