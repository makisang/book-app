package com.raider.book.mvp.presenter;


import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SignUpCallback;
import com.raider.book.base.BasePresenter;
import com.raider.book.mvp.contract.RegisterContract;

public class RegisterPresenter implements BasePresenter {
    public static final int REGISTER_SUCCESS = -1;
    public static final int USERNAME_REPEAT = 202;

    RegisterContract.View iView;
    RegisterContract.Model iModel;

    public RegisterPresenter(RegisterContract.View iView, RegisterContract.Model iModel) {
        this.iView = iView;
        this.iModel = iModel;
        iView._setPresenter(this);
    }

    @Override
    public void onViewCreated() {

    }

    public void registerUser(AVUser avUser) {
        iView._showRegisterHint();
        avUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(AVException e) {
                iView._hideRegisterHint();
                if (e == null) {
                    iView._showRegisterResult(REGISTER_SUCCESS);
                } else {
                    iView._showRegisterResult(e.getCode());
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        iView = null;
        iModel = null;
    }
}
