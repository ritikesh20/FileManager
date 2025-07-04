package com.example.filemanager.imagexview;

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
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ImageGalleryActivity extends AppCompatActivity {

    FileHelperAdapter item;
    private boolean isAllFileSelected = false;
    ProgressBar progressBarImage;
    private SharedPreferences sharedPreferencesImage;
    public static final String KEY_SORT_OPTION = "sort_option";

    private Toolbar toolbarImage;
    private RecyclerView recyclerView;
    FastAdapter<FileHelperAdapter> imageFastAdapter;
    ItemAdapter<FileHelperAdapter> imageItemAdapter;
    List<FileHelperAdapter> imageList = new ArrayList<>();
    SelectExtension<FileHelperAdapter> selectExtension;
    int imageCount = 0;
    Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_gallery);

        recyclerView = findViewById(R.id.rvImageGallery);
        toolbarImage = findViewById(R.id.toolbarGalleryImage);

        progressBarImage = findViewById(R.id.progressBarImage);

        imageItemAdapter = new ItemAdapter<>();
        imageFastAdapter = FastAdapter.with(imageItemAdapter);
        imageList = new ArrayList<>();

        setSupportActionBar(toolbarImage);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Image");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // to show back button
        }

        selectExtension = imageFastAdapter.getExtension(SelectExtension.class);

        if (selectExtension == null) {
            selectExtension = new SelectExtension<>();
            imageFastAdapter.addExtension(selectExtension);
        }

        selectExtension.withSelectable(true);
        selectExtension.withMultiSelect(true);
        selectExtension.withSelectWithItemUpdate(true);

        sharedPreferencesImage = getSharedPreferences("sorting_pref", MODE_PRIVATE);


        recyclerViewSetUp();
        progressBarImage.setVisibility(View.VISIBLE);

        MediaStoreHelper.loadFile(this, "image", files -> {

            imageList.clear();
            imageList.addAll(files);
            applySavedSorting();
            imageItemAdapter.setNewList(files);
            progressBarImage.setVisibility(View.GONE);
            imageFastAdapter.notifyDataSetChanged();

        });

        click();

        clickBtnFileInfo();

        fileLongClick();


    }

    public void recyclerViewSetUp() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(imageFastAdapter);
    }

    void newSorting() {
        SortingHelper.sortingBy(this,
                imageList,
                imageFastAdapter,
                imageItemAdapter,
                sharedPreferencesImage,
                KEY_SORT_OPTION,
                sortedList -> {
                    Toast.makeText(ImageGalleryActivity.this, "List sorted", Toast.LENGTH_SHORT).show();
                }
        );
    }

    private void applySavedSorting() {

        int checkedId = sharedPreferencesImage.getInt(KEY_SORT_OPTION, R.id.nameAZ);

        if (checkedId == R.id.nameAZ) {
            Collections.sort(imageList, (name1, name2) -> name1.getName().compareToIgnoreCase(name2.getName()));
        } else if (checkedId == R.id.nameZA) {
            imageList.sort((name1, name2) -> name2.getName().compareToIgnoreCase(name1.getName()));
        }

        imageFastAdapter.notifyAdapterDataSetChanged();
        imageItemAdapter.setNewList(imageList);

        mainHandler.post(() -> imageItemAdapter.setNewList(imageList));

    }


    private void click() {
        imageFastAdapter.withOnClickListener(new OnClickListener<FileHelperAdapter>() {
            @Override
            public boolean onClick(View v, IAdapter<FileHelperAdapter> adapter, FileHelperAdapter item, int position) {

                if (!selectExtension.getSelections().isEmpty()) {
                    selectExtension.toggleSelection(position);
                    return true;
                }

                FileOperation.fileOpenWith(ImageGalleryActivity.this, item.getUri(), item.getMineTypes());
                return false;
            }
        });
    }

    private void clickBtnFileInfo() {
        imageFastAdapter.withEventHook(new ClickEventHook<FileHelperAdapter>() {
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

    private void fileLongClick() {

        imageFastAdapter.withOnLongClickListener((v, adapter, item, position) -> {

            selectExtension.toggleSelection(position);

            return true;
        });

        selectExtension.withSelectionListener(new ISelectionListener<FileHelperAdapter>() {
            @Override
            public void onSelectionChanged(FileHelperAdapter item, boolean selected) {

                int count = selectExtension.getSelections().size();

                if (count > 0) {
                    toolbarImage.setTitle(count + " selected");
                    toolbarImage.setSubtitle(getSelectedFileSize());
                    toolbarImage.setSubtitle(getSelectedFileSize());
                } else {
                    toolbarImage.setTitle("Image");
                    toolbarImage.setSubtitle("");
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
                Toast.makeText(ImageGalleryActivity.this, "All file Selected", Toast.LENGTH_SHORT).show();
            } else {
                selectExtension.deselect();
                Toast.makeText(ImageGalleryActivity.this, "All file deSelected", Toast.LENGTH_SHORT).show();
            }

            isAllFileSelected = !isAllFileSelected;

            return true;
        } else if (id == R.id.file_Share) {
            FileOperation.shareSelectedFiles(this, imageItemAdapter, selectExtension);
            return true;
        } else if (id == R.id.file_Delete) {
            FileOperation.deleteSelectedFiles(this, imageItemAdapter, selectExtension);
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

        PopupMenu popupMenu = new PopupMenu(ImageGalleryActivity.this, anchorView);
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
                FileOperation.shareSelectedFiles(this, imageItemAdapter, selectExtension);
            } else if (id == R.id.selRename) {
                showRenameInputDialog();
                selectExtension.deselect(position);
            } else if (id == R.id.selDelete) {
                FileOperation.deleteSelectedFiles(this, imageItemAdapter, selectExtension);
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

    private void showRenameInputDialog() {

        for (Integer index : selectExtension.getSelections()) {
            item = imageItemAdapter.getAdapterItem(index);
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
                imageFastAdapter.notifyDataSetChanged();
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

    void openSearching() {
        FileOperation.searchingIntent(ImageGalleryActivity.this);
    }

    private void addToDrive() {
        FileOperation.shareFilesToGoogleDrive(this, imageItemAdapter, selectExtension);
    }

    private void openSelectedFile() {

        Set<Integer> selectedIndices = selectExtension.getSelections();

        if (selectedIndices.isEmpty()) {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            return;
        }

        int index = selectedIndices.iterator().next();
        FileHelperAdapter selectedItem = imageItemAdapter.getAdapterItem(index);

        if (selectedItem != null) {
            FileOperation.fileOpenWith(this, selectedItem.getUri(), selectedItem.getMineTypes());
        } else {
            Toast.makeText(this, "Failed to open file", Toast.LENGTH_SHORT).show();
        }

    }

    public String getSelectedFileSize() {

        long fileSizeSum = 0;

        for (Integer selectedItem : selectExtension.getSelections()) {
            FileHelperAdapter fileSize = imageItemAdapter.getAdapterItem(selectedItem);
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
                return Long.parseLong(sizeStr); // assume it's already in bytes
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
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


}


