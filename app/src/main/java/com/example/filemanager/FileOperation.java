package com.example.filemanager;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.widget.Toast;

import com.example.filemanager.document.FileHelperAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class FileOperation {

    /*
     selected - share -> open with -> rename -> Add to starred -> move to trash ->
     move to -> copy to -> back up to google drive -> file info
     */


    public static void fileOpenWith(Context context, Uri fileUri, String mimeType) {
        if (mimeType == null || !mimeType.startsWith("video/")) {
            mimeType = "video/*";
        }
        try {
            Intent fileOpenIntent = new Intent(Intent.ACTION_VIEW);
            fileOpenIntent.setDataAndType(fileUri, mimeType);
            fileOpenIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            fileOpenIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(Intent.createChooser(fileOpenIntent, "Open With"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No app found to open this file", Toast.LENGTH_SHORT).show();
        }


    }


    public static boolean showRenameFileDialog(Context context, Uri fileUri, String newFileName) {

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, newFileName);

        ContentResolver resolver = context.getContentResolver();
        int rowFileNameUpdated = resolver.update(fileUri, values, null, null);


        return rowFileNameUpdated > 0;

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