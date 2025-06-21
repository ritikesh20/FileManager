package com.example.filemanager.internalstorage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filemanager.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InternalStorageActivity extends AppCompatActivity {

    private RecyclerView recyclerPathHistory;
    private ItemAdapter<PathHistoryAdapter> itemAdapterPathHistory;
    private FastAdapter<PathHistoryAdapter> fastAdapterPathHistory;
    private List<PathHistoryAdapter> historyPathList;
    private Context context;
    private boolean isAscending = true;
    private int selectedSortingOption = R.id.rBtnName;
    File clickedFile;
    int fileCount = 0;
    int folderCount = 0;

    boolean isCutMode = false;
    List<String> selectedFile;
    boolean isOpenWith = true;

    private ActivityResultLauncher<Intent> permissionLauncher;


    public static boolean isGridView = false;

    private RecyclerView recyclerView;
    private ItemAdapter<ISAdapter> itemAdapter;
    private FastAdapter<ISAdapter> fastAdapter;
    private List<ISAdapter> items;

    boolean isAllSelected = false;
    private ArrayList<String> pathList;

    TextView noFileText;
    private SelectExtension<ISAdapter> selectExtension;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    private String currentPath;

    Button btnIVPaste;
    ImageView btnIvCancel;

    private File currentDirectory;
    private Toolbar toolbarSelected;
    private Toolbar inStorageToolbar;
    FloatingActionButton btnNewMFolder;
    File[] filesAndFolders;

    private File apkFileToInstall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_internal_storage);

        btnNewMFolder = findViewById(R.id.btnNewFolderM);

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

        btnNewMFolder.setOnClickListener(v -> createFolderM());

        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        if (getPackageManager().canRequestPackageInstalls()) {
                            installApk(apkFileToInstall);
                        } else {
                            Toast.makeText(this, "Permission not granted to install unknown apps", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );


//        fileLoading();
        String path = getIntent().getStringExtra("path");

        if (path == null) {

            path = Environment.getExternalStorageDirectory().getAbsolutePath();

        }

        currentDirectory = new File(path);
        fileLoading(path);

        isPastDeselect();

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
                    btnNewMFolder.setVisibility(View.GONE);
                    toolbarSelected.setTitle(count + " Selected");
                    toolbarSelected.setSubtitle(getSelectedFileSize());
                    isOpenWith = false;

                } else {

                    toolbarSelected.setVisibility(View.GONE);
                    inStorageToolbar.setVisibility(View.VISIBLE);
                    btnNewMFolder.setVisibility(View.VISIBLE);
                    inStorageToolbar.setTitle("My files");
                    isOpenWith = true;
                    inStorageToolbar.setSubtitle(folderCount + " Folder" + fileCount + " Files");

                }

            });
            return true;

        });

        internalStorageToolbar();

        toolbarSelected.inflateMenu(R.menu.selected_menu);

        toolbarSelection();

        fastAdapter.withEventHook(new ClickEventHook<ISAdapter>() {
            @Override
            public void onClick(@NonNull View v, int position, @NonNull FastAdapter<ISAdapter> fastAdapter, @NonNull ISAdapter item) {

                showPopupMenu(v, item, position);

            }

            @Override
            public View onBind(@NonNull RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof ISAdapter.ViewHolder) {
                    return ((ISAdapter.ViewHolder) viewHolder).btnFileInfo;
                }
                return super.onBind(viewHolder);
            }
        });


    }

    // loading file with sorting
    void fileLoading(String path) {

        currentPath = path;

        pathList = getIntent().getStringArrayListExtra("pathList");

        if (pathList == null) {
            pathList = new ArrayList<>();
            pathList.add("Internal Storage");
        }

        executorService.execute(() -> {

            File root = new File(path);
            filesAndFolders = root.listFiles(); // File[] fileAndFolder

            // loading file with sorting
            sortItemsWith("NewestDate");

            handler.post(() -> {

                if (filesAndFolders == null || filesAndFolders.length == 0) {
                    noFileText.setVisibility(View.VISIBLE);
                    return;
                }

                noFileText.setVisibility(View.INVISIBLE);
                setLayout();

                recyclerView.setAdapter(fastAdapter);
                itemAdapter.set(items);

                fastAdapter.withOnClickListener((v, adapter, item, position) -> {

                    clickedFile = item.getFile();

                    if (isOpenWith) {

                        if (clickedFile.isDirectory()) {

                            currentPath = clickedFile.getAbsolutePath();

                            ArrayList<String> newPathList = new ArrayList<>(pathList);
                            newPathList.add(clickedFile.getName());

                            Intent intent = new Intent(this, InternalStorageActivity.class);
                            intent.putExtra("path", clickedFile.getAbsolutePath());
                            intent.putStringArrayListExtra("pathList", newPathList);

                            startActivity(intent);

                        } else {
                            openWith(clickedFile);
                        }
                    }
                    return true;
                });

            });
        });

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

    private List<PathHistoryAdapter> buildPathSistoryList
            (List<String> pathList) {

        List<PathHistoryAdapter> list = new ArrayList<>();


        for (int i = 0; i < pathList.size(); i++) {
            boolean showNext = i < pathList.size() - 1;
            list.add(new PathHistoryAdapter(pathList.get(i), showNext));
        }

        return list;
    }



    void internalStorageToolbar() {

        inStorageToolbar.setOnMenuItemClickListener(item -> {

            int id = item.getItemId();

            if (id == android.R.id.home) {
                onBackPressed();
                return true;
            } else if (id == R.id.isChangeView) {
                isGridView = !isGridView;
                setLayout();
                fastAdapter.notifyAdapterDataSetChanged();
                recreate();
                return true;
            } else if (id == R.id.isSorting) {
                sorting();
                return true;
            } else if (id == R.id.isSelectAll) {
                if (!isAllSelected) {
                    selectExtension.select();
                } else {
                    selectExtension.deselect();
                }
                isAllSelected = !isAllSelected;

                return true;
            } else if (id == R.id.isCreateFolder) {
                createFolderM();
                return true;
            } else if (id == R.id.selFileInfo) {

                sorting();
                return true;
            }

            return false;
        });
    }

    void toolbarSelection() {

        toolbarSelected.setOnMenuItemClickListener(item -> {

            int id = item.getItemId();

            if (id == R.id.selShare) {

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

            } else if (id == R.id.selMove) {
//                List<String> selectedPaths = new ArrayList<>();
//                for (ISAdapter itemCut : selectExtension.getSelectedItems()) {
//                    selectedPaths.add(itemCut.getFile().getAbsolutePath());
//                }
//                ClipboardHelper.cutFiles(selectedPaths);
//                Toast.makeText(this, "Cut " + selectedPaths.size(), Toast.LENGTH_SHORT).show();
//                btnIVPaste.setVisibility(View.VISIBLE);
//                btnIvCancel.setVisibility(View.VISIBLE);
                isCutMode = true;
                fileSelected();
//                ClipboardHelper.cut(selectedFile);

                btnIVPaste.setVisibility(View.VISIBLE);
                return true;
            } else if (id == R.id.selCopy) {
//                ClipboardHelper.copy(selectedFile);
                isCutMode = false;
                fileSelected();
                btnIVPaste.setVisibility(View.VISIBLE);

                return true;

            } else if (id == R.id.selAll) {
                if (!isAllSelected) {
                    selectExtension.select();
                } else {
                    selectExtension.deselect();
                }
                isAllSelected = !isAllSelected;

                return true;
            } else if (id == R.id.selDelete) {
                deleteSelectedFilesWithConfirmation();
//                deleteSelectedFilesDirectly();
                Toast.makeText(context, "Delete File Successfully", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.selFileInfo) {
//                deleteFile(new File(currentPath));
            }

            return false;
        });

    }

    private void fileSelected() {

        selectedFile = new ArrayList<>();

        for (ISAdapter select : selectExtension.getSelectedItems()) {
            selectedFile.add(select.getFile().getAbsolutePath());
        }

//        ClipboardHelper.cut(selectedFile);

        if (isCutMode) {
            ClipboardHelper.cut(selectedFile);
        } else {
            ClipboardHelper.copy(selectedFile);
        }

        btnIVPaste.setVisibility(View.VISIBLE);

    }

    void createFolderM() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = LayoutInflater.from(this).inflate(R.layout.new_folder, null);
        builder.setView(view);

        AlertDialog alertDialog = builder.create();

        TextInputEditText etNewFolder = view.findViewById(R.id.etNewFolder);

        etNewFolder.setInputType(InputType.TYPE_CLASS_TEXT);

        MaterialButton btnCancel = view.findViewById(R.id.btnCancel);

        MaterialButton btnCreateFolder = view.findViewById(R.id.btnCreateFolder);

        btnCreateFolder.setOnClickListener(v -> {

            String myFolderName = Objects.requireNonNull(etNewFolder.getText()).toString().trim();

            if (!myFolderName.isEmpty()) {
                File myFolder = new File(currentPath, myFolderName);

                if (!myFolder.exists()) {
                    boolean created = myFolder.mkdirs();
                    if (created) {
                        Toast.makeText(InternalStorageActivity.this, "Folder Created", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                        recreate();
                    } else {
                        Toast.makeText(InternalStorageActivity.this, "Failed to create folder", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(InternalStorageActivity.this, "Folder already exists", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(InternalStorageActivity.this, "Folder name can't be empty", Toast.LENGTH_LONG).show();
            }

        });

        btnCancel.setOnClickListener(v -> {
            alertDialog.dismiss();
        });


        alertDialog.show();

    }


    private void openWith(File clickedFile) {

        if (clickedFile.isFile()) {

//            MIME (Multipurpose Internet Mail Extensions) type ek standard hota hai jo batata hai ki file ka type/content kya hai.

            String mime = URLConnection.guessContentTypeFromName(clickedFile.getName());

            if (mime != null && mime.startsWith("audio")) {

                try {

                    Uri audioUri = FileProvider.getUriForFile(this, this.getPackageName() + ".provider", clickedFile);

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(audioUri, "audio/*");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    this.startActivity(intent);

                } catch (Exception e) {
                    Toast.makeText(this, "Cannot play audio " + e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            } else if (mime != null && mime.startsWith("image")) {

                try {

                    Uri imageUri = FileProvider.getUriForFile(this, this.getPackageName() + ".provider", clickedFile);

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(imageUri, "image/*");
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    this.startActivity(intent);

                } catch (Exception e) {
                    Toast.makeText(this, "Cannot open image " + e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            } else if (mime != null && mime.startsWith("application/pdf")) {

                try {
                    Toast.makeText(InternalStorageActivity.this, "PDF FIle", Toast.LENGTH_SHORT).show();
                    Uri appPdfUri = FileProvider.getUriForFile(this, this.getPackageName() + ".provider", clickedFile);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(appPdfUri, "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    this.startActivity(intent);

                } catch (Exception e) {
                    Toast.makeText(this, "No Pdf Reader Installed", Toast.LENGTH_SHORT).show();
                }
            } else if (mime != null && mime.startsWith("video/")) {
                try {

                    Uri videoUri = FileProvider.getUriForFile(this, this.getPackageName() + ".provider", clickedFile);

                    Intent videoIntent = new Intent(Intent.ACTION_VIEW);
                    videoIntent.setDataAndType(videoUri, "video/*");
                    videoIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    this.startActivity(videoIntent);

                } catch (Exception e) {
                    Toast.makeText(this, "NO Video Player" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else if (mime != null && mime.startsWith("apk")) {

                apkFileToInstall = clickedFile;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (!getPackageManager().canRequestPackageInstalls()) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        permissionLauncher.launch(intent);
                    } else {
                        installApk(clickedFile);
                    }
                } else {
                    installApk(clickedFile);
                }

            }

        }
    }

    private void installApk(File apkFile) {

        try {

            Uri apkUri = FileProvider.getUriForFile(this, this.getPackageName() + ".provider", apkFile);

            Intent apkIntent = new Intent(Intent.ACTION_VIEW);
            apkIntent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            apkIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            apkIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // optional, safer on newer Android versions
            startActivity(apkIntent);

        } catch (Exception e) {
            Toast.makeText(this, "No App Installer found: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void sorting() {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.bottomsheet_items, null);
        bottomSheetDialog.setContentView(view);

        RadioGroup btnSortingType = view.findViewById(R.id.btnRGImageSorting);

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


//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        MenuItem selectAllItem = menu.findItem(R.id.menu_select_all);
//
//        if (isSelected) {
//            selectAllItem.setTitle("Deselect All");
//
//        } else {
//            selectAllItem.setTitle("Select All");
//        }
//    }

    private void showPopupMenu(View anchorView, ISAdapter item, int position) {

        PopupMenu popupMenu = new PopupMenu(InternalStorageActivity.this, anchorView);
        popupMenu.getMenuInflater().inflate(R.menu.selected_menu, popupMenu.getMenu());

        MenuItem selectAllItem = popupMenu.getMenu().findItem(R.id.selAll);
        MenuItem renameFile = popupMenu.getMenu().findItem(R.id.selRename);


        if (isAllSelected) {

            selectAllItem.setTitle("Deselect All");
        } else {
            selectAllItem.setTitle("Select All");
        }

//        if (selectedFile.size() > 5 ){
//            renameFile.setVisible(false);
//        }
//        else {
//            renameFile.setVisible(true);
//        }

        popupMenu.setOnMenuItemClickListener(menuItem -> {

            int id = menuItem.getItemId();

            int count = selectExtension.getSelections().size();

            if (id == R.id.selOneFile) {
                boolean isOneFileSelected = false;

                if (!isOneFileSelected) {
                    inStorageToolbar.setVisibility(View.GONE);
                    toolbarSelected.setVisibility(View.VISIBLE);
                    count++;
                    selectExtension.toggleSelection(position);
                    isOpenWith = false;
                } else {
                    count--;
                    selectExtension.deselect();
                    isOpenWith = true;
                }

                toolbarSelected.setTitle(count + " Selected");

            } else if (id == R.id.selOpenWith) {
                openWith(item.getFile());
            } else if (id == R.id.selShare) {
                singleFileShare(item.getFile());

            } else if (id == R.id.selMove) {

//                selectedFile.add(item.getFile().getAbsolutePath());
//                ClipboardHelper.copyMove(selectedFile);
//                btnIVPaste.setVisibility(View.VISIBLE);

                return true;
            } else if (id == R.id.selCopy) {
//                selectedFile.add(item.getFile().getAbsolutePath());
//                ClipboardHelper.copyMove(selectedFile);
//                btnIVPaste.setVisibility(View.VISIBLE);
                return true;
            } else if (id == R.id.selRename) {
                showRenameDialog(item.getFile(), item, position);
                return true;
            } else if (id == R.id.selAll) {
                if (!isAllSelected) {
                    selectExtension.select();
                    isAllSelected = true;
                    selectAllItem.setTitle("Deselect All");
                } else {
                    selectExtension.deselect();
                    isAllSelected = false;
                    selectAllItem.setTitle("Select All");
                }
                return true;
            } else if (id == R.id.selDelete) {
                deleteFile(item.getFile(), item, position);
            } else if (id == R.id.selFileInfo) {

                return true;
            }
            return true;
        });
        popupMenu.show();

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return super.onPrepareOptionsMenu(menu);
    }


    void selectedClear(List<String> list) {
        list.clear();
        selectExtension.deselect();
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

    private void copyDirectory(File source, File destination) throws
            IOException {

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

        menu.findItem(R.id.isChangeView).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_list).actionBar());


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

        if (ext != null && !ext.isEmpty()) {
            String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext.toLowerCase());
            if (mime != null) {
                return mime;
            }
            if (file.getName().endsWith(".pdf")) return "application/pdf";
            if (file.getName().endsWith(".mp4")) return "video/mp4";
            if (file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg"))
                return "image/jpeg";
            if (file.getName().endsWith(".png")) return "image/png";
        }
        return "*/*";
    }

    private void showRenameDialog(File file, ISAdapter item, int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.rename_item, null);
        builder.setView(view);

        TextInputEditText etRename = view.findViewById(R.id.etRename);
        Button btnOk = view.findViewById(R.id.btnOkRename);
        Button btnCancel = view.findViewById(R.id.btnCancelRename);

        etRename.setInputType(InputType.TYPE_CLASS_TEXT);
        etRename.setText(file.getName());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        btnOk.setOnClickListener(v -> {

            String newRename = etRename.getText().toString().trim();

            if (!newRename.isEmpty()) {
                File newFile = new File(file.getParentFile(), newRename);
                if (newFile.exists()) {
                    Toast.makeText(this, "File/Folder already exists!", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean success = file.renameTo(newFile);

                if (success) {
                    item.setFile(newFile);
                    Toast.makeText(this, "Rename Successfully", Toast.LENGTH_SHORT).show();
                    fastAdapter.notifyItemChanged(position);

                } else {
                    Toast.makeText(this, "Rename failed", Toast.LENGTH_SHORT).show();
                }
                alertDialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(v -> alertDialog.dismiss());
    }

    void deleteFile(File singleFileDelete, ISAdapter item, int position) {

        AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(this);
        View deleteView = LayoutInflater.from(this).inflate(R.layout.delete_item, null);
        deleteBuilder.setView(deleteView);
        AlertDialog alertDialog = deleteBuilder.create();

        Button btnDeletedDelete = deleteView.findViewById(R.id.btnDeleteDelete);
        Button btnDeletedOk = deleteView.findViewById(R.id.btnDeleteCancel);

        alertDialog.show();

        btnDeletedDelete.setOnClickListener(v -> {

            if (singleFileDelete.exists()) {

                boolean isDeleteItem = deleteRecursively(singleFileDelete);

                if (isDeleteItem) {
                    Toast.makeText(context, "Deleted file: " + singleFileDelete.getName(), Toast.LENGTH_SHORT).show();
                    fastAdapter.notifyItemRemoved(position);
                    fastAdapter.notifyAdapterDataSetChanged();
                } else {
                    Toast.makeText(context, "Can't delete this file", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "File no exits", Toast.LENGTH_SHORT).show();
            }


            alertDialog.dismiss();
        });

        btnDeletedOk.setOnClickListener(v -> {
            alertDialog.dismiss();
        });


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
                    deleteRecursively(fileToDelete); // agar folder huaa to
                } else {
                    if (fileToDelete.delete()) {
                        Toast.makeText(this, "File deleted!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Delete failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

        selectExtension.deselect();
        ClipboardHelper.clear();

        recreate();
        Toast.makeText(this, "Delete File", Toast.LENGTH_SHORT).show();
    }


    private boolean deleteRecursively(File file) {
        boolean allDeleted = true;

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File child : files) {
                    if (!deleteRecursively(child)) {
                        allDeleted = false;
                    }
                }
            }
        }

        if (!file.delete()) {
            allDeleted = false;
        }

        return allDeleted;
    }

    private void deleteSelectedFilesWithConfirmation() {

        Set<ISAdapter> selectedItems = selectExtension.getSelectedItems();

        if (selectedItems.isEmpty()) {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = LayoutInflater.from(this).inflate(R.layout.delete_item, null);

        builder.setView(view);

        AlertDialog alertDialog = builder.create();

        Button btnDelete = findViewById(R.id.btnDeleteDelete);
        Button btnDeleteCancel = findViewById(R.id.btnDeleteCancel);

        btnDelete.setOnClickListener(v -> {
            boolean allSuccess = true;

            for (ISAdapter item : selectedItems) {
                File file = item.getFile();
                if (file != null && file.exists()) {
                    boolean success = deleteRecursive(file);
                    if (!success) {
                        allSuccess = false;
                    }
                }
            }

            if (allSuccess) {
                Toast.makeText(this, "Deleted successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Some files could not be deleted", Toast.LENGTH_SHORT).show();
            }

            selectExtension.deselect();
            fileLoading(currentPath);
        });

//        new AlertDialog.Builder(this)
//                .setTitle("Delete Confirmation")
//                .setMessage("Are you sure you want to delete selected files/folders?")
//                .setPositiveButton("Yes", (dialog, which) -> {
//
//
//                })
//                .setNegativeButton("Cancel", null)
//                .show();
        btnDelete.setOnClickListener(v -> {
            alertDialog.dismiss();
        });

    }

    private boolean deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            File[] children = fileOrDirectory.listFiles();
            if (children != null) {
                for (File child : children) {
                    if (!deleteRecursive(child)) {
                        return false;
                    }
                }
            }
        }
        return fileOrDirectory.delete();
    }


//    private void deleteRecursively(File file) {
//        if (file.isDirectory()) {
//            File[] files = file.listFiles();
//            if (files != null) {
//                for (File child : files) {
//                    deleteRecursively(child);
//                }
//            }
//            file.delete();
//        }
//
//    }


    void isPastDeselect() {

        if (ClipboardHelper.isIsPasting()) {

            btnIvCancel.setImageResource(R.drawable.cancel);

            btnIVPaste.setVisibility(View.VISIBLE);
            btnIvCancel.setVisibility(View.VISIBLE);

            btnIVPaste.setOnClickListener(v -> {
                ClipboardHelper.setIsPasting(false);
                pasteFiles(currentDirectory);
                btnIVPaste.setVisibility(View.GONE);
                btnIvCancel.setVisibility(View.GONE);
            });
            btnIvCancel.setOnClickListener(v -> {
                selectExtension.deselect();
                ClipboardHelper.setIsPasting(false);

                btnIVPaste.setVisibility(View.GONE);
                btnIvCancel.setVisibility(View.GONE);

            });


        }
    }


    void singleFileShare(File file) {

        try {

            if (file.isDirectory()) {
                Toast.makeText(InternalStorageActivity.this, "Your can't Share a folder", Toast.LENGTH_SHORT).show();
                return;
            }

            Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);

            String mimeTypes = getMimeType(file);
            if (mimeTypes == null) {
                mimeTypes = "*/*";
            }
            shareIntent.setType(mimeTypes);
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            shareIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "Share file"));

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    void shareSelectedFile() {

        ArrayList<File> shareFiles = new ArrayList<>();

        ArrayList<Uri> uris = new ArrayList<>();

        for (File file : shareFiles) {
            Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);

            uris.add(uri);
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);

        shareIntent.setType("*/*");

        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);

        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(Intent.createChooser(shareIntent, "Open with"));


    }

}