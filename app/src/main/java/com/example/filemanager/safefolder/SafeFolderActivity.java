package com.example.filemanager.safefolder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filemanager.R;
import com.example.filemanager.internalstorage.ISAdapter;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SafeFolderActivity extends AppCompatActivity {

    private Toolbar toolbarSafeBox;
    private RecyclerView recyclerView;
    private ItemAdapter<ISAdapter> itemAdapterSafeFolder;
    private FastAdapter<ISAdapter> fastAdapterSafeFolder;
    private SafeBoxFileDB safeBoxFileDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_folder);


        toolbarSafeBox = findViewById(R.id.toolbarSafeBox);
        recyclerView = findViewById(R.id.recyclerViewSafeBox);

        setSupportActionBar(toolbarSafeBox);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Safe Folder");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

        itemAdapterSafeFolder = new ItemAdapter<>();
        fastAdapterSafeFolder = FastAdapter.with(itemAdapterSafeFolder);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(fastAdapterSafeFolder);

        safeBoxFileDB = SafeBoxFileDB.getInstance(this);

        loadSafeBoxFiles();

    }

    private void loadSafeBoxFiles() {

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(() -> {

            List<SafeBoxFile> safeList = safeBoxFileDB.safeBoxFileDao().getAllSafeBoxFiles();
            List<ISAdapter> items = new ArrayList<>();

            for (SafeBoxFile file : safeList) {
                File f = new File(file.fileSavePath);
                if (f.exists()) {
                    items.add(new ISAdapter(f));
                }
            }
            handler.post(() -> {
                itemAdapterSafeFolder.setNewList(items);
                fastAdapterSafeFolder.notifyDataSetChanged();
            });
        });
    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
