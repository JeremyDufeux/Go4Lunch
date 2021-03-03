package com.jeremydufeux.go4lunch.ui.fragment;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jeremydufeux.go4lunch.BaseFragment;
import com.jeremydufeux.go4lunch.R;

public class RestaurantDetailsFragment extends BaseFragment {

    public RestaurantDetailsFragment() {}

    public static RestaurantDetailsFragment newInstance() {
        return new RestaurantDetailsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_restaurant_details, container, false);
    }
}