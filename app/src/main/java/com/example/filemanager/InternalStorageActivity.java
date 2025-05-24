package com.example.filemanager;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filemanager.adapter.ISAdapter;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class InternalStorageActivity extends AppCompatActivity {

    public static boolean isGridView = false;

    private RecyclerView recyclerView;
    private TextView noFileText;
    private ItemAdapter<ISAdapter> itemAdapter;
    private FastAdapter<ISAdapter> fastAdapter;

//    private RecyclerView recyclerViewHistoryPath;
    Toolbar inStorageToolbar;

    LinearLayout pathBar;
    List<String> pathList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internal_storage);

        inStorageToolbar = findViewById(R.id.toolbarInternalStorage);
        pathBar = findViewById(R.id.pathBar);

        recyclerView = findViewById(R.id.rvInternalStorage);
        noFileText = findViewById(R.id.tvNoFile);

        itemAdapter = new ItemAdapter<>();
        fastAdapter = FastAdapter.with(itemAdapter);

        setSupportActionBar(inStorageToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My File");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        String path = getIntent().getStringExtra("path");

        pathList = getIntent().getStringArrayListExtra("pathList");

        if (pathList == null) {
            pathList = new ArrayList<>();
            addToPathBar();
        } else {
            updatePathBar();
        }

        assert path != null;
        File root = new File(path);
        File[] filesAndFolders = root.listFiles();

        int fileCount = 0;
        int folderCount = 0;

        if (filesAndFolders == null || filesAndFolders.length == 0) {
            noFileText.setVisibility(View.VISIBLE);
            return;
        }

        noFileText.setVisibility(View.INVISIBLE);
        setLayout();

        List<ISAdapter> items = new ArrayList<>();

        for (File file : filesAndFolders) {
            items.add(new ISAdapter(file));
            if (file.isDirectory()) {

                folderCount++;
            } else {
                fileCount++;
            }
        }




        String subTitle = folderCount + " Folder " + fileCount + " File";
        Objects.requireNonNull(getSupportActionBar()).setSubtitle(subTitle);

        fastAdapter.withOnClickListener((v, adapter, item, position) -> {

            File clickedFile = item.getFile();

            if (clickedFile.isDirectory()) {

                ArrayList<String> updatedPathList = new ArrayList<>(pathList);
                updatedPathList.add(clickedFile.getName());

                Intent intent = new Intent(this, InternalStorageActivity.class);
                intent.putExtra("path", clickedFile.getAbsolutePath());
                intent.putStringArrayListExtra("pathList", updatedPathList);
                startActivity(intent);

            }
            else {
                openWith(clickedFile);
            }

            return true;
        });


        recyclerView.setAdapter(fastAdapter);
        itemAdapter.set(items);


    }

    void setLayout() {
        recyclerView.setLayoutManager(isGridView ? new GridLayoutManager(this, 3) : new LinearLayoutManager(this));
    }

    void openWith(File clickedFile) {

        if (clickedFile.isFile()) {

            String mine = URLConnection.guessContentTypeFromName(clickedFile.getName());

            if (mine != null && mine.startsWith("audio")) {
                try {
                    Uri audioUri = FileProvider.getUriForFile(this, this.getPackageName() + ".provider", clickedFile);

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(audioUri, "audio/*");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    this.startActivity(intent);

                } catch (Exception e) {
                    Toast.makeText(this, "Cannot play audio", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else if (mine != null && mine.startsWith("image")) {
                try {
                    Uri imageUri = FileProvider.getUriForFile(
                            this,
                            this.getPackageName() + ".provider",
                            clickedFile
                    );

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(imageUri, "image/*");
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    this.startActivity(intent);

                } catch (Exception e) {
                    Toast.makeText(this, "Cannot open image" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else if (mine != null && mine.startsWith("application/pdf")) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(clickedFile), "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                try {
                    startActivity(intent);
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
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Unsupported File " + mine, Toast.LENGTH_SHORT).show();
            }
        }


    }


    void addToPathBar() {
        pathList.add("Internal Storage");
        updatePathBar();
    }

    void updatePathBar() {

        pathBar.removeAllViews();

        for (int i = 0; i < pathList.size(); i++) {

            String folder = pathList.get(i);
            TextView textView = new TextView(this);
            textView.setPadding(10, 0, 0, 0);

            textView.setText(folder);
            textView.setTextSize(18);

            pathBar.addView(textView);

            if (i != pathList.size() - 1) {

                ImageView arrow = new ImageView(this);
                arrow.setImageResource(R.drawable.next);
                pathBar.addView(arrow);

            }
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(R.drawable.crown);
            pathBar.addView(imageView);

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.menu_instorage, menu);

        menu.findItem(R.id.isSearch).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_search).actionBar());

        menu.findItem(R.id.isHome).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_home).actionBar());

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.isChangeView) {
            isGridView = !isGridView;
            setLayout();
            fastAdapter.notifyAdapterDataSetChanged();
            recreate();
            return true;
        } else if (id == R.id.isHome) {
            Intent intent = new Intent(InternalStorageActivity.this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.isSearch) {
            Toast.makeText(this, "Searching Feature coming soon", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }


}


