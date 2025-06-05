package com.example.filemanager.adapter;

import com.example.filemanager.imagexview.ImageFA;

import java.util.Comparator;

public class SortingNDF implements Comparator<ImageFA> {

    @Override
    public int compare(ImageFA ndf, ImageFA odf) {
        return Long.compare(ndf.getPhotoDate(), odf.getPhotoDate());
    }

}
