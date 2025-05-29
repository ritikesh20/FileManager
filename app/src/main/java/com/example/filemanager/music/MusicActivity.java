package com.example.filemanager.music;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filemanager.R;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MusicActivity extends AppCompatActivity {

    private RecyclerView rvMusic;
    private ItemAdapter<MusicAdapter> itemAdapterMusic;
    private FastAdapter<MusicAdapter> fastAdapterMusic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        rvMusic = findViewById(R.id.recyclerViewMusic);
        itemAdapterMusic = new ItemAdapter<>();
        fastAdapterMusic = FastAdapter.with(itemAdapterMusic);

        rvMusic.setLayoutManager(new LinearLayoutManager(this));

        loadAudioFiles();

        rvMusic.setAdapter(fastAdapterMusic);

        fastAdapterMusic.withOnClickListener(new OnClickListener<MusicAdapter>() {
            @Override
            public boolean onClick(View v, IAdapter<MusicAdapter> adapter, MusicAdapter item, int position) {
                Toast.makeText(MusicActivity.this, "This is a Music File", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

    }

    private void loadAudioFiles() {

        List<MusicAdapter> musicList = new ArrayList<>();

        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME
        };

        Cursor cursor = getContentResolver().query(
                musicUri,
                projection,
                null,
                null,
                null
        );

        if (cursor != null) {

            while (cursor.moveToNext()) {
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                File file = new File(path);

                if (file.exists() && path.endsWith(".mp3")) {
                    musicList.add(new MusicAdapter(file));
                }
            }

            cursor.close();
        }
        itemAdapterMusic.setNewList(musicList);
    }

}


/*
//            do {
//                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
//
//                File file = new File(path);
//
//                if (file.exists() && path.endsWith(".mp3")) {
//                    musicList.add(new MusicAdapter(file));
//                }
//
//            } while (cursor.moveToNext());


    private RecyclerView rvMusic;
    private ItemAdapter<MusicAdapter> itemAdapterMusic;
    private FastAdapter<MusicAdapter> fastAdapterMusic;


        File rootDir = Environment.getExternalStorageDirectory();
        List<File> musicFile = getAllMusic(rootDir);
        List<MusicAdapter> musicList = new ArrayList<>();

        for (File file : musicFile){
            musicList.add(new MusicAdapter(file));
        }

        itemAdapterMusic.setNewList(musicList);

    private List<File> getAllMusic(File dir) {
        List<File> musicList = new ArrayList<>();
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    musicList.addAll(getAllMusic(file));
                } else if (file.getName().endsWith(".mp3")) {
                    musicList.add(file);
                }
            }
        }
        return musicList;
    }
 */