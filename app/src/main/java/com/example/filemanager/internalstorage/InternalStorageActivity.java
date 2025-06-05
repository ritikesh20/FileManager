package com.example.filemanager.internalstorage;

import android.app.AlertDialog;
import android.content.Context;
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
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.filemanager.MainActivity;
import com.example.filemanager.R;
import com.example.filemanager.music.MusicAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.select.SelectExtension;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.hdodenhof.circleimageview.CircleImageView;

public class InternalStorageActivity extends AppCompatActivity {

    private RecyclerView recyclerPathHistory;
    private ItemAdapter<PathHistoryAdapter> itemAdapterPathHistory;
    private FastAdapter<PathHistoryAdapter> fastAdapterPathHistory;
    private List<PathHistoryAdapter> historyPathList;
    private Context context;
    private boolean isAscending = true;
    private int selectedSortingOption = R.id.rBtnName;

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

    private Toolbar toolbarSelected;
    private Toolbar inStorageToolbar;

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

        itemAdapter = new ItemAdapter<>();
        fastAdapter = FastAdapter.with(itemAdapter);

        inStorageToolbar = findViewById(R.id.toolbarInternalStorage);
        toolbarSelected = findViewById(R.id.toolbarSelected);

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

        fileLoading();

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

        toolbarSelected.inflateMenu(R.menu.selected_menu);


        fastAdapter.withOnLongClickListener((v, adapter, item, position) -> {
            inStorageToolbar.setVisibility(View.GONE);
            toolbarSelected.setVisibility(View.VISIBLE);
            selectExtension.toggleSelection(position);
            return false;
        });

        selectExtension.withSelectionListener((item, selected) -> {

            int count = selectExtension.getSelections().size();

            if (count > 0) {
                toolbarSelected.setTitle(count + " Selected");
                toolbarSelected.setSubtitle(getSelectedFileSize());
            } else {
                toolbarSelected.setTitle("0 Selected");
            }


        });

        internalStorageToolbar();

        toolbarSelection();

    }

    void fileLoading() {
        String path = getIntent().getStringExtra("path");

        pathList = getIntent().getStringArrayListExtra("pathList");

        if (pathList == null) {
            pathList = new ArrayList<>();
            pathList.add("Internal Storage");
        }
        executorService.execute(() -> {

            assert path != null;
            File root = new File(path);
            File[] filesAndFolders = root.listFiles();

            int fileCount = 0;
            int folderCount = 0;

            List<ISAdapter> tempItems = new ArrayList<>();

            if (filesAndFolders != null) {
                for (File file : filesAndFolders) {
                    tempItems.add(new ISAdapter(file));
                    if (file.isDirectory()) {
                        folderCount++;
                    } else {
                        fileCount++;
                    }
                }
            }

            int finalFolderCount = folderCount;
            int finalFileCount = fileCount;

            handler.post(() -> {

                if (filesAndFolders == null || filesAndFolders.length == 0) {
                    noFileText.setVisibility(View.VISIBLE);
                    return;
                }

                noFileText.setVisibility(View.INVISIBLE);
                setLayout();

                items = tempItems;

                inStorageToolbar.setSubtitle(finalFolderCount + " Folder " + finalFileCount + " File");

                recyclerView.setAdapter(fastAdapter);
                itemAdapter.set(items);

                fastAdapter.withOnClickListener((v, adapter, item, position) -> {

                    File clickedFile = item.getFile();

                    if (clickedFile.isDirectory()) {

                        ArrayList<String> newPathList = new ArrayList<>(pathList);
                        newPathList.add(clickedFile.getName());

                        Intent intent = new Intent(this, InternalStorageActivity.class);
                        intent.putExtra("path", clickedFile.getAbsolutePath());
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
            } else if (id == R.id.isHome) {

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;

            } else if (id == R.id.isSearch) {
                Toast.makeText(this, "Searching Feature coming soon", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.isSorting) {
                sortingIS();
                return true;
            }

            return false;
        });
    }

    void toolbarSelection() {
        toolbarSelected.setOnMenuItemClickListener(item -> {

            int id = item.getItemId();

            if (id == R.id.selCopy) {
                Toast.makeText(context, "CopyFile", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.selDelete) {
                Toast.makeText(context, "Delete File", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.selAll) {
                if (!isAllSelected) {
                    selectExtension.select(); // .getAdapterItems()

                } else {
                    selectExtension.deselect();
                }
                isAllSelected = !isAllSelected;

                return true;
            } else if (id == R.id.selCopy2) {
                Toast.makeText(context, "Delete File", Toast.LENGTH_SHORT).show();
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

    void setLayout() {
        recyclerView.setLayoutManager(isGridView ? new GridLayoutManager(this, 3) : new LinearLayoutManager(this));
    }

    private List<PathHistoryAdapter> buildPathSistoryList(List<String> pathList) {

        List<PathHistoryAdapter> list = new ArrayList<>();

//        for (String name : pathList) {
//            list.add(new PathHistoryAdapter(name));
//        }

        for (int i = 0; i < pathList.size(); i++) {
            boolean showNext = i < pathList.size() - 1;
            list.add(new PathHistoryAdapter(pathList.get(i), showNext));
        }

        return list;
    }


    void openWith(File clickedFile) {

        if (clickedFile.isFile()) {

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
            } else {
                Toast.makeText(this, "Unsupported File " + mine, Toast.LENGTH_SHORT).show();
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

        menu.findItem(R.id.isSearch).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_search).actionBar());
        menu.findItem(R.id.isHome).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_home).actionBar());
        menu.findItem(R.id.isSorting).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_sort).actionBar());
        menu.findItem(R.id.isChangeView).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_list).actionBar());
        menu.findItem(R.id.isCreateFile).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_description).actionBar());
        menu.findItem(R.id.isCreateFolder).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_folder).actionBar());
        menu.findItem(R.id.isAdvance).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_settings).actionBar());
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


    private void sortingIS() {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);

        View view = LayoutInflater.from(this).inflate(R.layout.bottomsheet_items, null);

        bottomSheetDialog.setContentView(view);

        RadioGroup btnSortingType = view.findViewById(R.id.btnRGImageSorting);

        btnSortingType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.rbBtnNDF) {
                    Collections.sort(items, (newFile, oldFile) -> Long.compare(oldFile.getFile().lastModified(), newFile.getFile().lastModified()));

                } else if (checkedId == R.id.rbBtnODF) {
//                Collections.sort(musicList, Comparator.comparingLong(newFile -> newFile.getFile().lastModified()));
                    items.sort(Comparator.comparingLong(newFile -> newFile.getFile().lastModified()));
                } else if (checkedId == R.id.rbBtnLargeFirst) {
                    Collections.sort(items, (largestFile, smallestFile) -> Long.compare(smallestFile.getFile().length(), largestFile.getFile().length()));
                } else if (checkedId == R.id.rbBtnSmallestFirst) {
                    Collections.sort(items, (largestFile, smallestFile) -> Long.compare(largestFile.getFile().length(), smallestFile.getFile().length()));
                } else if (checkedId == R.id.nameAZ) {
//                Collections.sort(musicList, (name1,name2) -> name1.getFile().getName().compareToIgnoreCase(name2.getFile().getName()));
                    items.sort((name1, name2) -> name1.getFile().getName().compareToIgnoreCase(name2.getFile().getName()));
                } else if (checkedId == R.id.nameZA) {
                    Collections.sort(items, (name1, name2) -> name2.getFile().getName().compareToIgnoreCase(name1.getFile().getName()));
                }

                handler.post(() -> itemAdapter.setNewList(items));
                fastAdapter.notifyAdapterDataSetChanged();

            }
        });


        bottomSheetDialog.show();

    }


    void showBottomSheetIS(File file) {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);

        View view = LayoutInflater.from(this).inflate(R.layout.bottomsheet_info_item, null);

        bottomSheetDialog.setContentView(view);


        long sizeInBytes = file.length();
        String convertedSize;

        LinearLayoutCompat btnCopy;
        LinearLayoutCompat btnMove;
        LinearLayoutCompat btnSafeBox;
        LinearLayoutCompat btnRename;
        LinearLayoutCompat btnDelete;
        LinearLayoutCompat btnOpenWith;
        LinearLayoutCompat btnShare;

        CircleImageView filePreviewIcon;

        filePreviewIcon = view.findViewById(R.id.previewFileCIV);

        btnCopy = view.findViewById(R.id.btnFileCopy);
        btnMove = view.findViewById(R.id.btnFileMove);
        btnSafeBox = view.findViewById(R.id.btnFileSaveBox);

        btnRename = view.findViewById(R.id.btnFileRename);
        btnOpenWith = view.findViewById(R.id.btnFileOpenWith);
        btnShare = view.findViewById(R.id.btnFileShare);
        btnDelete = view.findViewById(R.id.btnFileDelete);

        TextView tvFileName = view.findViewById(R.id.tvRenameFileName);
        TextView tvFileSize = view.findViewById(R.id.tvFileSize);

        tvFileName.setText(file.getName());
        long reSize;


        if (sizeInBytes < 1024) {
            convertedSize = sizeInBytes + " B";
        } else if (sizeInBytes < 1024 * 1024) {
            reSize = sizeInBytes / 1024;
            convertedSize = reSize + " KB";
        } else if (sizeInBytes < 1024 * 1024 * 1014) {
            reSize = sizeInBytes / (1024 * 1024);
            convertedSize = reSize + " MB";
        } else {
            reSize = sizeInBytes / (1024 * 1024 * 1024);
            convertedSize = reSize + " GB";
        }

        tvFileSize.setText(convertedSize);

        btnRename.setOnClickListener(v -> {
            showRenameDialog(file);
            Toast.makeText(this, "File Rename", Toast.LENGTH_SHORT).show();
        });

        if (file.isDirectory()) {
            filePreviewIcon.setImageResource(R.drawable.openfolder);
        } else {
            String name = file.getName().toLowerCase();

            if (name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".jpeg")) {
                Glide.with(this).load(file).into(filePreviewIcon);
            } else if (name.endsWith(".mp3")) {
                filePreviewIcon.setImageResource(R.drawable.musicicons);
            } else if (name.endsWith(".mp4") || name.endsWith(".avi")) {
                Glide.with(this).load(file).into(filePreviewIcon);
            } else if (name.endsWith("apk")) {
                filePreviewIcon.setImageResource(R.drawable.apkicons);
            }
            else if (name.endsWith(".txt")) {
                filePreviewIcon.setImageResource(R.drawable.txtfile);
            }

            else {
                filePreviewIcon.setImageResource(R.drawable.newdocument);
            }
        }


        btnOpenWith.setOnClickListener(v -> {

            Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, getMimeType(file));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, "Open with"));

        });

        btnShare.setOnClickListener(v -> {

            Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
            Intent shareIntent = new Intent(Intent.ACTION_VIEW);
            shareIntent.setType(getMimeType(file));
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(shareIntent);

        });


        btnDelete.setOnClickListener(v -> {

            if (file.delete()) {
                Toast.makeText(this, file.getName() + " Delete File", Toast.LENGTH_SHORT).show();
                recreate();
            }

        });


        bottomSheetDialog.show();
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


}


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