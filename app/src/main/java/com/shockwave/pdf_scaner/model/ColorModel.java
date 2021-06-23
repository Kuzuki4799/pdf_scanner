package com.shockwave.pdf_scaner.model;

public class ColorModel {
    public int color;
    public boolean isSelected;

    public ColorModel(int color) {
        this.color = color;
        this.isSelected = false;
    }

    public ColorModel(int color, boolean isSelected) {
        this.color = color;
        this.isSelected = isSelected;
    }
}
