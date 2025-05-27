package com.example.filemanager;

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
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.filemanager.imagexview.ImageGalleryActivity;
import com.google.android.material.navigation.NavigationView;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

public class MainActivity extends AppCompatActivity {

    DrawerLayout homeDrawerLayout;

    NavigationView navigationView;

    CardView btnCVInternalStorage;
    CardView btnImageView;

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 100;
    private static final int REQUEST_CODE_MANAGE_ALL_FILES_ACCESS_PERMISSION = 101;


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
        int result = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        ) {
            Toast.makeText(this, "Give the Permission", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 111);
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
