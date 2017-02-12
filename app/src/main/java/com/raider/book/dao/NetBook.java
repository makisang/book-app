package com.raider.book.dao;


import android.os.Parcel;

public class NetBook extends Book {

    public String cover_url;
    public String catalog_url;
    public String home_url;

    public NetBook() {
    }

    private NetBook(Parcel in) {
        id = in.readInt();
        description = in.readString();
        title = in.readString();
        author = in.readString();
        length = in.readLong();
        path = in.readString();
        cover_url = in.readString();
        catalog_url = in.readString();
        home_url = in.readString();
    }

    public static final Creator<NetBook> CREATOR = new Creator<NetBook>() {
        @Override
        public NetBook createFromParcel(Parcel in) {
            return new NetBook(in);
        }

        @Override
        public NetBook[] newArray(int size) {
            return new NetBook[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(description);
        dest.writeString(title);
        dest.writeString(author);
        dest.writeLong(length);
        dest.writeString(path);
        dest.writeString(cover_url);
        dest.writeString(catalog_url);
        dest.writeString(home_url);
    }
}
