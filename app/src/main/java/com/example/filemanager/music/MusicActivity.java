package com.example.filemanager.music;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filemanager.FileHelperAdapter;
import com.example.filemanager.FileOperation;
import com.example.filemanager.MediaStoreHelper;
import com.example.filemanager.R;
import com.example.filemanager.SortingHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.mikepenz.fastadapter.select.SelectExtension;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MusicActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMusic;
    public static boolean isMusicView = false;
    FileHelperAdapter item;
    private SharedPreferences sharedPreferencesMusic;
    private ItemAdapter<FileHelperAdapter> itemAdapterMusic;
    private FastAdapter<FileHelperAdapter> fastAdapterMusic;
    List<FileHelperAdapter> musicList = new ArrayList<>();

    SelectExtension<FileHelperAdapter> selectExtension;

    Button btnMusicCopy;
    Button btnMusicMove;
    Button btnMusicPasting;

    Toolbar toolbarMusic;

    private List<String> selectedFile;
    private boolean isAllFileSelected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        toolbarMusic = findViewById(R.id.toolbarMusic);

        recyclerViewMusic = findViewById(R.id.recyclerViewMusic);
        itemAdapterMusic = new ItemAdapter<>();
        fastAdapterMusic = FastAdapter.with(itemAdapterMusic);

        recyclerViewMusic.setAdapter(fastAdapterMusic);
        sharedPreferencesMusic = getSharedPreferences("MusicPref", MODE_PRIVATE);
        setSupportActionBar(toolbarMusic);

        btnMusicCopy = findViewById(R.id.btnMusicCopy);
        btnMusicMove = findViewById(R.id.btnMusicMove);
        btnMusicPasting = findViewById(R.id.btnMusicPast);
        layout();

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

        MediaStoreHelper.loadFile(this, "Audio", files -> {
            musicList.clear();
            musicList.addAll(files);
            itemAdapterMusic.setNewList(files);
            fastAdapterMusic.notifyDataSetChanged();
        });


        click();

        fileLongClick();

        clickBtnFileInfo();
    }

    private void layout() {
        if (isMusicView) {
            recyclerViewMusic.setLayoutManager(new GridLayoutManager(this, 3));
        } else {
            recyclerViewMusic.setLayoutManager(new LinearLayoutManager(this));
        }
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
//                            musicList.add(new MusicAdapter(file));
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

    void click() {
        fastAdapterMusic.withOnClickListener(new OnClickListener<FileHelperAdapter>() {
            @Override
            public boolean onClick(View v, IAdapter<FileHelperAdapter> adapter, FileHelperAdapter item, int position) {

                FileOperation.fileOpenWith(MusicActivity.this, item.getUri(), item.getMineTypes());
                return false;
            }
        });
    }

    void clickBtnFileInfo() {
        fastAdapterMusic.withEventHook(new ClickEventHook<FileHelperAdapter>() {
            @Override
            public void onClick(@NonNull View v, int position, @NonNull FastAdapter<FileHelperAdapter> fastAdapter, @NonNull FileHelperAdapter item) {

                selectExtension.select(position);

                showBtnFileInfo(v, item, position);
            }

            @Override
            public View onBind(@NonNull RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof FileHelperAdapter.ViewHolder) {
                    return ((FileHelperAdapter.ViewHolder) viewHolder).btnFileInfo;
                }
                return super.onBind(viewHolder);
            }
        });
    }

    void fileLongClick() {

        fastAdapterMusic.withOnLongClickListener((v, adapter, item, position) -> {

            selectExtension.toggleSelection(position);

            return true;

        });

        selectExtension.withSelectionListener((item, selected) -> {

            int count = selectExtension.getSelections().size();

            if (count > 0) {
                toolbarMusic.setTitle(count + " selected");
                toolbarMusic.setSubtitle(getSelectedFileSize());
                toolbarMusic.setSubtitle(getSelectedFileSize());
            } else {
                toolbarMusic.setTitle("Music");
                toolbarMusic.setSubtitle("");
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
            openSearching();
        } else if (id == R.id.file_ChangeView) {
            isMusicView = !isMusicView;
            layout();
//            fastAdapterMusic.notifyDataSetChanged();
            recreate();
            return true;
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
            FileOperation.shareSelectedFiles(this, itemAdapterMusic, selectExtension);
            return true;
        } else if (id == R.id.file_Delete) {
            FileOperation.deleteSelectedFiles(this, itemAdapterMusic, selectExtension);
            return true;
        } else if (id == R.id.file_SortBy) {
            newSorting();
            return true;
        } else if (id == R.id.file_OpenWith) {
            openSelectedFile();
            return true;
        } else if (id == R.id.file_Rename) {
            showRenameInputDialog();
            return true;
        } else if (id == R.id.file_AddToStarred) {

            return true;
        } else if (id == R.id.file_SafeFolder) {

        } else if (id == R.id.file_BackToGoogleDrive) {
            addToDrive();
        } else if (id == R.id.file_FileInfo) {

        }

        return super.onOptionsItemSelected(item);
    }

    private void showBtnFileInfo(View anchorView, FileHelperAdapter item, int position) {

        PopupMenu popupMenu = new PopupMenu(MusicActivity.this, anchorView);
        popupMenu.getMenuInflater().inflate(R.menu.selected_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(menuItem -> {

            int id = menuItem.getItemId();

            if (id == R.id.selOneFile) {
                boolean isOneFileSelected = false;
                if (!isOneFileSelected) {
                    selectExtension.select(position);
                } else {
                    selectExtension.deselect(position);
                }
                return true;
            } else if (id == R.id.selOpenWith) {
                openSelectedFile();
                selectExtension.deselect(position);
                return true;
            } else if (id == R.id.selShare) {
                FileOperation.shareSelectedFiles(this, itemAdapterMusic, selectExtension);
                return true;
            } else if (id == R.id.selRename) {
                showRenameInputDialog();
                selectExtension.deselect(position);
                return true;
            } else if (id == R.id.selDelete) {
                FileOperation.deleteSelectedFiles(this, itemAdapterMusic, selectExtension);
                selectExtension.deselect(position);
                return true;
            } else if (id == R.id.selAddToStarred) {
                selectExtension.deselect(position);
                return true;
            } else if (id == R.id.selBackToGoogleDrive) {
                selectExtension.deselect(position);
                return true;
            } else if (id == R.id.selFileInfo) {

                return true;
            }
            return true;
        });
        popupMenu.show();
    }


    private void showRenameInputDialog() {

        for (Integer index : selectExtension.getSelections()) {
            item = itemAdapterMusic.getAdapterItem(index);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = LayoutInflater.from(this).inflate(R.layout.rename_item, null);
        builder.setView(view);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        TextInputEditText etName = view.findViewById(R.id.etRename);

        Button btnOkRename = view.findViewById(R.id.btnOkRename);
        Button btnCancelRename = view.findViewById(R.id.btnCancelRename);

        etName.setText(item.getName());

        btnOkRename.setOnClickListener(v -> {

            String newFileName = etName.getText().toString().trim();

            if (!newFileName.isEmpty()) {
                Uri fileUri = item.getUri();
                boolean renameSuccess = FileOperation.showRenameFileDialog(this, fileUri, newFileName);
                fastAdapterMusic.notifyDataSetChanged();
                Toast.makeText(this, renameSuccess ? "File renamed successfully" : "Failed to rename file", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "File name cannot be empty", Toast.LENGTH_SHORT).show();
            }
            alertDialog.dismiss();
        });

        btnCancelRename.setOnClickListener(v -> {
            alertDialog.dismiss();
        });

//        int index = selectExtension.getSelections().iterator().next();
//        FileHelperAdapter item = itemAdapterVideo.getAdapterItem(index);


    }

    void newSorting() {
        SortingHelper.sortingBy(this,
                musicList,
                fastAdapterMusic,
                itemAdapterMusic,
                sharedPreferencesMusic,
                "MUSICSORTINGBY",
                sortedList -> {
                    Toast.makeText(MusicActivity.this, "List sorted", Toast.LENGTH_SHORT).show();
                }
        );
    }

    void openSearching() {
        FileOperation.searchingIntent(MusicActivity.this);
    }

    private void addToDrive() {
        FileOperation.shareFilesToGoogleDrive(this, itemAdapterMusic, selectExtension);
    }

    private void openSelectedFile() {

        Set<Integer> selectedIndices = selectExtension.getSelections();

        if (selectedIndices.isEmpty()) {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            return;
        }

        int index = selectedIndices.iterator().next();
        FileHelperAdapter selectedItem = itemAdapterMusic.getAdapterItem(index);

        if (selectedItem != null) {
            FileOperation.fileOpenWith(this, selectedItem.getUri(), selectedItem.getMineTypes());
        } else {
            Toast.makeText(this, "Failed to open file", Toast.LENGTH_SHORT).show();
        }

    }

    public String getSelectedFileSize() {

        long fileSizeSum = 0;

        for (Integer selectedItem : selectExtension.getSelections()) {
            FileHelperAdapter fileSize = itemAdapterMusic.getAdapterItem(selectedItem);
            fileSizeSum += parseSizeToBytes(fileSize.getSize());
        }

        return FileOperation.sizeCal(fileSizeSum);
    }

    public long parseSizeToBytes(String sizeStr) {

        sizeStr = sizeStr.trim().toUpperCase();

        try {
            if (sizeStr.endsWith("KB")) {
                return (long) (Double.parseDouble(sizeStr.replace("KB", "").trim()) * 1024);
            } else if (sizeStr.endsWith("MB")) {
                return (long) (Double.parseDouble(sizeStr.replace("MB", "").trim()) * 1024 * 1024);
            } else if (sizeStr.endsWith("GB")) {
                return (long) (Double.parseDouble(sizeStr.replace("GB", "").trim()) * 1024 * 1024 * 1024);
            } else if (sizeStr.endsWith("B")) {
                return Long.parseLong(sizeStr.replace("B", "").trim());
            } else {
                return Long.parseLong(sizeStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }





}



