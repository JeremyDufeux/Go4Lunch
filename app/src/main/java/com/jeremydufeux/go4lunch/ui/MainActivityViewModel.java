package com.jeremydufeux.go4lunch.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.jeremydufeux.go4lunch.models.Workmate;
import com.jeremydufeux.go4lunch.repositories.WorkmatesDataRepository;


public class MainActivityViewModel extends ViewModel {

    private final WorkmatesDataRepository mWorkmatesDataRepository;

    public MainActivityViewModel(WorkmatesDataRepository workmatesDataRepository) {
        this.mWorkmatesDataRepository = workmatesDataRepository;
    }

    public LiveData<Workmate> getWorkmateWithId(String uid){
        return mWorkmatesDataRepository.getWorkmateWithId(uid);
    }

}
