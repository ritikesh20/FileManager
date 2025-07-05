package com.example.filemanager.internalstorage;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filemanager.MediaStoreHelper;
import com.example.filemanager.R;
import com.example.filemanager.favouritesection.FavouriteDao;
import com.example.filemanager.favouritesection.FavouriteDatabase;
import com.example.filemanager.favouritesection.FavouriteItem;
import com.example.filemanager.safefolder.SafeBoxFile;
import com.example.filemanager.safefolder.SafeBoxFileDB;
import com.example.filemanager.trashbin.RecycleBinItem;
import com.example.filemanager.trashbin.RecyclerBinDatabase;
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
import com.mikepenz.iconics.view.IconicsImageView;

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
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InternalStorageActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferencesIS;

    boolean isAllFileSelected = false;

    // fastAdapter Path History
    private RecyclerView recyclerPathHistory;
    private ItemAdapter<PathHistoryAdapter> itemAdapterPathHistory;
    private FastAdapter<PathHistoryAdapter> fastAdapterPathHistory;

    Context context;

    File clickedFile;
    int fileCount = 0;
    int folderCount = 0;

    private boolean isCutMode = false;
    private boolean isCopyMode = false;

    private List<String> selectedFile;
    boolean isOpenWith = true;

    private ActivityResultLauncher<Intent> permissionLauncher;

    public static boolean isGridView = false;

    // fastAdapter SetUp
    private RecyclerView recyclerView;
    private ItemAdapter<ISAdapter> itemAdapter;
    private FastAdapter<ISAdapter> fastAdapter;
    private List<ISAdapter> items;
    private ArrayList<String> pathList;

    boolean isAllSelected = false;

    TextView noFileText;
    private SelectExtension<ISAdapter> selectExtension;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    private String currentPath;

    Button btnIVPaste;
    ImageView btnIvCancel;

    public static File currentDirectory;

    private Toolbar inStorageToolbar;

    FloatingActionButton btnNewMFolder;

    File[] filesAndFolders;

    private File apkFileToInstall;

    private ISAdapter singleItemSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_internal_storage);

        btnNewMFolder = findViewById(R.id.btnNewFolderM);

        recyclerPathHistory = findViewById(R.id.rvPathHistory);
        recyclerPathHistory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        itemAdapterPathHistory = new ItemAdapter<>();
        fastAdapterPathHistory = FastAdapter.with(itemAdapterPathHistory);

        recyclerView = findViewById(R.id.rvInternalStorage);
        noFileText = findViewById(R.id.tvNoFile);

        btnIVPaste = findViewById(R.id.btnIconPast);
        btnIvCancel = findViewById(R.id.btnIconCancel);

        itemAdapter = new ItemAdapter<>();
        fastAdapter = FastAdapter.with(itemAdapter);

        inStorageToolbar = findViewById(R.id.toolbarInternalStorage);

        items = new ArrayList<>();

        setSupportActionBar(inStorageToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Files");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        sharedPreferencesIS = getSharedPreferences("Image_Prefs", MODE_PRIVATE);

        selectExtension = fastAdapter.getExtension(SelectExtension.class);

        if (selectExtension == null) {
            selectExtension = new SelectExtension<>();
            fastAdapter.addExtension(selectExtension);
        }

        selectExtension.withSelectable(true);
        selectExtension.withMultiSelect(true);
        selectExtension.withSelectWithItemUpdate(true);

        btnNewMFolder.setOnClickListener(v -> createFolderM());

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (getPackageManager().canRequestPackageInstalls()) {
                    installApk(apkFileToInstall);
                } else {
                    Toast.makeText(this, "Permission not granted to install unknown apps", Toast.LENGTH_SHORT).show();
                }
            }
        });

        setLayout();
//      fileLoading();

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

            Toast.makeText(this, "index " + position, Toast.LENGTH_SHORT).show();

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

        fileLongClick();

        fastAdapter.withEventHook(new ClickEventHook<>() {

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
            String savedSortKey = sharedPreferencesIS.getString("sorting_key", "az");
            sortItemsWith(savedSortKey);

            handler.post(() -> {

                if (filesAndFolders == null || filesAndFolders.length == 0) {
                    noFileText.setVisibility(View.VISIBLE);
                    return;
                }

                noFileText.setVisibility(View.INVISIBLE);

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

    // file loading with sorting
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

    private void sorting() {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.bottomsheet_items, null);
        bottomSheetDialog.setContentView(view);

        RadioGroup btnSortingType = view.findViewById(R.id.btnRGImageSorting);

        btnSortingType.setOnCheckedChangeListener((group, checkedId) -> {

            String sortKey = "az";

            if (checkedId == R.id.rbBtnNDF) {
                sortKey = "NewestDate";
            } else if (checkedId == R.id.rbBtnODF) {
                sortKey = "OldestDate";
            } else if (checkedId == R.id.rbBtnLargeFirst) {
                sortKey = "LargerSize";
            } else if (checkedId == R.id.rbBtnSmallestFirst) {
                sortKey = "SmallerSize";
            } else if (checkedId == R.id.nameAZ) {
                sortKey = "az";
            } else if (checkedId == R.id.nameZA) {
                sortKey = "za";
            }

            if (sortKey == null) {
                Toast.makeText(InternalStorageActivity.this, "Null Key", Toast.LENGTH_SHORT).show();
            } else {

                sortItemsWith(sortKey);

                SharedPreferences.Editor sortByKey = sharedPreferencesIS.edit();
                sortByKey.putString("sorting_key", sortKey);
                sortByKey.apply();

                Toast.makeText(InternalStorageActivity.this, "Sorting applied: " + sortKey, Toast.LENGTH_SHORT).show();

                fastAdapter.notifyAdapterDataSetChanged();
            }
        });

        bottomSheetDialog.show();
    }

    private void fileSelected() {

        selectedFile = new ArrayList<>();

        for (ISAdapter select : selectExtension.getSelectedItems()) {
            selectedFile.add(select.getFile().getAbsolutePath());
        }

        if (isCutMode) {
            CopyMovePastHelper.cut(selectedFile);
        } else if (isCopyMode) {
            CopyMovePastHelper.copy(selectedFile);
        }

    }

    private List<PathHistoryAdapter> buildPathSistoryList(List<String> pathList) {

        List<PathHistoryAdapter> list = new ArrayList<>();

        for (int i = 0; i < pathList.size(); i++) {
            boolean showNext = i < pathList.size() - 1;
            list.add(new PathHistoryAdapter(pathList.get(i), showNext));

        }
        return list;
    }

    private void fileLongClick() {

        fastAdapter.withOnLongClickListener((v, adapter, itemx, position) -> {

            selectExtension.withSelectionListener((item, selected) -> {

                int count = selectExtension.getSelections().size();

                if (count > 0) {

                    inStorageToolbar.setTitle(count + " selected");
                    btnNewMFolder.setVisibility(View.GONE);
                    isOpenWith = false;
                    inStorageToolbar.setSubtitle(getSelectedFileSize());

                } else {

                    btnNewMFolder.setVisibility(View.VISIBLE);
                    inStorageToolbar.setTitle("My files");
                    isOpenWith = true;
                    inStorageToolbar.setSubtitle(folderCount + " Folder " + fileCount + " Files");
//                    recreate();
                }

            });

            singleItemSelect = itemx;
            selectExtension.toggleSelection(position);

            return false;

        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        int selectCount = selectExtension.getSelections().size();

        MenuItem newFolder = menu.findItem(R.id.file_newFolder);
        MenuItem searching = menu.findItem(R.id.file_search);
        MenuItem isListGridView = menu.findItem(R.id.file_ChangeView);
        MenuItem selectAll = menu.findItem(R.id.file_Selected);
        MenuItem sortingType = menu.findItem(R.id.file_SortBy);
        MenuItem openWith = menu.findItem(R.id.file_OpenWith);
        MenuItem share = menu.findItem(R.id.file_Share);
        MenuItem trash = menu.findItem(R.id.file_Trash);
        MenuItem moveTo = menu.findItem(R.id.file_Move);
        MenuItem copyTo = menu.findItem(R.id.file_Copy);
        MenuItem rename = menu.findItem(R.id.file_Rename);
        MenuItem addToFav = menu.findItem(R.id.file_AddToStarred);
        MenuItem moveSafeFolder = menu.findItem(R.id.file_SafeFolder);
        MenuItem googleDrive = menu.findItem(R.id.file_BackToGoogleDrive);
        MenuItem delete = menu.findItem(R.id.file_Delete);
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
            newFolder.setVisible(true);

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
            trash.setVisible(true);
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
        menu.findItem(R.id.file_Trash).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_delete).color(Color.BLACK).actionBar());


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
        } else if (id == R.id.file_ChangeView) {
            isGridView = !isGridView;
            setLayout();
            fastAdapter.notifyDataSetChanged();
            return true;
        } else if (id == R.id.file_Selected) {

            selectAllFile();

            return true;
        } else if (id == R.id.file_newFolder) {
            createFolderM();
        } else if (id == R.id.file_Share) {
            fileSelected();
            shareSelectedFiles();
            selectedClear(selectedFile);
            return true;
        } else if (id == R.id.file_Trash) {
            trashSheet();
            return true;
        } else if (id == R.id.file_SortBy) {
            sorting();
            return true;
        } else if (id == R.id.file_OpenWith) {
            fileSelected();
            shareSelectedFiles();
            return true;
        } else if (id == R.id.file_Move) {
            isCutMode = true;
            isCopyMode = false;
            fileSelected();
            MediaStoreHelper.goStorageTypes(this, "Move to");
            btnIVPaste.setVisibility(View.VISIBLE);
            return true;
        } else if (id == R.id.file_Copy) {
            MediaStoreHelper.goStorageTypes(this, "Copy to");
            isCutMode = false;
            isCopyMode = true;
            fileSelected();
            btnIVPaste.setVisibility(View.VISIBLE);
            return true;
        } else if (id == R.id.file_Rename) {
            if (singleItemSelect != null && singleItemSelect.getFile() != null) {
                showRenameDialog(singleItemSelect.getFile(), singleItemSelect, 0);
                fastAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (id == R.id.file_AddToStarred) {
            favoriteItems();
            return true;
        } else if (id == R.id.file_SafeFolder) {
            sendToSafeFolder(singleItemSelect.getFile(), () -> fileLoading(currentPath));
            return true;
        } else if (id == R.id.file_BackToGoogleDrive) {
            fileSelected();
            shareSelectedFilesGoogleDrive();
            return true;
        } else if (id == R.id.file_Delete) {
            deleteSelectedFile();
            return true;
        } else if (id == R.id.file_FileInfo) {
            sendFileInfo(singleItemSelect.getFile());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void selectAllFile() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isAllFileSelected) {
                    selectExtension.select();
                } else {
                    selectExtension.deselect();
                }

                isAllFileSelected = !isAllFileSelected;

            }
        });


    }


    private void showPopupMenu(View anchorView, ISAdapter item, int position) {

        PopupMenu popupMenu = new PopupMenu(InternalStorageActivity.this, anchorView);
        popupMenu.getMenuInflater().inflate(R.menu.selected_menu, popupMenu.getMenu());

        MenuItem selectAllItem = popupMenu.getMenu().findItem(R.id.selAll);
        MenuItem selItem = popupMenu.getMenu().findItem(R.id.selOneFile);

        if (isAllSelected) {
            selectAllItem.setTitle("Deselect All");
            selItem.setTitle("Deselect All");

        } else {
            selectAllItem.setTitle("Select All");
            selItem.setTitle("Select All");
        }

        popupMenu.setOnMenuItemClickListener(menuItem -> {

            int id = menuItem.getItemId();
            boolean isOneFileSelected = false;
            if (id == R.id.selOneFile) {
                if (!isOneFileSelected) {
                    selectExtension.toggleSelection(position);
                    isOpenWith = false;
                } else {
                    selectExtension.deselect();
                    isOpenWith = true;
                }
                return true;
            } else if (id == R.id.selOpenWith) {
//                fileSelected();
                selectExtension.toggleSelection(position);
                openWith(item.getFile());
                return true;
            } else if (id == R.id.selShare) {
                singleFileShare(item.getFile());
                return true;
            } else if (id == R.id.selMove) {
                MediaStoreHelper.goStorageTypes(this, "Move to");
                fileSelected();
                selectedFile.add(item.getFile().getAbsolutePath());
                CopyMovePastHelper.cut(selectedFile);
                btnIVPaste.setVisibility(View.VISIBLE);
                return true;
            } else if (id == R.id.selCopy) {
                MediaStoreHelper.goStorageTypes(this, "Copy to");
                fileSelected();
                selectedFile.add(item.getFile().getAbsolutePath());
                CopyMovePastHelper.copy(selectedFile);
                btnIVPaste.setVisibility(View.VISIBLE);
                return true;
            } else if (id == R.id.selRename) {
                showRenameDialog(item.getFile(), item, position);
                return true;
            } else if (id == R.id.selAll) {

                runOnUiThread(() -> {
                    if (!isAllSelected) {
                        selectExtension.select();
                        isAllSelected = true;
                        selectAllItem.setTitle("Deselect All");
                    } else {
                        selectExtension.deselect();
                        isAllSelected = false;
                        selectAllItem.setTitle("Select All");
                    }
                });

                return true;
            } else if (id == R.id.selDelete) {
                selectExtension.select(position);
                deleteSelectedFile();
                return true;
            } else if (id == R.id.selFileInfo) {
                sendFileInfo(item.getFile());
                return true;
            } else if (id == R.id.selFile_SafeFolder) {
                sendToSafeFolder(item.getFile(), () -> fileLoading(currentPath));
                return true;
            } else if (id == R.id.selMoveToBin) {
                selectExtension.select(position);
                trashSheet();
                return true;
            }
            return true;
        });

        popupMenu.show();

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
            shareIntent.setType("*/*");
        }

        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share With"));
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

                File isMyFolderCreated = new File(currentPath, myFolderName);

                if (!isMyFolderCreated.exists()) {

                    boolean created = isMyFolderCreated.mkdirs();

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

            // MIME Multipurpose Internet Mail Extensions

            String mime = URLConnection.guessContentTypeFromName(clickedFile.getName());

            if (mime != null && mime.startsWith("audio")) {

                try {

                    Uri audioUri = FileProvider.getUriForFile(InternalStorageActivity.this, this.getPackageName() + ".provider", clickedFile);

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
            } else if (mime != null && mime.startsWith("application/vnd.android.package-archive")) {

                apkFileToInstall = clickedFile;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (!getPackageManager().canRequestPackageInstalls()) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        permissionLauncher.launch(intent);
                    } else {
                        installApk(apkFileToInstall);
                    }
                } else {
                    installApk(apkFileToInstall);
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
            apkIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(apkIntent);

        } catch (Exception e) {
            Toast.makeText(this, "No App Installer found: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    void selectedClear(List<String> list) {
        list.clear();
        selectExtension.deselect();
    }

    private void pasteFiles(File destinationDir) {

        if (CopyMovePastHelper.isEmpty()) {
            Toast.makeText(this, "No files selected", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {

            for (String path : CopyMovePastHelper.getFilePaths()) {
                File source = new File(path);
                File dest = new File(destinationDir, source.getName());

                if (dest.exists()) {
                    runOnUiThread(() -> Toast.makeText(this, "File Already Exists: " + dest.getName(), Toast.LENGTH_SHORT).show());
                    continue;
                }

                boolean success = false;

                if (CopyMovePastHelper.isCut()) {
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
                            success = false;
                        }
                    } else {
                        success = copyFile(source, dest);
                    }
                }

                if (!success) {
                    File finalSource = source;
                    runOnUiThread(() ->
                            Toast.makeText(this, "Failed: " + finalSource.getName(), Toast.LENGTH_SHORT).show()
                    );
                }
            }


            runOnUiThread(() -> {

                Toast.makeText(this, "Paste complete", Toast.LENGTH_SHORT).show();

                CopyMovePastHelper.clear();
                itemAdapter.setNewList(new ArrayList<>());
                recreate();

            });

        }).start();
    }

//    private File getNonConflictingFile(File file) {
//
//        if (!file.exists()) {
//            return file;
//        }
//
//        String name = file.getName();
//        String baseName;
//        String extension = "";
//
//
//        int dotIndex = name.lastIndexOf('.');
//
//        if (dotIndex != -1 && file.isFile()) {
//            baseName = name.substring(0, dotIndex);
//            extension = name.substring(dotIndex);
//        } else {
//            baseName = name;
//        }
//
//        int index = 1;
//        File newFile;
//        do {
//            String newName = baseName + "(" + index + ")" + extension;
//            newFile = new File(file.getParent(), newName);
//            index++;
//        } while (newFile.exists());
//
//        return newFile;
//
//    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        MenuInflater menuInflater = getMenuInflater();
//        menuInflater.inflate(R.menu.menu_instorage, menu);
//
//        try {
//            Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
//            method.setAccessible(true);
//            method.invoke(menu, true);
//        } catch (Exception e) {
//            Toast.makeText(this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//
//        menu.findItem(R.id.isChangeView).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_list).actionBar());
//
//
//        return true;
//
//    }

    private boolean copyFile(File source, File dest) {

        try {

            FileInputStream pickLocation = new FileInputStream(source);
            FileOutputStream dropLocation = new FileOutputStream(dest);

            byte[] buffer = new byte[1024]; // temp store file

            int len;

            while ((len = pickLocation.read(buffer)) > 0) {
                dropLocation.write(buffer, 0, len);
            }

            pickLocation.close();
            dropLocation.close();

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
                    Toast.makeText(context, "Folder Already Exits", Toast.LENGTH_SHORT).show();
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

        if (CopyMovePastHelper.isIsPasting()) {

            btnIvCancel.setImageResource(R.drawable.cancel);

            btnIVPaste.setVisibility(View.VISIBLE);
            btnIvCancel.setVisibility(View.VISIBLE);

            btnIVPaste.setOnClickListener(v -> {
                CopyMovePastHelper.setIsPasting(false);
                pasteFiles(currentDirectory); // provide current folder location to pasting file function
                btnIVPaste.setVisibility(View.GONE);
                btnIvCancel.setVisibility(View.GONE);
            });

            btnIvCancel.setOnClickListener(v -> {
                selectExtension.deselect();
                CopyMovePastHelper.setIsPasting(false);
                btnIVPaste.setVisibility(View.GONE);
                btnIvCancel.setVisibility(View.GONE);
            });

        }
    }

    void setLayout() {
        if (isGridView) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
        recyclerView.setAdapter(fastAdapter);
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

            String newFileRename = Objects.requireNonNull(etRename.getText()).toString().trim();

            if (!newFileRename.isEmpty()) {

                File newFile = new File(file.getParentFile(), newFileRename);

                if (newFile.exists()) {
                    Toast.makeText(this, "File/Folder already exists!", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean isNameChange = file.renameTo(newFile);

                if (isNameChange) {
                    item.setFile(newFile);
                    Toast.makeText(this, "File Name Change Rename Successfully", Toast.LENGTH_SHORT).show();
                    fastAdapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(this, "Rename failed", Toast.LENGTH_SHORT).show();
                }
                alertDialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(v -> alertDialog.dismiss());
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

    private void deleteSelectedFile() {

        Set<ISAdapter> selectedItems = selectExtension.getSelectedItems();

        if (selectedItems.isEmpty()) {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = LayoutInflater.from(this).inflate(R.layout.delete_item, null);

        builder.setView(view);

        AlertDialog alertDialog = builder.create();

        Button btnDelete = view.findViewById(R.id.btnDeleteDelete);
        Button btnDeleteCancel = view.findViewById(R.id.btnDeleteCancel);

        btnDelete.setOnClickListener(v -> {
            boolean allSuccess = true;

            for (ISAdapter item : selectedItems) {
                File file = item.getFile();
                if (file != null && file.exists()) {
                    boolean success = deleteRecursively(file);
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


            handler.post(new Runnable() {
                @Override
                public void run() {
                    selectExtension.deselect();
                    fileLoading(currentPath);
                }
            });

            alertDialog.dismiss();
        });

        btnDeleteCancel.setOnClickListener(v -> {
            alertDialog.dismiss();
        });

        alertDialog.show();

    }

    // Move file to Safe Box
    void sendToSafeFolder(File selectedFileSend, Runnable onComplete) {

        SafeBoxFileDB safeBoxFileDB = SafeBoxFileDB.getInstance(InternalStorageActivity.this);

        executorService.execute(() -> {

            File safeFolder = new File(getFilesDir(), "SafeBox");

            if (!safeFolder.exists()) {
                safeFolder.mkdirs();
            }
            File destFile = new File(safeFolder, selectedFileSend.getName());

            try {

                Files.copy(selectedFileSend.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                boolean deleted = selectedFileSend.delete();

                if (deleted) {

                    SafeBoxFile safeBoxFile = new SafeBoxFile();
                    safeBoxFile.fileName = selectedFileSend.getName();
                    safeBoxFile.fileOriginalPath = selectedFileSend.getAbsolutePath();
                    safeBoxFile.fileSavePath = destFile.getAbsolutePath();

                    safeBoxFileDB.safeBoxFileDao().insert(safeBoxFile);

                    handler.post(() -> {
                        Toast.makeText(this, "Moved to Safe Box", Toast.LENGTH_SHORT).show();
                        if (onComplete != null) onComplete.run();
                    });

                } else {
                    handler.post(() -> Toast.makeText(this, "File couldn't be deleted", Toast.LENGTH_SHORT).show());
                }

            } catch (IOException e) {
                e.printStackTrace();
                handler.post(() -> Toast.makeText(this, "Failed to move", Toast.LENGTH_SHORT).show());
            }

        });
    }

    private void shareSelectedFilesGoogleDrive() {

        if (selectedFile == null || selectedFile.isEmpty()) {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<Uri> uriList = new ArrayList<>();

        for (ISAdapter select : selectExtension.getSelectedItems()) {
            File file = select.getFile();

            Uri uri = FileProvider.getUriForFile(
                    InternalStorageActivity.this,
                    getPackageName() + ".provider",
                    file
            );

            uriList.add(uri);
        }

        Intent googleDriveIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        googleDriveIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList);
        googleDriveIntent.setType("*/*");
        googleDriveIntent.setPackage("com.google.android.apps.docs");
        googleDriveIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(Intent.createChooser(googleDriveIntent, "Upload to Google Drive"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(InternalStorageActivity.this, "Google Drive app is not installed", Toast.LENGTH_LONG).show();
        }
    }

    private String getMimeType(Uri uri) {
        ContentResolver cr = getContentResolver();
        return cr.getType(uri);
    }

    void singleFileShare(File file) {

        try {
            if (file.isDirectory()) {
                Toast.makeText(InternalStorageActivity.this, "Your can't Share a folder", Toast.LENGTH_SHORT).show();
                return;
            }

            Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);

            shareIntent.setType("*/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "Share file"));

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    void singleFileOpen(File file) {


        if (file == null || !file.exists()) {
            Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
            return;
        }
        if (file.isDirectory()) {
            Toast.makeText(InternalStorageActivity.this, "Your can't a folder", Toast.LENGTH_SHORT).show();
            return;
        }

        try {


            Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);

            Intent shareIntent = new Intent(Intent.ACTION_VIEW);
            shareIntent.setDataAndType(uri, "*/*");
            shareIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(Intent.createChooser(shareIntent, "Open With"));

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    void sendFileInfo(File fileInfo) {

        Intent intent = new Intent(InternalStorageActivity.this, FileInfoActivity.class);
        intent.putExtra("filepath", fileInfo.getAbsolutePath());

        startActivity(intent);

    }

    private void trashSheet() {

        Set<ISAdapter> selectedItems = selectExtension.getSelectedItems();

        if (selectedItems.isEmpty()) {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            return;
        }

        BottomSheetDialog trashSheet = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.trash_item, null);
        CardView cardView = view.findViewById(R.id.btnMoveToTrash);

        IconicsImageView deleteIconSmall = view.findViewById(R.id.deleteIconSmall);

        deleteIconSmall.setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_delete).color(Color.BLACK).actionBar());

        trashSheet.setContentView(view);
        trashSheet.show();

        RecyclerBinDatabase db = RecyclerBinDatabase.getInstance(this);

        cardView.setOnClickListener(v -> {

            executorService.execute(() -> {

                File recyclerBinDir = new File(getFilesDir(), "newRecycler_bin");

                if (!recyclerBinDir.exists()) {
                    recyclerBinDir.mkdirs();
                }

                for (ISAdapter isAdapter : selectedItems) {

                    File file = new File(isAdapter.getFile().getPath());

                    File dest = new File(recyclerBinDir, file.getName());

                    try {
                        Files.copy(file.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);

                        boolean move = file.delete();

                        if (move) {

                            RecycleBinItem entity = new RecycleBinItem();
                            entity.fileName = dest.getName();
                            entity.originalPath = file.getAbsolutePath();
                            entity.deletedPath = dest.getAbsolutePath();
                            entity.deletedAt = System.currentTimeMillis();

                            db.recycleBinDao().insert(entity);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    selectExtension.deselect();
                                    Toast.makeText(InternalStorageActivity.this, file.getName() + " moved to Bin", Toast.LENGTH_SHORT).show();
                                    fastAdapter.notifyDataSetChanged();
                                }
                            });


                        } else {
                            runOnUiThread(() ->
                                    Toast.makeText(this, "Failed to delete: " + file.getName(), Toast.LENGTH_SHORT).show());
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() ->
                                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }

                }

                runOnUiThread(trashSheet::dismiss);

            });

        });
    }

    private void favoriteItems() {

        Set<ISAdapter> selectedFile = selectExtension.getSelectedItems();
        FavouriteDao dao = FavouriteDatabase.getInstance(getApplicationContext()).favouriteDao();

        if (selectedFile.isEmpty()) {
            Toast.makeText(context, "No file selected", Toast.LENGTH_SHORT).show();
            return;
        }

        executorService.execute(() -> {


            for (ISAdapter selected : selectedFile) {
                FavouriteItem favouriteItem = new FavouriteItem(
                        selected.getFile().getAbsolutePath(),
                        selected.getFile().getName(),
                        System.currentTimeMillis()
                );
                dao.insert(favouriteItem);
            }

            handler.post(() -> {
                Toast.makeText(this, "File add to Favorite ", Toast.LENGTH_SHORT).show();
            });


        });

    }


}
