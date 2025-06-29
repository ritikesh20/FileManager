package com.example.filemanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.filemanager.document.FileHelperAdapter;
import com.example.filemanager.favouritesection.AppDatabase;
import com.example.filemanager.favouritesection.FavouriteDao;
import com.example.filemanager.favouritesection.FavouriteItem;
import com.example.filemanager.music.NewSearchActivity;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.select.SelectExtension;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileOperation {

    /*
     selected - share -> open with -> rename -> Add to starred -> move to trash ->
     move to -> copy to -> back up to google drive -> file info
     */

    public static void searchingIntent(Activity activity) {
        Intent searching = new Intent(activity, NewSearchActivity.class);
        activity.startActivity(searching);
    }


    //    selected
    public static List<FileHelperAdapter> getSelectedFiles(

            ItemAdapter<FileHelperAdapter> itemAdapter,
            SelectExtension<FileHelperAdapter> selectExtension) {

        List<FileHelperAdapter> selectedFiles = new ArrayList<>();

        for (int index : selectExtension.getSelections()) {
            FileHelperAdapter item = itemAdapter.getAdapterItem(index);
            if (item != null) selectedFiles.add(item);
        }

        return selectedFiles;
    }

    public static void shareSelectedFiles(Activity activity, ItemAdapter<FileHelperAdapter> itemAdapter, SelectExtension<FileHelperAdapter> selectExtension) {

        List<FileHelperAdapter> selectedFiles = getSelectedFiles(itemAdapter, selectExtension);

        if (selectedFiles.isEmpty()) {
            Toast.makeText(activity, "No files selected", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<Uri> uris = new ArrayList<>();
        for (FileHelperAdapter item : selectedFiles) {
            uris.add(item.getUri());
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.setType("*/*");
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        activity.startActivity(Intent.createChooser(shareIntent, "Share files via"));
    }

    public static void fileOpenWith(Context context, Uri uri, String mimeType) {

        try {
            Intent intentOpenWith = new Intent(Intent.ACTION_VIEW);
            intentOpenWith.setDataAndType(uri, mimeType);
            intentOpenWith.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            intentOpenWith.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            context.startActivity(Intent.createChooser(intentOpenWith, "Open With"));
        } catch (Exception e) {
            Toast.makeText(context, "Cannot open this file", Toast.LENGTH_SHORT).show();
            throw new RuntimeException(e);
        }

    }

    public static boolean showRenameFileDialog(Context context, Uri fileUri, String newFileName) {

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, newFileName);

        ContentResolver resolver = context.getContentResolver();
        int rowFileNameUpdated = resolver.update(fileUri, values, null, null);


        return rowFileNameUpdated > 0;

    }

    public static void sendToFavourite(Activity activity, SelectExtension<FileHelperAdapter> selectExtension) {

        Set<FileHelperAdapter> selectedFavItem = selectExtension.getSelectedItems();

        if (selectedFavItem.isEmpty()) {
            Toast.makeText(activity, "No file selected", Toast.LENGTH_SHORT).show();
            return;
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            FavouriteDao dao = AppDatabase.getInstance(activity).favouriteVideoDao();

            for (FileHelperAdapter fileHelperAdapter : selectedFavItem) {
                FavouriteItem favouriteItem = new FavouriteItem(
                        fileHelperAdapter.getUri().toString(),
                        fileHelperAdapter.getName(),
                        false,
                        fileHelperAdapter.getDocDate(),
                        fileHelperAdapter.getSize(),
                        fileHelperAdapter.getMineTypes(),
                        0
                );
                dao.insert(favouriteItem);
            }

            activity.runOnUiThread(() ->
                    Toast.makeText(activity, "File Add to Favourite Successfully", Toast.LENGTH_SHORT).show()
            );
        });
    }

    public static void shareFilesToGoogleDrive(Context context, List<FileHelperAdapter> selectedFiles) {
        if (selectedFiles == null || selectedFiles.isEmpty()) {
            Toast.makeText(context, "No files selected", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<Uri> uris = new ArrayList<>();
        for (FileHelperAdapter file : selectedFiles) {
            uris.add(file.getUri());
        }

        Intent intent;
        if (uris.size() == 1) {
            intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_STREAM, uris.get(0));
        } else {
            intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        }

        intent.setType("*/*");
        intent.setPackage("com.google.android.apps.docs"); // Open only in Google Drive
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            context.startActivity(Intent.createChooser(intent, "Upload to Google Drive"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "Google Drive app is not installed", Toast.LENGTH_LONG).show();
        }
    }

    public static void deleteSelectedFiles(Activity activity, ItemAdapter<FileHelperAdapter> itemAdapter, SelectExtension<FileHelperAdapter> selectExtension) {

        List<FileHelperAdapter> selectedFiles = getSelectedFiles(itemAdapter, selectExtension);

        if (selectedFiles.isEmpty()) {
            Toast.makeText(activity, "No file selected", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.delete_item, null);

        Button btnDelete = view.findViewById(R.id.btnDeleteDelete);
        Button btnCancel = view.findViewById(R.id.btnDeleteCancel);
        deleteBuilder.setView(view);

        AlertDialog alertDialog = deleteBuilder.create();

        btnDelete.setOnClickListener(v -> {

            int deletedCount = 0;

            for (FileHelperAdapter deleteItem : selectedFiles) {

                Uri uri = deleteItem.getUri();

                try {
                    int rows = activity.getContentResolver().delete(uri, null, null);
                    if (rows > 0) {
                        deletedCount++;
                    }
                } catch (SecurityException e) {
                    Toast.makeText(activity, "Permission denied for: " + deleteItem.getName(), Toast.LENGTH_SHORT).show();
                }
            }

            alertDialog.dismiss();
            Toast.makeText(activity, deletedCount + " files deleted", Toast.LENGTH_SHORT).show();
            activity.recreate();
        });

        btnCancel.setOnClickListener(v -> alertDialog.dismiss());

        alertDialog.show();
    }


    public static void showRenameDialog(Activity activity, ItemAdapter<FileHelperAdapter> itemAdapter, SelectExtension<FileHelperAdapter> selectExtension) {

        int index = selectExtension.getSelections().iterator().next();
        FileHelperAdapter item = itemAdapter.getAdapterItem(index);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Rename File");

        final EditText input = new EditText(activity);
        input.setHint("Enter new file name");
        input.setText(item.getName());
        builder.setView(input);

        builder.setPositiveButton("Rename", (dialog, which) -> {
            String newName = input.getText().toString().trim();

            if (!newName.isEmpty()) {
                Uri fileUri = item.getUri();
                boolean success = FileOperation.showRenameFileDialog(activity, fileUri, newName);
                activity.recreate();
                Toast.makeText(activity, success ? "File renamed successfully" : "Failed to rename file", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, "File name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    public static String sizeCal(long size) {

        float kb = size / 1024f;
        float mb = kb / 1024f;
        float gb = mb / 1024f;

        if (gb >= 1) {
            return String.format("%.2f Gb", gb);
        } else if (mb >= 1) {
            return String.format("%.2f Mb", mb);
        } else {
            return String.format("%.2f KB", kb);
        }
    }


    public static boolean copyFileFromUri(Context context, Uri sourceUri, File destFile) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(sourceUri);
            if (inputStream == null) return false;

            OutputStream outputStream = new FileOutputStream(destFile);
            byte[] buffer = new byte[1024];
            int length;

            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.flush();
            inputStream.close();
            outputStream.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getFileNameFromUri(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            String name = cursor.getString(nameIndex);
            cursor.close();
            return name;
        }
        return "unknown_file";
    }

}

/*
    public static boolean copyFile(Context context, Uri sourceUri, Uri targetFolderUri, String newFileName) {
        ContentResolver resolver = context.getContentResolver();

        // Create new file in the destination folder
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, newFileName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, resolver.getType(sourceUri));

        Uri newFileUri = resolver.insert(targetFolderUri, values);
        if (newFileUri == null) return false;

        // Open streams and copy
        try (InputStream in = resolver.openInputStream(sourceUri);
             OutputStream out = resolver.openOutputStream(newFileUri)) {

            if (in == null || out == null) return false;

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

            out.flush();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;

    }

    public static boolean moveFile(Context context, Uri sourceUri, Uri targetFolderUri, String newFileName) {
        boolean copied = copyFile(context, sourceUri, targetFolderUri, newFileName);
        if (!copied) return false;

        try {
            ContentResolver resolver = context.getContentResolver();
            return resolver.delete(sourceUri, null, null) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

 */