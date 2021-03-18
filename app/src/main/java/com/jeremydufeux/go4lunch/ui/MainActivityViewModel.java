package com.jeremydufeux.go4lunch.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.jeremydufeux.go4lunch.models.Workmate;
import com.jeremydufeux.go4lunch.repositories.WorkmatesRepository;


public class MainActivityViewModel extends ViewModel {

    private final WorkmatesRepository mWorkmatesRepository;

    public MainActivityViewModel(WorkmatesRepository workmatesRepository) {
        this.mWorkmatesRepository = workmatesRepository;
    }

    public LiveData<Workmate> getWorkmateWithId(String uid){
        return mWorkmatesRepository.getWorkmateWithId(uid);
    }

}
