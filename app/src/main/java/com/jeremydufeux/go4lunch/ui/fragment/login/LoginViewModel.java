package com.jeremydufeux.go4lunch.ui.fragment.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;
import com.jeremydufeux.go4lunch.models.Workmate;
import com.jeremydufeux.go4lunch.repositories.WorkmatesRepository;
import com.jeremydufeux.go4lunch.utils.LiveEvent.LiveEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class LoginViewModel extends ViewModel {

    private final WorkmatesRepository mWorkmatesRepository;
    private final Executor mExecutor;

    @Inject
    public LoginViewModel(WorkmatesRepository workmatesRepository, Executor executor) {
        mWorkmatesRepository = workmatesRepository;
        mExecutor = executor;
    }

    public void createWorkmate(FirebaseUser currentUser) {
        String uId = currentUser.getUid();
        String displayName = currentUser.getProviderData().get(1).getDisplayName();

        List<String> parts = Arrays.asList(displayName.split(" "));
        String firstName = parts.get(0);
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < parts.size(); i++) {
            sb.append(parts.get(i));
            if(i != parts.size()-1) sb.append(" ");
        }
        String lastName = sb.toString();

        String email = currentUser.getProviderData().get(1).getEmail();
        String pictureUrl = Objects.requireNonNull(currentUser.getProviderData().get(1).getPhotoUrl()).toString();

        Workmate workmate = new Workmate(uId, displayName, firstName, lastName, email, pictureUrl);

        mExecutor.execute(() ->
            mWorkmatesRepository.createWorkmate(workmate)
        );
    }

    public LiveData<LiveEvent> observeCreateWorkmateResult(){
        return mWorkmatesRepository.observeCreateWorkmateResult();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
