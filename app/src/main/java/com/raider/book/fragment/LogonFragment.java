package com.raider.book.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVUser;
import com.raider.book.R;
import com.raider.book.activity.LogonActivity;
import com.raider.book.activity.MainActivity;
import com.raider.book.mvp.contract.LogonContract;
import com.raider.book.mvp.presenter.LogonPresenter;
import com.raider.book.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class LogonFragment extends Fragment implements LogonContract.View {

    private LogonPresenter mPresenter;
    private boolean bViewStubInflated;

    @BindView(R.id.logon_btn)
    Button logonBtn;
    @BindView(R.id.username_input)
    EditText usernameEt;
    @BindView(R.id.pwd_input)
    EditText pwdEt;
    @BindView(R.id.show_third_party)
    TextView thirdPartyBtn;
    @BindView(R.id.register)
    TextView registerBtn;
    @BindView(R.id.my_progress)
    ProgressBar progressBar;
    @BindView(R.id.view_stub)
    ViewStub viewStub;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_logon, container, false);
        ButterKnife.bind(this, root);
        init();
        return root;
    }

    @Override
    public void _setPresenter(LogonPresenter presenter) {
        mPresenter = presenter;
    }

    private void init() {
        // Init ViewStub.
        viewStub.setOnInflateListener(new ViewStub.OnInflateListener() {
            @Override
            public void onInflate(ViewStub stub, View inflated) {
                bViewStubInflated = true;
            }
        });
    }

    @OnClick(R.id.logon_btn)
    void logon() {
        String username = usernameEt.getText().toString();
        String password = pwdEt.getText().toString();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(getContext(), getString(R.string.logon_loss), Toast.LENGTH_SHORT).show();
            return;
        }

        mPresenter.sendLogon(username, password);
    }

    @OnClick(R.id.show_third_party)
    void showThirdPartyLogon() {
        if (!bViewStubInflated) {
            View inflate = viewStub.inflate();
            inflate.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.register)
    void goToRegister() {
        ((LogonActivity) getActivity()).showRegisterFragment();
    }

    @Override
    public void _showRegisterHint() {
        progressBar.setVisibility(View.VISIBLE);
        if (Utils.isOrAfterLollipop()) {
            progressBar.setIndeterminateTintList(getResources().getColorStateList(R.color.progress_tint_white));
            progressBar.setElevation(getResources().getDimension(R.dimen.elevation_btn_pressed));
        }
    }

    @Override
    public void _hideRegisterHint() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void _showLogonException(int code) {
        String message = null;
        switch (code) {
            case LogonPresenter.USERNAME_PWD_MISMATCH:
                message = getString(R.string.username_pwd_mismatch);
                break;
            case LogonPresenter.USER_NOT_FOUND:
                message = getString(R.string.user_not_found);
                break;
        }

        if (TextUtils.isEmpty(message)) return;

        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton(getString(R.string.confirm), null)
                .create()
                .show();
    }

    @Override
    public void _onLogonSuccess(AVUser avUser) {
        MainActivity.start(getActivity());
    }

}
