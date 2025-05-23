package com.example.filemanager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.filemanager.imagexview.ImageGalleryActivity;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

public class MainActivity extends AppCompatActivity {

    CardView btnCVInternalStorage;
    CardView btnImageView;

    Toolbar homeToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCVInternalStorage = findViewById(R.id.cardViewInternalStorage);
        homeToolbar = findViewById(R.id.toolbarHome);

        setSupportActionBar(homeToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Dashboard");
        }

        btnImageView = findViewById(R.id.btnShowGallery);

        btnImageView.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ImageGalleryActivity.class);

            startActivity(intent);
        });


        btnCVInternalStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()) { // permission Allowed

                    Intent intent = new Intent(MainActivity.this, InternalStorageActivity.class);
                    String path = Environment.getExternalStorageDirectory().getPath();
                    intent.putExtra("path", path);
                    startActivity(intent);

                } else {
                    requestPermission();
                }

            }
        });

    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Give the Permission", Toast.LENGTH_SHORT).show();
        } else {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 111);

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
            Toast.makeText(this, "Searching Feature Coming Soon", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.homeRefresh) {
            Toast.makeText(this, "Searching Feature Coming Soon", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

}











/*
            btnCVInternalStorage.setOnClickListener(v -> {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    openFileListActivity();
                } else {

                    try {

                        Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);

                    } catch (Exception e) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                        startActivity(intent);
                    }
                }
            } else {
                if (checkPermission()) {
                    openFileListActivity();
                } else {
                    requestPermission();
                }
            }
        });

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 111) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openFileListActivity();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 111);
    }

    private void openFileListActivity() {

        Intent intent = new Intent(MainActivity.this, FileListActivity.class);
        String path = Environment.getExternalStorageDirectory().getPath();
        intent.putExtra("path", path);
        startActivity(intent);

    }

 */