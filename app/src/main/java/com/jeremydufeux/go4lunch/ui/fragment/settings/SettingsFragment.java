package com.jeremydufeux.go4lunch.ui.fragment.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.jeremydufeux.go4lunch.R;
import com.jeremydufeux.go4lunch.databinding.FragmentSettingsBinding;
import com.jeremydufeux.go4lunch.models.Workmate;
import com.jeremydufeux.go4lunch.utils.liveEvent.LiveEvent;
import com.jeremydufeux.go4lunch.utils.liveEvent.ShowSnackbarLiveEvent;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;

@AndroidEntryPoint
public class SettingsFragment extends Fragment {

    private static final int PERMS_RC_IMAGE = 2;
    private static final int RC_CHOOSE_PHOTO = 200;

    private SettingsViewModel mViewModel;
    private FragmentSettingsBinding mBinding;

    private Uri mUriNewProfilePic;
    private List<String> unitArray;

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

    private void configureViewModels() {
        mViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mViewModel.startObservers();
        mViewModel.observeEvents().observe(getViewLifecycleOwner(), this::onEventReceived);
        mViewModel.observeCurrentUser().observe(getViewLifecycleOwner(), this::onUserDataChange);

        mBinding = FragmentSettingsBinding.inflate(getLayoutInflater());

        updateUi();

        return mBinding.getRoot();
    }

    // ---------------
    // Ui
    // ---------------

    private void updateUi() {
        mBinding.settingsFragmentEnableNotificationsSw.setChecked(mViewModel.isNotificationEnabled());
        mBinding.settingsFragmentPicFl.frameLayoutProfilePic.setOnClickListener(v -> loadPicture());
        mBinding.settingsFragmentSaveBtn.setOnClickListener(v -> saveSettings());

        unitArray = Arrays.asList(getResources().getStringArray(R.array.unit_array_short));
        mBinding.settingsFragmentUnitSp.setSelection(unitArray.indexOf(getString(mViewModel.getUserDistanceUnit())));
    }

    private void onUserDataChange(Workmate workmate) {
        Glide.with(this).load(workmate.getPictureUrl()).apply(RequestOptions.circleCropTransform()).into(mBinding.settingsFragmentPicFl.picIv);

        mBinding.settingsFragmentNameTv.setText(workmate.getFullName());
        mBinding.settingsFragmentNicknameEt.setText(workmate.getNickname());

        mViewModel.observeCurrentUser().removeObservers(this);
    }

    private void onEventReceived(LiveEvent event){
        if(event instanceof ShowSnackbarLiveEvent){
            showSnackBar(((ShowSnackbarLiveEvent) event).getStingId());
        }
    }

    private void showSnackBar(int stringId){
        Snackbar.make(mBinding.getRoot(), getString(stringId), Snackbar.LENGTH_LONG).show();
    }

    // ---------------
    // Settings
    // ---------------

    private void saveSettings(){
        String newNickName = mBinding.settingsFragmentNicknameEt.getText().toString();
        if(newNickName.isEmpty()) {
            showSnackBar(R.string.nickname_empty);
        } else {
            boolean newEnableNotification = mBinding.settingsFragmentEnableNotificationsSw.isChecked();

            int spinnerPosition = mBinding.settingsFragmentUnitSp.getSelectedItemPosition();
            String newUserUnit = unitArray.get(spinnerPosition);
            int newUnitResource;
            if(newUserUnit.equals(getString(R.string.unit_feet_short))){
                newUnitResource = R.string.unit_feet_short;
            } else {
                newUnitResource = R.string.unit_meter_short;
            }

            mViewModel.saveSettings(newNickName, newEnableNotification, mUriNewProfilePic, newUnitResource);
        }
    }

    // ---------------
    // Storage
    // ---------------

    @SuppressWarnings("deprecation")
    @AfterPermissionGranted(PERMS_RC_IMAGE)
    private void loadPicture() {
        if (EasyPermissions.hasPermissions(requireActivity(), READ_EXTERNAL_STORAGE)) {
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RC_CHOOSE_PHOTO);
        } else {
            requestPermissions( new String[]{READ_EXTERNAL_STORAGE}, PERMS_RC_IMAGE);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_CHOOSE_PHOTO) {
            if (resultCode == RESULT_OK) { //SUCCESS
                mUriNewProfilePic = data.getData();
                Glide.with(this) //SHOWING PREVIEW OF IMAGE
                        .load(mUriNewProfilePic)
                        .apply(RequestOptions.circleCropTransform())
                        .into(mBinding.settingsFragmentPicFl.picIv);
            }
        }
    }
}