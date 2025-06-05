package com.example.filemanager;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.recyclerview.widget.RecyclerView;

import com.example.filemanager.internalstorage.ISAdapter;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.AbstractList;
import java.util.Comparator;
import java.util.List;

public class SortingHelper {

    private static final String PREFS_Name = "sortingPrefs";
    private static final String SORT_OPTION_KEY = "sort_option";
    private static final String SORT_ORDER_KEY = "sort_order";

    private final Context context;
    private int selectedSortingOption;
    private boolean isAscending;

    public SortingHelper(Context context) {
        this.context = context;

    }


    private void loadSortingPreferences() {

        SharedPreferences prefs = context.getSharedPreferences(PREFS_Name, Context.MODE_PRIVATE);
        selectedSortingOption = prefs.getInt(SORT_OPTION_KEY, R.id.rdBtnSorting);
        isAscending = prefs.getBoolean(SORT_ORDER_KEY, true);

    }

    public void saveSortingPreferences(int optionId, boolean ascending) {
        selectedSortingOption = optionId;
        isAscending = ascending;

        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_Name, Context.MODE_PRIVATE).edit();
        editor.putInt(SORT_ORDER_KEY, selectedSortingOption);
        editor.putBoolean(SORT_ORDER_KEY, isAscending);
        editor.apply();

    }

    private int getSelectedSortingOption() {
        return selectedSortingOption;
    }

    private boolean isAscending() {
        return isAscending;
    }

    public void sortList(List<AbstractList> items, ItemAdapter<AbstractItem> itemAdapter, FastAdapter<AbstractItem> fastAdapter, RecyclerView recyclerView) {

        Comparator<ISAdapter> comparator = null;

        if (selectedSortingOption == R.id.rBtnName) {
            comparator = Comparator.comparing(fileName -> fileName.getFile().getName().toLowerCase());
        } else if (selectedSortingOption == R.id.rBtnLastDate) {
            comparator = Comparator.comparing(fileModifierData -> fileModifierData.getFile().lastModified());
        } else if (selectedSortingOption == R.id.rBtnSize) {
            comparator = Comparator.comparing(fileSize -> fileSize.getFile().length());
        }


        if (comparator != null) {
            if (!isAscending) {
                comparator = comparator.reversed();
            }


//            items.sort(comparator);
//            itemAdapter.setNewList(items);
            fastAdapter.notifyAdapterDataSetChanged();
            recyclerView.setAdapter(fastAdapter);


        }


    }

    void learnSharePref() {


    }

}
