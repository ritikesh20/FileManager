package com.example.filemanager.videolist;


import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.mikepenz.fastadapter.ISelectionListener;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.mikepenz.fastadapter.select.SelectExtension;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class VideoActivity extends AppCompatActivity {

    FileHelperAdapter item;
    private SharedPreferences sharedPreferencesVideo;
    String KEY_SORT_OPTION_VIDEO = "sort_option";
    public static boolean isVideoView = false;
    private Toolbar toolbarVideo;
    private RecyclerView recyclerViewVideo;
    private ItemAdapter<FileHelperAdapter> itemAdapterVideo;
    private FastAdapter<FileHelperAdapter> fastAdapterVideo;
    private List<FileHelperAdapter> videoList = new ArrayList<>();
    private SelectExtension<FileHelperAdapter> selectExtension;

    Handler mainHandler = new Handler(Looper.getMainLooper());

    private boolean isAllFileSelected = false;

    private final List<FileHelperAdapter> selectedFiles = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video);

        toolbarVideo = findViewById(R.id.toolbarVideo);
        sharedPreferencesVideo = getSharedPreferences("MyViewPrefs", MODE_PRIVATE);

        setSupportActionBar(toolbarVideo);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Video");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_arrow_back).actionBar());
        }

        recyclerViewVideo = findViewById(R.id.recyclerViewVideo);
        itemAdapterVideo = new ItemAdapter<>();
        fastAdapterVideo = FastAdapter.with(itemAdapterVideo);


        selectExtension = fastAdapterVideo.getExtension(SelectExtension.class);

        if (selectExtension == null) {
            selectExtension = new SelectExtension<>();
            fastAdapterVideo.addExtension(selectExtension);
        }

        selectExtension.withSelectable(true);
        selectExtension.withMultiSelect(true);
        selectExtension.withSelectWithItemUpdate(true);
        layout();

        // loading video
        MediaStoreHelper.loadFile(this, "video", files -> {
            videoList.clear();
            videoList.addAll(files);
            sortingPref();
//            itemAdapterVideo.setNewList(files);
            fastAdapterVideo.notifyDataSetChanged();

        });

        fileLongClick();

        fastAdapterVideo.withEventHook(new ClickEventHook<FileHelperAdapter>() {
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

        fastAdapterVideo.withOnClickListener((v, adapter, item, position) -> {
            FileOperation.fileOpenWith(VideoActivity.this, item.getUri(), item.getMineTypes());
            return false;
        });

    }

    void layout() {
        if (isVideoView) {
            recyclerViewVideo.setLayoutManager(new GridLayoutManager(this, 3));
        } else {
            recyclerViewVideo.setLayoutManager(new LinearLayoutManager(this));
        }
        recyclerViewVideo.setAdapter(fastAdapterVideo);
    }


    private void selectedFiles() {

        selectedFiles.clear();

        for (int index : selectExtension.getSelections()) {
            FileHelperAdapter item = itemAdapterVideo.getAdapterItem(index);
            if (item != null) {
                selectedFiles.add(item);
            }
        }

    }

    private void newSorting() {
        SortingHelper.sortingBy(this,
                videoList,
                fastAdapterVideo,
                itemAdapterVideo,
                sharedPreferencesVideo,
                KEY_SORT_OPTION_VIDEO,
                sortedList -> {
                    Toast.makeText(VideoActivity.this, "List sorted", Toast.LENGTH_SHORT).show();
                }
        );
    }

    private void sortingPref() {
        SortingHelper.applySorting(this, videoList, fastAdapterVideo, itemAdapterVideo, sharedPreferencesVideo, KEY_SORT_OPTION_VIDEO);
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
            return true;
        } else if (id == R.id.file_ChangeView) {
            isVideoView = !isVideoView;
            layout();
//            fastAdapterVideo.notifyDataSetChanged();
            recreate();
            return true;
        } else if (id == R.id.file_Selected) {
            if (!isAllFileSelected) {
                selectExtension.select();
                Toast.makeText(VideoActivity.this, "All file Selected", Toast.LENGTH_SHORT).show();
            } else {
                selectExtension.deselect();
                Toast.makeText(VideoActivity.this, "All file deSelected", Toast.LENGTH_SHORT).show();
            }
            isAllFileSelected = !isAllFileSelected;

            return true;
        } else if (id == R.id.file_Share) {
            FileOperation.shareSelectedFiles(this, itemAdapterVideo, selectExtension);
        } else if (id == R.id.file_Delete) {
            FileOperation.deleteSelectedFiles(this, itemAdapterVideo, selectExtension);
        } else if (id == R.id.file_SortBy) {
            newSorting();
            return true;
        } else if (id == R.id.file_OpenWith) {
            selectExtension.select();
            openSelectedFile();
            return true;
        } else if (id == R.id.file_Rename) {
            selectedFiles();
            showRenameInputDialog();
            return true;
        } else if (id == R.id.file_AddToStarred) {
            return true;
        } else if (id == R.id.file_SafeFolder) {
            return true;
        } else if (id == R.id.file_BackToGoogleDrive) {
            addToDrive();
            return true;
        } else if (id == R.id.file_FileInfo) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showBtnFileInfo(View anchorView, FileHelperAdapter item, int position) {

        PopupMenu popupMenu = new PopupMenu(VideoActivity.this, anchorView);
        popupMenu.getMenuInflater().inflate(R.menu.selected_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(menuItem -> {

            int id = menuItem.getItemId();

            if (id == R.id.selOneFile) {
                boolean isOneFileSelected = false;
                if (!isOneFileSelected) {
                    selectExtension.toggleSelection(position);
                } else {
                    selectExtension.deselect(position);
                }
                return true;
            } else if (id == R.id.selOpenWith) {
                openSelectedFile();
                selectExtension.deselect(position);
            } else if (id == R.id.selShare) {
                FileOperation.shareSelectedFiles(this, itemAdapterVideo, selectExtension);
            } else if (id == R.id.selRename) {
                showRenameInputDialog();
                selectExtension.deselect(position);
            } else if (id == R.id.selDelete) {
                FileOperation.deleteSelectedFiles(this, itemAdapterVideo, selectExtension);
                selectExtension.deselect(position);
            } else if (id == R.id.selAddToStarred) {
                selectExtension.deselect(position);
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

    void openSearching() {
        FileOperation.searchingIntent(VideoActivity.this);
    }

    private void showRenameInputDialog() {

        for (Integer index : selectExtension.getSelections()) {
            item = itemAdapterVideo.getAdapterItem(index);
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
                fastAdapterVideo.notifyDataSetChanged();
                Toast.makeText(this, renameSuccess ? "File renamed successfully" : "Failed to rename file", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "File name cannot be empty", Toast.LENGTH_SHORT).show();
            }
            alertDialog.dismiss();
        });

        btnCancelRename.setOnClickListener(v -> {
            alertDialog.dismiss();
        });

//            if (!newName.isEmpty()) {
//                Uri fileUri = item.getUri();
//                boolean success = FileOperation.showRenameFileDialog(this, fileUri, newName);
//                recreate();
//                Toast.makeText(this, success ? "File renamed successfully" : "Failed to rename file", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, "File name cannot be empty", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

//        final EditText input = new EditText(this);
//        input.setHint("Enter new file name");
//        input.setText(item.getName());
//        builder.setView(input);
//
//        builder.setPositiveButton("Rename", (dialog, which) -> {
//            String newName = input.getText().toString().trim();
//
        //        int index = selectExtension.getSelections().iterator().next();
//        FileHelperAdapter item = itemAdapterVideo.getAdapterItem(index);

//        List<Integer> indexOfFile = new ArrayList<>(selectExtension.getSelections());
//        int indexx = indexOfFile.get(0);
//        FileHelperAdapter itemx = itemAdapterVideo.getAdapterItem(indexx);

    }


    void fileLongClick() {

        fastAdapterVideo.withOnLongClickListener((v, adapter, item, position) -> {

            selectExtension.toggleSelection(position);

            selectExtension.withSelectionListener(new ISelectionListener<FileHelperAdapter>() {
                @Override
                public void onSelectionChanged(FileHelperAdapter item, boolean selected) {

                    int count = selectExtension.getSelections().size();

                    if (count > 0) {
                        toolbarVideo.setTitle(count + " selected");
                        toolbarVideo.setSubtitle(FileOperation.getSelectedFileSize(selectExtension, itemAdapterVideo));
                    } else {
                        toolbarVideo.setTitle("Video");
                        toolbarVideo.setSubtitle("");
                    }

                }
            });


            // cal add file size also
            return true;
        });
    }


    private void addToDrive() {
        FileOperation.shareFilesToGoogleDrive(this, itemAdapterVideo, selectExtension);
    }

        private void applySavedSorting() {

        int checkedId = sharedPreferencesVideo.getInt(KEY_SORT_OPTION_VIDEO, R.id.nameAZ);

        if (checkedId == R.id.rbBtnNDF) {
            Collections.sort(videoList, (nfd, ofd) -> SortingHelper.dateConvertor(nfd.getDocDate()).compareTo(SortingHelper.dateConvertor(ofd.getDocDate())));
        } else if (checkedId == R.id.rbBtnODF) {
            Collections.sort(videoList, (nfd, ofd) -> SortingHelper.dateConvertor(ofd.getDocDate()).compareTo(SortingHelper.dateConvertor(nfd.getDocDate())));
        } else if (checkedId == R.id.rbBtnLargeFirst) {
            Collections.sort(videoList, (lff, sff) -> Long.compare(FileOperation.convertFileSizeStringToLong(lff.getSize()), FileOperation.convertFileSizeStringToLong(sff.getSize())));
        } else if (checkedId == R.id.rbBtnSmallestFirst) {
            Collections.sort(videoList, (lff, sff) -> Long.compare(FileOperation.convertFileSizeStringToLong(sff.getSize()), FileOperation.convertFileSizeStringToLong(lff.getSize())));
        } else if (checkedId == R.id.nameAZ) {
            Collections.sort(videoList, (name1, name2) -> name1.getName().compareTo(name2.getName()));
        } else if (checkedId == R.id.nameZA) {
            Collections.sort(videoList, (name1, name2) -> name2.getName().compareTo(name1.getName()));
        }

        fastAdapterVideo.notifyAdapterDataSetChanged();
        itemAdapterVideo.setNewList(videoList);

        mainHandler.post(() -> itemAdapterVideo.setNewList(videoList));

    }



    private void openSelectedFile() {

        Set<Integer> selectedIndices = selectExtension.getSelections();

        if (selectedIndices.isEmpty()) {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            return;
        }

        int index = selectedIndices.iterator().next();
        FileHelperAdapter selectedItem = itemAdapterVideo.getAdapterItem(index);

        if (selectedItem != null) {
            FileOperation.fileOpenWith(this, selectedItem.getUri(), selectedItem.getMineTypes());
        } else {
            Toast.makeText(this, "Failed to open file", Toast.LENGTH_SHORT).show();
        }

    }

    // not using function
    private void uploadVideo() {

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

}