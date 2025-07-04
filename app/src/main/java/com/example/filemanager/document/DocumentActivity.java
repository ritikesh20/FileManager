package com.example.filemanager.document;

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
import java.util.List;
import java.util.Set;

public class DocumentActivity extends AppCompatActivity {

    private FileHelperAdapter item;
    private boolean isAllFileSelected = false;
    private RecyclerView recyclerViewDocument;
    private Toolbar toolbarDocument;
    private SharedPreferences sharedPreferencesDocument;
    private ItemAdapter<FileHelperAdapter> itemAdapterDocument;
    private FastAdapter<FileHelperAdapter> fastAdapterDocument;
    private final List<FileHelperAdapter> listDocument = new ArrayList<>();
    private SelectExtension<FileHelperAdapter> selectExtension;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);

        toolbarDocument = findViewById(R.id.toolbarDocument);
        recyclerViewDocument = findViewById(R.id.recyclerViewDocument);

        itemAdapterDocument = new ItemAdapter<>();
        fastAdapterDocument = FastAdapter.with(itemAdapterDocument);
        recyclerViewDocument.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDocument.setAdapter(fastAdapterDocument);

        sharedPreferencesDocument = getSharedPreferences("DocumentPref", MODE_PRIVATE);

        setSupportActionBar(toolbarDocument);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Document File");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        selectExtension = fastAdapterDocument.getExtension(SelectExtension.class);

        if (selectExtension == null) {
            selectExtension = new SelectExtension<>();
            fastAdapterDocument.addExtension(selectExtension);
        }

        selectExtension.withSelectable(true);
        selectExtension.withMultiSelect(true);
        selectExtension.withSelectWithItemUpdate(true);

        MediaStoreHelper.loadFile(this, "document", files -> {
            listDocument.clear();
            listDocument.addAll(files);
            itemAdapterDocument.setNewList(listDocument);
            fastAdapterDocument.notifyDataSetChanged();

        });

        click();
        clickBtnFileInfo();
        fileLongClick();

    }


    void click() {
        fastAdapterDocument.withOnClickListener(new OnClickListener<FileHelperAdapter>() {
            @Override
            public boolean onClick(View v, IAdapter<FileHelperAdapter> adapter, FileHelperAdapter item, int position) {

                if (!selectExtension.getSelections().isEmpty()) {
                    selectExtension.toggleSelection(position);
                    return true;
                }

                FileOperation.fileOpenWith(DocumentActivity.this, item.getUri(), item.getMineTypes());
                return false;
            }
        });
    }

    void clickBtnFileInfo() {
        fastAdapterDocument.withEventHook(new ClickEventHook<FileHelperAdapter>() {
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

        fastAdapterDocument.withOnLongClickListener((v, adapter, item, position) -> {

            selectExtension.toggleSelection(position);

            return true;
        });

        selectExtension.withSelectionListener(new ISelectionListener<FileHelperAdapter>() {
            @Override
            public void onSelectionChanged(FileHelperAdapter item, boolean selected) {

                int count = selectExtension.getSelections().size();

                if (count > 0) {
                    toolbarDocument.setTitle(count + " selected");
                    toolbarDocument.setSubtitle(getSelectedFileSize());
                    toolbarDocument.setSubtitle(getSelectedFileSize());
                } else {
                    toolbarDocument.setTitle("Document");
                    toolbarDocument.setSubtitle("");
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
                Toast.makeText(DocumentActivity.this, "All file Selected", Toast.LENGTH_SHORT).show();
            } else {
                selectExtension.deselect();
                Toast.makeText(DocumentActivity.this, "All file deSelected", Toast.LENGTH_SHORT).show();
            }

            isAllFileSelected = !isAllFileSelected;

            return true;
        } else if (id == R.id.file_Share) {
            FileOperation.shareSelectedFiles(this, itemAdapterDocument, selectExtension);
            return true;
        } else if (id == R.id.file_Delete) {
            FileOperation.deleteSelectedFiles(this, itemAdapterDocument, selectExtension);
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

        PopupMenu popupMenu = new PopupMenu(DocumentActivity.this, anchorView);
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
                FileOperation.shareSelectedFiles(this, itemAdapterDocument, selectExtension);
            } else if (id == R.id.selRename) {
                showRenameInputDialog();
                selectExtension.deselect(position);
            } else if (id == R.id.selDelete) {
                FileOperation.deleteSelectedFiles(this, itemAdapterDocument, selectExtension);
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
            item = itemAdapterDocument.getAdapterItem(index);
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
                fastAdapterDocument.notifyDataSetChanged();
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

    private void newSorting() {
        SortingHelper.sortingBy(this,
                listDocument,
                fastAdapterDocument,
                itemAdapterDocument,
                sharedPreferencesDocument,
                "VIDEOSORTINGBY",
                sortedList -> {
                    Toast.makeText(DocumentActivity.this, "List sorted", Toast.LENGTH_SHORT).show();
                }
        );
    }

    void openSearching() {
        FileOperation.searchingIntent(DocumentActivity.this);
    }

    private void addToDrive() {
        FileOperation.shareFilesToGoogleDrive(this, itemAdapterDocument, selectExtension);
    }

    private void openSelectedFile() {

        Set<Integer> selectedIndices = selectExtension.getSelections();

        if (selectedIndices.isEmpty()) {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            return;
        }

        int index = selectedIndices.iterator().next();
        FileHelperAdapter selectedItem = itemAdapterDocument.getAdapterItem(index);

        if (selectedItem != null) {
            FileOperation.fileOpenWith(this, selectedItem.getUri(), selectedItem.getMineTypes());
        } else {
            Toast.makeText(this, "Failed to open file", Toast.LENGTH_SHORT).show();
        }

    }

    public String getSelectedFileSize() {

        long fileSizeSum = 0;

        for (Integer selectedItem : selectExtension.getSelections()) {
            FileHelperAdapter fileSize = itemAdapterDocument.getAdapterItem(selectedItem);
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