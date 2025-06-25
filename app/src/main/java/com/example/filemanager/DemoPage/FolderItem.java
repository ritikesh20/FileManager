package com.example.filemanager.DemoPage;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.filemanager.R;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FolderItem extends AbstractItem<FolderItem, FolderItem.ViewHolder> {

    private final File folder;

    public FolderItem(File folder) {
        this.folder = folder;
    }

    public File getFolder() {
        return folder;
    }

    @Override
    public int getType() {
        return R.id.folder_item_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_folder;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(@NonNull View v) {
        return new ViewHolder(v);
    }

    public static class ViewHolder extends FastAdapter.ViewHolder<FolderItem> {
        private final TextView name;
        private final TextView date;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.folder_name);
            date = itemView.findViewById(R.id.folder_date);
        }

        @Override
        public void bindView(FolderItem item, @NonNull List<Object> payloads) {
            name.setText(item.folder.getName());
            date.setText(new SimpleDateFormat("dd-MMM-yyyy").format(new Date(item.folder.lastModified())));
        }

        @Override
        public void unbindView(@NonNull FolderItem item) {
            name.setText(null);
            date.setText(null);
        }


    }
}
