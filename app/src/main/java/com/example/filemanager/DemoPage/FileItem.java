package com.example.filemanager.DemoPage;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.filemanager.R;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class FileItem extends AbstractItem<FileItem, FileItem.ViewHolder> {

    private final File file;

    public FileItem(File file) {
        this.file = file;
    }
    public File getFile() {
        return file;
    }

    @Override
    public int getType() {
        return R.id.file_item_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_file;
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    public static class ViewHolder extends FastAdapter.ViewHolder<FileItem> {

        ImageView xicon;
        TextView xname, xsize, xdate;

        public ViewHolder(View itemView) {
            super(itemView);
            xicon = itemView.findViewById(R.id.fileXimage);
            xname = itemView.findViewById(R.id.file_name);
            xsize = itemView.findViewById(R.id.file_size);
            xdate = itemView.findViewById(R.id.file_date);
        }

        @Override
        public void bindView(FileItem item, List<Object> payloads) {

            xname.setText(item.file.getName());
            xsize.setText(android.text.format.Formatter.formatShortFileSize(itemView.getContext(), item.file.length()));
            xdate.setText(new SimpleDateFormat("dd-MMM-yyyy").format(new Date(item.file.lastModified())));


            String name = item.getFile().getName().toLowerCase();

            if (name.endsWith(".mp3") || name.endsWith(".wav")) {
                xicon.setImageResource(R.drawable.musicicons);
            } else if (name.endsWith(".mp4")) {
                Glide.with(itemView.getContext()).load(item.getFile()).error(R.drawable.videoicons).into(xicon);
            } else if (name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".jpeg")) {
                Glide.with(itemView.getContext()).load(item.getFile()).error(R.drawable.img).into(xicon);
            } else if (name.endsWith(".apk")) {
                Glide.with(itemView.getContext()).load(item.getFile()).error(R.drawable.apkicons).into(xicon);
            } else if (name.endsWith(".pdf")) {
                Glide.with(itemView.getContext()).load(item.getFile()).error(R.drawable.pdficons).into(xicon);
            } else {
                xicon.setImageResource(R.drawable.newdocument);
            }

        }

        @Override
        public void unbindView(FileItem item) {
            xname.setText(null);
            xsize.setText(null);
            xdate.setText(null);
        }
    }
}
