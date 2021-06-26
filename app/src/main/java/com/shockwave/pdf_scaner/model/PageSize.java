package com.shockwave.pdf_scaner.model;

public enum PageSize {
    A3(297.0f, 420.0f),
    A4(210.0f, 297.0f),
    A5(148.0f, 210.0f),
    B4(250.0f, 353.0f),
    B5(176.0f, 250.0f),
    LETTER(216.0f, 279.0f),
    LEGAL(216.0f, 356.0f),
    EXECUTIVE(184.0f, 297.0f),
    BUSINESS_CARD(85.0f, 55.0f);

    public final float width;
    public final float height;

    PageSize(float w, float h) {
        this.width = w;
        this.height = h;
    }
}
