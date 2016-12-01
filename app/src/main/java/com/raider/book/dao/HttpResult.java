package com.raider.book.dao;

import java.util.List;

public class HttpResult<T> {
    public boolean success;
    public String message;

    public List<T> data;
}
