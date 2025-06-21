package com.example.filemanager.internalstorage;

import java.util.ArrayList;
import java.util.List;

public class ClipboardHelper {

    private static final ArrayList<String> filePaths = new ArrayList<>();

    private static boolean isCutOperation = false;
    private static boolean isPasting = false;


    public static void copy(List<String> files) {
        filePaths.clear();
        filePaths.addAll(files);
        isCutOperation = false;
        isPasting = true;
    }

    public static void cut(List<String> files) {
        filePaths.clear();
        filePaths.addAll(files);
        isCutOperation = true;
        isPasting = true;
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
        isPasting = false;
    }

    public static boolean isIsPasting() {
        return isPasting;
    }

    public static void setIsPasting(boolean value) {
        isPasting = value;
    }


}
