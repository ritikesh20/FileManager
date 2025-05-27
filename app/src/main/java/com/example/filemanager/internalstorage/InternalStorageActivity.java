package com.example.filemanager.internalstorage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filemanager.MainActivity;
import com.example.filemanager.R;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;


public class InternalStorageActivity extends AppCompatActivity {

    RecyclerView recyclerPathHistory;
    ItemAdapter<PathHistoryAdapter> itemAdapterPathHistory;
    FastAdapter<PathHistoryAdapter> fastAdapterPathHistory;
    List<PathHistoryAdapter> historyPathList;

    boolean isAscending = true;
    int selectedSortingOption = R.id.rBtnName;

    List<ISAdapter> items;
    public static boolean isGridView = false;

    private RecyclerView recyclerView;
    private TextView noFileText;
    private ItemAdapter<ISAdapter> itemAdapter;
    private FastAdapter<ISAdapter> fastAdapter;

    Toolbar inStorageToolbar;
    ArrayList<String> pathList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internal_storage);

        recyclerPathHistory = findViewById(R.id.rvPathHistory);
        recyclerPathHistory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        itemAdapterPathHistory = new ItemAdapter<>();
        fastAdapterPathHistory = FastAdapter.with(itemAdapterPathHistory);
        historyPathList = new ArrayList<>();

        inStorageToolbar = findViewById(R.id.toolbarInternalStorage);

        recyclerView = findViewById(R.id.rvInternalStorage);
        noFileText = findViewById(R.id.tvNoFile);

        itemAdapter = new ItemAdapter<>();
        fastAdapter = FastAdapter.with(itemAdapter);

        setSupportActionBar(inStorageToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My File");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        String path = getIntent().getStringExtra("path");

        pathList = getIntent().getStringArrayListExtra("pathList");

        if (pathList == null) {
            pathList = new ArrayList<>();
            pathList.add("Internal Storage");
        }

        itemAdapterPathHistory.setNewList(buildPathSistoryList(pathList));

//        for (String folderName : pathList) {
//            historyPathList.add(new PathHistoryAdapter(folderName));
//        }

//        itemAdapterPathHistory.setNewList(historyPathList);
        recyclerPathHistory.setAdapter(fastAdapterPathHistory);

        fastAdapterPathHistory.withOnClickListener((view, adapter, item, position) -> {

            ArrayList<String> newPathList = new ArrayList<>();

            for (int i = 0; i <= position; i++) {
                newPathList.add(itemAdapterPathHistory.getAdapterItem(i).getPathName());
            }

            // Build absolute path
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


        assert path != null;
        File root = new File(path);
        File[] filesAndFolders = root.listFiles();

        int fileCount = 0;
        int folderCount = 0;

        if (filesAndFolders == null || filesAndFolders.length == 0) {
            noFileText.setVisibility(View.VISIBLE);
            return;
        }

        noFileText.setVisibility(View.INVISIBLE);
        setLayout();

        items = new ArrayList<>();

        for (File file : filesAndFolders) {
            items.add(new ISAdapter(file));
            if (file.isDirectory()) {
                folderCount++;
            } else {
                fileCount++;
            }
        }


        String subTitle = folderCount + " Folder " + fileCount + " File";
        Objects.requireNonNull(getSupportActionBar()).setSubtitle(subTitle);

        fastAdapter.withOnClickListener((v, adapter, item, position) -> {

            File clickedFile = item.getFile();

            if (clickedFile.isDirectory()) {

//                pathList.add(clickedFile.getName());

                ArrayList<String> newPathList = new ArrayList<>(pathList);
                newPathList.add(clickedFile.getName());

                Intent intent = new Intent(this, InternalStorageActivity.class);
                intent.putExtra("path", clickedFile.getAbsolutePath());
//                intent.putStringArrayListExtra("pathList", pathList);
                intent.putStringArrayListExtra("pathList", newPathList);

                startActivity(intent);

            } else {
                openWith(clickedFile);
            }

            return true;
        });

        recyclerView.setAdapter(fastAdapter);
        itemAdapter.set(items);


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
                    Toast.makeText(this, "Cannot play audio", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
            else if (mine != null && mine.startsWith("image")) {
                try {
                    Uri imageUri = FileProvider.getUriForFile(
                            this,
                            this.getPackageName() + ".provider",
                            clickedFile
                    );

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(imageUri, "image/*");
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    this.startActivity(intent);

                } catch (Exception e) {
                    Toast.makeText(this, "Cannot open image" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
            else if (mine != null && mine.startsWith("application/pdf")) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(clickedFile), "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                try {
                    startActivity(intent);
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
                    e.printStackTrace();
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
            e.printStackTrace();
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

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
            Intent intent = new Intent(InternalStorageActivity.this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.isSearch) {
            Toast.makeText(this, "Searching Feature coming soon", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.isSorting) {
            shortingAlert();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // If we are at root, finish normally
        if (pathList == null || pathList.size() <= 1) {
            super.onBackPressed();
            return;
        }

        // Remove the last folder from path list
        pathList.remove(pathList.size() - 1);

        // Get parent folder path
        String currentPath = getIntent().getStringExtra("path");
        if (currentPath != null) {
            File currentFolder = new File(currentPath);
            File parentFolder = currentFolder.getParentFile();

            if (parentFolder != null) {
                Intent intent = new Intent(this, InternalStorageActivity.class);
                intent.putExtra("path", parentFolder.getAbsolutePath());
                intent.putStringArrayListExtra("pathList", pathList);
                startActivity(intent);
                finish(); // close current activity to prevent stacking
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }


    private void shortingAlert() {

        AlertDialog.Builder sortingBuilder = new AlertDialog.Builder(this);
        sortingBuilder.setTitle("Sorting");

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View dialogView = layoutInflater.inflate(R.layout.sorting_item, null);
        sortingBuilder.setView(dialogView);

        RadioGroup btnRadioGroup = dialogView.findViewById(R.id.rdBtnSorting);

        btnRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                selectedSortingOption = checkedId;
            }

        });


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


        AlertDialog alertDialog = sortingBuilder.create();
        alertDialog.show();

    }

    private void sortingType(int sortingOption, boolean isAscending) {

        Comparator<ISAdapter> comparator = null;

        if (sortingOption == R.id.rBtnName){
            comparator = Comparator.comparing(name1 -> name1.getFile().getName().toLowerCase());
        }
        else if (sortingOption == R.id.rBtnLastDate) {
            comparator = Comparator.comparing(date1 -> date1.getFile().lastModified());
        }
        else if (sortingOption == R.id.rBtnSize){
            comparator = Comparator.comparing(size1 -> size1.getFile().length());
        }

        if (comparator != null){
            if (!isAscending){
                comparator = comparator.reversed();
            }

//            Collections.sort(items,comparator);
            items.sort(comparator);
            itemAdapter.setNewList(items);
            fastAdapter.notifyAdapterDataSetChanged();
            recyclerView.setAdapter(fastAdapter);

        }

    }


}


