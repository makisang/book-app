package com.raider.book.fragment;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.avos.avoscloud.AVUser;
import com.raider.book.R;
import com.raider.book.activity.LogonActivity;
import com.raider.book.mvp.contract.RegisterContract;
import com.raider.book.mvp.presenter.RegisterPresenter;
import com.raider.book.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterFragment extends Fragment implements RegisterContract.View {

    private RegisterPresenter mPresenter;
    private LogonActivity logonActivity;

    @BindView(R.id.username_input)
    EditText usernameEt;
    @BindView(R.id.pwd_input)
    EditText pwdEt;
    @BindView(R.id.register_btn)
    Button registerBtn;
    @BindView(R.id.my_progress)
    ProgressBar progressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logonActivity = (LogonActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_register, container, false);
        ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public void _setPresenter(RegisterPresenter presenter) {
        mPresenter = presenter;
    }

    @OnClick(R.id.register_btn)
    void register() {
        String username = usernameEt.getText().toString();
        String password = pwdEt.getText().toString();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(getContext(), getString(R.string.register_loss), Toast.LENGTH_SHORT).show();
            return;
        }

        AVUser avUser = new AVUser();
        avUser.setUsername(username);
        avUser.setPassword(password);
        mPresenter.registerUser(avUser);
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
    public void _showRegisterResult(int code) {
        String message = null;
        switch (code) {
            case RegisterPresenter.REGISTER_SUCCESS:
                message = getString(R.string.register_success);
                break;
            case RegisterPresenter.USERNAME_REPEAT:
                message = getString(R.string.username_repeat);
        }

        if (TextUtils.isEmpty(message)) return;

        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logonActivity.returnToLogonFragment();
                    }
                })
                .create()
                .show();
    }


}
