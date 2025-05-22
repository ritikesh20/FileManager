package com.example.filemanager.imagexview;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filemanager.R;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ImageGalleryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    FastAdapter<ImageFA> imageFastAdapter;
    ItemAdapter<ImageFA> imageItemAdapter;
    List<ImageFA> imageList;
    private Toolbar toolbar;

    //    boolean isListView = false;
    private static final int REQUEST_PERMISSION_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_gallery);

        recyclerView = findViewById(R.id.rvImageGallery);
        toolbar = findViewById(R.id.toolbarGalleryImage);

        imageItemAdapter = new ItemAdapter<>();
        imageFastAdapter = FastAdapter.with(imageItemAdapter);
        imageList = new ArrayList<>();

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Image");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // to show back button
        }

        recyclerViewSetUp();
        checkPermissionImage();

        fullImage();
    }

    public void recyclerViewSetUp() {

//        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(imageFastAdapter);
    }

    public void checkPermissionImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_PERMISSION_CODE);
            } else {
                loadImage();
            }
        } else {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
            } else {
                loadImage();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadImage();
            } else {
                Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void loadImage() {

        Uri collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//      String[] projection = new String[]{MediaStore.Images.Media._ID};

        String[] projection = {

                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.SIZE

        };

        Cursor cursor = getContentResolver().query(
                collection,
                projection,
                null,
                null,
                MediaStore.Images.Media.DATE_ADDED + " ASC" // DESC
        );

        if (cursor != null) {
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int nameCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            int dataCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN);
            int sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);

            while (cursor.moveToNext()) {

                long id = cursor.getLong(idColumn);
                String name = cursor.getString(nameCol);
                long dateTaken = cursor.getLong(dataCol);
                long sizeInBytes = cursor.getLong(sizeCol);

                Uri contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

//                String imageSize = getFormattedImageSize(this, contentUri);
                String imageSize = formatSize(sizeInBytes);

                String imageDate = DateFormat.format("dd MMM yyyy, hh:mm a", new Date(dateTaken)).toString();

                imageList.add(new ImageFA(

                        contentUri,
                        name,
                        imageSize,
                        imageDate

                ));

            }
            cursor.close();
        }

        imageItemAdapter.clear();
        imageItemAdapter.set(imageList);
    }

    private String formatSize(long bytes) {
        double kb = bytes / 1024.0;
        double mb = kb / 1024.0;
        if (mb >= 1) return String.format(Locale.getDefault(), "%.2f MB", mb);
        else return String.format(Locale.getDefault(), "%.2f KB", kb);
    }


//    public String getFormattedImageSize(Context context, Uri uri) {
//        String sizeStr = "Unknown";
//        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
//        if (cursor != null) {
//            int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
//            if (sizeIndex != -1 && cursor.moveToFirst()) {
//                long sizeInBytes = cursor.getLong(sizeIndex);
//                double kb = sizeInBytes / 1024.0;
//                double mb = kb / 1024.0;
//                if (mb >= 1) {
//                    sizeStr = String.format(Locale.getDefault(), "%.2f MB", mb);
//                } else {
//                    sizeStr = String.format(Locale.getDefault(), "%.2f KB", kb);
//                }
//            }
//            cursor.close();
//        }
//        return sizeStr;
//    }

    private void filterImages(String query) {
        List<ImageFA> filteredList = new ArrayList<>();

        for (ImageFA imageQuery : imageList) {
            if (imageQuery.getImageName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(imageQuery);
            }
        }

        imageItemAdapter.set(filteredList);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_image, menu);

        menu.findItem(R.id.menuImage_search).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_search).color(Color.BLACK).actionBar());
        menu.findItem(R.id.menuImage_SortBy).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_filter).color(Color.BLACK).actionBar());
        menu.findItem(R.id.menuImage_ChangeView).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_grid_on).color(Color.BLACK).actionBar());

        MenuItem searchItem = menu.findItem(R.id.menuImage_search);

        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setQueryHint("Searching........");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterImages(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterImages(newText);
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        else if (id == R.id.menuImage_SortBy) {
            Collections.shuffle(imageList);
            imageItemAdapter.setNewList(imageList);
            return true;
        }
        else if (id == R.id.menu_SizeSL) {

            imageList.sort((size1, size2) -> size1.imageSize.compareTo(size2.imageSize));
            imageItemAdapter.setNewList(imageList);

            return true;

        } else if (id == R.id.menu_SizeLS) {

//            Collections.sort(imageList, (a, b) -> {
//                long sizeA = extractBytesFromSize(a.getImageSize());
//                long sizeB = extractBytesFromSize(b.getImageSize());
//                return Long.compare(sizeA, sizeB);
//            });

            imageList.sort((size1, size2) -> size2.imageSize.compareTo(size1.imageSize));
            imageItemAdapter.setNewList(imageList);

            return true;

        } else if (id == R.id.menuNameAZ) {

            imageList.sort((name1, name2) -> name1.imageName.compareTo(name2.imageName));
            imageItemAdapter.setNewList(imageList);

            return true;

        } else if (id == R.id.menuNameZA) {

            imageList.sort((name1, name2) -> name2.imageName.compareTo(name1.imageName));
            imageItemAdapter.setNewList(imageList);

            return true;

        }

        imageFastAdapter.notifyAdapterDataSetChanged();
        return super.onOptionsItemSelected(item);
    }

    void fullImage() {

        imageFastAdapter.withOnClickListener((v, adapter, item, position) -> {
            Intent intent = new Intent(ImageGalleryActivity.this, FullScreenImageActivity.class);
            intent.putExtra("image", item.getImageUri().toString());

            startActivity(intent);
            return true;

        });

    }


}













