package com.example.filemanager.imagexview;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.filemanager.R;

public class FullScreenImageActivity extends AppCompatActivity {

    SubsamplingScaleImageView fullImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        fullImageView = findViewById(R.id.showFullImageView);

        String imageUriString = getIntent().getStringExtra("image");

        if (imageUriString != null) {

            Uri imageUri = Uri.parse(imageUriString);

            fullImageView.setImage(ImageSource.uri(imageUri));

        }

    }
}