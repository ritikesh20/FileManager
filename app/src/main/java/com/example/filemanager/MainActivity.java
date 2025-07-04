package com.example.filemanager;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filemanager.apps.AppsActivity;
import com.example.filemanager.document.DocumentActivity;
import com.example.filemanager.favouritesection.FavouriteActivity;
import com.example.filemanager.imagexview.ImageGalleryActivity;
import com.example.filemanager.internalstorage.InternalStorageActivity;
import com.example.filemanager.music.MusicActivity;
import com.example.filemanager.music.NewSearchActivity;
import com.example.filemanager.recentfiles.RecentFilesActivity;
import com.example.filemanager.safefolder.LogInActivity;
import com.example.filemanager.trashbin.TrashActivity;
import com.example.filemanager.videolist.HomeFileAdapter;
import com.example.filemanager.videolist.VideoActivity;
import com.google.android.material.navigation.NavigationView;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    DrawerLayout homeDrawerLayout;
    NavigationView navigationView;

    CardView btnCVInternalStorage, btnImageView,
            btnCVDownload, btnVideo,
            btnShowAudioFile, btnCVDocument,
            btnCVSDCard,
            btnCVApps, btnCvFavourite, btnCVSafeFolder;

    IconicsImageView btnHomeIconShowSideNav;

    TextView btnRecentFile;
    TextView etSearchingFile;
    IconicsImageView
            homeIconDownload, homeIconImage, homeIconVideo, homeIconAudio,
            homeIconDoc, homeIconApps, homeIconStarred, homeIconSafeFolder,
            homeIconInternalStorage, homeIconSDCard, homeSideNav;

    RecyclerView recyclerView;
    private ItemAdapter<HomeFileAdapter> itemAdapterRecentFile;
    private FastAdapter<HomeFileAdapter> fastAdapterRecentFile;

    List<HomeFileAdapter> recentFileList = new ArrayList<>();


    long totalImageSize = 0;
    long totalVideoSize = 0;
    long totalAudioSize = 0;
    long totalDocSize = 0;
    long totalApkSize = 0;

    TextView tvAudioSize;
    TextView tvVideoSize;
    TextView tvImageSize;
    TextView tvDocSize;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applyThemeMode();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btnCvFavourite = findViewById(R.id.btnShowStarred);

        btnRecentFile = findViewById(R.id.btnSeeAllRecentFile);
        btnRecentFile.setOnClickListener(v -> {

            Intent intent = new Intent(this, RecentFilesActivity.class);
            startActivity(intent);

        });

        addIconHome();

        tvAudioSize = findViewById(R.id.tvAudioFileSize);
        tvVideoSize = findViewById(R.id.tvVideoFileSize);
        tvImageSize = findViewById(R.id.tvImageFileSize);
        tvDocSize = findViewById(R.id.tvDocFileSize);

        homeDrawerLayout = findViewById(R.id.homeDreawerLayout);
        navigationView = findViewById(R.id.home_nav_DL);
        btnHomeIconShowSideNav = findViewById(R.id.homeIconShowSideNav);

        btnHomeIconShowSideNav.setOnClickListener(v -> {
            if (homeDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                homeDrawerLayout.closeDrawer(GravityCompat.START);
            } else {
                homeDrawerLayout.openDrawer(GravityCompat.START);
            }
        });


        recyclerView = findViewById(R.id.recyclerViewHome);
        itemAdapterRecentFile = new ItemAdapter<>();
        fastAdapterRecentFile = FastAdapter.with(itemAdapterRecentFile);
        etSearchingFile = findViewById(R.id.btnFileSearchingName);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(fastAdapterRecentFile);

        btnCVInternalStorage = findViewById(R.id.cardViewInternalStorage);
        btnCVDownload = findViewById(R.id.btnDownload);
        btnShowAudioFile = findViewById(R.id.btnShowAudioFile);
        btnImageView = findViewById(R.id.btnShowGallery);
        btnVideo = findViewById(R.id.btnShowVideo);
        btnCVDocument = findViewById(R.id.btnShowDocument);
        btnCVApps = findViewById(R.id.btnCVApps);
        btnCVSafeFolder = findViewById(R.id.btnSafe);

        etSearchingFile.setOnClickListener(v -> {
            Intent intent = new Intent(this, NewSearchActivity.class);
            startActivity(intent);
        });

        btnCVSafeFolder.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LogInActivity.class);
            startActivity(intent);
        });

        btnCvFavourite.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FavouriteActivity.class);
            startActivity(intent);
        });

        btnCVSDCard = findViewById(R.id.cardViewMemoryCardStorage);

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
            if (downloadDir != null) {
                String path = downloadDir.getPath();
                Intent intent = new Intent(MainActivity.this, InternalStorageActivity.class);
                intent.putExtra("path", path);
                startActivity(intent);
            }


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

        btnCVApps.setOnClickListener(v -> {
            Intent appsIntent = new Intent(this, AppsActivity.class);
            startActivity(appsIntent);
        });

        openNavOption();

        loadRecentFiles();

        fastAdapterRecentFile.withOnClickListener((v, adapter, item, position) -> {

            Uri openFileUri = item.getUri();
            String mimeType = item.getRecentMimeType();

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(openFileUri, mimeType);
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);

            try {
                assert v != null;
                v.getContext().startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(MainActivity.this, "No app found to open this file ", Toast.LENGTH_SHORT).show();
            }
            return true;
        });

        getInternalStorageInfo();
    }

    private void loadRecentFiles() {

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(new Runnable() {
            @Override
            public void run() {

                List<HomeFileAdapter> tempRecentFile = new ArrayList<>();

                Uri uriRecentFile = MediaStore.Files.getContentUri("external");

                String[] projection = {

                        MediaStore.Files.FileColumns._ID,
                        MediaStore.Files.FileColumns.DISPLAY_NAME,
                        MediaStore.Files.FileColumns.MIME_TYPE,
                        MediaStore.Files.FileColumns.SIZE,
                        MediaStore.Files.FileColumns.DATE_MODIFIED,
                        MediaStore.Files.FileColumns.RELATIVE_PATH
                };

                String selection =
                        MediaStore.Files.FileColumns.MEDIA_TYPE + "=? OR " +
                                MediaStore.Files.FileColumns.MEDIA_TYPE + "=? OR " +
                                MediaStore.Files.FileColumns.MEDIA_TYPE + "=?";

                String[] selectionArgs = {
                        String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
                        String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO),
                        String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO)
                };

                String sortingTypes = MediaStore.Files.FileColumns.DATE_ADDED + " DESC";

                ContentResolver contentResolver = getContentResolver();

                Cursor cursor = contentResolver.query(uriRecentFile, projection, selection, selectionArgs, sortingTypes);


                if (cursor != null) {
                    int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID);
                    int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME);
                    int mimeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE);
                    int folderColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.RELATIVE_PATH);

                    int count = 0;
                    while (cursor.moveToNext() && count <= 20) {
                        count++;
                        long id = cursor.getLong(idColumn);
                        String name = cursor.getString(nameColumn);
                        String mime = cursor.getString(mimeColumn);
                        String folder = cursor.getString(folderColumn);

                        Uri recentFileUri = ContentUris.withAppendedId(uriRecentFile, id);

                        tempRecentFile.add(new HomeFileAdapter(recentFileUri, name, folder, mime));
                    }
                    cursor.close();
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        recentFileList.clear();
                        recentFileList.addAll(tempRecentFile);
                        itemAdapterRecentFile.setNewList(recentFileList);
                        fastAdapterRecentFile.notifyDataSetChanged();

                    }
                });

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.all_type, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    private void addIconHome() {

        homeSideNav = findViewById(R.id.homeIconShowSideNav);
        homeIconDownload = findViewById(R.id.homeIconsDownload);
        homeIconImage = findViewById(R.id.homeIconsImage);
        homeIconVideo = findViewById(R.id.homeIconsVideo);
        homeIconAudio = findViewById(R.id.homeIconAudio);
        homeIconDoc = findViewById(R.id.homeIconDoc);
        homeIconApps = findViewById(R.id.homeIconApps);

        homeIconStarred = findViewById(R.id.homeIconStarred);
        homeIconSafeFolder = findViewById(R.id.homeIconSafeFolder);

        homeIconInternalStorage = findViewById(R.id.homeIconInternalStorage);
        homeIconSDCard = findViewById(R.id.homeIconSDCard);

        homeIconDownload.setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_file_download).sizeDp(20));
        homeIconImage.setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_image));

        homeIconVideo.setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_movie));

        homeIconAudio.setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_music_note));
        homeIconDoc.setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_drafts));

        homeIconApps.setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_apps));
        homeIconStarred.setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_star));
        homeIconSafeFolder.setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_lock));
        homeIconInternalStorage.setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_phone_android));
        homeIconSDCard.setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_sd_card));

        homeSideNav.setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_menu));

    }

    void openNavOption() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                if (id == R.id.nav_Setting) {
                    Intent settingIntent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(settingIntent);
                    return true;
                } else if (id == R.id.nav_Trash) {
                    Intent trashBin = new Intent(MainActivity.this, TrashActivity.class);
                    startActivity(trashBin);
                    return true;
                }

                homeDrawerLayout.closeDrawer(GravityCompat.START);
                return false;
            }
        });
    }

    private void getInternalStorageInfo() {

        totalAudioSize = 0;
        totalVideoSize = 0;
        totalImageSize = 0;
        totalApkSize = 0;
        totalDocSize = 0;

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            @Override
            public void run() {

                File root = Environment.getExternalStorageDirectory();
                calFileSize(root);

                String audioFileSize = FileOperation.sizeCal(totalAudioSize);
                String videoFileSize = FileOperation.sizeCal(totalVideoSize);
                String imageFileSize = FileOperation.sizeCal(totalImageSize);
                String docFileSize = FileOperation.sizeCal(totalDocSize);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvAudioSize.setText(audioFileSize);
                        tvVideoSize.setText(videoFileSize);
                        tvImageSize.setText(imageFileSize);
                        tvDocSize.setText(docFileSize);
                    }
                });
            }
        });
    }


    private void calFileSize(File dir) {

        if (dir == null || !dir.exists() || !dir.isDirectory()) return;

        File[] files = dir.listFiles();

        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                calFileSize(file);
            } else {
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
                }
            }
        }
    }


    void applyThemeMode() {

        SharedPreferences themeModePref = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = themeModePref.getString("app_mode", "defaultMode");

        switch (theme) {
            case "lightMode":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "darkMode":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case "defaultMode":
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }

    }


}


