package com.example.filemanager;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.widget.Toast;

import com.example.filemanager.document.FileHelperAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MediaStoreHelper {

    public interface FileLoadCallback {
        void onFilesLoaded(List<FileHelperAdapter> files);
    }

    public static void loadFile(Context context, String type, FileLoadCallback callback) {

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(new Runnable() {
            @Override
            public void run() {

                List<FileHelperAdapter> fileList = new ArrayList<>();

                Uri fileUri = MediaStore.Files.getContentUri("external");

                String[] projection = {

                        MediaStore.Files.FileColumns._ID,
                        MediaStore.Files.FileColumns.DISPLAY_NAME,
                        MediaStore.Files.FileColumns.SIZE,
                        MediaStore.Files.FileColumns.DATE_MODIFIED,
                        MediaStore.Files.FileColumns.MIME_TYPE,

                };

                String selection = null;
                String[] selectionArgs = null;

                switch (type.toLowerCase()) {

                    case "image":
                        selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "= ?";
                        selectionArgs = new String[]{
                                String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)
                        };
                        break;

                    case "video":

                        selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "=? ";

                        selectionArgs = new String[]{
                                String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)
                        };
                        break;

                    case "audio": {

                        selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "=? ";
                        selectionArgs = new String[]{
                                String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO)
                        };

                        break;
                    }

                    case "document": {

                        selection = MediaStore.Files.FileColumns.MIME_TYPE + "=? OR " +
                                MediaStore.Files.FileColumns.MIME_TYPE + "=? OR " +
                                MediaStore.Files.FileColumns.MIME_TYPE + "=?";
//                        selection = MediaStore.Files.FileColumns.MEDIA_TYPE + " IN (? ? ?)";

                        selectionArgs = new String[]{
                                "application/pdf",
                                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                                "text/plain"
                        };
                        break;
                    }

                    case "recentfile":

                        selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "=? OR " +
                                MediaStore.Files.FileColumns.MEDIA_TYPE + "=? OR " +
                                MediaStore.Files.FileColumns.MEDIA_TYPE + "=? ";

                        selectionArgs = new String[]{
                                String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
                                String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO),
                                String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO)
                        };

                        break;


                    default:

                        selection = null;
                        selectionArgs = null;

                }

                String sortingOrder = MediaStore.Files.FileColumns.DATE_ADDED + " DESC";

                ContentResolver contentResolver = context.getContentResolver();

                Cursor cursor = contentResolver.query(
                        fileUri, projection, selection, selectionArgs, sortingOrder
                );

                if (cursor != null) {

                    int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID);
                    int nameColum = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME);
                    int sizeInBytesColum = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE);
                    int dataModifiedColum = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED);
                    int mimeColum = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE);

                    while (cursor.moveToNext()) {

                        long id = cursor.getLong(idColumn);
                        String name = cursor.getString(nameColum);
                        long size = cursor.getLong(sizeInBytesColum);
                        long data = cursor.getLong(dataModifiedColum);
                        String mime = cursor.getString(mimeColum);

                        String newDate = DateFormat.format("dd MMM yyy", new Date(data)).toString();

                        String newSize = sizeCal(size);

                        Uri contentUri = ContentUris.withAppendedId(fileUri, id);

                        fileList.add(new FileHelperAdapter(contentUri, name, mime, newDate, newSize));

                    }
                    cursor.close();
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFilesLoaded(fileList);
                    }
                });
            }
        });

    }


    public static void fileOpenWith(Context context, Uri uri, String mimeTypes) {

        try {

            Intent intentOpenWith = new Intent(Intent.ACTION_VIEW);
            intentOpenWith.setDataAndType(uri, mimeTypes);
            intentOpenWith.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            intentOpenWith.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            context.startActivity(Intent.createChooser(intentOpenWith, "Open With"));
        } catch (Exception e) {
            Toast.makeText(context, "Cannot open this file", Toast.LENGTH_SHORT).show();
            throw new RuntimeException(e);
        }

    }

    private static String sizeCal(long size) {

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


















}
