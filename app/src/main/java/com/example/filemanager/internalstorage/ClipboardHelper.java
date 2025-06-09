package com.example.filemanager.internalstorage;

import java.util.ArrayList;
import java.util.List;

public class ClipboardHelper {

    private static final ArrayList<String> filePaths = new ArrayList<>();

    private static boolean isCutOperation = false;



    public static void copyFiles(List<String> files) {
        filePaths.clear();
        filePaths.addAll(files);
        isCutOperation = false;
    }

    public static void cutFiles(List<String> files) {
        filePaths.clear();
        filePaths.addAll(files);
        isCutOperation = true;
    }


    public static ArrayList<String> getFilePaths() {
        return filePaths;
    }

    public static boolean isCut() {
        return isCutOperation;
    }

    public static boolean isEmpty() {
        return filePaths.isEmpty();
    }

    public static void clear() {
        filePaths.clear();
        isCutOperation = false;
    }
}
