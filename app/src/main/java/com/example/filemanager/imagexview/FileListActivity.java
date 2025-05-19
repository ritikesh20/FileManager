package com.example.filemanager.imagexview;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filemanager.R;
import com.example.filemanager.adapter.InternalStorageAdapter;

import java.io.File;

public class FileListActivity extends AppCompatActivity {

    TextView noFilesTv;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);

        noFilesTv = findViewById(R.id.nofiles_text);
        recyclerView = findViewById(R.id.recyclerView);

        Intent intent = getIntent();
        String path = intent.getStringExtra("path");

        File root = new File(path);

        File[] fileAndFolders = root.listFiles();

        if (fileAndFolders == null || fileAndFolders.length == 0){
            noFilesTv.setVisibility(View.VISIBLE);
            return;
        }

        noFilesTv.setVisibility(View.INVISIBLE);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new InternalStorageAdapter(getApplicationContext(),fileAndFolders));
    }
}