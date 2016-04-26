package com.raider.book.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wkq on 2016/4/14.
 * represent book object.
 */
public class BookData implements Parcelable {
    public String name;
    public String path;

    public BookData(String name, String path) {
        this.name = name;
        this.path = path;
    }

    private BookData(Parcel source) {
        name = source.readString();
        path = source.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(path);
    }

    public static final Parcelable.Creator<BookData> CREATOR = new Parcelable.Creator<BookData>() {

        @Override
        public BookData createFromParcel(Parcel source) {
            return new BookData(source);
        }

        @Override
        public BookData[] newArray(int size) {
            return new BookData[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof BookData)) return false;
        BookData other = (BookData) o;
        return this.path.equals(other.path);
    }
}
