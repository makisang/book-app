package com.raider.book.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.raider.book.R;
import com.raider.book.fragment.LogonFragment;
import com.raider.book.fragment.RegisterFragment;
import com.raider.book.mvp.model.LogonModel;
import com.raider.book.mvp.model.RegisterModel;
import com.raider.book.mvp.presenter.LogonPresenter;
import com.raider.book.mvp.presenter.RegisterPresenter;

public class LogonActivity extends AppCompatActivity {

    private RegisterFragment registerFragment;
    private LogonFragment logonFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logon);

        initFragments();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frame_container, logonFragment)
                .commit();
    }

    private void initFragments() {
        logonFragment = new LogonFragment();
        registerFragment = new RegisterFragment();
        // Init presenters.
        LogonPresenter logonPresenter = new LogonPresenter(logonFragment, new LogonModel(getApplicationContext()));
        RegisterPresenter registerPresenter = new RegisterPresenter(registerFragment, new RegisterModel(getApplicationContext()));
    }

    public void showRegisterFragment() {
        getSupportFragmentManager().beginTransaction()
                .hide(logonFragment)
                .add(R.id.frame_container, registerFragment)
                .addToBackStack("register")
                .commit();
    }

    public void returnToLogonFragment() {
        getSupportFragmentManager().beginTransaction()
                .remove(registerFragment)
                .show(logonFragment)
                .commit();
    }
}
