package com.example.filemanager.music;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filemanager.MediaStoreHelper;
import com.example.filemanager.R;
import com.example.filemanager.internalstorage.ClipboardHelper;
import com.example.filemanager.internalstorage.InternalStorageActivity;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.OnLongClickListener;
import com.mikepenz.fastadapter.select.SelectExtension;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MusicActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMusic;

    private ItemAdapter<MusicAdapter> itemAdapterMusic;
    private FastAdapter<MusicAdapter> fastAdapterMusic;

    List<MusicAdapter> musicList = new ArrayList<>();

    SelectExtension<MusicAdapter> selectExtension;

    Button btnMusicCopy;
    Button btnMusicMove;
    Button btnMusicPasting;


    private List<String> selectedFile;
    private boolean isCutMode = false;
    private boolean isAllFileSelected = false;

    private File destination = InternalStorageActivity.currentDirectory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        Toolbar toolbarMusic = findViewById(R.id.toolbarMusic);

        recyclerViewMusic = findViewById(R.id.recyclerViewMusic);
        itemAdapterMusic = new ItemAdapter<>();
        fastAdapterMusic = FastAdapter.with(itemAdapterMusic);
        recyclerViewMusic.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMusic.setAdapter(fastAdapterMusic);
        setSupportActionBar(toolbarMusic);

        btnMusicCopy = findViewById(R.id.btnMusicCopy);
        btnMusicMove = findViewById(R.id.btnMusicMove);
        btnMusicPasting = findViewById(R.id.btnMusicPast);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Music");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        selectExtension = fastAdapterMusic.getExtension(SelectExtension.class);

        if (selectExtension == null) {
            selectExtension = new SelectExtension<>();
            fastAdapterMusic.addExtension(selectExtension);
        }

        selectExtension.withSelectable(true);
        selectExtension.withMultiSelect(true);
        selectExtension.withSelectWithItemUpdate(true);

        fastAdapterMusic.withOnLongClickListener(new OnLongClickListener<MusicAdapter>() {
            @Override
            public boolean onLongClick(View v, IAdapter<MusicAdapter> adapter, MusicAdapter item, int position) {

                selectExtension.toggleSelection(position);

                return false;
            }
        });


        btnMusicCopy.setOnClickListener(v -> {
            isCutMode = false;
            fileSelected();
            MediaStoreHelper.goStorageTypes(this, "Move to");
            btnMusicPasting.setVisibility(View.VISIBLE);
        });

        btnMusicMove.setOnClickListener(v -> {
            isCutMode = true;
            fileSelected();
            MediaStoreHelper.goStorageTypes(this, "Copy to");
            btnMusicPasting.setVisibility(View.VISIBLE);
        });

        isPastDeselect();
//        uploadMusic();
        loadingMusic();

    }

    void loadingMusic() {
        MediaStoreHelper.loadFile(this, "audio", files -> {
            Toast.makeText(this, "Music Loading Successfully", Toast.LENGTH_SHORT).show();
        });
    }

    void uploadMusic() {

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                musicList.clear();

                Uri videoUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

                String[] projection = {
                        MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.DISPLAY_NAME
                };

                Cursor cursor = getContentResolver().query(videoUri, projection, null, null, null);

                if (cursor != null) {

                    while (cursor.moveToNext()) {

                        String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));

                        File file = new File(path);

                        if (file.exists() && path.endsWith(".mp3")) {
                            musicList.add(new MusicAdapter(file));
                        }

                    }
                    cursor.close();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        itemAdapterMusic.setNewList(musicList);
                    }
                });
            }

        });


    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        int selectCount = selectExtension.getSelections().size();

        MenuItem searching = menu.findItem(R.id.file_search);
        MenuItem isListGridView = menu.findItem(R.id.file_ChangeView);
        MenuItem selectAll = menu.findItem(R.id.file_Selected);
        MenuItem sortingType = menu.findItem(R.id.file_SortBy);
        MenuItem openWith = menu.findItem(R.id.file_OpenWith);
        MenuItem share = menu.findItem(R.id.file_Share);
        MenuItem delete = menu.findItem(R.id.file_Delete);
        MenuItem moveTo = menu.findItem(R.id.file_Move);
        MenuItem copyTo = menu.findItem(R.id.file_Copy);
        MenuItem rename = menu.findItem(R.id.file_Rename);
        MenuItem addToFav = menu.findItem(R.id.file_AddToStarred);
        MenuItem moveSafeFolder = menu.findItem(R.id.file_SafeFolder);
        MenuItem googleDrive = menu.findItem(R.id.file_BackToGoogleDrive);

        MenuItem fileInfo = menu.findItem(R.id.file_FileInfo);

        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setVisible(false);
        }

        if (selectCount == 0) {
            selectAll.setTitle("Select All");
            selectAll.setVisible(true);
            sortingType.setVisible(true);
            searching.setVisible(true);
            isListGridView.setVisible(true);
        } else if (selectCount == 1) {
            searching.setVisible(false);
            isListGridView.setVisible(false);
            share.setVisible(true);
            delete.setVisible(true);
            selectAll.setVisible(true);
            openWith.setVisible(true);
            moveTo.setVisible(true);
            copyTo.setVisible(true);
            selectAll.setTitle("Select All");
            rename.setVisible(true);
            addToFav.setVisible(true);
            moveSafeFolder.setVisible(true);
            googleDrive.setVisible(true);
            fileInfo.setVisible(true);

        } else {

            selectAll.setTitle("Deselect All");
            share.setVisible(true);
            delete.setVisible(true);
            searching.setVisible(false);
            isListGridView.setVisible(false);
            selectAll.setVisible(true);
            moveTo.setVisible(true);
            copyTo.setVisible(true);
            addToFav.setVisible(true);
            moveSafeFolder.setVisible(true);
            googleDrive.setVisible(true);
        }


        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.all_type, menu);

        menu.findItem(R.id.file_search).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_search).color(Color.BLACK).actionBar());
        menu.findItem(R.id.file_ChangeView).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_grid_on).color(Color.BLACK).actionBar());
        menu.findItem(R.id.file_Share).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_share).color(Color.BLACK).actionBar());
        menu.findItem(R.id.file_Delete).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_delete).color(Color.BLACK).actionBar());


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
        } else if (id == R.id.file_search) {

        } else if (id == R.id.file_ChangeView) {

        } else if (id == R.id.file_Selected) {
            if (!isAllFileSelected) {
                selectExtension.select();
                Toast.makeText(MusicActivity.this, "All file Selected", Toast.LENGTH_SHORT).show();
            } else {
                selectExtension.deselect();
                Toast.makeText(MusicActivity.this, "All file deSelected", Toast.LENGTH_SHORT).show();
            }

            isAllFileSelected = !isAllFileSelected;

            return true;
        } else if (id == R.id.file_Share) {

        } else if (id == R.id.file_Delete) {

        } else if (id == R.id.file_SortBy) {

            return true;
        } else if (id == R.id.file_OpenWith) {

            return true;
        } else if (id == R.id.file_Move) {
        } else if (id == R.id.file_Copy) {

        } else if (id == R.id.file_Rename) {

            return true;
        } else if (id == R.id.file_AddToStarred) {

        } else if (id == R.id.file_SafeFolder) {

        } else if (id == R.id.file_BackToGoogleDrive) {

        } else if (id == R.id.file_FileInfo) {

        }

        return super.onOptionsItemSelected(item);
    }

    private void fileSelected() {

        selectedFile = new ArrayList<>();

        for (MusicAdapter select : selectExtension.getSelectedItems()) {
            selectedFile.add(select.getFile().getAbsolutePath());
        }

        if (isCutMode) {
            ClipboardHelper.cut(selectedFile);
        } else {
            ClipboardHelper.copy(selectedFile);
        }

        btnMusicPasting.setVisibility(View.VISIBLE);

    }

    private void pasteFiles(File destinationDir) {

        if (ClipboardHelper.isEmpty()) {
            Toast.makeText(this, "No files selected", Toast.LENGTH_SHORT).show();
            return;
        }

        for (String path : ClipboardHelper.getFilePaths()) {
            File source = new File(path); // copy file path
            File dest = new File(destinationDir, source.getName()); // past copy file folder location

            if (dest.exists()) {
                dest = getNonConflictingFile(dest);
            }

            boolean success = false;

            if (ClipboardHelper.isCut()) { // isCut check krha rha hai file copy hai ya fir past hai
                success = source.renameTo(dest); //renameTo method ka kaam hota hai file ya folder ko ek location se dusare location pe move karna ya rename karna.
                if (!success) {
                    success = moveFileManually(source, dest);
                }
            } else {
                if (source.isDirectory()) {
                    try {
                        copyDirectory(source, dest); // recursive call on folder
                        success = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    success = copyFile(source, dest);
                }
            }

            if (!success) {
                Toast.makeText(this, "Failed: " + source.getName(), Toast.LENGTH_SHORT).show();
            }

        }

        ClipboardHelper.clear();
        itemAdapterMusic.setNewList(new ArrayList<>());
        recreate();

    }

    private File getNonConflictingFile(File file) {

        if (!file.exists()) return file;

        String name = file.getName();
        String baseName;
        String extension = "";

        int dotIndex = name.lastIndexOf('.');

        if (dotIndex != -1 && file.isFile()) {
            baseName = name.substring(0, dotIndex);
            extension = name.substring(dotIndex);
        } else {
            baseName = name;
        }

        int index = 1;
        File newFile;
        do {
            String newName = baseName + "(" + index + ")" + extension;
            newFile = new File(file.getParent(), newName);
            index++;
        } while (newFile.exists());

        return newFile;

    }

    private boolean copyFile(File source, File dest) {

        try {
            FileInputStream pickFileLocation = new FileInputStream(source);
            FileOutputStream dropFileLocation = new FileOutputStream(dest);

            byte[] buffer = new byte[1024]; // temporary store file

            int len;

            while ((len = pickFileLocation.read(buffer)) > 0) {
                dropFileLocation.write(buffer, 0, len);
            }

            pickFileLocation.close();
            dropFileLocation.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean moveFileManually(File source, File dest) {
        boolean copied = copyFile(source, dest);
        if (copied) {
            return source.delete();
        }
        return false;
    }

    private void copyDirectory(File source, File destination) throws IOException {

        if (!destination.exists()) {
            destination.mkdirs();
        }

        File[] files = source.listFiles();

        if (files != null) {

            for (File file : files) {
                File newDest = new File(destination, file.getName());
                if (newDest.exists()) {
                    newDest = getNonConflictingFile(newDest);
                }

                if (file.isDirectory()) {
                    copyDirectory(file, newDest);
                } else {
                    Files.copy(file.toPath(), newDest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }

    void isPastDeselect() {

        if (ClipboardHelper.isIsPasting()) {

            btnMusicPasting.setVisibility(View.VISIBLE);

            btnMusicPasting.setOnClickListener(v -> {
                ClipboardHelper.setIsPasting(false);
//                pasteFiles(currentDirectory);
                pasteFiles(destination);
                btnMusicPasting.setVisibility(View.GONE);

            });

        }
    }


    private void shareSelectedFiles() {

        if (selectedFile == null || selectedFile.isEmpty()) {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<Uri> uriList = new ArrayList<>();

        for (String path : selectedFile) {
            File file = new File(path);
            Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
            uriList.add(uri);
        }

        Intent shareIntent = new Intent();
        if (uriList.size() == 1) {
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, uriList.get(0));
            shareIntent.setType(getMimeType(uriList.get(0)));
        } else {
            shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList);
            shareIntent.setType("*/*"); // or a common mime type
        }

        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share File(s) via"));
    }

    private String getMimeType(Uri uri) {
        ContentResolver cr = getContentResolver();
        return cr.getType(uri);
    }


    void loadUtils() {
        //        MediaStoreHelper.loadFile(this, "audio", files -> {
//            musicList.clear();
//            musicList.addAll(files);
//            itemAdapterMusic.setNewList(musicList);
//            fastAdapterMusic.notifyDataSetChanged();
//
//        });
//                MediaStoreHelper.fileOpenWith(v.getContext(), item.getUri(), item.getMineTypes());
    }
}



