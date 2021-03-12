package com.jeremydufeux.go4lunch.ui.fragment;

import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.jeremydufeux.go4lunch.BaseFragment;
import com.jeremydufeux.go4lunch.R;
import com.jeremydufeux.go4lunch.databinding.FragmentListViewBinding;
import com.jeremydufeux.go4lunch.injection.Injection;
import com.jeremydufeux.go4lunch.injection.ViewModelFactory;
import com.jeremydufeux.go4lunch.models.Place;
import com.jeremydufeux.go4lunch.ui.SharedViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class ListViewFragment extends BaseFragment {

    private SharedViewModel mSharedViewModel;

    private FragmentListViewBinding mBinding;
    private ListViewPlacesAdapter mAdapter;

    private final Subject<Location> mObservableLocation = PublishSubject.create();

    public ListViewFragment() {}

    public static ListViewFragment newInstance() {
        return new ListViewFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureViewModel();
    }

    private void configureViewModel() {
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory();
        mSharedViewModel = new ViewModelProvider(requireActivity(), viewModelFactory).get(SharedViewModel.class);
        mSharedViewModel.observePlaceList().observe(this, this::onPlacesChanged);
        mSharedViewModel.observeLocationPermissionGranted().observe(this, this::onLocationPermissionGranted);
    }

    private void onLocationPermissionGranted(Boolean granted) {
        if (granted){
            mSharedViewModel.observeUserLocation().observe(this, this::onUserPositionChanged);
        }
    }

    private void onUserPositionChanged(Location location) {
        mObservableLocation.onNext(location);
    }

    void onPlacesChanged(List<Place> places) {
        mAdapter.updateList(places);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentListViewBinding.inflate(getLayoutInflater());
        configureRecyclerView();

        return mBinding.getRoot();
    }

    private void configureRecyclerView() {
        mAdapter = new ListViewPlacesAdapter(requireContext(), Glide.with(this), mSharedViewModel.getLocation(), mObservableLocation);
        mBinding.listViewFragmentRecyclerView.setAdapter(mAdapter);
        mBinding.listViewFragmentRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));

        DividerItemDecoration itemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(new ColorDrawable(getResources().getColor(R.color.very_light_grey)));
        mBinding.listViewFragmentRecyclerView.addItemDecoration(itemDecoration);
    }
}