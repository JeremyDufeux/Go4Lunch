package com.jeremydufeux.go4lunch.ui.fragment.login;

import android.animation.Animator;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.jeremydufeux.go4lunch.R;
import com.jeremydufeux.go4lunch.databinding.DialogEmailBinding;

import org.jetbrains.annotations.NotNull;

public class EmailDialog extends DialogFragment {
    public static final int MODE_SIGN_IN = 0;
    public static final int MODE_SIGN_UP = 1;
    public static final int MODE_RESET_PASSWORD = 2;

    DialogEmailBinding mBinding;
    EmailDialogListener mListener;
    AlertDialog mAlertDialog;

    boolean nameOk = false;
    boolean nickNameOk = false;
    boolean emailOk = false;
    boolean passwordOk = false;

    int mode = MODE_SIGN_IN;

    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        mBinding = DialogEmailBinding.inflate(getActivity().getLayoutInflater());

        mBinding.dialogEmailEmailEt.setNextFocusDownId(mBinding.dialogEmailPasswordEt.getId());

        mBinding.dialogEmailSignUpButton.setOnClickListener(v ->{
                mListener.onPositivePressed(
                        mode,
                        mBinding.dialogEmailEmailEt.getText().toString(),
                        mBinding.dialogEmailPasswordEt.getText().toString(),
                        mBinding.dialogEmailNameEt.getText().toString(),
                        mBinding.dialogEmailNicknameEt.getText().toString());
                mAlertDialog.dismiss();
                });

        mBinding.dialogEmailSignUpTv.setOnClickListener(v -> {
            mode = MODE_SIGN_UP;
            updateUi();
        });

        mBinding.dialogEmailResetPasswordTv.setOnClickListener(v -> {
            mode = MODE_RESET_PASSWORD;
            updateUi();
        });

        configureTextWatchers();

        builder.setNeutralButton(R.string.cancel, null);
        builder.setTitle(R.string.sign_in);
        builder.setView(mBinding.getRoot());

        mAlertDialog = builder.create();
        return mAlertDialog;
    }

    private void updateUi() {
        if(mode == MODE_SIGN_UP) {
            mAlertDialog.setTitle(R.string.sign_up);

            mBinding.dialogEmailSignUpButton.setText(R.string.sign_up);

            hideViewWithAlphaAnimation(mBinding.dialogEmailResetPasswordTv);
            hideViewWithAlphaAnimation(mBinding.dialogEmailSignUpTv);

            showViewWithAlphaAnimation(mBinding.dialogEmailNameEt);
            showViewWithAlphaAnimation(mBinding.dialogEmailNicknameEt);
            showViewWithAlphaAnimation(mBinding.dialogEmailPasswordLengthTv);

            mBinding.dialogEmailPasswordEt.setNextFocusDownId(mBinding.dialogEmailNameEt.getId());
            mBinding.dialogEmailNameEt.setNextFocusDownId(mBinding.dialogEmailNicknameEt.getId());
        } else if(mode == MODE_RESET_PASSWORD){
            mAlertDialog.setTitle(R.string.reset_password);

            mBinding.dialogEmailSignUpButton.setText(R.string.send_reset_password_email);

            hideViewWithAlphaAnimation(mBinding.dialogEmailPasswordEt);
            hideViewWithAlphaAnimation(mBinding.dialogEmailResetPasswordTv);
            hideViewWithAlphaAnimation(mBinding.dialogEmailSignUpTv);
        }
        enableButton();
    }

    private void showViewWithAlphaAnimation(View view){
        view.animate().alpha(1.0f).setDuration(1000).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {}

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        }).start();
    }

    private void hideViewWithAlphaAnimation(View view){
        view.animate().alpha(0.0f).setDuration(500).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        }).start();
    }

    private void configureTextWatchers() {
        mBinding.dialogEmailNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nameOk = !TextUtils.isEmpty(s);
                enableButton();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
        mBinding.dialogEmailNicknameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nickNameOk = !TextUtils.isEmpty(s);
                enableButton();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        mBinding.dialogEmailEmailEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                emailOk = !TextUtils.isEmpty(s) && Patterns.EMAIL_ADDRESS.matcher(s).matches();
                enableButton();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        mBinding.dialogEmailPasswordEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordOk = s.length() >= 6;
                enableButton();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private void enableButton() {
        if(mode == MODE_SIGN_UP) {
            mBinding.dialogEmailSignUpButton.setEnabled(nameOk && nickNameOk && emailOk && passwordOk);
        } else if(mode == MODE_RESET_PASSWORD){
            mBinding.dialogEmailSignUpButton.setEnabled(emailOk);
        } else if(mode == MODE_SIGN_IN){
            mBinding.dialogEmailSignUpButton.setEnabled(emailOk && passwordOk);
        }
    }

    public void setListener(EmailDialogListener listener) {
        mListener = listener;
    }

    public interface EmailDialogListener {
        void onPositivePressed(int signUp, String email, String password, String name, String nickname);
    }
}
