package com.example.filemanager.adapter;

import com.example.filemanager.imagexview.ImageFA;

import java.util.Comparator;

public class SortingSL implements Comparator<ImageFA> {

    @Override
    public int compare(ImageFA small, ImageFA large) {
        return Long.compare(small.getSizeInByte(), large.getSizeInByte());
    }

}
