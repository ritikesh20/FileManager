package com.example.filemanager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filemanager.adapter.ISAdapter;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;


public class InternalStorageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView noFileText;
    private ItemAdapter<ISAdapter> itemAdapter;
    private FastAdapter<ISAdapter> fastAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internal_storage);

        recyclerView = findViewById(R.id.rvInternalStorage);
        noFileText = findViewById(R.id.tvNoFile);

        itemAdapter = new ItemAdapter<>();
        fastAdapter = FastAdapter.with(itemAdapter);

        String path = getIntent().getStringExtra("path");

        assert path != null;
        File root = new File(path);
        File[] filesAndFolders = root.listFiles();

        if (filesAndFolders == null || filesAndFolders.length == 0) {
            noFileText.setVisibility(View.VISIBLE);
            return;
        }

        noFileText.setVisibility(View.INVISIBLE);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<ISAdapter> items = new ArrayList<>();

        for (File file : filesAndFolders) {
            items.add(new ISAdapter(file));
        }

        recyclerView.setAdapter(fastAdapter);
        itemAdapter.set(items);


//        fastAdapter.withOnClickListener(new OnClickListener<ISAdapter>() {
//            @Override
//            public boolean onClick(View v, IAdapter<ISAdapter> adapter, ISAdapter item, int position) {
//                return false;
//            }
//        });

        fastAdapter.withOnClickListener((v, adapter, item, position) -> {

            File clickedFile = item.getFile();

            if (clickedFile.isDirectory()) {

                Intent intent = new Intent(this, InternalStorageActivity.class);
                intent.putExtra("path", clickedFile.getAbsolutePath());
                startActivity(intent);

            } else if (clickedFile.isFile()) {

                String mine = URLConnection.guessContentTypeFromName(clickedFile.getName());

                if (mine != null && mine.startsWith("audio")) {
                    Toast.makeText(this, "Dhan Dhana Dhan", Toast.LENGTH_SHORT).show();
                    try {
                        Uri audioUri = FileProvider.getUriForFile(this, this.getPackageName() + ".provider", clickedFile);

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(audioUri, "audio/*");
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        this.startActivity(intent);

                    } catch (Exception e) {
                        Toast.makeText(this, "Cannot play audio", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else if (mine != null && mine.startsWith("image")) {
                    try {
                        Uri imageUri = FileProvider.getUriForFile(
                                this,
                                this.getPackageName() + ".provider",
                                clickedFile
                        );

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(imageUri, "image/*");
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        this.startActivity(intent);

                    } catch (Exception e) {
                        Toast.makeText(this, "Cannot open image" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }


                } else if (mine != null && mine.startsWith("application/pdf")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(clickedFile), "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    try {
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(this, "No Pdf Reader Installed", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, " Unsupported File type : " + mine, Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        });

    }

}

/*

//                        Intent intent = new Intent(this, FileImageViewActivity.class);
//                        intent.putExtra("image_path", clickedFile.getAbsolutePath());
//                        startActivity(intent);
 */


/*
String mime = URLConnection.guessContentTypeFromName(clickedFile.getName());

                if (mime != null && mime.startsWith("image")) {

                    Intent intent = new Intent(this, FileImageViewActivity.class);
                    intent.putExtra("image_path", clickedFile.getAbsolutePath());
                    startActivity(intent);

                } else {
                    Toast.makeText(InternalStorageActivity.this, "Na na Na", Toast.LENGTH_SHORT).show();
                }


                try {
                        Intent intent = new Intent(this, FileImageViewActivity.class);
                        intent.putExtra("image_path", clickedFile.getAbsolutePath());
                        startActivity(intent);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                     Uri uri;

                    uri = FileProvider.getUriForFile(
                            this,
                            BuildConfig.APPLICATION_ID + ".provider",
                            clickedFile
                    );

                    Intent openIntent = new Intent(Intent.ACTION_VIEW);
                    openIntent.setDataAndType(uri, mine);
                    openIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    Intent chooser = Intent.createChooser(openIntent, "Open with");

                    try {
                        startActivity(chooser);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(this, "No app found to open this file", Toast.LENGTH_SHORT).show();
                    }
 */


/*
String mintType = URLConnection.guessContentTypeFromName(clickedFile.getName());

                if (mintType != null && mintType.startsWith("audio")) {
                    try {

                        Uri audioUri = FileProvider.getUriForFile(this, this.getPackageName() + ".provider", clickedFile);

                        Intent audioIntent = new Intent(Intent.ACTION_VIEW);
                        audioIntent.setDataAndType(audioUri,"audio/*");
                        audioIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        this.startActivity(audioIntent);
                    } catch (Exception e) {
                        Toast.makeText(this, "No Music Player Installed", Toast.LENGTH_SHORT).show();
                        throw new RuntimeException(e);
                    }
                }
                else {
                    Toast.makeText(this, "invalid file", Toast.LENGTH_SHORT).show();
                }
 */