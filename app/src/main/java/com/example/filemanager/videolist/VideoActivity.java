package com.example.filemanager.videolist;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filemanager.R;
import com.example.filemanager.music.MusicAdapter;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VideoActivity extends AppCompatActivity {

    private RecyclerView recyclerViewVideo;
    private ItemAdapter<MusicAdapter> itemAdapterVideo;
    FastAdapter<MusicAdapter> fastAdapterVideo;
    private Toolbar toolbarVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        toolbarVideo = findViewById(R.id.toolbarVideo);

        setSupportActionBar(toolbarVideo);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Video");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerViewVideo = findViewById(R.id.recyclerViewVideo);
        itemAdapterVideo = new ItemAdapter<>();
        fastAdapterVideo = FastAdapter.with(itemAdapterVideo);

        recyclerViewVideo.setLayoutManager(new LinearLayoutManager(this));

        uploadVideo();

        recyclerViewVideo.setAdapter(fastAdapterVideo);

        fastAdapterVideo.withOnClickListener(new OnClickListener<MusicAdapter>() {
            @Override
            public boolean onClick(View v, IAdapter<MusicAdapter> adapter, MusicAdapter item, int position) {
                Toast.makeText(VideoActivity.this, "This is a Video File", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

    }

    void uploadVideo() {

        List<MusicAdapter> videoList = new ArrayList<>();

        Uri videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        String[] projection = new String[]{
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DISPLAY_NAME
        };

        Cursor cursor = getContentResolver().query(videoUri, projection, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));

                File file = new File(path);

                if (file.exists() && path.endsWith(".mp4")) {
                    videoList.add(new MusicAdapter(file));
                }
            }
            cursor.close();
        }

        itemAdapterVideo.setNewList(videoList);


    }
}