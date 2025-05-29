package com.example.filemanager.document;

import android.content.ContentResolver;
import android.content.ContentUris;
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
import java.util.Locale;

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
                MediaStore.Files.FileColumns.DATE_TAKEN,
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
                documentUri, projection, selection, selectionArgs, MediaStore.Files.FileColumns.DATE_ADDED + " DESC"
        );

        if (cursor != null) {

            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME));
                String mime = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE));
                String docDate = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_TAKEN));
                long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE));

                Uri document = ContentUris.withAppendedId(documentUri, id);

                String docDateConvertor = DateFormat.format("dd-MMM-yyy", new Date(docDate)).toString();

                String sizeConvertor = docSizeCalculator(size);

                documentList.add(new DocumentAdapter(document, name, mime, docDateConvertor , sizeConvertor));

            }

            cursor.close();
            
        }

        itemAdapterDocument.setNewList(documentList);

    }

    private String docSizeCalculator(long bytes) {
        double kb = bytes / 1024.0;
        double mb = kb / 1024.0;
        if (mb >= 1) {
            return String.format(Locale.getDefault(), "%.2f MB", mb);
        } else {
            return String.format(Locale.getDefault(), "%.2f", kb);
        }
    }


}