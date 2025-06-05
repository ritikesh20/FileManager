package com.example.filemanager.adapter;

import com.example.filemanager.music.MusicAdapter;

import java.util.Comparator;

public class NameSort implements Comparator<MusicAdapter> {


    @Override
    public int compare(MusicAdapter name1, MusicAdapter name2) {

        return name1.getFile().getName().compareToIgnoreCase(name2.getFile().getName());

    }
}
