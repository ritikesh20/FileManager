package com.example.filemanager;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

import java.io.File;

public class FileImageViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_image_view);

        ImageView imageShow = findViewById(R.id.fullImageViewFile);

        String imagePath = getIntent().getStringExtra("image_path");

//        if (imagePath != null){
//
//        }
        try {
            Uri imageUri = Uri.parse(imagePath);
            imageShow.setImageURI(imageUri);
        } catch (Exception e) {
            Toast.makeText(this, "Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
            throw new RuntimeException(e);

        }
    }
}

