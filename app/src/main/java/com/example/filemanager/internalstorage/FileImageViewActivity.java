package com.example.filemanager.internalstorage;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.filemanager.R;

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

