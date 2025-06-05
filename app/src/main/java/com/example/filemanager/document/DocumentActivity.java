package com.example.filemanager.document;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filemanager.R;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DocumentActivity extends AppCompatActivity {

    private Toolbar toolbarDocument;
    private RecyclerView recyclerViewDocument;

    ItemAdapter<DocumentAdapter> itemAdapterDocument;
    FastAdapter<DocumentAdapter> fastAdapterDocument;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);

        toolbarDocument = findViewById(R.id.toolbarDocument);
        recyclerViewDocument = findViewById(R.id.recyclerViewDocument);

        setSupportActionBar(toolbarDocument);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Document File");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerViewDocument.setLayoutManager(new LinearLayoutManager(this));

        itemAdapterDocument = new ItemAdapter<>();

        fastAdapterDocument = FastAdapter.with(itemAdapterDocument);

        recyclerViewDocument.setAdapter(fastAdapterDocument);

        loadDocument();

    }

    void loadDocument() {

        long sumSize = 0;

        List<DocumentAdapter> documentList = new ArrayList<>();

        Uri documentUri;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            documentUri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            documentUri = MediaStore.Files.getContentUri("external");
        }

        String[] projection = {

                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.SIZE

        };

        String selection = MediaStore.Files.FileColumns.MIME_TYPE + "=? OR " +
                MediaStore.Files.FileColumns.MIME_TYPE + "=? OR " +
                MediaStore.Files.FileColumns.MIME_TYPE + "=?";

        String[] selectionArgs = new String[]{
                "application/pdf",
                "application/vnd.openxmlformats-officedoument.wordprocessingml.document",
                "text/plain"
        };

        ContentResolver contentResolver = getContentResolver();

        Cursor cursor = contentResolver.query(
                documentUri,
                projection,
                selection,
                selectionArgs,
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC"
        );

        if (cursor != null) {

            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID);
            int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME);
            int mimeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE);
            int docDateColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE);

            while (cursor.moveToNext()) {

                long id = cursor.getLong(idColumn);
                String name = cursor.getString(nameColumn);
                String mime = cursor.getString(mimeColumn);
                long dateTaken = cursor.getLong(docDateColumn) * 1000L; // Convert seconds to milliseconds
                long sizeInBytes = cursor.getLong(sizeColumn);

                sumSize = sumSize + sizeInBytes;

                Uri document = ContentUris.withAppendedId(documentUri, id);

                String docDateConvertor = DateFormat.format("dd MMM yyyy", new Date(dateTaken)).toString();

                String sizeConvertor = docSizeCalculator(sizeInBytes);

                documentList.add(new DocumentAdapter(document, name, mime, docDateConvertor, sizeConvertor));

            }
            String ans = docSizeCalculator(sumSize);
            toolbarDocument.setSubtitle(ans + "My Size");
            cursor.close();



        }

        itemAdapterDocument.setNewList(documentList);

    }

    private String docSizeCalculator(long bytes) {
//        double kb = bytes / 1024.0;
//        double mb = kb / 1024.0;
//        double gb = mb / 1014.0;
//
//        if (mb >= 1) {
//            return String.format(Locale.getDefault(), "%.2f MB", mb);
//        }
//        else if (gb > 1) {
//            return String.format(Locale.getDefault(), "%.2f GB", gb);
//        }
//        else {
//            return String.format(Locale.getDefault(), "%.2f", kb);
//        }
        float kb = bytes / 1024f;
        float mb = kb / 1024f;
        float gb = mb / 1024f;

        if (gb >= 1) {
            return String.format("%.2f GB", gb);
        } else if (mb >= 1) {
            return String.format("%.2f MB", mb);
        } else {
            return String.format("%.2f KB", kb);
        }

    }
}