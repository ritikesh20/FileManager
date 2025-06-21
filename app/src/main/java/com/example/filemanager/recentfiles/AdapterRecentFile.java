package com.example.filemanager.recentfiles;

import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;

import com.example.filemanager.R;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

public class AdapterRecentFile extends AbstractItem<AdapterRecentFile, AdapterRecentFile.ViewHolder> {

    private Uri fileUri;
    private String fileName;
    private String folderName;
    private String mime;

    public AdapterRecentFile(Uri fileUri, String fileName, String folderName, String mime) {
        this.fileUri = fileUri;
        this.fileName = fileName;
        this.folderName = folderName;
        this.mime = mime;
    }

    public Uri getFileUri() {
        return fileUri;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFolderName() {
        return folderName;
    }

    public String getMime() {
        return mime;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return 104;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.home_recent;
    }

    public static class ViewHolder extends FastAdapter.ViewHolder<AdapterRecentFile> {

        private ImageView fileImagePreView;
        private TextView fileName, fileFolderName;

        public ViewHolder(View itemView) {
            super(itemView);

            fileImagePreView = itemView.findViewById(R.id.homeRecentImage);
            fileName = itemView.findViewById(R.id.homeRecentName);
            fileFolderName = itemView.findViewById(R.id.homeRecentFolderName);

        }

        @Override
        public void bindView(AdapterRecentFile item, @NonNull List<Object> payloads) {

            fileName.setText(item.fileName);
            fileFolderName.setText(item.folderName);
            fileImagePreView.setImageResource(R.drawable.newdocument);




        }

        @Override
        public void unbindView(@NonNull AdapterRecentFile item) {

            fileName.setText(null);
            fileFolderName.setText(null);
            fileImagePreView.setImageResource(R.drawable.folderg);

        }
    }
}
