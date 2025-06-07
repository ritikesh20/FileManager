package com.example.filemanager.internalstorage;

import java.util.ArrayList;
import java.util.List;

public class ClipboardHelper {

    private static final ArrayList<String> copiedFiles = new ArrayList<>();

    public static void copyFiles(List<String> files) {
        copiedFiles.clear();
        copiedFiles.addAll(files);
    }

    public static ArrayList<String> getCopiedFiles() {
        return copiedFiles;
    }

    public static boolean isEmpty() {
        return copiedFiles.isEmpty();
    }

    public static void clear() {
        copiedFiles.clear();
    }
}
