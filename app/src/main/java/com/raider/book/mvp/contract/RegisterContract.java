package com.raider.book.mvp.contract;


import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SignUpCallback;
import com.raider.book.base.BaseView;
import com.raider.book.mvp.presenter.RegisterPresenter;

public class RegisterContract {

    public interface View extends BaseView<RegisterPresenter> {
        void _showRegisterHint();

        void _hideRegisterHint();

        void _showRegisterResult(int code);
    }

    public interface Model {
        void postRegister(AVUser avUser, SignUpCallback callback);
    }

}
