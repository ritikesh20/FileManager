package com.example.filemanager.videolist;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;


public class VideoClipboardHelper {
    private static List<Uri> copiedUris = new ArrayList<>();
    private static boolean isCut = false;

    public static void copy(List<Uri> uris) {
        copiedUris.clear();
        copiedUris.addAll(uris);
        isCut = false;
    }

    public static void cut(List<Uri> uris) {
        copiedUris.clear();
        copiedUris.addAll(uris);
        isCut = true;
    }

    public static List<Uri> getCopiedUris() {
        return copiedUris;
    }

    public static boolean isCutMode() {
        return isCut;
    }

    public static boolean hasData() {
        return copiedUris != null && !copiedUris.isEmpty();
    }

    public static void clear() {
        copiedUris.clear();
        isCut = false;
    }
}
