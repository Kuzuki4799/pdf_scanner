package com.shockwave.pdf_scaner.model;

public class PageModel {
    public String name;
    public String detail;
    public boolean isSelected = false;

    public PageModel(String name, String detail, boolean isSelected) {
        this.name = name;
        this.detail = detail;
        this.isSelected = isSelected;
    }

    public PageModel(String name, String detail) {
        this.name = name;
        this.detail = detail;
    }
}
