package com.jeremydufeux.go4lunch.ui.fragment.workmates;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.jeremydufeux.go4lunch.databinding.FragmentWorkmatesBinding;
import com.jeremydufeux.go4lunch.utils.LiveEvent.LiveEvent;
import com.jeremydufeux.go4lunch.utils.LiveEvent.ShowSnackbarLiveEvent;

import org.jetbrains.annotations.NotNull;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkmatesFragment extends Fragment {

    private WorkmatesViewModel mViewModel;
    private FragmentWorkmatesBinding mBinding;
    private WorkmatesAdapter mAdapter;

    public WorkmatesFragment() {}

    public static WorkmatesFragment newInstance() {
        return new WorkmatesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureViewModel();
    }

    private void configureViewModel() {
        mViewModel = new ViewModelProvider(requireActivity()).get(WorkmatesViewModel.class);
        mViewModel.startObservers();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentWorkmatesBinding.inflate(getLayoutInflater());

        configureRecyclerView();
        configureObservers();

        return mBinding.getRoot();
    }

    private void configureRecyclerView() {
        mAdapter = new WorkmatesAdapter(Glide.with(this));
        mBinding.fragmentWorkmatesRv.setAdapter(mAdapter);
        mBinding.fragmentWorkmatesRv.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
    }

    private void configureObservers(){
        mViewModel.observeEvents().observe(getViewLifecycleOwner(), onEventReceived());
        mViewModel.observeWorkmateList().observe(getViewLifecycleOwner(), mAdapter::updateList);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViewModel.clearDisposables();
        mViewModel.observeWorkmateList().removeObserver(mAdapter::updateList);
    }

    // ---------------
    // Event
    // ---------------

    private Observer<LiveEvent> onEventReceived() {
        return event -> {
            if(event instanceof ShowSnackbarLiveEvent){
                showSnackBar(((ShowSnackbarLiveEvent) event).getStingId());
            }
        };
    }

    // ---------------
    // Utils
    // ---------------

    private void showSnackBar(int stringId){
        Snackbar.make(mBinding.fragmentWorkmatesRv, getString(stringId), Snackbar.LENGTH_LONG).show();
    }
}