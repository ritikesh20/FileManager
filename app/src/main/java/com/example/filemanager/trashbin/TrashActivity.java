package com.example.filemanager.trashbin;

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

public class TrashActivity extends AppCompatActivity {


    private RecyclerView recyclerViewTrash;
    private Toolbar toolbarTrash;
    private ItemAdapter<ISAdapter> itemAdapterTrash;
    private FastAdapter<ISAdapter> fastAdapterTrash;
    private List<ISAdapter> listTrash;
    private RecyclerBinDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash);

        recyclerViewTrash = findViewById(R.id.recyclerViewTrash);
        toolbarTrash = findViewById(R.id.toolbarTrash);

        itemAdapterTrash = new ItemAdapter<>();
        fastAdapterTrash = FastAdapter.with(itemAdapterTrash);
        listTrash = new ArrayList<>();

        recyclerViewTrash.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTrash.setAdapter(fastAdapterTrash);


        db = RecyclerBinDatabase.getInstance(this);

        setSupportActionBar(toolbarTrash);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Trash");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        loadingBin();
    }

    private void loadingBin() {

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(() -> {

            List<RecycleBinItem> allBinItem = db.recycleBinDao().getAll();

            List<ISAdapter> items = new ArrayList<>();

            for (RecycleBinItem binItem : allBinItem) {
                if (binItem.deletedPath != null) {
                    File f = new File(binItem.deletedPath);
                    if (f.exists()) {
                        items.add(new ISAdapter(f));
                    }
                }
            }

            handler.post(() -> {
                itemAdapterTrash.setNewList(items);
            });
        });
    }


}