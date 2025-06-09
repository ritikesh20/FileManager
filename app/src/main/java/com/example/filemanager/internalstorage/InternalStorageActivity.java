package com.example.filemanager.internalstorage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filemanager.MainActivity;
import com.example.filemanager.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.select.SelectExtension;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InternalStorageActivity extends AppCompatActivity {

    //    private SharedPreferences sharedPreferences;
    private static final String Sorting_PREF_NAME = "SortingPrefs";
    private static final String Sorting_KEY_SORTING = "sorting_by";

//    private List<File> currentFileX = new ArrayList<>();

    private RecyclerView recyclerPathHistory;
    private ItemAdapter<PathHistoryAdapter> itemAdapterPathHistory;
    private FastAdapter<PathHistoryAdapter> fastAdapterPathHistory;
    private List<PathHistoryAdapter> historyPathList;
    private Context context;
    private boolean isAscending = true;
    private int selectedSortingOption = R.id.rBtnName;
    String sortingBy;
    Menu mainMenu;

    int fileCount = 0;
    int folderCount = 0;

    private List<ISAdapter> items;
    public static boolean isGridView = false;

    private RecyclerView recyclerView;
    private ItemAdapter<ISAdapter> itemAdapter;
    private FastAdapter<ISAdapter> fastAdapter;

    boolean isAllSelected;
    private ArrayList<String> pathList;

    TextView noFileText;
    private SelectExtension<ISAdapter> selectExtension;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    private String currentPath;

    ImageView btnIVPaste;
    ImageView btnIvCancel;
    private File currentDirectory;
    private Toolbar toolbarSelected;
    private Toolbar inStorageToolbar;

    File[] filesAndFolders;

    List<File> folders = new ArrayList<>();
    List<File> files = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_internal_storage);

        recyclerPathHistory = findViewById(R.id.rvPathHistory);
        recyclerPathHistory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        itemAdapterPathHistory = new ItemAdapter<>();
        fastAdapterPathHistory = FastAdapter.with(itemAdapterPathHistory);
        historyPathList = new ArrayList<>();

        recyclerView = findViewById(R.id.rvInternalStorage);
        noFileText = findViewById(R.id.tvNoFile);

        btnIVPaste = findViewById(R.id.btnIconPast);
        btnIvCancel = findViewById(R.id.btnIconCancel);

        itemAdapter = new ItemAdapter<>();
        fastAdapter = FastAdapter.with(itemAdapter);

        inStorageToolbar = findViewById(R.id.toolbarInternalStorage);
        toolbarSelected = findViewById(R.id.toolbarSelected);

        items = new ArrayList<>();

        folders = new ArrayList<>();
        files = new ArrayList<>();

        setSupportActionBar(inStorageToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My File");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        selectExtension = fastAdapter.getExtension(SelectExtension.class);

        if (selectExtension == null) {
            selectExtension = new SelectExtension<>();
            fastAdapter.addExtension(selectExtension);
        }

        selectExtension.withSelectable(true);
        selectExtension.withMultiSelect(true);
        selectExtension.withSelectWithItemUpdate(true);

//        fileLoading();
        String path = getIntent().getStringExtra("path");

        if (path == null) {
            path = Environment.getExternalStorageDirectory().getAbsolutePath();
        }

        fileLoading(path);

//        sharedPreferences = getSharedPreferences(Sorting_PREF_NAME, MODE_PRIVATE);
//        sortingBy = sharedPreferences.getString(Sorting_KEY_SORTING, "AZ");

        btnIVPaste.setImageResource(R.drawable.paste);
        btnIvCancel.setImageResource(R.drawable.cancel);

        itemAdapterPathHistory.setNewList(buildPathSistoryList(pathList));

        recyclerPathHistory.setAdapter(fastAdapterPathHistory);

        fastAdapterPathHistory.withOnClickListener((view, adapter, item, position) -> {

            ArrayList<String> newPathList = new ArrayList<>();

            for (int i = 0; i <= position; i++) {
                newPathList.add(itemAdapterPathHistory.getAdapterItem(i).getPathName());
            }

            StringBuilder fullPath = new StringBuilder(Environment.getExternalStorageDirectory().getAbsolutePath());
            for (int i = 1; i < newPathList.size(); i++) {
                fullPath.append("/").append(newPathList.get(i));
            }

            Intent intent = new Intent(this, InternalStorageActivity.class);
            intent.putExtra("path", fullPath.toString());
            intent.putStringArrayListExtra("pathList", newPathList);
            startActivity(intent);
            finish();
            return true;
        });

        inStorageToolbar.inflateMenu(R.menu.menu_instorage);

        fastAdapter.withOnLongClickListener((v, adapter, itemx, position) -> {

            inStorageToolbar.setVisibility(View.GONE);
            toolbarSelected.setVisibility(View.VISIBLE);

            selectExtension.toggleSelection(position);

            selectExtension.withSelectionListener((item, selected) -> {

                int count = selectExtension.getSelections().size();

                if (count > 0) {
                    toolbarSelected.setTitle(count + " Selected");
                    toolbarSelected.setSubtitle(getSelectedFileSize());

                } else {

                    toolbarSelected.setVisibility(View.GONE);
                    inStorageToolbar.setVisibility(View.VISIBLE);

                    inStorageToolbar.setTitle("My files");
                    inStorageToolbar.setSubtitle(folderCount + " Folder" + fileCount + " Files");

                }

            });
            return true;

        });

        internalStorageToolbar();

        toolbarSelected.inflateMenu(R.menu.selected_menu);

        toolbarSelection();

    }


    void fileLoading(String path) {
        currentPath = path;

        currentDirectory = new File(path);

        pathList = getIntent().getStringArrayListExtra("pathList");

        if (pathList == null) {
            pathList = new ArrayList<>();
            pathList.add("Internal Storage");
        }

        //String finalPath = path;
        executorService.execute(() -> {

            //            if (filesAndFolders != null) {
//                Arrays.sort(filesAndFolders, (f1, f2) -> {
//                    if (f1.isDirectory() && !f2.isDirectory()) return -1;
//                    if (!f1.isDirectory() && f2.isDirectory()) return 1;
//                    return Long.compare(f2.lastModified(), f1.lastModified());
//                });
//            }

            File root = new File(path);
            filesAndFolders = root.listFiles();

            // loading file with sorting
            sortItemsWith("NewestDate");

//            boolean firstFolderShown = false;
//            boolean firstFileShown = false;
//            assert filesAndFolders != null;
//            for (File file : filesAndFolders) {
//                if (!file.getName().startsWith(".")) {
//
//                    boolean showHeader = false;
//                    String headerTxt = "";
//
//                    if (file.isDirectory() && !firstFolderShown) {
//                        showHeader = true;
//                        headerTxt = "DIRECTORIES";
//                        firstFolderShown = true;
//                    } else if (!file.isDirectory() && !firstFileShown) {
//                        showHeader = true;
//                        headerTxt = "FILES";
//                        firstFileShown = true;
//                    }
//
//                    ISAdapter isAdapter = new ISAdapter(file, showHeader, headerTxt);
//
//                    items.add(isAdapter);
//
//                }
//            }
//            int finalFolderCount = folderCount;
//            int finalFileCount = fileCount;

            handler.post(() -> {

                if (filesAndFolders == null || filesAndFolders.length == 0) {
                    noFileText.setVisibility(View.VISIBLE);
                    return;
                }

                noFileText.setVisibility(View.INVISIBLE);
                setLayout();
//                sortingBy = sharedPreferences.getString(Sorting_KEY_SORTING, "az");

//                inStorageToolbar.setSubtitle(finalFolderCount + " Folder " + finalFileCount + " File");

//                newSorting("az");
                recyclerView.setAdapter(fastAdapter);
                itemAdapter.set(items);


                fastAdapter.withOnClickListener((v, adapter, item, position) -> {

                    File clickedFile = item.getFile();

                    if (clickedFile.isDirectory()) {

                        currentPath = clickedFile.getAbsolutePath();

                        ArrayList<String> newPathList = new ArrayList<>(pathList);
                        newPathList.add(clickedFile.getName());

                        Intent intent = new Intent(this, InternalStorageActivity.class); // recall file Loading
                        intent.putExtra("path", clickedFile.getAbsolutePath()); //
                        intent.putStringArrayListExtra("pathList", newPathList);

                        startActivity(intent);

                    } else {
                        openWith(clickedFile);
                    }
                    return true;
                });


            });
        });
    }

    private void sorting() {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.bottomsheet_items, null);
        bottomSheetDialog.setContentView(view);

        RadioGroup btnSortingType = view.findViewById(R.id.btnRGImageSorting);

        // Mapping RadioButton IDs to sorting keys
        Map<Integer, String> sortMap = new HashMap<>();
        sortMap.put(R.id.rbBtnNDF, "NewestDate");
        sortMap.put(R.id.rbBtnODF, "OldestDate");
        sortMap.put(R.id.rbBtnLargeFirst, "LargerSize");
        sortMap.put(R.id.rbBtnSmallestFirst, "SmallerSize");
        sortMap.put(R.id.nameAZ, "az");
        sortMap.put(R.id.nameZA, "za");

        btnSortingType.setOnCheckedChangeListener((group, checkedId) -> {
            String sortKey = sortMap.get(checkedId);
            if (sortKey == null) {
                Toast.makeText(InternalStorageActivity.this, "Null Key", Toast.LENGTH_SHORT).show();
            } else if (sortKey != null) {
                sortItemsWith(sortKey);
                Toast.makeText(InternalStorageActivity.this, "Hello Hello", Toast.LENGTH_SHORT).show();
//                itemAdapter.setNewList(items);


                fastAdapter.notifyAdapterDataSetChanged();
            }
        });

        bottomSheetDialog.show();
    }

    void sortItemsWith(String sortingBy) {
        Arrays.sort(filesAndFolders, (f1, f2) -> {

            if (f1.isDirectory() && !f2.isDirectory()) return -1;
            if (!f1.isDirectory() && f2.isDirectory()) return 1;

            switch (sortingBy) {
                case "NewestDate":
                    return Long.compare(f2.lastModified(), f1.lastModified());
                case "OldestDate":
                    return Long.compare(f1.lastModified(), f2.lastModified());
                case "LargerSize":
                    return Long.compare(f2.length(), f1.length());
                case "SmallerSize":
                    return Long.compare(f1.length(), f2.length());
                case "za":
                    return f2.getName().compareToIgnoreCase(f1.getName());
                case "az":
                default:
                    return f1.getName().compareToIgnoreCase(f2.getName());
            }
        });

        items.clear();

        boolean firstFolderShown = false;
        boolean firstFileShown = false;

        folderCount = 0;
        fileCount = 0;

        // loading internal storage file here
        for (File file : filesAndFolders) {
            if (!file.getName().startsWith(".")) {
                boolean showHeader = false;
                String headerTxt = "";

                if (file.isDirectory()) {
                    folderCount++;
                    if (!firstFolderShown) {
                        showHeader = true;
                        headerTxt = "DIRECTORIES";
                        firstFolderShown = true;
                    }
                } else {
                    fileCount++;
                    if (!firstFileShown) {
                        showHeader = true;
                        headerTxt = "FILES";
                        firstFileShown = true;
                    }
                }

                items.add(new ISAdapter(file, showHeader, headerTxt));
            }
        }

        handler.post(() -> {
            itemAdapter.setNewList(items);
            inStorageToolbar.setSubtitle(folderCount + " Folder " + fileCount + " File");
        });

    }

    void internalStorageToolbar() {

        inStorageToolbar.setOnMenuItemClickListener(item -> {

            int id = item.getItemId();

            if (id == android.R.id.home) {
                Toast.makeText(context, "Go back", Toast.LENGTH_SHORT).show();
                onBackPressed();
                return true;
            } else if (id == R.id.isChangeView) {
                isGridView = !isGridView;
                setLayout();
                fastAdapter.notifyAdapterDataSetChanged();
                recreate();
                return true;
            } else if (id == R.id.isHome) {

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);

                return true;

            } else if (id == R.id.isCreateFolder) {
                createFolder();
                return true;
            } else if (id == R.id.isSearch) {

                Toast.makeText(this, "Searching Feature coming soon", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.isCutPast) {
                pasteFiles(currentDirectory);

            } else if (id == R.id.isCut) {

                List<String> selectedPaths = new ArrayList<>();
                for (ISAdapter itemCut : selectExtension.getSelectedItems()) {
                    selectedPaths.add(itemCut.getFile().getAbsolutePath());
                }
                ClipboardHelper.cutFiles(selectedPaths);
                Toast.makeText(this, "Cut " + selectedPaths.size(), Toast.LENGTH_SHORT).show();

                btnIVPaste.setVisibility(View.VISIBLE);
                btnIvCancel.setVisibility(View.VISIBLE);

                return true;

            } else if (id == R.id.isSorting) {
//                sortingIS();
                sorting();
                return true;
            } else if (id == R.id.isChangeView) {


            } else if (id == R.id.isCopy) {

                List<String> selectedPaths = new ArrayList<>();

                for (ISAdapter file : selectExtension.getSelectedItems()) {
                    selectedPaths.add(file.getFile().getAbsolutePath());
                }

                ClipboardHelper.copyFiles(selectedPaths);

                if (!selectedPaths.isEmpty()) {
                    ClipboardHelper.copyFiles(selectedPaths);
                    Toast.makeText(this, "Files copied!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "No file selected to copy", Toast.LENGTH_SHORT).show();
                }

                btnIVPaste.setVisibility(View.VISIBLE);
                btnIvCancel.setVisibility(View.VISIBLE);

                return true;
            } else if (id == R.id.isClose) {

                deleteSelectedFilesDirectly();
                return true;
            }

            return false;
        });
    }

    void toolbarSelection() {

        toolbarSelected.setOnMenuItemClickListener(item -> {

            int id = item.getItemId();

            if (id == R.id.selCopy) {

                List<String> selectedPaths = new ArrayList<>();

                for (ISAdapter file : selectExtension.getSelectedItems()) {
                    selectedPaths.add(file.getFile().getAbsolutePath());
                }

                if (!selectedPaths.isEmpty()) {
                    ClipboardHelper.copyFiles(selectedPaths);
                    Toast.makeText(this, "Files copied!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "No file selected to copy", Toast.LENGTH_SHORT).show();
                }

                btnIVPaste.setVisibility(View.VISIBLE);
                btnIvCancel.setVisibility(View.VISIBLE);

                toolbarSelected.setVisibility(View.GONE);
                inStorageToolbar.setVisibility(View.VISIBLE);


                return true;

            } else if (id == R.id.selDelete) {

//                for (ISAdapter file : selectExtension.getSelectedItems()) {
//                    File fileToDelete = new File(file.getFile().getAbsolutePath());
//                    if (fileToDelete.exists()) {
//                        if (fileToDelete.delete()) {
//                            Toast.makeText(this, "File deleted!", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(this, "Delete failed!", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
                deleteSelectedFilesDirectly();
                toolbarSelected.setVisibility(View.GONE);
                inStorageToolbar.setVisibility(View.VISIBLE);
                recreate();
                Toast.makeText(context, "Delete File", Toast.LENGTH_SHORT).show();
                return true;

            } else if (id == R.id.selAll) {
                if (!isAllSelected) {
                    selectExtension.select();

                } else {
                    selectExtension.deselect();
                }
                isAllSelected = !isAllSelected;

                return true;
            } else if (id == R.id.selCopy2) {
                return true;
            } else if (id == R.id.selMove) {
                List<String> selectedPaths = new ArrayList<>();
                for (ISAdapter itemCut : selectExtension.getSelectedItems()) {
                    selectedPaths.add(itemCut.getFile().getAbsolutePath());
                }
                ClipboardHelper.cutFiles(selectedPaths);
                Toast.makeText(this, "Cut " + selectedPaths.size(), Toast.LENGTH_SHORT).show();


                toolbarSelected.setVisibility(View.GONE);
                inStorageToolbar.setVisibility(View.VISIBLE);

                return true;
            } else if (id == R.id.selSelect) {

                Toast.makeText(context, "Delete File", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.selProperties) {
                Toast.makeText(context, "Delete File", Toast.LENGTH_SHORT).show();
                return true;
            }

            return false;
        });

    }

    void newPasting() {
//        if (copiedFiles.isEmpty()) {
//            Toast.makeText(this, "No files to paste", Toast.LENGTH_SHORT).show();
//            return true;
//        }
//
//        if (currentPath == null) {
//            Toast.makeText(this, "No destination path found", Toast.LENGTH_SHORT).show();
//            return true;
//        }
//
//        for (String filePath : copiedFiles) {
//            File source = new File(filePath);
//            File destination = getNonConflictingFile(new File(currentPath, source.getName())); // 🟡 <--- calling it here
//
//            try {
//                if (source.isDirectory()) {
//                    copyDirectory(source, destination);
//                } else {
//                    Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
//                }
//            } catch (IOException e) {
//                Toast.makeText(this, "Paste failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                e.printStackTrace();
//            }
//        }

    }

    private void pasteFiles(File destinationDir) {

        if (ClipboardHelper.isEmpty()) {
            Toast.makeText(this, "No files to paste", Toast.LENGTH_SHORT).show();
            return;
        }

        for (String path : ClipboardHelper.getFilePaths()) {
            File source = new File(path);
            File dest = new File(destinationDir, source.getName());

            if (dest.exists()) {
                dest = getNonConflictingFile(dest);
            }

            boolean success = false;

            if (ClipboardHelper.isCut()) {
                success = source.renameTo(dest);
                if (!success) {
                    success = moveFileManually(source, dest);
                }
            } else {
                if (source.isDirectory()) {
                    try {
                        copyDirectory(source, dest);
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
        itemAdapter.setNewList(new ArrayList<>());
        recreate();
//        fileLoading();

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
            FileInputStream in = new FileInputStream(source);
            FileOutputStream out = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            in.close();
            out.close();
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

    void setLayout() {
        recyclerView.setLayoutManager(isGridView ? new GridLayoutManager(this, 3) : new LinearLayoutManager(this));
    }

    private List<PathHistoryAdapter> buildPathSistoryList(List<String> pathList) {

        List<PathHistoryAdapter> list = new ArrayList<>();


        for (int i = 0; i < pathList.size(); i++) {
            boolean showNext = i < pathList.size() - 1;
            list.add(new PathHistoryAdapter(pathList.get(i), showNext));
        }

        return list;
    }

    private void openWith(File clickedFile) {

        if (clickedFile.isFile()) {

            // MINE = Multipurpose Internet Mail Extensions

            String mine = URLConnection.guessContentTypeFromName(clickedFile.getName());

            if (mine != null && mine.startsWith("audio")) {
                try {

                    Uri audioUri = FileProvider.getUriForFile(this, this.getPackageName() + ".provider", clickedFile);

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(audioUri, "audio/*");

                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    this.startActivity(intent);

                } catch (Exception e) {
                    Toast.makeText(this, "Cannot play audio " + e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            } else if (mine != null && mine.startsWith("image")) {
                try {
                    Uri imageUri = FileProvider.getUriForFile(this, this.getPackageName() + ".provider", clickedFile);

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(imageUri, "image/*");
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    this.startActivity(intent);

                } catch (Exception e) {
                    Toast.makeText(this, "Cannot open image " + e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            } else if (mine != null && mine.startsWith("application/pdf")) {

                try {
                    Uri appPdfUri = FileProvider.getUriForFile(this, this.getPackageName() + ".provider", clickedFile);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(appPdfUri, "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    this.startActivity(intent);

                } catch (Exception e) {
                    Toast.makeText(this, "No Pdf Reader Installed", Toast.LENGTH_SHORT).show();
                }
            } else if (mine != null && mine.startsWith("video/")) {
                try {

                    Uri videoUri = FileProvider.getUriForFile(this, this.getPackageName() + ".provider", clickedFile);

                    Intent videoIntent = new Intent(Intent.ACTION_VIEW);
                    videoIntent.setDataAndType(videoUri, "video/*");
                    videoIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    this.startActivity(videoIntent);

                } catch (Exception e) {
                    Toast.makeText(this, "NO Video Player" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }


        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_instorage, menu);

        try {
            Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
            method.setAccessible(true);
            method.invoke(menu, true);
        } catch (Exception e) {
            Toast.makeText(this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        menu.findItem(R.id.isCut).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_content_cut).actionBar());
        menu.findItem(R.id.isCutPast).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_content_paste).actionBar());
        menu.findItem(R.id.isSearch).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_search).actionBar());
        menu.findItem(R.id.isHome).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_home).actionBar());
        menu.findItem(R.id.isSorting).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_sort).actionBar());
        menu.findItem(R.id.isChangeView).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_list).actionBar());
        menu.findItem(R.id.isCreateFolder).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_folder).actionBar());
        menu.findItem(R.id.isClose).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_close).actionBar());

        return true;

    }

    @Override
    public void onBackPressed() {

        if (pathList == null || pathList.size() <= 1) {
            super.onBackPressed();
            return;
        }

        pathList.remove(pathList.size() - 1);

        String currentPath = getIntent().getStringExtra("path");
        if (currentPath != null) {
            File currentFolder = new File(currentPath);
            File parentFolder = currentFolder.getParentFile();

            if (parentFolder != null) {
                Intent intent = new Intent(this, InternalStorageActivity.class);
                intent.putExtra("path", parentFolder.getAbsolutePath());
                intent.putStringArrayListExtra("pathList", pathList);
                startActivity(intent);
                finish();
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    private void changeView() {

        SharedPreferences sharedPreferences = getSharedPreferences("UserView", MODE_PRIVATE);

        SharedPreferences.Editor editorUserView = sharedPreferences.edit();


        AlertDialog.Builder viewChangeAlertDialog = new AlertDialog.Builder(this);
        viewChangeAlertDialog.setTitle("View Types");

        LayoutInflater layoutInflater = LayoutInflater.from(this);

        View view = layoutInflater.inflate(R.layout.view_item, null);
        RadioGroup btnRGUserView = view.findViewById(R.id.btnRGUserView);
        viewChangeAlertDialog.setView(view);

        boolean isListView = sharedPreferences.getBoolean("isListView", true);

        if (isListView) {
            btnRGUserView.check(R.id.btnRbListView);
        } else {
            btnRGUserView.check(R.id.btnRbGriView);
        }


        viewChangeAlertDialog.setPositiveButton("Yes", (dialog, which) -> {

            int checkedId = btnRGUserView.getCheckedRadioButtonId();

            if (checkedId == R.id.btnRbListView) {
                editorUserView.putBoolean("isListView", true);
                editorUserView.apply();
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                itemAdapter.set(items);
                recyclerView.setAdapter(fastAdapter);

            } else if (checkedId == R.id.btnRbGriView) {
                editorUserView.putBoolean("isListView", false);
                editorUserView.apply();
                recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
                recyclerView.setAdapter(fastAdapter);
                itemAdapter.set(items);
            }

        });

        viewChangeAlertDialog.setNegativeButton("No", (dialog, which) -> dialog.dismiss());


        btnRGUserView.setOnCheckedChangeListener((group, checkedId) -> {

            if (checkedId == R.id.btnRbListView) {
                Toast.makeText(InternalStorageActivity.this, "ListView", Toast.LENGTH_SHORT).show();
            } else if (checkedId == R.id.btnRbGriView) {
                Toast.makeText(InternalStorageActivity.this, "GridView", Toast.LENGTH_SHORT).show();
            }

        });

        AlertDialog alertDialog = viewChangeAlertDialog.create();
        alertDialog.show();

    }

    private String getMimeType(File file) {
        String ext = MimeTypeMap.getFileExtensionFromUrl(file.getAbsolutePath());
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
    }

    private void showRenameDialog(File file) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Rename File");

        final EditText newFileName = new EditText(this);
        newFileName.setText(file.getName());
        newFileName.setSelection(file.getName().lastIndexOf('.'));
        builder.setView(newFileName);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newName = newFileName.getText().toString().trim();

            if (newName.isEmpty()) {
                Toast.makeText(context, "File name can't be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            File newFile = new File(file.getParent(), newName);

            if (newFile.exists()) {
                Toast.makeText(context, "File with this name already exists", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean renamedFile = file.renameTo(newFile);

            if (renamedFile) {
                Toast.makeText(context, "File renamed successfully", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(context, "Rename failed", Toast.LENGTH_SHORT).show();
            }

        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private String getSelectedFileSize() {

        long sumSize = 0;

        for (Integer selectedItem : selectExtension.getSelections()) {
            ISAdapter itemSize = itemAdapter.getAdapterItem(selectedItem);
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

    private void deleteSelectedFilesDirectly() {
        List<ISAdapter> selectedItems = new ArrayList<>(selectExtension.getSelectedItems());

        if (selectedItems.isEmpty()) {
            Toast.makeText(this, "No files selected", Toast.LENGTH_SHORT).show();
            return;
        }

        for (ISAdapter file : selectedItems) {
            File fileToDelete = new File(file.getFile().getAbsolutePath());
            if (fileToDelete.exists()) {
                if (fileToDelete.isDirectory()) {
                    deleteRecursively(fileToDelete); // in case it's a folder
                } else {
                    if (fileToDelete.delete()) {
                        Toast.makeText(this, "File deleted!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Delete failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

        // Clear selection and update UI
        selectExtension.deselect();
        ClipboardHelper.clear(); // Optional

        recreate(); // recreate current activity to refresh everything
        Toast.makeText(this, "Delete File", Toast.LENGTH_SHORT).show();
    }

    private boolean deleteRecursively(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File child : files) {
                    deleteRecursively(child);
                }
            }
        }
        return file.delete();
    }

    void createFolder() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Folder Name");

        EditText editText = new EditText(this);
        builder.setView(editText);


        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String myFolderName = editText.getText().toString().trim();

                if (!myFolderName.isEmpty()) {
                    File myFolder = new File(currentPath, myFolderName);

                    if (!myFolder.exists()) {
                        boolean created = myFolder.mkdirs();
                        if (created) {
                            Toast.makeText(InternalStorageActivity.this, "Folder Created", Toast.LENGTH_SHORT).show();
                            recreate();  // Refresh UI
                        } else {
                            Toast.makeText(InternalStorageActivity.this, "Failed to create folder", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(InternalStorageActivity.this, "Folder already exists", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(InternalStorageActivity.this, "Folder name can't be empty", Toast.LENGTH_LONG).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", null);

        builder.show();

    }

//    private void createxxFolder() {
//
//        File myFolder = new File(currentPath, "MyMessi");
//
//        if (!myFolder.exists()) {
//            boolean created = myFolder.mkdirs(); // mkdirs: creates full path if needed
//
//            if (created) {
//
//                Toast.makeText(this, "Folder created at: " + myFolder.getPath(), Toast.LENGTH_LONG).show();
//                fileLoading();
//            } else {
//                Toast.makeText(this, "Failed to create folder", Toast.LENGTH_LONG).show();
//            }
//        } else {
//            Toast.makeText(this, "Folder already exists", Toast.LENGTH_LONG).show();
//        }
//    }


    void myIsPasting() {
//        if (ClipboardHelper.isEmpty()) {
//            Toast.makeText(this, "No files to paste", Toast.LENGTH_SHORT).show();
//            return true;
//        }
//
//        if (currentPath == null) {
//            Toast.makeText(this, "No destination path found", Toast.LENGTH_SHORT).show();
//            return true;
//        }
//
//        List<String> copiedFiles = ClipboardHelper.getFilePaths();
//
//        executorService.execute(() -> {
//
//            for (String filePath : copiedFiles) {
//                File source = new File(filePath);
//                File destination = getNonConflictingFile(new File(currentPath, source.getName()));
//
//                try {
//                    if (source.isDirectory()) {
//                        copyDirectory(source, destination);
//                    } else {
//                        Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
//                    }
//                } catch (IOException e) {
//                    handler.post(() -> Toast.makeText(this, "Paste failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
//                    e.printStackTrace();
//                }
//            }
//
//            handler.post(() -> {
//                Toast.makeText(this, "Paste successful!", Toast.LENGTH_SHORT).show();
//                fileLoading(); // Refresh UI
//                ClipboardHelper.clear(); // Optional: clear after paste
//            });
//        });
//
//
//        Toast.makeText(this, "Paste successful!", Toast.LENGTH_SHORT).show();
//
//        itemAdapter.setNewList(new ArrayList<>());
//        fileLoading(); // Refresh the current directory
//        return true;

    }


    void xCopyRename() {
//        private void copyDirectory(File source, File destination) throws IOException {
//            if (!destination.exists()) {
//                destination.mkdirs();
//            }
//
//            File[] files = source.listFiles();
//            if (files != null) {
//                for (File file : files) {
//                    File newDest = new File(destination, file.getName());
//                    if (file.isDirectory()) {
//                        copyDirectory(file, newDest);
//                    } else {
//                        Files.copy(file.toPath(), newDest.toPath(), StandardCopyOption.REPLACE_EXISTING);
//                    }
//                }
//            }
//        }
//
//        private File getNonConflictingFile(File file) {
//            if (!file.exists()) return file;
//
//            String name = file.getName();
//            String baseName;
//            String extension = "";
//
//            int dotIndex = name.lastIndexOf('.');
//            if (dotIndex != -1) {
//                baseName = name.substring(0, dotIndex);
//                extension = name.substring(dotIndex);
//            } else {
//                baseName = name;
//            }
//
//            int index = 1;
//            File newFile;
//            do {
//                String newName = baseName + "(" + index + ")" + extension;
//                newFile = new File(file.getParent(), newName);
//                index++;
//            } while (newFile.exists());
//
//            return newFile;
//        }
    }

}

/*
void newSorting(String sortingBy) {
        Collections.sort(items, (f1, f2) -> {
            if (f1.getFile().isDirectory() && f1.getFile().isDirectory()) {
                return -1;
            } else if (!f1.getFile().isDirectory() && f2.getFile().isDirectory()) {
                return 1;
            } else {
                switch (sortingBy) {
                    case "name":
                        return f1.getFile().getName().compareToIgnoreCase(f2.getFile().getName());
                    case "NewestDate":
                        return Long.compare(f2.getFile().lastModified(), f1.getFile().lastModified());
                    case "OldestDate":
                        return Long.compare(f1.getFile().lastModified(), f2.getFile().lastModified());
                    case "LargerSize":
                        return Long.compare(f2.getFile().length(), f1.getFile().length());
                    case "SmallerSize":
                        return Long.compare(f1.getFile().length(), f2.getFile().length());
                    case "az":
                        return f1.getFile().getName().compareToIgnoreCase(f2.getFile().getName());
                    case "za":
                        return f2.getFile().getName().compareToIgnoreCase(f1.getFile().getName());
                    default:
                        return f1.getFile().getName().compareToIgnoreCase(f2.getFile().getName());
                }
            }
        });
    }
 */

// ok

/*
private void shortingAlert() {

        AlertDialog.Builder sortingBuilder = new AlertDialog.Builder(this);
        sortingBuilder.setTitle("Sorting");
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View dialogView = layoutInflater.inflate(R.layout.sorting_item, null);
        sortingBuilder.setView(dialogView);
        RadioGroup btnRadioGroup = dialogView.findViewById(R.id.rdBtnSorting);

        sortingBuilder.setPositiveButton("Ascending", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isAscending = true;
                sortingType(selectedSortingOption, isAscending);

                Toast.makeText(InternalStorageActivity.this, "Sorting By Ascending Order", Toast.LENGTH_SHORT).show();
            }
        });
        sortingBuilder.setNegativeButton("Descending", ((dialog, which) -> {

            isAscending = false;
            sortingType(selectedSortingOption, isAscending);

            Toast.makeText(this, "Sorting By Descending Order", Toast.LENGTH_SHORT).show();
        }));

        btnRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                selectedSortingOption = checkedId;

            }
        });

        AlertDialog alertDialog = sortingBuilder.create();
        alertDialog.show();
    }

    private void sortingType(int sortingOption, boolean isAscending) {

        Comparator<ISAdapter> comparator = null;

        if (sortingOption == R.id.rBtnName) {
//            comparator = Comparator.comparing(name1 -> name1.getFile().getName().toLowerCase());
            Collections.sort(items, (name1, name2) -> name1.getFile().getName().compareToIgnoreCase(name2.getFile().getName()));
        } else if (sortingOption == R.id.rBtnLastDate) {
//            comparator = Comparator.comparing(date1 -> date1.getFile().lastModified());
            items.sort((newFile, oldFile) -> Long.compare(oldFile.getFile().lastModified(), newFile.getFile().lastModified()));
        } else if (sortingOption == R.id.rBtnSize) {
//            comparator = Comparator.comparing(size1 -> size1.getFile().length());
            Collections.sort(items, (largeFile, smallFile) -> Long.compare(smallFile.getFile().length(), largeFile.getFile().length()));
        }

        if (comparator != null) {

            if (!isAscending) {
                comparator = comparator.reversed();
            }

            items.sort(comparator);
//            itemAdapter.setNewList(items);
            handler.post(() -> itemAdapter.set(items));
            fastAdapter.notifyAdapterDataSetChanged();
            recyclerView.setAdapter(fastAdapter);

        }

    }
 */

// sorting
/*
private void sortingIS() {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);

        View view = LayoutInflater.from(this).inflate(R.layout.bottomsheet_items, null);

        bottomSheetDialog.setContentView(view);
        sortingBy = "name";
        RadioGroup btnSortingType = view.findViewById(R.id.btnRGImageSorting);

//        btnSortingType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//
//                if (checkedId == R.id.rbBtnNDF) {
//                    // Collections.sort(items, (newFile, oldFile) -> Long.compare(oldFile.getFile().lastModified(), newFile.getFile().lastModified()));
//                    sortingBy = "NewestDate";
//                } else if (checkedId == R.id.rbBtnODF) {
////                Collections.sort(musicList, Comparator.comparingLong(newFile -> newFile.getFile().lastModified()));
////                    items.sort(Comparator.comparingLong(newFile -> newFile.getFile().lastModified()));
//                    sortingBy = "OldestDate";
//                } else if (checkedId == R.id.rbBtnLargeFirst) {
////                    Collections.sort(items, (largestFile, smallestFile) -> Long.compare(smallestFile.getFile().length(), largestFile.getFile().length()));
//                    sortingBy = "LargerSize";
//                } else if (checkedId == R.id.rbBtnSmallestFirst) {
////                    Collections.sort(items, (largestFile, smallestFile) -> Long.compare(largestFile.getFile().length(), smallestFile.getFile().length()));
//                    sortingBy = "SmallerSize";
//
//                } else if (checkedId == R.id.nameAZ) {
////                Collections.sort(musicList, (name1,name2) -> name1.getFile().getName().compareToIgnoreCase(name2.getFile().getName()));
////                    items.sort((name1, name2) -> name1.getFile().getName().compareToIgnoreCase(name2.getFile().getName()));
//                    sortingBy = "az";
//                } else if (checkedId == R.id.nameZA) {
////                    Collections.sort(items, (name1, name2) -> name2.getFile().getName().compareToIgnoreCase(name1.getFile().getName()));
//
//                    sortingBy = "za";
//
//
//                }
//
//                handler.post(() -> itemAdapter.setNewList(items));
//                fastAdapter.notifyAdapterDataSetChanged();
//
//            }
//        });
//
//        newSorting(sortingBy);
//        bottomSheetDialog.show();

    }
 */