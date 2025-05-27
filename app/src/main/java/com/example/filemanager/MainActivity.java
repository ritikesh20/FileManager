package com.example.filemanager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.filemanager.imagexview.ImageGalleryActivity;
import com.example.filemanager.internalstorage.InternalStorageActivity;
import com.google.android.material.navigation.NavigationView;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    DrawerLayout homeDrawerLayout;

    NavigationView navigationView;

    CardView btnCVInternalStorage;
    CardView btnImageView;
    CardView btnCVDownload;

    CardView btnShowAudioFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar homeToolbar = findViewById(R.id.toolbarHome);
        homeDrawerLayout = findViewById(R.id.homeDreawerLayout);
        navigationView = findViewById(R.id.home_nav_DL);

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

        btnImageView.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ImageGalleryActivity.class);
            startActivity(intent);
        });

        btnCVInternalStorage.setOnClickListener(v -> {

            if (checkPermission()) {

                String path = Environment.getExternalStorageDirectory().getPath();
                Intent intent = new Intent(MainActivity.this, InternalStorageActivity.class);
                intent.putExtra("path", path);
                startActivity(intent);

            } else {
                requestPermission();
            }

        });

        btnCVDownload.setOnClickListener(v -> {

            if (checkPermission()) {

                File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

                String path = downloadDir.getPath();

                Intent intent = new Intent(MainActivity.this, InternalStorageActivity.class);
                intent.putExtra("path", path);
                startActivity(intent);

            } else {

                requestPermission();

            }
        });

        btnShowAudioFile.setOnClickListener(v -> {
            if (checkPermission()){
                startActivity(new Intent(this, MusicActivity.class));
            }
            else {
                Toast.makeText(this, "No Permission Granted", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private boolean checkPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }

    }

    private void requestPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            } catch (Exception e) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
            }

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 111);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.menu_home, menu);

        menu.findItem(R.id.homeSearch).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_search).actionBar());
        menu.findItem(R.id.homeRefresh).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_refresh).actionBar());

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
