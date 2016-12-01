package com.raider.book.mvp.model;

import android.content.Context;

import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SignUpCallback;
import com.raider.book.mvp.contract.RegisterContract;


public class RegisterModel implements RegisterContract.Model {

    public RegisterModel(Context context) {
    }

    @Override
    public void postRegister(AVUser avUser, SignUpCallback callback) {
        avUser.signUpInBackground(callback);
    }
}
