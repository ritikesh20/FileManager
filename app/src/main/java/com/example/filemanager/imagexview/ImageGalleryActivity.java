package com.example.filemanager.imagexview;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filemanager.FileOperation;
import com.example.filemanager.MediaStoreHelper;
import com.example.filemanager.R;
import com.example.filemanager.document.FileHelperAdapter;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageGalleryActivity extends AppCompatActivity {

    ProgressBar progressBarImage;
    public static final String PREF_NAME = "sorting_pref";
    public static final String KEY_SORT_OPTION = "sort_option";
    SharedPreferences sharedPreferences;

    private RecyclerView recyclerView;
    FastAdapter<FileHelperAdapter> imageFastAdapter;
    ItemAdapter<FileHelperAdapter> imageItemAdapter;
    List<FileHelperAdapter> imageList = new ArrayList<>();

    int imageCount = 0;
    private static final int REQUEST_PERMISSION_CODE = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_gallery);

        recyclerView = findViewById(R.id.rvImageGallery);
        Toolbar toolbar = findViewById(R.id.toolbarGalleryImage);

        progressBarImage = findViewById(R.id.progressBarImage);

        imageItemAdapter = new ItemAdapter<>();
        imageFastAdapter = FastAdapter.with(imageItemAdapter);
        imageList = new ArrayList<>();

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Image");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // to show back button
        }

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        recyclerViewSetUp();
        progressBarImage.setVisibility(View.VISIBLE);

        MediaStoreHelper.loadFile(this, "image", files -> {

            imageList.clear();
            imageList.addAll(files);
            imageItemAdapter.setNewList(files);
            progressBarImage.setVisibility(View.GONE);
            imageFastAdapter.notifyDataSetChanged();

        });

        imageFastAdapter.withOnClickListener(new OnClickListener<FileHelperAdapter>() {
            @Override
            public boolean onClick(View v, IAdapter<FileHelperAdapter> adapter, FileHelperAdapter item, int position) {

                FileOperation.fileOpenWith(v.getContext(), item.getUri(), item.getMineTypes());

                return false;
            }
        });


    }

    public void recyclerViewSetUp() {

//      recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(imageFastAdapter);
    }

    private void loadImage() {

//        progressBarImage.setVisibility(View.VISIBLE);
//
//        executorService.execute(() -> {
//
//            Uri collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//            String[] projection = {
//
//                    MediaStore.Images.Media._ID,
//                    MediaStore.Images.Media.DISPLAY_NAME,
//                    MediaStore.Images.Media.DATE_ADDED,
//                    MediaStore.Images.Media.SIZE
//
//            };
//
//            Cursor cursor = getContentResolver().query(
//                    collection,
//                    projection,
//                    null,
//                    null,
//                    MediaStore.Images.Media.DATE_ADDED + "  DESC"
//            );
//
//            if (cursor != null) {
//
//                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
//                int nameCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
//                int dataCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);
//                int sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);
//
//                while (cursor.moveToNext()) {
//                    imageCount++;
//                    long id = cursor.getLong(idColumn);
//                    String name = cursor.getString(nameCol);
//                    long dateTaken = cursor.getLong(dataCol) * 1000L;
//                    long sizeInBytes = cursor.getLong(sizeCol);
//
//                    Uri contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
//
////               String imageSize = getFormattedImageSize(this, contentUri);
//                    String imageSize = formatSize(sizeInBytes);
//
//                    String imageDate = DateFormat.format("dd MMM yyyy", new Date(dateTaken)).toString();
//
//                    imageList.add(new ImageFA(contentUri, name, imageSize, imageDate, sizeInBytes, dateTaken));
//
//
//                }
//                cursor.close();
//            }
//
//            mainHandler.post(() -> {
//
//                toolbar.setSubtitle(imageCount + " files");
//                applySavedSorting();
//                imageItemAdapter.set(imageList);
//
//                progressBarImage.setVisibility(View.GONE);
//
//            });
//        });

    }

    private void filterImages(String query) {

//        List<ImageFA> filteredList = new ArrayList<>();
//
//        for (ImageFA imageQuery : imageList) {
//            if (imageQuery.getImageName().toLowerCase().contains(query.toLowerCase())) {
//                filteredList.add(imageQuery);
//            }
//        }
//
//        imageItemAdapter.set(filteredList);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_image, menu);

        try {
            Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
            method.setAccessible(true);
            method.invoke(menu, true);
        } catch (Exception e) {
            Toast.makeText(this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        menu.findItem(R.id.menuImage_search).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_search).color(Color.BLACK).actionBar());
        menu.findItem(R.id.menuImage_SortBy).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_filter_list).color(Color.BLACK).actionBar());
        menu.findItem(R.id.menuImage_ChangeView).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_grid_on).color(Color.BLACK).actionBar());

        MenuItem searchItem = menu.findItem(R.id.menuImage_search);

        SearchView searchView = (SearchView) searchItem.getActionView();

        if (searchView != null) {
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
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.menuImage_SortBy) {
            sorting();
            return true;
        }

//        imageFastAdapter.notifyAdapterDataSetChanged();
        return super.onOptionsItemSelected(item);
    }

    void fullImage() {

//        imageFastAdapter.withOnClickListener((v, adapter, item, position) -> {
//            Intent intent = new Intent(ImageGalleryActivity.this, FullScreenImageActivity.class);
//            intent.putExtra("image", item.getImageUri().toString());
//
//            startActivity(intent);
//            return true;
//
//        });

    }

    void sorting() {

//        BottomSheetDialog dialog = new BottomSheetDialog(this);
//
//        View view = LayoutInflater.from(this).inflate(R.layout.bottomsheet_items, null);
//
//        dialog.setContentView(view);
//
//        RadioGroup btnRadioGroup = view.findViewById(R.id.btnRGImageSorting);
//
//        int savedSortOptionId = sharedPreferences.getInt(KEY_SORT_OPTION, R.id.nameAZ);
//
//        btnRadioGroup.check(savedSortOptionId);
//
//        btnRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putInt(KEY_SORT_OPTION, checkedId);
//            editor.apply();
////          sharedPreferences.edit().putInt(KEY_SORT_OPTION, checkedId).apply();
//
//            if (checkedId == R.id.nameAZ) {
//
//                imageList.sort((name1, name2) -> name1.imageName.compareToIgnoreCase(name2.imageName));
//            } else if (checkedId == R.id.nameZA) {
//                imageList.sort((name1, name2) -> name2.imageName.compareToIgnoreCase(name1.imageName));
//            } else if (checkedId == R.id.rbBtnNDF) {
//                imageList.sort((ndf, odf) -> Long.compare(odf.getPhotoDate(), ndf.getPhotoDate()));
//            } else if (checkedId == R.id.rbBtnODF) {
//                Collections.sort(imageList, (ndf, odf) -> Long.compare(ndf.getPhotoDate(), odf.getPhotoDate()));
//            } else if (checkedId == R.id.rbBtnLargeFirst) {
//                Collections.sort(imageList, (largeSizeFile, smallSizeFile) -> Long.compare(smallSizeFile.getSizeInByte(), largeSizeFile.getSizeInByte()));
//
//            } else if (checkedId == R.id.rbBtnSmallestFirst) {
//                Collections.sort(imageList, (largeSizeFile, smallSizeFile) -> Long.compare(largeSizeFile.getSizeInByte(), smallSizeFile.getSizeInByte()));
//            }
//
//          imageItemAdapter.setNewList(imageList);
//            mainHandler.post(() -> imageItemAdapter.set(imageList));
//            imageFastAdapter.notifyAdapterDataSetChanged();
//            dialog.dismiss();
//
//        });
//
//
//        dialog.show();

    }

    private void applySavedSorting() {

//        int checkedId = sharedPreferences.getInt(KEY_SORT_OPTION, R.id.nameAZ);
//
//        if (checkedId == R.id.nameAZ) {
//            Collections.sort(imageList, (name1, name2) -> name1.imageName.compareToIgnoreCase(name2.imageName));
//        } else if (checkedId == R.id.nameZA) {
//            imageList.sort((name1, name2) -> name2.imageName.compareToIgnoreCase(name1.imageName));
//
//        } else if (checkedId == R.id.rbBtnNDF) {
//            imageList.sort((ndf, odf) -> Long.compare(odf.getPhotoDate(), ndf.getPhotoDate()));
//
//        } else if (checkedId == R.id.rbBtnODF) {
//            Collections.sort(imageList, (ndf, odf) -> Long.compare(ndf.getPhotoDate(), odf.getPhotoDate()));
//        } else if (checkedId == R.id.rbBtnLargeFirst) {
//
//            Collections.sort(imageList, (largeSizeFile, smallSizeFile) -> Long.compare(smallSizeFile.getSizeInByte(), largeSizeFile.getSizeInByte()));
//
//        } else if (checkedId == R.id.rbBtnSmallestFirst) {
//
//            Collections.sort(imageList, (largeSizeFile, smallSizeFile) -> Long.compare(largeSizeFile.getSizeInByte(), smallSizeFile.getSizeInByte()));
//
//        }
//
//        imageFastAdapter.notifyAdapterDataSetChanged();
////        imageItemAdapter.setNewList(imageList);
//        mainHandler.post(() -> imageItemAdapter.set(imageList));

    }


}


