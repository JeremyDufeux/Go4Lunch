package com.jeremydufeux.go4lunch.ui.fragment.workmates;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.jeremydufeux.go4lunch.R;
import com.jeremydufeux.go4lunch.databinding.FragmentWorkmatesBinding;
import com.jeremydufeux.go4lunch.models.Workmate;
import com.jeremydufeux.go4lunch.utils.liveEvent.LiveEvent;
import com.jeremydufeux.go4lunch.utils.liveEvent.ShowSnackbarLiveEvent;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

import static com.jeremydufeux.go4lunch.utils.Utils.isToday;

@AndroidEntryPoint
public class WorkmatesFragment extends Fragment implements WorkmatesAdapter.OnWorkmateListener {

    private WorkmatesViewModel mViewModel;
    private FragmentWorkmatesBinding mBinding;
    private WorkmatesAdapter mAdapter;

    private List<Workmate> mWorkmateList = new ArrayList<>();

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
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewModel.startObservers();

        mBinding = FragmentWorkmatesBinding.inflate(getLayoutInflater());

        configureRecyclerView();
        configureObservers();

        return mBinding.getRoot();
    }

    private void configureRecyclerView() {
        mAdapter = new WorkmatesAdapter(this);
        mBinding.fragmentWorkmatesRv.setAdapter(mAdapter);
        mBinding.fragmentWorkmatesRv.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
    }

    private void configureObservers(){
        mViewModel.observeEvents().observe(getViewLifecycleOwner(), onEventReceived());
        mViewModel.observeWorkmateList().observe(getViewLifecycleOwner(), this::onWorkmateListChanged);
    }

    private void onWorkmateListChanged(List<Workmate> workmateList){
        mWorkmateList = workmateList;
        mAdapter.updateList(mWorkmateList);
    }


    @Override
    public void onWorkmateClick(int position) {
        Workmate workmate = mWorkmateList.get(position);

        if(!workmate.getChosenRestaurantId().isEmpty() && isToday(workmate.getChosenRestaurantDate())) {
            Bundle bundle = new Bundle();
            bundle.putString(getString(R.string.arg_restaurant_id), workmate.getChosenRestaurantId());
            Navigation.findNavController(mBinding.getRoot()).navigate(R.id.restaurant_details_fragment, bundle);
        } else {
            showSnackBar(getString(R.string.workmate_didnt_chose_restaurant, workmate.getNickname()));
        }
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

    private void showSnackBar(String message){
        Snackbar.make(mBinding.fragmentWorkmatesRv, message, Snackbar.LENGTH_LONG).show();
    }

    private void showSnackBar(int stringId){
        Snackbar.make(mBinding.fragmentWorkmatesRv, getString(stringId), Snackbar.LENGTH_LONG).show();
    }
}