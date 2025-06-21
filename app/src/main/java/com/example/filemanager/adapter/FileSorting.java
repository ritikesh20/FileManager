package com.example.filemanager.adapter;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.example.filemanager.R;
import com.example.filemanager.internalstorage.ClipboardHelper;
import com.example.filemanager.internalstorage.ISAdapter;
import com.example.filemanager.music.MusicAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.mikepenz.iconics.view.IconicsImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FileSorting {

    MusicAdapter obj = new MusicAdapter();

    void myIsPasting() {
//        if (ClipboardHelper.isEmpty()) {
//            Toast.makeText(this, "No files to paste", Toast.LENGTH_SHORT).show();
//            return true;
//        }
//
//        if (currentPath == null) {
//            Toast.makeText(this, "No destination path found", Toast.LENGTH_SHORT).show();
//            return true;
//        }
//
//        List<String> copiedFiles = ClipboardHelper.getFilePaths();
//
//        executorService.execute(() -> {
//
//            for (String filePath : copiedFiles) {
//                File source = new File(filePath);
//                File destination = getNonConflictingFile(new File(currentPath, source.getName()));
//
//                try {
//                    if (source.isDirectory()) {
//                        copyDirectory(source, destination);
//                    } else {
//                        Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
//                    }
//                } catch (IOException e) {
//                    handler.post(() -> Toast.makeText(this, "Paste failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
//                    e.printStackTrace();
//                }
//            }
//
//            handler.post(() -> {
//                Toast.makeText(this, "Paste successful!", Toast.LENGTH_SHORT).show();
//                fileLoading(); // Refresh UI
//                ClipboardHelper.clear(); // Optional: clear after paste
//            });
//        });
//
//
//        Toast.makeText(this, "Paste successful!", Toast.LENGTH_SHORT).show();
//
//        itemAdapter.setNewList(new ArrayList<>());
//        fileLoading(); // Refresh the current directory
//        return true;

    }

    void newPasting() {
//        if (copiedFiles.isEmpty()) {
//            Toast.makeText(this, "No files to paste", Toast.LENGTH_SHORT).show();
//            return true;
//        }
//
//        if (currentPath == null) {
//            Toast.makeText(this, "No destination path found", Toast.LENGTH_SHORT).show();
//            return true;
//        }
//
//        for (String filePath : copiedFiles) {
//            File source = new File(filePath);
//            File destination = getNonConflictingFile(new File(currentPath, source.getName())); // 🟡 <--- calling it here
//
//            try {
//                if (source.isDirectory()) {
//                    copyDirectory(source, destination);
//                } else {
//                    Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
//                }
//            } catch (IOException e) {
//                Toast.makeText(this, "Paste failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                e.printStackTrace();
//            }
//        }

    }

    void xCopyRename() {
//        private void copyDirectory(File source, File destination) throws IOException {
//            if (!destination.exists()) {
//                destination.mkdirs();
//            }
//
//            File[] files = source.listFiles();
//            if (files != null) {
//                for (File file : files) {
//                    File newDest = new File(destination, file.getName());
//                    if (file.isDirectory()) {
//                        copyDirectory(file, newDest);
//                    } else {
//                        Files.copy(file.toPath(), newDest.toPath(), StandardCopyOption.REPLACE_EXISTING);
//                    }
//                }
//            }
//        }
//
//        private File getNonConflictingFile(File file) {
//            if (!file.exists()) return file;
//
//            String name = file.getName();
//            String baseName;
//            String extension = "";
//
//            int dotIndex = name.lastIndexOf('.');
//            if (dotIndex != -1) {
//                baseName = name.substring(0, dotIndex);
//                extension = name.substring(dotIndex);
//            } else {
//                baseName = name;
//            }
//
//            int index = 1;
//            File newFile;
//            do {
//                String newName = baseName + "(" + index + ")" + extension;
//                newFile = new File(file.getParent(), newName);
//                index++;
//            } while (newFile.exists());
//
//            return newFile;
//        }
    }


//    void showBottomSheetIS(File file) {
//
//        IconicsImageView reChangeIcons = findViewById(R.id.btnFileDetails);
//
//
//        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
//
//        View view = LayoutInflater.from(this).inflate(R.layout.bottomsheet_info_item, null);
//
//        bottomSheetDialog.setContentView(view);
//
//
//        long sizeInBytes = file.length();
//        String convertedSize;
//
//        LinearLayoutCompat btnCopy;
//        LinearLayoutCompat btnMove;
//        LinearLayoutCompat btnSafeBox;
//        LinearLayoutCompat btnRename;
//        LinearLayoutCompat btnDelete;
//        LinearLayoutCompat btnOpenWith;
//        LinearLayoutCompat btnShare;
//
//        CircleImageView filePreviewIcon;
//
//        filePreviewIcon = view.findViewById(R.id.previewFileCIV);
//
//        btnCopy = view.findViewById(R.id.btnFileCopy);
//        btnMove = view.findViewById(R.id.btnFileMove);
//        btnSafeBox = view.findViewById(R.id.btnFileSaveBox);
//
//        btnRename = view.findViewById(R.id.btnFileRename);
//        btnOpenWith = view.findViewById(R.id.btnFileOpenWith);
//        btnShare = view.findViewById(R.id.btnFileShare);
//        btnDelete = view.findViewById(R.id.btnFileDelete);
//
//        TextView tvFileName = view.findViewById(R.id.tvRenameFileName);
//        TextView tvFileSize = view.findViewById(R.id.tvFileSize);
//
//        tvFileName.setText(file.getName());
//
//        long reSize;
//
//
//        if (sizeInBytes < 1024) {
//            convertedSize = sizeInBytes + " B";
//        } else if (sizeInBytes < 1024 * 1024) {
//            reSize = sizeInBytes / 1024;
//            convertedSize = reSize + " KB";
//        } else if (sizeInBytes < 1024 * 1024 * 1014) {
//            reSize = sizeInBytes / (1024 * 1024);
//            convertedSize = reSize + " MB";
//        } else {
//            reSize = sizeInBytes / (1024 * 1024 * 1024);
//            convertedSize = reSize + " GB";
//        }
//
//        tvFileSize.setText(convertedSize);
//
//        btnRename.setOnClickListener(v -> {
//            showRenameDialog(file);
//            Toast.makeText(this, "File Rename", Toast.LENGTH_SHORT).show();
//        });
//
//        // for shows icons on bottom sheet
//
//        if (file.isDirectory()) {
//            filePreviewIcon.setImageResource(R.drawable.openfolder);
//        } else {
//            String name = file.getName().toLowerCase();
//
//            if (name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".jpeg")) {
//                Glide.with(this).load(file).into(filePreviewIcon);
//            } else if (name.endsWith(".mp3")) {
//                filePreviewIcon.setImageResource(R.drawable.musicicons);
//            } else if (name.endsWith(".mp4") || name.endsWith(".avi")) {
//                Glide.with(this).load(file).into(filePreviewIcon);
//            } else if (name.endsWith("apk")) {
//                filePreviewIcon.setImageResource(R.drawable.apkicons);
//            } else if (name.endsWith(".txt")) {
//                filePreviewIcon.setImageResource(R.drawable.txtfile);
//            } else {
//                filePreviewIcon.setImageResource(R.drawable.newdocument);
//            }
//        }
//
//        btnCopy.setOnClickListener(v -> {
//            List<String> selectedPaths = new ArrayList<>();
//
//            for (ISAdapter files : selectExtension.getSelectedItems()) {
//                selectedPaths.add(files.getFile().getAbsolutePath());
//            }
//
//            if (!selectedPaths.isEmpty()) {
//                ClipboardHelper.copyFiles(selectedPaths);
//                Toast.makeText(this, "Files copied!", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, "No file selected to copy", Toast.LENGTH_SHORT).show();
//            }
//
//        });
//
//        btnOpenWith.setOnClickListener(v -> {
//
//            Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setDataAndType(uri, getMimeType(file));
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            startActivity(Intent.createChooser(intent, "Open with"));
//
//        });
//
//        btnShare.setOnClickListener(v -> {
//
//            Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
//            Intent shareIntent = new Intent(Intent.ACTION_VIEW);
//            shareIntent.setType(getMimeType(file));
//            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
//            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            startActivity(shareIntent);
//
//        });
//
//
//        btnDelete.setOnClickListener(v -> {
//
//            if (file.delete()) {
//                Toast.makeText(this, file.getName() + " Delete File", Toast.LENGTH_SHORT).show();
//                recreate();
//            }
//
//        });
//
//
//        bottomSheetDialog.show();
//
//    }


}


/*

//            if (filesAndFolders != null) {
//                Arrays.sort(filesAndFolders, (f1, f2) -> {
//                    if (f1.isDirectory() && !f2.isDirectory()) return -1;
//                    if (!f1.isDirectory() && f2.isDirectory()) return 1;
//                    return Long.compare(f2.lastModified(), f1.lastModified());
//                });
//            }

 */