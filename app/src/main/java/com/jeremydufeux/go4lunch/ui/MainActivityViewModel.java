package com.jeremydufeux.go4lunch.ui;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.jeremydufeux.go4lunch.models.Workmate;
import com.jeremydufeux.go4lunch.repositories.UserDataRepository;
import com.jeremydufeux.go4lunch.repositories.WorkmatesRepository;

public class MainActivityViewModel extends ViewModel {

    private final WorkmatesRepository mWorkmatesRepository;
    private final UserDataRepository mUserDataRepository;

    public MainActivityViewModel(WorkmatesRepository workmatesRepository, UserDataRepository userDataRepository) {
        mWorkmatesRepository = workmatesRepository;
        mUserDataRepository = userDataRepository;
    }

    public LiveData<Workmate> getWorkmateWithId(String uid){
        return mWorkmatesRepository.getWorkmateWithId(uid);
    }

    public void setLocation(Location location){
        mUserDataRepository.setLocation(location);
    }
}
