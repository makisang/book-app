package com.raider.book.mvp.presenter;


import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.raider.book.base.BasePresenter;
import com.raider.book.mvp.contract.LogonContract;

public class LogonPresenter implements BasePresenter {
    public static final int USERNAME_PWD_MISMATCH = 210;
    public static final int USER_NOT_FOUND= 211;

    LogonContract.View iView;
    LogonContract.Model iModel;

    public LogonPresenter(LogonContract.View iView, LogonContract.Model iModel) {
        this.iView = iView;
        this.iModel = iModel;
        iView._setPresenter(this);
    }

    @Override
    public void onViewCreated() {

    }

    public void sendLogon(String username, String password) {
        iView._showRegisterHint();
        AVUser.logInInBackground(username, password, new LogInCallback<AVUser>() {
            @Override
            public void done(AVUser avUser, AVException e) {
                iView._hideRegisterHint();
                if (avUser == null) {
                    iView._showLogonException(e != null ? e.getCode() : 0);
                    return;
                }
                iView._onLogonSuccess(avUser);
            }
        });
    }


    @Override
    public void onDestroy() {

    }
}
