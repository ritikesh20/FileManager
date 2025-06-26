package com.example.filemanager.music;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filemanager.R;
import com.example.filemanager.internalstorage.ISAdapter;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NewSearchActivity extends AppCompatActivity {

    private Toolbar toolbarSearchFile;
    private EditText edSearchFile;
    private RecyclerView recyclerViewSearch;

    private ItemAdapter<ISAdapter> itemAdapterSearching;
    private FastAdapter<ISAdapter> fastAdapterSearching;
    private final List<File> allResults = new ArrayList<>();

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

//    private ProgressBar progressBarSearching;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_search);


        toolbarSearchFile = findViewById(R.id.toolbarSearching);
        edSearchFile = findViewById(R.id.etSearching);
        recyclerViewSearch = findViewById(R.id.recyclerViewSearch);

        setSupportActionBar(toolbarSearchFile);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        itemAdapterSearching = new ItemAdapter<>();
        fastAdapterSearching = FastAdapter.with(itemAdapterSearching);
        recyclerViewSearch.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSearch.setAdapter(fastAdapterSearching);

        edSearchFile.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        edSearchFile.setInputType(EditorInfo.TYPE_CLASS_TEXT);


        edSearchFile.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                executorService.execute(() -> {
                    allResults.clear();
                    String query = s.toString().trim();
                    if (!query.isEmpty()) {
                        searchFilesRecursively(Environment.getExternalStorageDirectory(), query);
                        handler.post(NewSearchActivity.this::updateAdapter);
                    } else {
                        handler.post(() -> itemAdapterSearching.clear());
                    }
                });

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        onClick();

    }

    private void searchFilesRecursively(File dir, String query) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().startsWith(".")) {
                    continue;
                }
                if (file.getName().toLowerCase().contains(query.toLowerCase())) {
                    allResults.add(file);
                }
                if (file.isDirectory()) {
                    searchFilesRecursively(file, query);
                }
            }
        }
    }

    void onClick() {
        fastAdapterSearching.withOnClickListener(new OnClickListener<ISAdapter>() {
            @Override
            public boolean onClick(View v, IAdapter<ISAdapter> adapter, ISAdapter item, int position) {

                if (item.getFile().isDirectory()) {
                    executorService.execute(() -> {
                        allResults.clear();

                        File[] innerFiles = item.getFile().listFiles();
                        if (innerFiles != null) {
                            Collections.addAll(allResults, innerFiles);
                        }

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                updateAdapter();
                            }
                        });
                    });

                } else {
                    openWith(item.getFile());
                }

                return false;
            }
        });
    }


    private void updateAdapter() {
        List<ISAdapter> items = new ArrayList<>();
        for (File file : allResults) {
            items.add(new ISAdapter(file, false, ""));
        }
        itemAdapterSearching.setNewList(items);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openWith(File clickedFile) {

        if (clickedFile.isFile()) {

            String mine = URLConnection.guessContentTypeFromName(clickedFile.getName());

            if (mine != null && mine.startsWith("audio")) {

                try {

                    Uri audioUri = FileProvider.getUriForFile(NewSearchActivity.this, this.getPackageName() + ".provider", clickedFile);

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(audioUri, "audio/*");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    this.startActivity(intent);

                } catch (Exception e) {
                    Toast.makeText(this, "Cannot play audio " + e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            } else if (mine != null && mine.startsWith("image")) {

                try {

                    Uri imageUri = FileProvider.getUriForFile(this, this.getPackageName() + ".provider", clickedFile);

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(imageUri, "image/*");
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    this.startActivity(intent);

                } catch (Exception e) {
                    Toast.makeText(this, "Cannot open image " + e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            } else if (mine != null && mine.startsWith("application/pdf")) {

                try {
                    Toast.makeText(NewSearchActivity.this, "PDF FIle", Toast.LENGTH_SHORT).show();
                    Uri appPdfUri = FileProvider.getUriForFile(this, this.getPackageName() + ".provider", clickedFile);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(appPdfUri, "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    this.startActivity(intent);

                } catch (Exception e) {
                    Toast.makeText(this, "No Pdf Reader Installed", Toast.LENGTH_SHORT).show();
                }
            } else if (mine != null && mine.startsWith("video/")) {
                try {

                    Uri videoUri = FileProvider.getUriForFile(this, this.getPackageName() + ".provider", clickedFile);

                    Intent videoIntent = new Intent(Intent.ACTION_VIEW);
                    videoIntent.setDataAndType(videoUri, "video/*");
                    videoIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    this.startActivity(videoIntent);

                } catch (Exception e) {
                    Toast.makeText(this, "NO Video Player" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
