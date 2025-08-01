package com.example.filemanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.filemanager.music.NewSearchActivity;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.select.SelectExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    public static List<FileHelperAdapter> getSelectedFiles(ItemAdapter<FileHelperAdapter> itemAdapter, SelectExtension<FileHelperAdapter> selectExtension) {

        List<FileHelperAdapter> selectedFiles = new ArrayList<>();

        for (int index : selectExtension.getSelections()) {
            FileHelperAdapter item = itemAdapter.getAdapterItem(index);
            if (item != null) {
                selectedFiles.add(item);
            }
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

        ContentValues values1 = new ContentValues();
        values1.put(MediaStore.MediaColumns.DISPLAY_NAME, newFileName);


        return rowFileNameUpdated > 0;

    }

    public static void shareFilesToGoogleDrive(Activity activity, ItemAdapter<FileHelperAdapter> itemAdapter, SelectExtension<FileHelperAdapter> selectExtension) {

        List<FileHelperAdapter> selectedFiles = getSelectedFiles(itemAdapter, selectExtension);

        if (selectedFiles.isEmpty()) {
            Toast.makeText(activity, "No files selected", Toast.LENGTH_SHORT).show();
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
        intent.setPackage("com.google.android.apps.docs");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            activity.startActivity(Intent.createChooser(intent, "Upload to Google Drive"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, "Google Drive app is not installed", Toast.LENGTH_LONG).show();
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

    public static String getSelectedFileSize(SelectExtension<FileHelperAdapter> selectExtension, ItemAdapter<FileHelperAdapter> itemAdapter) {

        long fileSizeSum = 0;

        for (Integer selectedItem : selectExtension.getSelections()) {
            FileHelperAdapter fileSize = itemAdapter.getAdapterItem(selectedItem);
            fileSizeSum += convertFileSizeStringToLong(fileSize.getSize());
        }

        return fileSizeCal(fileSizeSum);
    }

    public static String fileSizeCal(long fileSizeInString) {

        float kb = fileSizeInString / 1024f;
        float mb = kb / 1024f;
        float gb = mb / 1024f;

        if (gb >= 1) {
            return String.format(Locale.ROOT, "%.2f GB", gb);
        } else if (mb > 1) {
            return String.format(Locale.ROOT, "%.2f MB", mb);
        } else {
            return String.format(Locale.ROOT, "%.2f KB", kb);
        }


    }

    static public long convertFileSizeStringToLong(String fileSize) {

        fileSize = fileSize.toUpperCase().trim();

        try {

            if (fileSize.endsWith("KB")) {
                return (long) (Double.parseDouble(fileSize.replace("KB", "").trim()) * 1024);
            } else if (fileSize.endsWith("MB")) {
                return (long) (Double.parseDouble(fileSize.replace("MB", "").trim()) * 1024 * 1024);
            } else if (fileSize.endsWith("GB")) {
                return (long) (Double.parseDouble(fileSize.replace("GB", "").trim()) * 1024 * 1024 * 1024);
            } else if (fileSize.endsWith("B")) {
                return Long.parseLong(fileSize.replace("B", "").trim());
            } else {
                return Long.parseLong(fileSize);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

    }

}
