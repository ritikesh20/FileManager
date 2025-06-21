package com.example.filemanager.videolist;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class VideoActivity extends AppCompatActivity {

    private static String VIDEOSHAREPREFS = "VideoPrefs";
    private static String VIDEOSORTINGBY = "VideoSortingBy";

    SharedPreferences sharedPreferences;

    private RecyclerView recyclerViewVideo;
    private ItemAdapter<FileHelperAdapter> itemAdapterVideo;
    FastAdapter<FileHelperAdapter> fastAdapterVideo;
    List<FileHelperAdapter> videoList = new ArrayList<>();

    private Toolbar toolbarVideo;
    int videoCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        toolbarVideo = findViewById(R.id.toolbarVideo);

        sharedPreferences = getSharedPreferences(VIDEOSHAREPREFS, MODE_PRIVATE);

        setSupportActionBar(toolbarVideo);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Video");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_arrow_back).actionBar()); // to change back arrow icons
        }

        recyclerViewVideo = findViewById(R.id.recyclerViewVideo);
        itemAdapterVideo = new ItemAdapter<>();
        fastAdapterVideo = FastAdapter.with(itemAdapterVideo);

        recyclerViewVideo.setLayoutManager(new LinearLayoutManager(this));

//        uploadVideo();

        recyclerViewVideo.setAdapter(fastAdapterVideo);
        saveSorting();

        MediaStoreHelper.loadFile(this, "video", files -> {

            videoList.clear();
            videoList.addAll(files);
            itemAdapterVideo.setNewList(files);
            fastAdapterVideo.notifyDataSetChanged();

        });

        fastAdapterVideo.withOnClickListener(new OnClickListener<FileHelperAdapter>() {
            @Override
            public boolean onClick(View v, IAdapter<FileHelperAdapter> adapter, FileHelperAdapter item, int position) {

                MediaStoreHelper.fileOpenWith(v.getContext(), item.getUri(), item.getMineTypes());

                return false;
            }
        });
    }

    void uploadVideo() {

//        videoList = new ArrayList<>();
//
//        Uri videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
//
//        String[] projection = {
//                MediaStore.Video.Media._ID,
//                MediaStore.Video.Media.TITLE,
//                MediaStore.Video.Media.DATA,
//                MediaStore.Video.Media.DISPLAY_NAME
//        };
//
//        Cursor cursor = getContentResolver().query(videoUri, projection, null, null, null);
//
//        if (cursor != null) {
//            while (cursor.moveToNext()) {
//                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
//
//                File file = new File(path);
//
//                if (file.exists() && path.endsWith(".mp4")) {
//                    videoCount++;
//                    videoList.add(new MusicAdapter(file));
//                }
//            }
//            cursor.close();
//        }
//
//        itemAdapterVideo.setNewList(videoList);
//        getSupportActionBar().setSubtitle(videoCount + " Video");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_image, menu);

        menu.findItem(R.id.menuImage_search).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_search).color(Color.BLACK).actionBar());
        menu.findItem(R.id.menuImage_SortBy).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_filter_list).color(Color.BLACK).actionBar());
        menu.findItem(R.id.menuImage_ChangeView).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_grid_on).color(Color.BLACK).actionBar());
        menu.findItem(R.id.menuImage_Home).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_home).color(Color.BLACK).actionBar());
        menu.findItem(R.id.menuImage_SetHome).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_home).color(Color.BLACK).actionBar());
        menu.findItem(R.id.menuImage_AdvanceSetting).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_settings).color(Color.BLACK).actionBar());
        menu.findItem(R.id.menuImage_Closed).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_close).color(Color.BLACK).actionBar());

        try {
            Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
            method.setAccessible(true);
            method.invoke(menu, true);
        } catch (Exception e) {
            Toast.makeText(this, "error  " + e.getMessage(), Toast.LENGTH_SHORT).show();
            throw new RuntimeException(e);
        }


        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.menuImage_SortBy) {
            sortBy();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void sortBy() {

//        BottomSheetDialog sortingSheet = new BottomSheetDialog(this);
//
//        View view = LayoutInflater.from(this).inflate(R.layout.bottomsheet_items, null);
//        sortingSheet.setContentView(view);
//
//        RadioGroup radioGroup = view.findViewById(R.id.btnRGImageSorting);
//
//        int saveOption = sharedPreferences.getInt(VIDEOSORTINGBY, R.id.rbBtnNDF);
//
//        radioGroup.check(saveOption);
//
//        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
//
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putInt(VIDEOSORTINGBY , checkedId);
//            editor.apply();
//
//            if (checkedId == R.id.rbBtnNDF) {
//                Collections.sort(videoList,(newFile, oldFile) -> Long.compare(oldFile.getFile().lastModified(), newFile.getFile().lastModified()));
//            }
//            else if (checkedId == R.id.rbBtnODF) {
//                Collections.sort(videoList, Comparator.comparingLong(newFile -> newFile.getFile().lastModified()));
//            }
//            else if (checkedId == R.id.rbBtnLargeFirst) {
//                Collections.sort(videoList, (largestFile, smallestFile) -> Long.compare(smallestFile.getFile().length(), largestFile.getFile().length()));
//            }
//            else if (checkedId == R.id.rbBtnSmallestFirst) {
//                Collections.sort(videoList, (largestFile, smallestFile) -> Long.compare(largestFile.getFile().length(), smallestFile.getFile().length()));
//            }
//            else if (checkedId == R.id.nameAZ) {
//                Collections.sort(videoList, (name1,name2) -> name1.getFile().getName().compareToIgnoreCase(name2.getFile().getName()));
//            }
//            else if (checkedId == R.id.nameZA) {
//                Collections.sort(videoList, (name1,name2) -> name2.getFile().getName().compareToIgnoreCase(name1.getFile().getName()));
//            }
//
//            itemAdapterVideo.setNewList(videoList);
//            fastAdapterVideo.notifyAdapterDataSetChanged();
//            sortingSheet.dismiss();
//        });
//
//        sortingSheet.show();
    }

    private void saveSorting() {

//        int saveOption = sharedPreferences.getInt(VIDEOSORTINGBY,R.id.rbBtnNDF);
//
//        if (saveOption == R.id.rbBtnNDF) {
//            Collections.sort(videoList,(newFile, oldFile) -> Long.compare(oldFile.getFile().lastModified(), newFile.getFile().lastModified()));
//        }
//        else if (saveOption == R.id.rbBtnODF) {
//            Collections.sort(videoList, Comparator.comparingLong(newFile -> newFile.getFile().lastModified()));
//        }
//        else if (saveOption == R.id.rbBtnLargeFirst) {
//            Collections.sort(videoList, (largestFile, smallestFile) -> Long.compare(smallestFile.getFile().length(), largestFile.getFile().length()));
//        }
//        else if (saveOption == R.id.rbBtnSmallestFirst) {
//            Collections.sort(videoList, (largestFile, smallestFile) -> Long.compare(largestFile.getFile().length(), smallestFile.getFile().length()));
//        }
//        else if (saveOption == R.id.nameAZ) {
//            Collections.sort(videoList, (name1,name2) -> name1.getFile().getName().compareToIgnoreCase(name2.getFile().getName()));
//        }
//        else if (saveOption == R.id.nameZA) {
//            Collections.sort(videoList, (name1,name2) -> name2.getFile().getName().compareToIgnoreCase(name1.getFile().getName()));
//        }
//
//        itemAdapterVideo.setNewList(videoList);
//        fastAdapterVideo.notifyAdapterDataSetChanged();
    }
}