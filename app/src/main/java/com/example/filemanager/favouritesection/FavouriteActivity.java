package com.example.filemanager.favouritesection;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filemanager.R;
import com.example.filemanager.videolist.FavouriteAdapter;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavouriteActivity extends AppCompatActivity {

    private RecyclerView recyclerViewFav;
    ItemAdapter<FavouriteAdapter> itemAdapterFav;
    FastAdapter<FavouriteAdapter> favouriteAdapterFav;
    List<FavouriteAdapter> listFav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        recyclerViewFav = findViewById(R.id.recyclerViewFav);
        listFav = new ArrayList<>();
        itemAdapterFav = new ItemAdapter<>();
        favouriteAdapterFav = FastAdapter.with(itemAdapterFav);
        recyclerViewFav.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewFav.setAdapter(favouriteAdapterFav);

        loadFavItems();

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
}