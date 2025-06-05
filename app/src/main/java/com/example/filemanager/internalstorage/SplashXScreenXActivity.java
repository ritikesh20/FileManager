package com.example.filemanager.internalstorage;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.filemanager.MainActivity;
import com.example.filemanager.R;

public class SplashXScreenXActivity extends AppCompatActivity {

    Button btnWithPermission, btnWithOutPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        btnWithPermission = findViewById(R.id.withPermission);
        btnWithOutPermission = findViewById(R.id.withOutPermission);


        btnWithOutPermission.setOnClickListener(v -> {
            startActivity(new Intent(SplashXScreenXActivity.this, MainActivity.class));
        });

        btnWithPermission.setOnClickListener(v -> {

            if (checkPermission()) {

                String path = Environment.getExternalStorageDirectory().getPath();
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("path", path);
                startActivity(intent);

            } else {
                requestPermission();
            }

        });

    }

    private boolean checkPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
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


}


