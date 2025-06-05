package com.example.filemanager.adapter;


import com.example.filemanager.imagexview.ImageFA;

import java.util.Comparator;

public class SortingLS implements Comparator<ImageFA> {

    @Override
    public int compare(ImageFA large, ImageFA small) {
        return Long.compare(large.getSizeInByte(), small.getSizeInByte());
    }


}
