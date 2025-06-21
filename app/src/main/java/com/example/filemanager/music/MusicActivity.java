package com.example.filemanager.music;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filemanager.MediaStoreHelper;
import com.example.filemanager.R;
import com.example.filemanager.document.FileHelperAdapter;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import java.util.ArrayList;
import java.util.List;

public class MusicActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMusic;
    private ItemAdapter<FileHelperAdapter> itemAdapterMusic;
    private FastAdapter<FileHelperAdapter> fastAdapterMusic;

    List<FileHelperAdapter> musicList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        Toolbar toolbarMusic = findViewById(R.id.toolbarMusic);

        recyclerViewMusic = findViewById(R.id.recyclerViewMusic);
        itemAdapterMusic = new ItemAdapter<>();
        fastAdapterMusic = FastAdapter.with(itemAdapterMusic);
        recyclerViewMusic.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMusic.setAdapter(fastAdapterMusic);
        setSupportActionBar(toolbarMusic);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Music");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        MediaStoreHelper.loadFile(this, "audio", files -> {
            musicList.clear();
            musicList.addAll(files);
            itemAdapterMusic.setNewList(musicList);
            fastAdapterMusic.notifyDataSetChanged();

        });

        fastAdapterMusic.withOnClickListener(new OnClickListener<FileHelperAdapter>() {
            @Override
            public boolean onClick(View v, @NonNull IAdapter<FileHelperAdapter> adapter, @NonNull FileHelperAdapter item, int position) {

                MediaStoreHelper.fileOpenWith(v.getContext(), item.getUri(), item.getMineTypes());

                return false;
            }
        });

    }
}



