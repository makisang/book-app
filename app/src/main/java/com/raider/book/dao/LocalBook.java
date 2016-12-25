package com.raider.book.dao;

import android.os.Parcel;
import android.os.Parcelable;

public class LocalBook implements BookData {
    public String name;
    public String path;
    public long size;

    public LocalBook(String name, String path, long size) {
        this.name = name;
        this.path = path;
        this.size = size;
    }

    private LocalBook(Parcel source) {
        name = source.readString();
        path = source.readString();
        size = source.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(path);
        dest.writeLong(size);
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
