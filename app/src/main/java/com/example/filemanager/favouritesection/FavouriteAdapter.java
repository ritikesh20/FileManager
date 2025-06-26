package com.example.filemanager.favouritesection;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.filemanager.R;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import java.util.List;

public class FavouriteAdapter extends AbstractItem<FavouriteAdapter, FavouriteAdapter.ViewHolder> {

    FavouriteItem favouriteItem;

    public FavouriteAdapter(FavouriteItem favouriteItem) {
        this.favouriteItem = favouriteItem;
    }

//    @Override
//    public boolean isSelectable() {
//        return true;
//    }

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

    public static class ViewHolder extends FastAdapter.ViewHolder<FavouriteAdapter> {

        ImageView musicIcon;
        TextView musicName, musicDate, musicSize;
        IconicsImageView favListIcons;
        IconicsImageView btnFavMenu;

        public ViewHolder(View itemView) {
            super(itemView);

            musicIcon = itemView.findViewById(R.id.fileIcons);
            musicName = itemView.findViewById(R.id.tvFileName);
            musicDate = itemView.findViewById(R.id.tvFileModifierData);
            musicSize = itemView.findViewById(R.id.tvFileSize);
            favListIcons = itemView.findViewById(R.id.favIconFav);
            btnFavMenu = itemView.findViewById(R.id.btnFileDot);

        }


        @Override
        public void bindView(FavouriteAdapter item, @NonNull List<Object> payloads) {

            musicIcon.setImageResource(R.drawable.filebackup);
            musicName.setText(item.favouriteItem.getName());
            musicDate.setText(item.favouriteItem.getDateAdded());
            musicSize.setText(item.favouriteItem.getSize());

            if (item.isSelected()) {
                btnFavMenu.setIcon(new IconicsDrawable(itemView.getContext(), GoogleMaterial.Icon.gmd_check_circle).actionBar());
            } else {
                btnFavMenu.setIcon(new IconicsDrawable(itemView.getContext(), GoogleMaterial.Icon.gmd_more_vert).actionBar());
            }


            favListIcons.setIcon(new IconicsDrawable(itemView.getContext(), GoogleMaterial.Icon.gmd_star).actionBar());

        }

        @Override
        public void unbindView(@NonNull FavouriteAdapter item) {

            musicIcon.setImageResource(R.drawable.musicicons);
            musicName.setText(null);
            musicDate.setText(null);
            musicSize.setText(null);

        }

    }
}
