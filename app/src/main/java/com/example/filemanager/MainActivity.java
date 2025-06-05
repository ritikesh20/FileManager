package com.example.filemanager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.filemanager.document.DocumentActivity;
import com.example.filemanager.imagexview.ImageGalleryActivity;
import com.example.filemanager.internalstorage.InternalStorageActivity;
import com.example.filemanager.music.MusicActivity;
import com.example.filemanager.videolist.VideoActivity;
import com.google.android.material.navigation.NavigationView;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import java.io.File;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    DrawerLayout homeDrawerLayout;
    NavigationView navigationView;
    CardView btnCVInternalStorage, btnImageView, btnCVDownload, btnVideo, btnShowAudioFile, btnCVDocument, btnCVRecentFile;

    TextView tvInternalStorage;

    TextView tvTotalUsed;

    long totalImageSize = 0;
    long totalVideoSize = 0;
    long totalAudioSize = 0;
    long totalDocSize = 0;
    long totalApkSize = 0;

    TextView tvAudioSize;
    TextView tvVideoSize;
    TextView tvImageSize;
    TextView tvApkSize;
    TextView tvDocSize;


    ProgressBar pbFileLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pbFileLong = findViewById(R.id.pbFileSize);

        tvAudioSize = findViewById(R.id.tvAudioFileSize);
        tvVideoSize = findViewById(R.id.tvVideoFileSize);
        tvImageSize = findViewById(R.id.tvImageFileSize);
        tvApkSize = findViewById(R.id.tvApkFileSize);
        tvDocSize = findViewById(R.id.tvDocFileSize);





        Toolbar homeToolbar = findViewById(R.id.toolbarHome);
        homeDrawerLayout = findViewById(R.id.homeDreawerLayout);
        navigationView = findViewById(R.id.home_nav_DL);
        tvInternalStorage = findViewById(R.id.tvInternalStorage);
        tvTotalUsed = findViewById(R.id.tvStorageUsedTotal);

        setSupportActionBar(homeToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Dashboard");
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                homeDrawerLayout,
                homeToolbar,
                R.string.open,
                R.string.close
        );

        toggle.setDrawerIndicatorEnabled(true);
        homeDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        btnCVInternalStorage = findViewById(R.id.cardViewInternalStorage);
        btnCVDownload = findViewById(R.id.btnDownload);
        btnShowAudioFile = findViewById(R.id.btnShowAudioFile);
        btnImageView = findViewById(R.id.btnShowGallery);
        btnVideo = findViewById(R.id.btnShowVideo);
        btnCVDocument = findViewById(R.id.btnShowDocument);
        btnCVRecentFile = findViewById(R.id.btnShowRecentFile);

        btnImageView.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ImageGalleryActivity.class);
            startActivity(intent);
        });

        btnCVInternalStorage.setOnClickListener(v -> {

            String path = Environment.getExternalStorageDirectory().getPath();
            Intent intent = new Intent(MainActivity.this, InternalStorageActivity.class);
            intent.putExtra("path", path);
            startActivity(intent);

        });

        btnCVDownload.setOnClickListener(v -> {

            File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            String path = downloadDir.getPath();

            Intent intent = new Intent(MainActivity.this, InternalStorageActivity.class);
            intent.putExtra("path", path);
            startActivity(intent);

        });

        btnShowAudioFile.setOnClickListener(v -> {

            startActivity(new Intent(this, MusicActivity.class));
        });

        btnVideo.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, VideoActivity.class);
            startActivity(intent);
        });

        btnCVDocument.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DocumentActivity.class);
            startActivity(intent);
        });

        btnCVRecentFile.setOnClickListener(v -> {
            Toast.makeText(this, "Coming soon....", Toast.LENGTH_SHORT).show();
        });

        pbFileLong.setVisibility(View.VISIBLE);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {

                File root = Environment.getExternalStorageDirectory();
                calFileSize(root);

                String audioFileSize = formatSize(totalAudioSize);
                String videoFileSize = formatSize(totalVideoSize);
                String imageFileSize = formatSize(totalImageSize);
                String apkFileSize = formatSize(totalApkSize);
                String docFileSize = formatSize(totalDocSize);


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        getInternalStorageInfo();
                        pbFileLong.setVisibility(View.GONE);
                        tvAudioSize.setText(audioFileSize);
                        tvVideoSize.setText(videoFileSize);
                        tvImageSize.setText(imageFileSize);
                        tvApkSize.setText(apkFileSize);
                        tvDocSize.setText(docFileSize);

                    }
                });

            }
        });




    }

    private void getInternalStorageInfo() {

        File internalStorageDir = Environment.getDataDirectory();
        StatFs statFs = new StatFs(internalStorageDir.getPath());

        long blockSize, totalBlocks, availableBlocks;

        blockSize = statFs.getBlockSizeLong();
        totalBlocks = statFs.getBlockCountLong();
        availableBlocks = statFs.getAvailableBlocksLong();

        long totalSize = totalBlocks * blockSize;
        long availableSize = availableBlocks * blockSize;
        long usedSize = totalSize - availableSize;

        String tSize = formatSize(totalSize);
        String aSize = formatSize(availableSize);
        String uSize = formatSize(usedSize);


        tvInternalStorage.setText("Available " + aSize);
        String calSize = uSize + " Used of " + tSize;
        tvTotalUsed.setText(calSize);


    }

    private void calFileSize(File dir) {

        if (dir == null || !dir.exists() || !dir.isDirectory()) {
            return;
        }

        File[] files = dir.listFiles();

        if (files == null) {
            return;
        }

        for (File file : files){
            if (file.isDirectory()){
                calFileSize(file);
            }
            else {
                String name = file.getName().toLowerCase(Locale.ROOT);
                long size = file.length();

                if (name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png") || name.endsWith(".gif") || name.endsWith(".webp")) {
                    totalImageSize += size;
                } else if (name.endsWith(".mp4") || name.endsWith(".mkv") || name.endsWith(".avi") || name.endsWith(".3gp")) {
                    totalVideoSize += size;
                } else if (name.endsWith(".mp3") || name.endsWith(".wav") || name.endsWith(".aac") || name.endsWith(".m4a")) {
                    totalAudioSize += size;
                } else if (name.endsWith(".pdf") || name.endsWith(".doc") || name.endsWith(".docx") || name.endsWith(".ppt") || name.endsWith(".pptx") || name.endsWith(".txt")) {
                    totalDocSize += size;
                } else if (name.endsWith(".apk")) {
                    totalApkSize += size;
                }

            }
        }

    }

    private String formatSize(long sizeInBytes) {

        float kb = sizeInBytes / 1024f;
        float mb = kb / 1024f;
        float gb = mb / 1024f;

        if (gb >= 1) {
            return String.format("%.1f GB", gb);
        } else if (mb >= 1) {
            return String.format("%.1f MB", mb);
        } else {
            return String.format("%.1f KB", kb);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.menu_home, menu);

        menu.findItem(R.id.homeSearch).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_search).color(getColor(R.color.black)).actionBar());
        menu.findItem(R.id.homeRefresh).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_refresh).color(getColor(R.color.black)).actionBar());

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.homeSearch) {
            Toast.makeText(this, "Searching Feature Coming Soon", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.homePremium) {
            Toast.makeText(this, "Premium Feature Coming Soon", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.homeRefresh) {

            Intent intent = new Intent(this, MainActivity.class);
            Toast.makeText(this, "Refreshing", Toast.LENGTH_SHORT).show();
            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }

}
