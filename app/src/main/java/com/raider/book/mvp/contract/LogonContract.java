package com.raider.book.mvp.contract;


import com.avos.avoscloud.AVUser;
import com.raider.book.base.BaseView;
import com.raider.book.mvp.presenter.LogonPresenter;

public class LogonContract {

    public interface View extends BaseView<LogonPresenter> {
        void _showRegisterHint();

        void _hideRegisterHint();

        void _showLogonException(int code);

        void _onLogonSuccess(AVUser avUser);
    }

    public interface Model {

    }

}
