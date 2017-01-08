package com.raider.book.dao;


import android.os.Parcelable;

public abstract class Book implements Parcelable {
    public int id;
    public String description;
    public String title;
    public String author;
    public long length;
    public String path;

}
