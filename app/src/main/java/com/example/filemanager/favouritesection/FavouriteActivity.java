package com.example.filemanager.favouritesection;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filemanager.R;
import com.example.filemanager.internalstorage.ISAdapter;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.select.SelectExtension;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavouriteActivity extends AppCompatActivity {

    boolean isAllFileSelected = false;
    private RecyclerView recyclerViewFav;
    private ItemAdapter<ISAdapter> itemAdapterFav;
    private FastAdapter<ISAdapter> fastAdapterFav;
    private List<ISAdapter> listFav;
    SelectExtension<ISAdapter> selectExtension;
    private Toolbar toolbarFav;
    private boolean isOpen = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        toolbarFav = findViewById(R.id.toolbarStarred);

        recyclerViewFav = findViewById(R.id.recyclerViewFav);
        listFav = new ArrayList<>();
        itemAdapterFav = new ItemAdapter<>();
        fastAdapterFav = FastAdapter.with(itemAdapterFav);
        recyclerViewFav.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewFav.setAdapter(fastAdapterFav);

        selectExtension = fastAdapterFav.getExtension(SelectExtension.class);

        if (selectExtension == null) {
            selectExtension = new SelectExtension<>();
            fastAdapterFav.addExtension(selectExtension);
        }

        setSupportActionBar(toolbarFav);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Starred");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        selectExtension.withSelectable(true);
        selectExtension.withMultiSelect(true);
        selectExtension.withSelectWithItemUpdate(true);


        loadingFavoriteDojo();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.all_type, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.file_search) {

        } else if (id == R.id.file_ChangeView) {

        } else if (id == R.id.file_Selected) {
            if (!isAllFileSelected) {
                selectExtension.select();
            } else {
                selectExtension.deselect();
            }

            isAllFileSelected = !isAllFileSelected;

            return true;
        } else if (id == R.id.file_Share) {

        } else if (id == R.id.file_Delete) {

        } else if (id == R.id.file_SortBy) {

            return true;
        } else if (id == R.id.file_OpenWith) {

            return true;
        } else if (id == R.id.file_Move) {

        } else if (id == R.id.file_Copy) {

        } else if (id == R.id.file_Rename) {

            return true;
        } else if (id == R.id.file_AddToStarred) {

        } else if (id == R.id.file_SafeFolder) {

        } else if (id == R.id.file_BackToGoogleDrive) {

        } else if (id == R.id.file_FileInfo) {

        }

        return super.onOptionsItemSelected(item);

    }


    void loadingFavoriteDojo() {

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(new Runnable() {
            @Override
            public void run() {

                List<FavouriteItem> favouriteList = FavouriteDatabase.getInstance(FavouriteActivity.this).favouriteDao().getAllFavorites();
                List<ISAdapter> items = new ArrayList<>();

                for (FavouriteItem file : favouriteList) {
                    File favFile = new File(file.getFilePath());

                    if (favFile.exists()) {
                        items.add(new ISAdapter(favFile));
                    }

                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        itemAdapterFav.setNewList(items);
                        fastAdapterFav.notifyDataSetChanged();
                    }
                });


            }
        });


    }
}