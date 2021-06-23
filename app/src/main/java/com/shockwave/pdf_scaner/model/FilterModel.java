package com.shockwave.pdf_scaner.model;

import ja.burhanrashid52.photoeditor.PhotoFilter;

public class FilterModel {
    public String name = "";
    public boolean isSelected = false;
    public PhotoFilter photoFilter;

    public FilterModel(String name, PhotoFilter photoFilter) {
        this.name = name;
        this.photoFilter = photoFilter;
    }

    public FilterModel(String name, PhotoFilter photoFilter, boolean isSelected) {
        this.name = name;
        this.isSelected = isSelected;
        this.photoFilter = photoFilter;
    }
}
