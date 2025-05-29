package com.example.filemanager.document;

import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.filemanager.R;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

public class DocumentAdapter extends AbstractItem<DocumentAdapter, DocumentAdapter.ViewHolder> {

    Uri uri;
    String name;
    String mineTypes;
    String docDate;
    String size;

    public DocumentAdapter(Uri uri, String name, String mineTypes, String docDate, String size) {
        this.uri = uri;
        this.name = name;
        this.mineTypes = mineTypes;
        this.docDate = docDate;
        this.size = size;
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

    public static class ViewHolder extends FastAdapter.ViewHolder<DocumentAdapter> {

        ImageView documentIcons;
        TextView documentName, documentDate, documentSize;

        public ViewHolder(View itemView) {
            super(itemView);

            documentIcons = itemView.findViewById(R.id.musicIcons);
            documentName = itemView.findViewById(R.id.tvMusicName);
            documentDate = itemView.findViewById(R.id.tvMusicModifierData);
            documentSize = itemView.findViewById(R.id.tvMusicSize);

        }

        @Override
        public void bindView(DocumentAdapter item, @NonNull List<Object> payloads) {

            if (item.mineTypes.equals("application/pdf")) {
                documentIcons.setImageResource(R.drawable.pdf);
            } else if (item.mineTypes.endsWith("text/plain")) {
                documentIcons.setImageResource(R.drawable.txtfile);
            } else {
                documentIcons.setImageResource(R.drawable.newdocument);
            }

            documentName.setText(item.name);

            documentDate.setText(item.docDate);

            documentSize.setText(item.size);

        }

        @Override
        public void unbindView(@NonNull DocumentAdapter item) {

            documentName.setText(null);
            documentDate.setText(null);
            documentSize.setText(null);
        }
    }
}
