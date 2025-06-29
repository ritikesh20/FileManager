package com.example.filemanager.recentfiles;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filemanager.FileOperation;
import com.example.filemanager.MediaStoreHelper;
import com.example.filemanager.R;
import com.example.filemanager.document.FileHelperAdapter;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RecentFilesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ItemAdapter<FileHelperAdapter> itemAdapter;
    private FastAdapter<FileHelperAdapter> fastAdapter;
    private File file;
    private ProgressBar progressBar;
    private List<FileHelperAdapter> recentFileList = new ArrayList<>();
    private Toolbar toolbarRecentFile;
    int limit = 0;

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


        progressBar = findViewById(R.id.progressBarRecent);

        recyclerView = findViewById(R.id.recyclerViewRecentFile);
        itemAdapter = new ItemAdapter<>();
        fastAdapter = FastAdapter.with(itemAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(fastAdapter);

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
            itemAdapter.setNewList(recentFileList);
            progressBar.setVisibility(View.GONE);
            fastAdapter.notifyDataSetChanged();

        });

        fastAdapter.withOnClickListener(new OnClickListener<FileHelperAdapter>() {
            @Override
            public boolean onClick(View v, IAdapter<FileHelperAdapter> adapter, FileHelperAdapter item, int position) {

                FileOperation.fileOpenWith(v.getContext(), item.getUri(), item.getMineTypes());

                return true;
            }
        });

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


//    private String fileSizeCal(long sizeInBytes) {
//
//        float kb = sizeInBytes / 1024f;
//        float mb = kb / 1024f;
//        float gb = mb / 1024f;
//
//
//        if (gb >= 1) {
//            return String.format("%.2f GB", gb);
//        } else if (mb >= 1) {
//            return String.format("%.2f MB", mb);
//        } else {
//            return String.format("%.2f KB", kb);
//        }
//
//    }


//    private void loadRecentFileWithBatching() {
//
////        File root = Environment.getExternalStorageDirectory();
////
////        ExecutorService executorService = Executors.newSingleThreadExecutor();
////
////        executorService.submit(new Runnable() {
////            @Override
////            public void run() {
////
////                List<newRecentFile> recentFiles = getListRecentFile(root);
//////                recentFiles.sort((o1, o2) -> Long.compare(o2.getFile().lastModified(), o1.getFile().lastModified()));
////
////                int batchSize = 500;
////                int totalFiles = recentFiles.size();
////
////                for (int i = 0; i < totalFiles; i += batchSize) {
////                    int end = Math.max(i + batchSize, totalFiles);
////                    List<newRecentFile> batch = recentFiles.subList(i, end);
////
////                    runOnUiThread(new Runnable() {
////                        @Override
////                        public void run() {
////                            itemAdapter.setNewList(batch);
////                        }
////                    });
////
////
////                    try {
////                        Thread.sleep(200);
////                    } catch (InterruptedException e) {
////                        e.printStackTrace();
////                    }
////
////                }
////
////                runOnUiThread(() -> progressBar.setVisibility(View.GONE));
////            }
////        });
////
////        executorService.shutdown();
//
//    }
//
//    private List<newRecentFile> getListRecentFile(File dir) {
//
//        List<newRecentFile> fileItems = new ArrayList<>();
////
////        File[] files = dir.listFiles();
////
////        if (files != null) {
////            for (File file : files) {
////                if (file.isDirectory()) {
////                    fileItems.addAll(getListRecentFile(file));
////                } else {
////                    if (isMediaFile(file)) {
////                        fileItems.add(new newRecentFile(file));
////                    }
////                }
////            }
////        }
//        return fileItems;
////
//    }
//
//    private boolean isMediaFile(File file) {
//
////        String[] imageExtensions = {".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp"};
////        String[] videoExtensions = {".mp4", ".avi", ".mov", ".mkv", ".3gp", ".webm"};
////        String[] audioExtensions = {".mp3", ".aac", ".wav", ".flac", ".ogg", ".m4a"};
////        String[] apkExtensions = {".apk"};
////
////        String name = file.getName().toLowerCase();
////
////        for (String ext : imageExtensions) {
////            if (name.endsWith(ext)) return true;
////        }
////        for (String ext : videoExtensions) {
////            if (name.endsWith(ext)) return true;
////        }
////        for (String ext : audioExtensions) {
////            if (name.endsWith(ext)) return true;
////        }
////        for (String apk : apkExtensions) {
////            if (name.endsWith(apk)) return true;
////        }
//        return false;
////
//    }


}


// loading media types file

//void loadMediaStoreFiles() {
//
//    ExecutorService executor = Executors.newSingleThreadExecutor();
//
//    executor.execute(new Runnable() {
//        @RequiresApi(api = Build.VERSION_CODES.Q)
//        @Override
//        public void run() {
//
//            List<FileHelperAdapter> recentFileList = new ArrayList<>();
//
//            Uri recentFileUri;
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                recentFileUri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL);
//            } else {
//                recentFileUri = MediaStore.Files.getContentUri("external");
//            }
//
//            String[] projection = {
//
//                    MediaStore.Files.FileColumns._ID,
//                    MediaStore.Files.FileColumns.DISPLAY_NAME,
//                    MediaStore.Files.FileColumns.MIME_TYPE,
//                    MediaStore.Files.FileColumns.DATE_ADDED,
//                    MediaStore.Files.FileColumns.SIZE,
//
//            };
//
//            // WHERE mime_type = ? OR mime_type = ? OR mime_type = ?
//
//            String selection = MediaStore.Files.FileColumns.MIME_TYPE + " IN (?, ?, ?, ?, ?, ?)";
//
//            String[] selectionArgs = new String[]{
//                    "application/pdf",
//                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
//                    "text/plain",
//                    "image/jpeg",
//                    "image/png",
//                    "video/mp4"
//            };
//
//            String sorting = MediaStore.Files.FileColumns.DATE_ADDED + " DESC LIMIT 10";
//
//            ContentResolver contentResolver = getContentResolver();
//
//            Cursor cursor = contentResolver.query(recentFileUri, projection, selection, selectionArgs, sorting);
//
//            if (cursor != null) {
//
//                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID);
//                int nameColum = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME);
//                int mimeTypesColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE);
//                int fileDateColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED);
//                int fileSizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE);
//
//
//                while (cursor.moveToNext()) {
//
//                    long id = cursor.getLong(idColumn);
//                    String name = cursor.getString(nameColum);
//                    String mimeType = cursor.getString(mimeTypesColumn);
//                    long date = cursor.getLong(fileDateColumn);
//                    long sizeInBytes = cursor.getLong(fileSizeColumn);
//
//
//                    Uri uriRecentFiles = ContentUris.withAppendedId(recentFileUri, id);
//                    // name
//                    // mime
//                    String fileDate = DateFormat.format("dd MMM yyyy", new Date(date)).toString();
//                    String sizeCalculation = fileSizeCal(sizeInBytes);
//
////                        FileHelperAdapter documentAdapter = new FileHelperAdapter(uriRecentFiles, name, mimeType, fileDate, sizeCalculation);
//                    recentFileList.add(new FileHelperAdapter(uriRecentFiles, name, mimeType, fileDate, sizeCalculation));
//
//                }
//
//                cursor.close();
//
//            }
//
//            runOnUiThread(() -> {
//                itemAdapter.setNewList(recentFileList);
//                progressBar.setVisibility(View.GONE);
//            });
//        }
//    });
//    executor.shutdown();
//}


// normal file loading with ExecutorService
/*

    private void loadRecentFile() {

        File root = Environment.getExternalStorageDirectory();

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                List<newRecentFile> recentFiles = getListRecentFile(root);
                recentFiles.sort((o1, o2) -> Long.compare(o2.getFile().lastModified(), o1.getFile().lastModified()));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        itemAdapter.setNewList(recentFiles);
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });

        executorService.shutdown();

    }

    private List<newRecentFile> getListRecentFile(File dir) {

        List<newRecentFile> fileItems = new ArrayList<>();

        File[] files = dir.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    fileItems.addAll(getListRecentFile(file));
                } else {
                    if (isMediaFile(file)) {
                        fileItems.add(new newRecentFile(file));
                    }
                }
            }
        }
        return fileItems;
    }

    private boolean isMediaFile(File file) {

        String[] imageExtensions = {".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp"};
        String[] videoExtensions = {".mp4", ".avi", ".mov", ".mkv", ".3gp", ".webm"};
        String[] audioExtensions = {".mp3", ".aac", ".wav", ".flac", ".ogg", ".m4a"};
        String[] apkExtensions = {".apk"};

        String name = file.getName().toLowerCase();

        for (String ext : imageExtensions) {
            if (name.endsWith(ext)) return true;
        }
        for (String ext : videoExtensions) {
            if (name.endsWith(ext)) return true;
        }
        for (String ext : audioExtensions) {
            if (name.endsWith(ext)) return true;
        }
        for (String apk : apkExtensions){
            if (name.endsWith(apk)) return true;
        }


        return false;
    }

 */