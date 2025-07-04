package com.example.filemanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SortingHelper {

    public interface OnSortedCallback {
        void onSorted(List<FileHelperAdapter> sortedList);
    }

    public static void sortingBy(
            Context context,
            List<FileHelperAdapter> fileList,
            FastAdapter<FileHelperAdapter> fastAdapterFile,
            ItemAdapter<FileHelperAdapter> itemAdapterFile,
            SharedPreferences sharedPreferences,
            String preferenceKey,
            OnSortedCallback callback
    ) {

        BottomSheetDialog sortingSheet = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.bottomsheet_items, null);
        sortingSheet.setContentView(view);

        RadioGroup radioGroup = view.findViewById(R.id.btnRGImageSorting);

        int savedOption = sharedPreferences.getInt(preferenceKey, R.id.rbBtnNDF);

        radioGroup.check(savedOption);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {

            SharedPreferences.Editor sort = sharedPreferences.edit();
            sort.putInt(preferenceKey,checkedId);
            sort.apply();

            if (checkedId == R.id.rbBtnNDF) {
                Collections.sort(fileList, (nfd, ofd) -> parseFileDate(ofd.getDocDate()).compareTo(parseFileDate(nfd.getDocDate())));
            } else if (checkedId == R.id.rbBtnODF) {
                Collections.sort(fileList, (nfd, ofd) -> parseFileDate(nfd.getDocDate()).compareTo(parseFileDate(ofd.getDocDate())));
            } else if (checkedId == R.id.rbBtnLargeFirst) {
                Collections.sort(fileList, (largeFile, smallFile) -> Long.compare(parseSizeToBytes(smallFile.getSize()), parseSizeToBytes(largeFile.getSize())));
            } else if (checkedId == R.id.rbBtnSmallestFirst) {
                Collections.sort(fileList, (largeFile, smallFile) -> Long.compare(parseSizeToBytes(largeFile.getSize()), parseSizeToBytes(smallFile.getSize())));
            } else if (checkedId == R.id.nameAZ) {
                Collections.sort(fileList, (name1, name2) -> name1.getName().compareTo(name2.getName()));
            } else if (checkedId == R.id.nameZA) {
                Collections.sort(fileList, (name1, name2) -> name2.getName().compareTo(name1.getName()));
            }

            itemAdapterFile.setNewList(fileList);
            fastAdapterFile.notifyAdapterDataSetChanged();
            sortingSheet.dismiss();

            if (callback != null) callback.onSorted(fileList);
        });

        sortingSheet.show();
    }


    private static Date parseFileDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date(0);
        }
    }

    private static long parseSizeToBytes(String sizeStr) {

        sizeStr = sizeStr.trim().toUpperCase();

        try {
            if (sizeStr.endsWith("KB")) {
                return (long) (Double.parseDouble(sizeStr.replace("KB", "").trim()) * 1024);
            } else if (sizeStr.endsWith("MB")) {
                return (long) (Double.parseDouble(sizeStr.replace("MB", "").trim()) * 1024 * 1024);
            } else if (sizeStr.endsWith("GB")) {
                return (long) (Double.parseDouble(sizeStr.replace("GB", "").trim()) * 1024 * 1024 * 1024);
            } else if (sizeStr.endsWith("B")) {
                return Long.parseLong(sizeStr.replace("B", "").trim());
            } else {
                return Long.parseLong(sizeStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


}
