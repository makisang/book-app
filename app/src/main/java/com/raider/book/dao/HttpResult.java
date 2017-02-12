package com.raider.book.dao;

import java.util.List;

public class HttpResult<T> {
    public int code;
    public String message;

    public List<T> dataList;
}
