package com.example.filemanager.music;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filemanager.MainActivity;
import com.example.filemanager.R;
import com.example.filemanager.adapter.NameSort;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.select.SelectExtension;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class MusicActivity extends AppCompatActivity {

    private static final String AUDIO_SORTING_PREFs = "audioPref";
    private static final String SORTINGTYPE = "sortingTypes";
    private SharedPreferences sharedPreferences;

    private RecyclerView rvMusic;
    private ItemAdapter<MusicAdapter> itemAdapterMusic;
    private FastAdapter<MusicAdapter> fastAdapterMusic;
    SelectExtension<MusicAdapter> selectExtension;

    private Toolbar toolbarMusic;
    List<MusicAdapter> musicList;
    File file;
    int musicCount = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        toolbarMusic = findViewById(R.id.toolbarMusic);

        rvMusic = findViewById(R.id.recyclerViewMusic);
        itemAdapterMusic = new ItemAdapter<>();
        fastAdapterMusic = FastAdapter.with(itemAdapterMusic);
        fastAdapterMusic.setHasStableIds(true);

//        selectExtension = new SelectExtension<>();
//        fastAdapterMusic.addExtension(selectExtension);

        selectExtension = fastAdapterMusic.getExtension(SelectExtension.class);

        if (selectExtension == null) {
            selectExtension = new SelectExtension<>();
            fastAdapterMusic.addExtension(selectExtension);
        }

        selectExtension.withSelectable(true);
        selectExtension.withMultiSelect(true);
        selectExtension.withSelectWithItemUpdate(true);
//        selectExtension.withSelectOnLongClick(true);

        setSupportActionBar(toolbarMusic);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Music");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        sharedPreferences = getSharedPreferences(AUDIO_SORTING_PREFs, MODE_PRIVATE);

        rvMusic.setLayoutManager(new LinearLayoutManager(this));

        loadAudioFiles();
        saveSorting();

        fastAdapterMusic.withOnClickListener((v, adapter, item, position) -> {

            Uri uri = FileProvider.getUriForFile(MusicActivity.this, getPackageName() + ".provider", file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, getMimeType(file));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, "Open with"));

            return true;

        });

        fastAdapterMusic.withOnLongClickListener((v, adapter, item, position) -> {

            selectExtension.toggleSelection(position);


            return true;
        });

        selectExtension.withSelectionListener((item, selected) -> {

            int totalCount = selectExtension.getSelections().size();

            if (totalCount > 0) {

                toolbarMusic.setTitle(totalCount + " Selected");
                toolbarMusic.setSubtitle(getSelectedFileSize());

            } else {
                toolbarMusic.setTitle("Music");
                toolbarMusic.setSubtitle(musicCount + " Files");
            }


        });

    }

    private String getSelectedFileSize() {
        long sumSize = 0;

        for (Integer selectedItem : selectExtension.getSelections()) {
            MusicAdapter itemSize = itemAdapterMusic.getAdapterItem(selectedItem);
            sumSize += itemSize.getFile().length();
        }
        return calFileSize(sumSize);
    }

    String calFileSize(long sizeInBytes) {

        if (sizeInBytes >= 1024 * 1024 * 1024) {
            return String.format(Locale.getDefault(), "%.2f GB", (float) sizeInBytes / (1024 * 1024 * 1024));
        } else if (sizeInBytes >= 1024 * 1024) {
            return String.format(Locale.getDefault(), "%.2f MB", (float) sizeInBytes / (1024 * 1024));
        } else if (sizeInBytes >= 1024) {
            return String.format(Locale.getDefault(), "%.2f KB", (float) sizeInBytes / 1024);
        } else {
            return sizeInBytes + " B";
        }

    }

    private void loadAudioFiles() {

        musicList = new ArrayList<>();

        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME
        };

        Cursor cursor = getContentResolver().query(
                musicUri,
                projection,
                null,
                null,
                null
        );

        if (cursor != null) {

            while (cursor.moveToNext()) {
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                file = new File(path);

                if (file.exists() && path.endsWith(".mp3")) {
                    musicCount++;
                    musicList.add(new MusicAdapter(file));
                }
            }

            cursor.close();
        }

        toolbarMusic.setSubtitle(musicCount + " File");
        itemAdapterMusic.setNewList(musicList);
        rvMusic.setAdapter(fastAdapterMusic);
    }

    void showBottomSheet(File file) {

        BottomSheetDialog audioBottomSheetDialog = new BottomSheetDialog(this);

        View view = LayoutInflater.from(this).inflate(R.layout.bottomsheet_info_item, null);
        audioBottomSheetDialog.setContentView(view);

        long sizeInBytes = file.length();
        String convertedSize;

        LinearLayoutCompat btnCopy;
        LinearLayoutCompat btnMove;
        LinearLayoutCompat btnSafeBox;
        LinearLayoutCompat btnRename;
        LinearLayoutCompat btnDelete;
        LinearLayoutCompat btnOpenWith;
        LinearLayoutCompat btnShare;

        btnRename = view.findViewById(R.id.btnFileRename);
        btnOpenWith = view.findViewById(R.id.btnFileOpenWith);
        btnShare = view.findViewById(R.id.btnFileShare);
        btnDelete = view.findViewById(R.id.btnFileDelete);

        TextView tvFileName = view.findViewById(R.id.tvRenameFileName);
        TextView tvFileSize = view.findViewById(R.id.tvFileSize);

        tvFileName.setText(file.getName());


        long reSize;


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

        tvFileSize.setText(convertedSize);

        btnRename.setOnClickListener(v -> {
            Toast.makeText(this, "File Rename", Toast.LENGTH_SHORT).show();
        });


        btnOpenWith.setOnClickListener(v -> {

            Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, getMimeType(file));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, "Open with"));

        });

        btnShare.setOnClickListener(v -> {

            Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
            Intent shareIntent = new Intent(Intent.ACTION_VIEW);
            shareIntent.setType(getMimeType(file));
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(shareIntent);

        });


        btnDelete.setOnClickListener(v -> {

            if (file.delete()) {
                Toast.makeText(this, file.getName() + " Delete File", Toast.LENGTH_SHORT).show();
                recreate();
            }


        });

        audioBottomSheetDialog.show();

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
        }


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.menuImage_Home) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.menuImage_SortBy) {
            sortBy();
            return true;
        } else if (id == R.id.menuImage_Closed) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sortBy() {

        BottomSheetDialog sortingSheet = new BottomSheetDialog(this);

        View view = LayoutInflater.from(this).inflate(R.layout.bottomsheet_items, null);
        sortingSheet.setContentView(view);

        RadioGroup radioGroup = view.findViewById(R.id.btnRGImageSorting);

        int saveOption = sharedPreferences.getInt(SORTINGTYPE, R.id.rbBtnNDF);

        radioGroup.check(saveOption);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(SORTINGTYPE, checkedId);
            editor.apply();

            if (checkedId == R.id.rbBtnNDF) {
                Collections.sort(musicList, (newFile, oldFile) -> Long.compare(oldFile.getFile().lastModified(), newFile.getFile().lastModified()));
            } else if (checkedId == R.id.rbBtnODF) {
//                Collections.sort(musicList, Comparator.comparingLong(newFile -> newFile.getFile().lastModified()));
                musicList.sort(Comparator.comparingLong(newFile -> newFile.getFile().lastModified()));
            } else if (checkedId == R.id.rbBtnLargeFirst) {
                Collections.sort(musicList, (largestFile, smallestFile) -> Long.compare(smallestFile.getFile().length(), largestFile.getFile().length()));
            } else if (checkedId == R.id.rbBtnSmallestFirst) {
                Collections.sort(musicList, (largestFile, smallestFile) -> Long.compare(largestFile.getFile().length(), smallestFile.getFile().length()));

            } else if (checkedId == R.id.nameAZ) {
//                Collections.sort(musicList, (name1,name2) -> name1.getFile().getName().compareToIgnoreCase(name2.getFile().getName()));
                musicList.sort((name1, name2) -> name1.getFile().getName().compareToIgnoreCase(name2.getFile().getName()));
            } else if (checkedId == R.id.nameZA) {
//                Collections.sort(musicList, (name1,name2) -> name2.getFile().getName().compareToIgnoreCase(name1.getFile().getName()));
                Collections.sort(musicList, new NameSort());
            }

            itemAdapterMusic.setNewList(musicList);
            fastAdapterMusic.notifyAdapterDataSetChanged();

        });

        sortingSheet.show();
    }

    private void saveSorting() {

        int saveOption = sharedPreferences.getInt(SORTINGTYPE, R.id.rbBtnNDF);

        if (saveOption == R.id.rbBtnNDF) {
            Collections.sort(musicList, (newFile, oldFile) -> Long.compare(oldFile.getFile().lastModified(), newFile.getFile().lastModified()));
        } else if (saveOption == R.id.rbBtnODF) {
            Collections.sort(musicList, Comparator.comparingLong(newFile -> newFile.getFile().lastModified()));
        } else if (saveOption == R.id.rbBtnLargeFirst) {
            Collections.sort(musicList, (largestFile, smallestFile) -> Long.compare(smallestFile.getFile().length(), largestFile.getFile().length()));
        } else if (saveOption == R.id.rbBtnSmallestFirst) {
            Collections.sort(musicList, (largestFile, smallestFile) -> Long.compare(largestFile.getFile().length(), smallestFile.getFile().length()));
        } else if (saveOption == R.id.nameAZ) {
            Collections.sort(musicList, (name1, name2) -> name1.getFile().getName().compareToIgnoreCase(name2.getFile().getName()));
        } else if (saveOption == R.id.nameZA) {
            Collections.sort(musicList, (name1, name2) -> name2.getFile().getName().compareToIgnoreCase(name1.getFile().getName()));
        }

        itemAdapterMusic.setNewList(musicList);
        fastAdapterMusic.notifyAdapterDataSetChanged();


    }


    private String getMimeType(File file) {
        String ext = MimeTypeMap.getFileExtensionFromUrl(file.getAbsolutePath());
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
    }
}



