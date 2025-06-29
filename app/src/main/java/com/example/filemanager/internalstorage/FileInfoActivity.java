package com.example.filemanager.internalstorage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.filemanager.R;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileInfoActivity extends AppCompatActivity {

    IconicsImageView fileImage, fileDetailIcon, fileDateIcon;
    TextView fileName, fileModifiedData, fileSize, filePathName;
    String convertedSize;
    long reSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_file_info);

        fileDetailIcon = findViewById(R.id.fileNameIcons);
        fileDateIcon = findViewById(R.id.fileIconModifiedDate);
        fileImage = findViewById(R.id.fileInfoImage);
        fileName = findViewById(R.id.fileInfoName);
        filePathName = findViewById(R.id.fileInfoPathName);
        fileSize = findViewById(R.id.fileInfoSize);
        fileModifiedData = findViewById(R.id.fileInfoModifiedDate);

        fileDetailIcon.setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_info));
        fileDateIcon.setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_perm_contact_calendar));

        String filePath = getIntent().getStringExtra("filepath");

        assert filePath != null;
        File file = new File(filePath);

        String name = file.getName().toLowerCase();
        String fileType = "other";

        fileName.setText(file.getName());
        filePathName.setText(file.getAbsolutePath());
        long lastModifiedData = file.lastModified();
        long sizeInBytes = file.length();


        if (file.isDirectory()) {
            fileType = "folder";
        } else if (name.endsWith(".mp3") || name.endsWith(".wav")) {
            fileType = "audio";
        } else if (name.endsWith(".mp4")) {
            fileType = "video";
        } else if (name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".jpeg")) {
            fileType = "image";
        } else if (name.endsWith(".apk")) {
            fileType = "apk";
        } else if (name.endsWith(".pdf")) {
            fileType = "pdf";
        }

        switch (fileType) {
            case "folder":
                fileImage.setImageDrawable(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_folder).paddingDp(5).actionBar());
                break;
            case "audio":
                fileImage.setImageDrawable(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_music_note).actionBar());
                break;
            case "video":
                Glide.with(this).load(file).error(R.drawable.videoicons).into(fileImage);
                break;
            case "image":
                Glide.with(this).load(file).error(R.drawable.img).into(fileImage);
                break;
            case "apk":
                Glide.with(this).load(file).error(R.drawable.apkicons).into(fileImage);
                break;
            case "pdf":
                Glide.with(this).load(file).error(R.drawable.pdficons).into(fileImage);
                break;
            default:
                fileImage.setImageDrawable(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_attach_file));
                break;
        }

        SimpleDateFormat date = new SimpleDateFormat("dd-MMM-yyyy hh:mm a ", Locale.getDefault());
        String setDate = date.format(new Date(lastModifiedData));

        if (sizeInBytes < 1024) {
            convertedSize = sizeInBytes + " B";
        } else if (sizeInBytes < 1024 * 1024) {
            reSize = sizeInBytes / 1024;
            convertedSize = reSize + " KB";
        } else if (sizeInBytes < 1024 * 1024 * 1014) {
            reSize = sizeInBytes / (1024 * 1024);
            convertedSize = reSize + " MB";
        } else {
            reSize = sizeInBytes / (1024 * 1024 * 1024);
            convertedSize = reSize + " GB";
        }

        fileSize.setText(convertedSize);
        fileModifiedData.setText(setDate);

    }
}