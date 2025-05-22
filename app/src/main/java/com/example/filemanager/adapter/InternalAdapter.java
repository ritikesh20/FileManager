package com.example.filemanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filemanager.R;

import java.io.File;
//
//public class InternalAdapter extends RecyclerView.Adapter<InternalAdapter.ViewHolder> {
//
//    Context context;
//    File[] fileAndFolder;
//
//    public InternalAdapter(Context context, File[] fileAndFolder) {
//        this.context = context;
//        this.fileAndFolder = fileAndFolder;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.internal_storage_items, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//
//        File selectedFile = fileAndFolder[position];
//        holder.tvFileName.setText(selectedFile.getName());
//
//        if (selectedFile.isDirectory()){
//            holder.iconsFile.setImageResource(R.drawable.openfolder);
//        }
//        else {
//            holder.iconsFile.setImageResource(R.drawable.newdocument);
//        }
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return fileAndFolder.length;
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//
//        TextView tvFileName;
//        ImageView iconsFile;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            tvFileName = itemView.findViewById(R.id.file_name_text_view);
//            iconsFile = itemView.findViewById(R.id.file_icon_image);
//
//        }
//    }
//}
