package com.jeremydufeux.go4lunch.ui.fragment.settings;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jeremydufeux.go4lunch.databinding.FragmentSettingsBinding;
import com.jeremydufeux.go4lunch.models.Workmate;

import org.jetbrains.annotations.NotNull;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SettingsFragment extends Fragment {

    private SettingsViewModel mViewModel;
    private FragmentSettingsBinding mBinding;

    // ---------------
    // Setup
    // ---------------

    public SettingsFragment() {}

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureViewModels();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mViewModel.startObservers();
        mViewModel.observeCurrentUser().observe(getViewLifecycleOwner(), this::onUserDataChange);

        mBinding = FragmentSettingsBinding.inflate(getLayoutInflater());

        return mBinding.getRoot();
    }

    private void configureViewModels() {
        mViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
    }

    private void onUserDataChange(Workmate workmate) {
        Glide.with(this).load(workmate.getPictureUrl()).apply(RequestOptions.circleCropTransform()).into(mBinding.settingsFragmentNamePicIv);
        mBinding.settingsFragmentProfilePicGroup.setOnClickListener(v -> loadPicture());

        mBinding.settingsFragmentNameTv.setText(workmate.getFullName());
        mBinding.settingsFragmentNicknameEt.setText(workmate.getNickname());
        mBinding.settingsFragmentNicknameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enableSaveButton();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
        mBinding.settingsFragmentEnableNotificationsSw.setChecked(mViewModel.isNotificationEnabled());
        mBinding.settingsFragmentEnableNotificationsSw.setOnCheckedChangeListener((buttonView, isChecked) -> enableSaveButton());


        mBinding.settingsFragmentSaveBtn.setOnClickListener(v -> mViewModel.saveSettings(mBinding.settingsFragmentNicknameEt.getText().toString(),
                mBinding.settingsFragmentEnableNotificationsSw.isChecked()));
        mBinding.settingsFragmentDeleteAccountBtn.setOnClickListener(v -> mViewModel.deleteAccount());

        mViewModel.observeCurrentUser().removeObservers(this);
    }

    private void loadPicture() {

    }

    private void enableSaveButton() {
        mBinding.settingsFragmentSaveBtn.setEnabled(true);
    }
}