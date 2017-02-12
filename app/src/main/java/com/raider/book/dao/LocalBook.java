package com.raider.book.dao;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wkq on 2016/4/14.
 * represent book object.
 */
public class LocalBook extends Book {

    public LocalBook() {
    }

    public LocalBook(String title, String path, long length) {
        this.title = title;
        this.path = path;
        this.length = length;
    }

    private LocalBook(Parcel source) {
        title = source.readString();
        path = source.readString();
        length = source.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(path);
        dest.writeLong(length);
    }

    public static final Parcelable.Creator<LocalBook> CREATOR = new Parcelable.Creator<LocalBook>() {

        @Override
        public LocalBook createFromParcel(Parcel source) {
            return new LocalBook(source);
        }

        @Override
        public LocalBook[] newArray(int size) {
            return new LocalBook[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof LocalBook)) return false;
        LocalBook other = (LocalBook) o;
        return this.path.equals(other.path);
    }
}
