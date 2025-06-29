package com.example.filemanager.safefolder;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {SafeBoxFile.class}, version = 1)
public abstract class SafeBoxFileDB extends RoomDatabase {

    private static SafeBoxFileDB instance;

    public abstract SafeBoxFileDao safeBoxFileDao();

    public static synchronized SafeBoxFileDB getInstance(Context context) {

        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                                    SafeBoxFileDB.class, "Safe_Folder")
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .build();
        }
        return instance;
    }

}


/*


//    void sendToSafeFolder(File selectedFileSend) {
//
//        SafeBoxFileDB safeBoxFileDB =
//                Room.databaseBuilder(getApplicationContext(), SafeBoxFileDB.class, "safe_box_db")
//                        .fallbackToDestructiveMigration()
//                        .build();
//
//        fileMoveToSafeBoxDB(selectedFileSend, safeBoxFileDB);
//
//    }
//
//    // send file to safe box
//    void fileMoveToSafeBoxDB(File file, SafeBoxFileDB safeBoxDB) {
//
//        File safeFolder = new File(getFilesDir(), "SafeBox");
//
//        if (!safeFolder.exists()) {
//            safeFolder.mkdirs();
//        }
//
//        File destFile = new File(safeFolder, file.getName());
//
//        try {
//
//            Files.copy(file.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
//            boolean deleted = file.delete();
//
//            if (deleted) {
//                SafeBoxFile safeBoxFile = new SafeBoxFile();
//                safeBoxFile.fileName = file.getName();
//                safeBoxFile.fileOriginalPath = file.getAbsolutePath();
//                safeBoxFile.fileSavePath = destFile.getAbsolutePath();
//
//                executorService.execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        safeBoxDB.safeBoxFileDao().insert(safeBoxFile);
//                    }
//                });
//                Toast.makeText(this, "Moved to Safe Box", Toast.LENGTH_SHORT).show();
//            }
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(this, "Failed To Move", Toast.LENGTH_SHORT).show();
//        }
//
//    }
//
//    // load file form safe box
//    void loadSafeBoxFiles(SafeBoxFileDB safeBoxFileDB, RecyclerView recyclerView, ItemAdapter<ISAdapter> itemAdapterSafeBox) {
//
//        executorService.execute(new Runnable() {
//            @Override
//            public void run() {
//                List<SafeBoxFile> safeList = safeBoxFileDB.safeBoxFileDao().getAllSafeBoxFiles();
//
//                List<ISAdapter> items = new ArrayList<>();
//
//                for (SafeBoxFile file : safeList) {
//                    File f = new File(file.fileSavePath);
//                    if (f.exists()) {
//                        items.add(new ISAdapter(f));
//                    }
//                }
//
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        itemAdapter.setNewList(items);
//                    }
//                });
//
//
//            }
//        });
//    }
//
//    void restoreFromSafeBox(SafeBoxFile safeBoxFile, SafeBoxFileDB safeDB) {
//
//        File from = new File(safeBoxFile.fileSavePath);
//        File to = new File(safeBoxFile.fileOriginalPath);
//
//        try {
//            Files.copy(from.toPath(), to.toPath(), StandardCopyOption.REPLACE_EXISTING);
//            boolean deleted = from.delete();
//
//            if (deleted) {
//                executorService.execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        safeDB.safeBoxFileDao().delete(safeBoxFile);
//                    }
//                });
//                Toast.makeText(this, "Restored to original location", Toast.LENGTH_SHORT).show();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(this, "Restore failed", Toast.LENGTH_SHORT).show();
//        }
//
//    }

 */