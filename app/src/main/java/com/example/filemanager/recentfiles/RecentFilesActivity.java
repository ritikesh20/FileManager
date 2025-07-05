package com.example.filemanager.recentfiles;

import android.app.AlertDialog;
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
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
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
import com.mikepenz.fastadapter.ISelectionListener;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.mikepenz.fastadapter.select.SelectExtension;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class RecentFilesActivity extends AppCompatActivity {

    private FileHelperAdapter item;
    private boolean isAllFileSelected;
    private RecyclerView recyclerView;
    private SharedPreferences sharedPreferencesRecentFile;
    private String KEY_OPTION_VIDEO_SORTING_TYPE = "sort_option_recentFile";
    private ItemAdapter<FileHelperAdapter> itemAdapterRecentFile;
    private FastAdapter<FileHelperAdapter> fastAdapterRecentFile;
    private File file;
    private ProgressBar progressBar;
    private List<FileHelperAdapter> recentFileList = new ArrayList<>();
    private SelectExtension<FileHelperAdapter> selectExtension;
    private Toolbar toolbarRecentFile;
    private int limit = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_files);

        toolbarRecentFile = findViewById(R.id.toolbarRecentFile);

        setSupportActionBar(toolbarRecentFile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Recent File");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        sharedPreferencesRecentFile = getSharedPreferences("recentPref", MODE_PRIVATE);

        progressBar = findViewById(R.id.progressBarRecent);

        recyclerView = findViewById(R.id.recyclerViewRecentFile);
        itemAdapterRecentFile = new ItemAdapter<>();
        fastAdapterRecentFile = FastAdapter.with(itemAdapterRecentFile);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(fastAdapterRecentFile);

        selectExtension = fastAdapterRecentFile.getExtension(SelectExtension.class);

        if (selectExtension == null) {
            selectExtension = new SelectExtension<>();
            fastAdapterRecentFile.addExtension(selectExtension);
        }

        selectExtension.withSelectable(true);
        selectExtension.withMultiSelect(true);
        selectExtension.withSelectWithItemUpdate(true);

        progressBar.setVisibility(View.VISIBLE);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String sizeLimit = prefs.getString("recentFile_size", "50");
        int recentFileLimit = Integer.parseInt(sizeLimit);

        if (recentFileLimit == 50) {
            limit = 50;
        } else if (recentFileLimit == 100) {
            limit = 100;
        } else if (recentFileLimit == 500) {
            limit = 500;
        } else {
            limit = 1000;
        }

        MediaStoreHelper.loadFile(this, "recentfile", files -> {

            int recentSize = limit;

            List<FileHelperAdapter> limitedList;

            if (files.size() > recentSize) {
                limitedList = files.subList(0, recentSize);
            } else {
                limitedList = files;
            }

            Objects.requireNonNull(getSupportActionBar()).setSubtitle(recentSize + " Files");

            recentFileList.clear();
            recentFileList.addAll(limitedList);
            sortingPref();
//            itemAdapterRecentFile.setNewList(recentFileList);
            progressBar.setVisibility(View.GONE);
            fastAdapterRecentFile.notifyDataSetChanged();

        });

        click();
        clickBtnFileInfo();
        fileLongClick();

    }

    private void loadMediaStoreFiles() {

//        // 1. Thread banate hain for background task
//        ExecutorService executor = Executors.newSingleThreadExecutor();
//
//        // 2. Handler main thread ke liye
//        Handler mainHandler = new Handler(Looper.getMainLooper());
//
//        executor.execute(() -> {
//            // 3. MediaStore query background me
//            List<FileHelperAdapter> tempList = new ArrayList<>();
//
//            String[] projection = {
//                    MediaStore.Files.FileColumns._ID,
//                    MediaStore.Files.FileColumns.DISPLAY_NAME,
//                    MediaStore.Files.FileColumns.DATA,
//                    MediaStore.Files.FileColumns.MIME_TYPE,
//                    MediaStore.Files.FileColumns.DATE_ADDED
//            };
//
//            Uri uri = MediaStore.Files.getContentUri("external");
//
//            String selection =
//                    MediaStore.Files.FileColumns.MEDIA_TYPE + "=? OR " +
//                            MediaStore.Files.FileColumns.MEDIA_TYPE + "=? OR " +
//                            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?";
//
//            String[] selectionArgs = {
//                    String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
//                    String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO),
//                    String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO)
//            };
//
//            String sortOrder = MediaStore.Files.FileColumns.DATE_ADDED + " DESC";
//
//            Cursor cursor = getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
//
//            if (cursor != null) {
//                int nameCol = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME);
//                int pathCol = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
//                int mimeCol = cursor.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE);
//
//                while (cursor.moveToNext()) {
//                    String name = cursor.getString(nameCol);
//                    String path = cursor.getString(pathCol);
//                    String mime = cursor.getString(mimeCol);
//                    tempList.add(new FileHelperAdapter(null, name, mime, path, null));
//                }
//                cursor.close();
//            }
//
//            // 4. Update UI on main thread using Handler
//            mainHandler.post(() -> {
//                fileList.clear();
//                fileList.addAll(tempList);
//                itemAdapter.setNewList(fileList);
//                fastAdapter.notifyDataSetChanged();
//                progressBar.setVisibility(View.GONE);
//            });
//        });
    }


    void click() {

        fastAdapterRecentFile.withOnClickListener(new OnClickListener<FileHelperAdapter>() {
            @Override
            public boolean onClick(View v, IAdapter<FileHelperAdapter> adapter, FileHelperAdapter item, int position) {

                FileOperation.fileOpenWith(RecentFilesActivity.this, item.getUri(), item.getMineTypes());

                return false;
            }
        });
    }

    void clickBtnFileInfo() {

        fastAdapterRecentFile.withEventHook(new ClickEventHook<FileHelperAdapter>() {
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

        fastAdapterRecentFile.withOnLongClickListener((v, adapter, item, position) -> {

            selectExtension.toggleSelection(position);

            return true;
        });

        selectExtension.withSelectionListener(new ISelectionListener<FileHelperAdapter>() {
            @Override
            public void onSelectionChanged(FileHelperAdapter item, boolean selected) {

                int count = selectExtension.getSelections().size();

                if (count > 0) {
                    toolbarRecentFile.setTitle(count + " selected");
                    toolbarRecentFile.setSubtitle(FileOperation.getSelectedFileSize(selectExtension,itemAdapterRecentFile));
                } else {
                    toolbarRecentFile.setTitle("Recent File");
                    toolbarRecentFile.setSubtitle("");
                }

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

        } else if (id == R.id.file_Selected) {
            if (!isAllFileSelected) {
                selectExtension.select();
                Toast.makeText(RecentFilesActivity.this, "All file Selected", Toast.LENGTH_SHORT).show();
            } else {
                selectExtension.deselect();
                Toast.makeText(RecentFilesActivity.this, "All file deSelected", Toast.LENGTH_SHORT).show();
            }

            isAllFileSelected = !isAllFileSelected;

            return true;
        } else if (id == R.id.file_Share) {
            FileOperation.shareSelectedFiles(this, itemAdapterRecentFile, selectExtension);
            return true;
        } else if (id == R.id.file_Delete) {
            FileOperation.deleteSelectedFiles(this, itemAdapterRecentFile, selectExtension);
            return true;
        } else if (id == R.id.file_SortBy) {
            newSorting();
            return true;
        } else if (id == R.id.file_OpenWith) {
            openSelectedFile();
            return true;
        } else if (id == R.id.file_Move) {
        } else if (id == R.id.file_Copy) {

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

        PopupMenu popupMenu = new PopupMenu(RecentFilesActivity.this, anchorView);
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
                return true;
            } else if (id == R.id.selShare) {
                FileOperation.shareSelectedFiles(this, itemAdapterRecentFile, selectExtension);
                return true;
            } else if (id == R.id.selRename) {
                showRenameInputDialog();
                selectExtension.deselect(position);
                return true;
            } else if (id == R.id.selDelete) {
                FileOperation.deleteSelectedFiles(this, itemAdapterRecentFile, selectExtension);
                selectExtension.deselect(position);
                return true;
            } else if (id == R.id.selAddToStarred) {

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
            item = itemAdapterRecentFile.getAdapterItem(index);
        }
//        int index = selectExtension.getSelections().iterator().next();
//        FileHelperAdapter item = itemAdapterVideo.getAdapterItem(index);


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
                fastAdapterRecentFile.notifyDataSetChanged();
                Toast.makeText(this, renameSuccess ? "File renamed successfully" : "Failed to rename file", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "File name cannot be empty", Toast.LENGTH_SHORT).show();
            }
            alertDialog.dismiss();
        });

        btnCancelRename.setOnClickListener(v -> {
            alertDialog.dismiss();
        });

    }

    void newSorting() {
        SortingHelper.sortingBy(this,
                recentFileList,
                fastAdapterRecentFile,
                itemAdapterRecentFile,
                sharedPreferencesRecentFile,
                KEY_OPTION_VIDEO_SORTING_TYPE,
                sortedList -> {
                    Toast.makeText(RecentFilesActivity.this, "List sorted", Toast.LENGTH_SHORT).show();
                }
        );
    }

    void openSearching() {
        FileOperation.searchingIntent(RecentFilesActivity.this);
    }

    private void addToDrive() {
        FileOperation.shareFilesToGoogleDrive(this, itemAdapterRecentFile, selectExtension);
    }

    private void openSelectedFile() {

        Set<Integer> selectedIndices = selectExtension.getSelections();

        if (selectedIndices.isEmpty()) {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            return;
        }

        int index = selectedIndices.iterator().next();
        FileHelperAdapter selectedItem = itemAdapterRecentFile.getAdapterItem(index);

        if (selectedItem != null) {
            FileOperation.fileOpenWith(this, selectedItem.getUri(), selectedItem.getMineTypes());
        } else {
            Toast.makeText(this, "Failed to open file", Toast.LENGTH_SHORT).show();
        }

    }


    private void sortingPref() {
        SortingHelper.applySorting(this, recentFileList,fastAdapterRecentFile,itemAdapterRecentFile,sharedPreferencesRecentFile,KEY_OPTION_VIDEO_SORTING_TYPE);
    }



}


// normal file loading with ExecutorService
//    void loadHomeRecentFile() {
//
//        listRecentFile.clear();
//
//        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//
//        String[] projection = {
//                MediaStore.Files.FileColumns._ID,
//                MediaStore.Files.FileColumns.DISPLAY_NAME,
//                MediaStore.Files.FileColumns.RELATIVE_PATH,
//                MediaStore.Files.FileColumns.MIME_TYPE
//        };
//
//        ContentResolver contentResolver = getContentResolver();
//        String sortOrder = MediaStore.Images.Media.DATE_MODIFIED + " DESC LIMIT 50";
//        Cursor cursor = contentResolver.query(
//                uri,
//                projection,
//                null,
//                null,
//                sortOrder
//        );
//
//        // find the colum index
//
//        if (cursor != null) {
//            int idColum = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID);
//            int fileNameColum = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
//            int filePathColum = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.RELATIVE_PATH);
//            int mimeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE);
//
//            while (cursor.moveToNext()) {
//                long id = cursor.getLong(idColum);
//                String fileName = cursor.getString(fileNameColum);
//                String filePath = cursor.getString(filePathColum);
//                String mime = cursor.getString(mimeColumn);
//
//
//                Uri recentFileUri = ContentUris.withAppendedId(uri, id);
//
//                listRecentFile.add(new AdapterRecentFile(recentFileUri, fileName, filePath, mime));
//
//            }
//            cursor.close();
//        }
//
//        itemAdapterRecentFile.setNewList(listRecentFile);
//        recyclerView.setAdapter(fastAdapterRecentFile);
//        fastAdapterRecentFile.notifyAdapterDataSetChanged();
//
//    }
