package com.example.filemanager.videolist;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class VideoClipboardHelper {

    private static final String PREF_NAME = "VideoClipboardPrefs";
    private static final String KEY_URI_LIST = "URIs";
    private static final String KEY_IS_CUT = "isCut";

    private static List<Uri> copiedUris = new ArrayList<>();
    private static boolean isCut = false;

    public static void copy(Context context, List<Uri> uris) {
        copiedUris.clear();
        copiedUris.addAll(uris);
        isCut = false;
        saveToPrefs(context);
    }

    public static void cut(Context context, List<Uri> uris) {
        copiedUris.clear();
        copiedUris.addAll(uris);
        isCut = true;
        saveToPrefs(context);
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

    public static void clear(Context context) {
        copiedUris.clear();
        isCut = false;
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
    }

    public static void loadFromPrefs(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        isCut = prefs.getBoolean(KEY_IS_CUT, false);
        String saved = prefs.getString(KEY_URI_LIST, null);
        copiedUris.clear();

        if (saved != null) {
            String[] uris = saved.split(";");
            for (String uriStr : uris) {
                copiedUris.add(Uri.parse(uriStr));
            }
        }
    }

    private static void saveToPrefs(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        StringBuilder builder = new StringBuilder();
        for (Uri uri : copiedUris) {
            builder.append(uri.toString()).append(";");
        }
        editor.putString(KEY_URI_LIST, builder.toString());
        editor.putBoolean(KEY_IS_CUT, isCut);
        editor.apply();
    }

}















//
//public class VideoClipboardHelper {
//    private static List<Uri> copiedUris = new ArrayList<>();
//    private static boolean isCut = false;
//
//    public static void copy(List<Uri> uris) {
//        copiedUris.clear();
//        copiedUris.addAll(uris);
//        isCut = false;
//    }
//
//    public static void cut(List<Uri> uris) {
//        copiedUris.clear();
//        copiedUris.addAll(uris);
//        isCut = true;
//    }
//
//    public static List<Uri> getCopiedUris() {
//        return copiedUris;
//    }
//
//    public static boolean isCutMode() {
//        return isCut;
//    }
//
//    public static boolean hasData() {
//        return copiedUris != null && !copiedUris.isEmpty();
//    }
//
//    public static void clear() {
//        copiedUris.clear();
//        isCut = false;
//    }
//}
