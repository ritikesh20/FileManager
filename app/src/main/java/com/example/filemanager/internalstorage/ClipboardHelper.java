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


    public static ArrayList<String> getFilePaths() { // function hai jo selected file ko return karta hai
        return filePaths;
    }


    public static boolean isCut() { // if isCut == true then cut operation else id isCut == false copyOperation
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

    public static boolean isIsPasting() { // to show btnCopy, btnPast
        return isPasting;
    }

    public static void setIsPasting(boolean value) { // change value after pasting
        isPasting = value;
    }


}
