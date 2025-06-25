package com.example.filemanager.DemoPage;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filemanager.MainActivity;
import com.example.filemanager.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbarHome;
    private ItemAdapter<IItem> itemAdapter;
    private FastAdapter<IItem> fastAdapter;
    private RecyclerView recyclerView;

    private List<IItem> finalList;
    private List<File> folders;
    private List<File> files;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbarHome = findViewById(R.id.toolbarHome);
        setSupportActionBar(toolbarHome);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Home Page");
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        itemAdapter = new ItemAdapter<>();
        fastAdapter = FastAdapter.with(itemAdapter);

        finalList = new ArrayList<>();
        folders = new ArrayList<>();
        files = new ArrayList<>();

        recyclerView.setAdapter(fastAdapter);

        fastAdapter.withOnClickListener(new OnClickListener<IItem>() {
            @Override
            public boolean onClick(View v, IAdapter<IItem> adapter, IItem item, int position) {
                if (item instanceof FolderItem) {
                    File clickedFolder = ((FolderItem) item).getFolder();
                    Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
                    intent.putExtra("path", clickedFolder.getAbsolutePath());
                    startActivity(intent);
                } else if (item instanceof FileItem) {
                    File clickedFile = ((FileItem) item).getFile();
                    openWith(clickedFile);
                }

                return false;
            }
        });


        fileLoading();
    }

    private void fileLoading() {

        String path = getIntent().getStringExtra("path");

        if (path == null) {
            path = Environment.getExternalStorageDirectory().getPath();
        }

        folders.clear();
        files.clear();
        finalList.clear();

        File currentDir = new File(path);
        File[] allFiles = currentDir.listFiles();

        if (allFiles != null) {
            for (File file : allFiles) {
                if (file.getName().startsWith(".")) {
                    continue;
                }
                if (file.isDirectory()) {
                    folders.add(file);
                } else {
                    files.add(file);
                }
            }
        }

        rebuildFinalList();
        itemAdapter.set(finalList);
    }

    private void rebuildFinalList() {

        finalList.clear();

        if (!folders.isEmpty()) {
            finalList.add(new HeaderItem("Directories"));
            for (File folder : folders) {
                finalList.add(new FolderItem(folder));
            }
        }

        if (!files.isEmpty()) {
            finalList.add(new HeaderItem("Files"));
            for (File file : files) {
                finalList.add(new FileItem(file));
            }
        }

    }

    private void openWith(File clickedFile) {

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

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.isSorting) {
            sortingIS();
        }

        return true;
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

                    folders.sort((newFile, oldFile) -> Long.compare(oldFile.lastModified(), newFile.lastModified()));
                    files.sort((newFile, oldFile) -> Long.compare(oldFile.lastModified(), newFile.lastModified()));

                } else if (checkedId == R.id.rbBtnODF) {
                    folders.sort((newFile, oldeFile) -> Long.compare(newFile.lastModified(), oldeFile.lastModified()));
                    files.sort((newFile, oldFile) -> Long.compare(newFile.lastModified(), oldFile.lastModified()));

                }
                else if (checkedId == R.id.rbBtnLargeFirst) {
                    folders.sort((newFile, oldFile) -> Long.compare(oldFile.lastModified(), newFile.lastModified()));
                    files.sort((newFile, oldFile) -> Long.compare(oldFile.lastModified(), newFile.lastModified()));
                }
                else if (checkedId == R.id.rbBtnSmallestFirst) {
                    folders.sort((newFile, oldeFile) -> Long.compare(newFile.length(), oldeFile.length()));
                    files.sort((newFile, oldFile) -> Long.compare(newFile.length(), oldFile.length()));
                }
                else if (checkedId == R.id.nameAZ) {

                    folders.sort((name1, name2) -> name2.getName().compareToIgnoreCase(name1.getName()));
                    files.sort((name1, name2) -> name1.getName().compareToIgnoreCase(name2.getName()));

                } else if (checkedId == R.id.nameZA) {

                    folders.sort((name1, name2) -> name1.getName().compareToIgnoreCase(name2.getName()));
                    files.sort((name1, name2) -> name1.getName().compareToIgnoreCase(name2.getName()));
                }
                rebuildFinalList();
                itemAdapter.set(finalList);
                fastAdapter.notifyAdapterDataSetChanged();

            }
        });

        bottomSheetDialog.show();
    }
}
