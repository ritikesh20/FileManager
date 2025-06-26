package com.example.filemanager.favouritesection;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filemanager.FileOperation;
import com.example.filemanager.R;
import com.example.filemanager.videolist.VideoActivity;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.OnLongClickListener;
import com.mikepenz.fastadapter.select.SelectExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavouriteActivity extends AppCompatActivity {

    boolean isAllFileSelected = false;
    private RecyclerView recyclerViewFav;
    private ItemAdapter<FavouriteAdapter> itemAdapterFav;
    private FastAdapter<FavouriteAdapter> fastAdapterFav;
    private List<FavouriteAdapter> listFav;
    SelectExtension<FavouriteAdapter> selectExtension;
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

        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Starred");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        selectExtension.withSelectable(true);
        selectExtension.withMultiSelect(true);
        selectExtension.withSelectWithItemUpdate(true);

        loadFavItems();

        fastAdapterFav.withOnLongClickListener(new OnLongClickListener<FavouriteAdapter>() {
            @Override
            public boolean onLongClick(View v, IAdapter<FavouriteAdapter> adapter, FavouriteAdapter item, int position) {
                selectExtension.toggleSelection(position);

//                int selectedFileCount = selectExtension.getSelectedItems().size();


                Toast.makeText(FavouriteActivity.this, "" + item.favouriteItem.getName(), Toast.LENGTH_SHORT).show();

                return true;
            }
        });

    }

    void click(boolean isOpenFile) {
        if (!isOpenFile) {
            fastAdapterFav.withOnClickListener((v, adapter, item, position) -> {

                Uri favFileUri = Uri.parse(item.favouriteItem.getUri());
                assert v != null;
                FileOperation.fileOpenWith(v.getContext(), favFileUri, item.favouriteItem.getMimeView());

                return false;
            });
        }
    }

    void loadFavItems() {

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                List<FavouriteItem> favList = AppDatabase.getInstance(FavouriteActivity.this).favouriteVideoDao().getAll();
                listFav.clear();

                for (FavouriteItem fav : favList) {
                    listFav.add(new FavouriteAdapter(fav));
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        itemAdapterFav.setNewList(listFav);
                    }
                });

            }
        });


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


}