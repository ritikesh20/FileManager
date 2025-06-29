package com.example.filemanager.videolist;


import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filemanager.FileOperation;
import com.example.filemanager.MediaStoreHelper;
import com.example.filemanager.R;
import com.example.filemanager.SortingHelper;
import com.example.filemanager.document.FileHelperAdapter;
import com.example.filemanager.favouritesection.AppDatabase;
import com.example.filemanager.favouritesection.FavouriteDao;
import com.example.filemanager.favouritesection.FavouriteItem;
import com.example.filemanager.internalstorage.InternalStorageActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.ISelectionListener;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.OnLongClickListener;
import com.mikepenz.fastadapter.select.SelectExtension;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VideoActivity extends AppCompatActivity {

    FileHelperAdapter item;
    SharedPreferences sharedPreferences;
    public static boolean isVideoView = false;
    private Toolbar toolbarVideo;
    private RecyclerView recyclerViewVideo;
    private ItemAdapter<FileHelperAdapter> itemAdapterVideo;
    private FastAdapter<FileHelperAdapter> fastAdapterVideo;
    private List<FileHelperAdapter> videoList = new ArrayList<>();
    private SelectExtension<FileHelperAdapter> selectExtension;

    private boolean isAllFileSelected = false;

    private List<FileHelperAdapter> selectedFiles = new ArrayList<>();


    Button btnVideoPasting;
    File currentDirectory = InternalStorageActivity.currentDirectory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video);

        btnVideoPasting = findViewById(R.id.btnVideoPasting);
        toolbarVideo = findViewById(R.id.toolbarVideo);
        sharedPreferences = getSharedPreferences("MyViewPrefs", MODE_PRIVATE);

        setSupportActionBar(toolbarVideo);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Video");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_arrow_back).actionBar()); // to change back arrow icons
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
            itemAdapterVideo.setNewList(files);
            fastAdapterVideo.notifyDataSetChanged();

        });

//        if (VideoClipboardHelper.hasData()) {
//            btnVideoPasting.setVisibility(View.VISIBLE);
//        } else {
//            btnVideoPasting.setVisibility(View.GONE);
//        }

//        fastAdapterVideo.withOnClickListener((v, adapter, item, position) -> {
//
//            if (v != null) {
//                FileOperation.fileOpenWith(v.getContext(), item.getUri(), item.getMineTypes());
//            }
//
//            return true;
//        });

        fastAdapterVideo.withOnLongClickListener((v, adapter, item, position) -> {

            selectExtension.toggleSelection(position);

            selectExtension.withSelectionListener((items, selected) -> {

                        int size = selectExtension.getSelections().size();
                        if (size > 0) {
                            toolbarVideo.setTitle(size + " selected");
                        } else {
                            toolbarVideo.setTitle("Video");
                        }
                    }
            );


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


//    private void selectedFiles() {
//
//        selectedFiles.clear();
//
//        for (int index : selectExtension.getSelections()) {
//            FileHelperAdapter item = itemAdapterVideo.getAdapterItem(index);
//            if (item != null) selectedFiles.add(item);
//        }
//
//    }

    // for selection
    void shareFromUtils() {
        FileOperation.getSelectedFiles(itemAdapterVideo, selectExtension);
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
            fastAdapterVideo.notifyDataSetChanged();
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

            return true;
        } else if (id == R.id.file_Move) {
            moveVideo();
        } else if (id == R.id.file_Copy) {
            copyVideo();
        } else if (id == R.id.file_Rename) {

            showRenameInputDialog();

            return true;
        } else if (id == R.id.file_AddToStarred) {
            selectedSendToFav();
        } else if (id == R.id.file_SafeFolder) {

        } else if (id == R.id.file_BackToGoogleDrive) {
            FileOperation.shareFilesToGoogleDrive(this, selectedFiles);
        } else if (id == R.id.file_FileInfo) {

        }

        return super.onOptionsItemSelected(item);
    }

    void openSearching() {
        FileOperation.searchingIntent(VideoActivity.this);
    }

    private void selectedSendToFav() {

        Set<FileHelperAdapter> selectedFavItem = selectExtension.getSelectedItems();

        if (selectedFavItem.isEmpty()) {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            return;
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(new Runnable() {
            @Override
            public void run() {
                FavouriteDao dao = AppDatabase.getInstance(VideoActivity.this).favouriteVideoDao();

                for (FileHelperAdapter fileHelperAdapter : selectedFavItem) {
                    FavouriteItem favouriteItem = new FavouriteItem(
                            fileHelperAdapter.getUri().toString(),
                            fileHelperAdapter.getName(),
                            false,
                            "" + fileHelperAdapter.getDocDate(),
                            "" + fileHelperAdapter.getSize(),
                            fileHelperAdapter.getMineTypes(),
                            0
                    );
                    dao.insert(favouriteItem);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(VideoActivity.this, "File Add to Favourite Successfully", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }

    private void addSingleToFavourite() {
        FileHelperAdapter fileHelperAdapter = new FileHelperAdapter();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            FavouriteDao dao = AppDatabase.getInstance(VideoActivity.this).favouriteVideoDao();
            FavouriteItem favouriteItem = new FavouriteItem(
                    fileHelperAdapter.getUri().toString(),
                    fileHelperAdapter.getName(),
                    false,
                    fileHelperAdapter.getDocDate(),
                    fileHelperAdapter.getSize(),
                    fileHelperAdapter.getMineTypes(),
                    0
            );
            dao.insert(favouriteItem);

            runOnUiThread(() ->
                    Toast.makeText(VideoActivity.this, "Single file added to Favourite", Toast.LENGTH_SHORT).show()
            );
        });
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

    private void myFileNameEdit() {
        FileOperation.showRenameDialog(this, itemAdapterVideo, selectExtension);
    }

    private void deletedSelectedFile() {

//        selectedFiles();
        shareFromUtils();

        if (selectedFiles.isEmpty()) {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }

        AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.delete_item, null);

        Button btnDelete = view.findViewById(R.id.btnDeleteDelete);
        Button btnCancel = view.findViewById(R.id.btnDeleteCancel);
        deleteBuilder.setView(view);

        AlertDialog alertDialog = deleteBuilder.create();

        btnDelete.setOnClickListener(v -> {

            int deletedCount = 0;

            for (FileHelperAdapter deleteItem : selectedFiles) {

                Uri uri = deleteItem.getUri();

                try {
                    int rows = getContentResolver().delete(uri, null, null);
                    if (rows > 0) {
                        deletedCount++;
                    }
                } catch (SecurityException e) {
                    Toast.makeText(this, "Permission denied for: " + item.getName(), Toast.LENGTH_SHORT).show();
                }
            }
            alertDialog.dismiss();
            Toast.makeText(this, deletedCount + "files deleted", Toast.LENGTH_SHORT).show();
            recreate();
        });


        btnCancel.setOnClickListener(v -> {
            alertDialog.dismiss();
        });

        alertDialog.show();
    }

    void fileLongClick() {
        fastAdapterVideo.withOnLongClickListener(new OnLongClickListener<FileHelperAdapter>() {
            @Override
            public boolean onLongClick(@NonNull View v, @NonNull IAdapter<FileHelperAdapter> adapter, @NonNull FileHelperAdapter item, int position) {

                selectExtension.toggleSelection(position);

                selectExtension.withSelectionListener(new ISelectionListener<FileHelperAdapter>() {
                    @Override
                    public void onSelectionChanged(FileHelperAdapter item, boolean selected) {

                        int count = selectExtension.getSelections().size();

                        if (count > 0) {
                            toolbarVideo.setTitle(count + " selected");
                            toolbarVideo.setSubtitle(getSelectedFileSize());
                            toolbarVideo.setSubtitle(getSelectedFileSize());
                        } else {
                            toolbarVideo.setTitle("Video");
                            toolbarVideo.setSubtitle("");
                        }

                    }
                });


                // cal add file size also
                return true;
            }
        });
    }

    private void copyVideo() {
        List<Uri> selectedUris = new ArrayList<>();
        for (FileHelperAdapter item : selectExtension.getSelectedItems()) {
            selectedUris.add(item.getUri());
        }
        Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
        btnVideoPasting.setVisibility(View.VISIBLE);
        MediaStoreHelper.goStorageTypes(this, "Copy From Video");
    }

    private void moveVideo() {

        List<Uri> selectedUris = new ArrayList<>();
        for (FileHelperAdapter item : selectExtension.getSelectedItems()) {
            selectedUris.add(item.getUri());
        }
        Toast.makeText(this, "Cut to clipboard", Toast.LENGTH_SHORT).show();
        btnVideoPasting.setVisibility(View.VISIBLE);

    }

    private void videoCutCopyPasting() {
        btnVideoPasting.setOnClickListener(v -> {
            if (!VideoClipboardHelper.hasData()) {
                Toast.makeText(this, "Nothing to paste", Toast.LENGTH_SHORT).show();
                return;
            }

            for (Uri uri : VideoClipboardHelper.getCopiedUris()) {
                String fileName = FileOperation.getFileNameFromUri(this, uri);
                File destFile = new File(currentDirectory, fileName);

                boolean success = FileOperation.copyFileFromUri(this, uri, destFile);

                if (success) {
                    Toast.makeText(this, "Copied: " + fileName, Toast.LENGTH_SHORT).show();

                    if (VideoClipboardHelper.isCutMode()) {
                        DocumentFile source = DocumentFile.fromSingleUri(this, uri);
                        if (source != null && source.exists()) source.delete();
                    }
                } else {
                    Toast.makeText(this, "Failed: " + fileName, Toast.LENGTH_SHORT).show();
                }
            }

            btnVideoPasting.setVisibility(View.GONE);
        });
    }

    public String getSelectedFileSize() {

        long fileSizeSum = 0;

        for (Integer selectedItem : selectExtension.getSelections()) {
            FileHelperAdapter fileSize = itemAdapterVideo.getAdapterItem(selectedItem);
            fileSizeSum += parseSizeToBytes(fileSize.getSize());
        }

//        return findFileSize(fileSizeSum);
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
                return Long.parseLong(sizeStr); // assume it's already in bytes
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // sorting working
    void newSorting() {
        SortingHelper.sortingBy(this,
                videoList,
                fastAdapterVideo,
                itemAdapterVideo,
                sharedPreferences,
                "VIDEOSORTINGBY",
                sortedList -> {
                    Toast.makeText(VideoActivity.this, "List sorted", Toast.LENGTH_SHORT).show();
                }
        );
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
//            editor.putInt(VIDEOSORTINGBY, checkedId);
//            editor.apply();
//
//            if (checkedId == R.id.rbBtnNDF) {
//                Collections.sort(videoList, (nfd, ofd) -> parseFileDate(ofd.getDocDate()).compareTo(parseFileDate(nfd.getDocDate())));
//            } else if (checkedId == R.id.rbBtnODF) {
//                Collections.sort(videoList, (nfd, ofd) -> parseFileDate(nfd.getDocDate()).compareTo(parseFileDate(ofd.getDocDate())));
//            } else if (checkedId == R.id.rbBtnLargeFirst) {
//                Collections.sort(videoList, (largeFile, smallFile) -> Long.compare(parseSizeToBytes(smallFile.getSize()), parseSizeToBytes(largeFile.getSize())));
//            } else if (checkedId == R.id.rbBtnSmallestFirst) {
//                Collections.sort(videoList, (largeFile, smallFile) -> Long.compare(parseSizeToBytes(largeFile.getSize()), parseSizeToBytes(smallFile.getSize())));
//            } else if (checkedId == R.id.nameAZ) {
//                Collections.sort(videoList, (name1, name2) -> name1.getName().compareTo(name2.getName()));
//            } else if (checkedId == R.id.nameZA) {
//                Collections.sort(videoList, (name1, name2) -> name2.getName().compareTo(name1.getName()));
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
    private void openWith() {

        for (Integer index : selectExtension.getSelections()) {
            item = itemAdapterVideo.getAdapterItem(index);
            FileOperation.fileOpenWith(this, item.getUri(), item.getMineTypes());
        }
        int index = selectExtension.getSelections().iterator().next();
        FileHelperAdapter item = itemAdapterVideo.getAdapterItem(index);
        FileOperation.fileOpenWith(this, item.getUri(), item.getMineTypes());

        Set<Integer> video = selectExtension.getSelections();
        FileHelperAdapter itemz = itemAdapterVideo.getAdapterItem(video.size());
        FileOperation.fileOpenWith(this, item.getUri(), item.getMineTypes());

    }

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

    // sharing file working
    private void shareSelectedFiles() {
//
//        Set<Integer> selectedIndexes = selectExtension.getSelections();
//
//        if (selectedIndexes.isEmpty()) {
//            Toast.makeText(this, "No files selected", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        ArrayList<Uri> urisToShare = new ArrayList<>();
//
//        for (int index : selectedIndexes) {
//            FileHelperAdapter file = itemAdapterVideo.getAdapterItem(index);
//            if (file != null) {
//                urisToShare.add(file.getUri());
//            }
//        }
//
//        if (urisToShare.isEmpty()) {
//            Toast.makeText(this, "No valid files to share", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        Intent shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
//        shareIntent.setType("*/*"); // Or detect the type dynamically if you prefer
//        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, urisToShare);
//        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//
//        startActivity(Intent.createChooser(shareIntent, "Share files via"));
    }

    private String findFileSize(long sizeInBytes) {

        float kb = sizeInBytes / 1024f;
        float mb = kb / 1024f;
        float gb = mb / 1024f;

        if (gb >= 1) {
            return String.format("%.2f Gb", gb);
        } else if (mb >= 1) {
            return String.format("%.2f Mb", mb);
        } else {
            return String.format("%.2f KB", kb);
        }
    }

    private void newShareSelectedFiles() {

//        selectedFiles();
        shareFromUtils();

        if (selectedFiles.isEmpty()) {
            Toast.makeText(this, "No files selected", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<Uri> uris = new ArrayList<>();
        for (FileHelperAdapter item : selectedFiles) {
            uris.add(item.getUri());
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.setType("*/*");
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(shareIntent, "Share files via"));
    }
}