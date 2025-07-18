package com.example.filemanager;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.filemanager.internalstorage.InternalStorageActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
                        MediaStore.Files.FileColumns.DATA,


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
                        selectionArgs = new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO)};

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
                                String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO),

                        };

                        break;


                    default:

                        selection = null;
                        selectionArgs = null;

                }

                String sortingOrder = MediaStore.Files.FileColumns.DATE_ADDED + " DESC";

                ContentResolver contentResolver = context.getContentResolver();

                Cursor cursor = contentResolver.query(fileUri, projection, selection, selectionArgs, sortingOrder);

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

                        Date fileXdate = new Date(data * 1000L);

                        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyy", Locale.getDefault());

                        String formattedDate = sdf.format(fileXdate);

//                        String newSize = FileOperation.sizeCal(size);
                        String newSize = FileOperation.fileSizeCal(size);

                        Uri contentUri = ContentUris.withAppendedId(fileUri, id);

                        fileList.add(new FileHelperAdapter(contentUri, name, mime, formattedDate, newSize));


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


    // option to selected internal storage or sd Card
    public static void goStorageTypes(Context context, String title) {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);


        View view = LayoutInflater.from(context).inflate(R.layout.copymove_item, null);

        IconicsImageView goToInternalStorage = view.findViewById(R.id.goToInternalStorage);
        IconicsImageView goToIconSDCard = view.findViewById(R.id.goToIconSDCard);

        goToInternalStorage.setIcon(new IconicsDrawable(context, GoogleMaterial.Icon.gmd_devices));
        goToIconSDCard.setIcon(new IconicsDrawable(context, GoogleMaterial.Icon.gmd_sd_card));

        bottomSheetDialog.show();
        bottomSheetDialog.setContentView(view);

        TextView tvTitleCopyMove = view.findViewById(R.id.titleCopyMove);

        LinearLayout btnInterStorage = view.findViewById(R.id.layoutInterStorage);

        tvTitleCopyMove.setText(title);

        btnInterStorage.setOnClickListener(v -> {
            Intent intent = new Intent(context, InternalStorageActivity.class);
            context.startActivity(intent);
        });

    }


}
