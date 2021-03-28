package com.jeremydufeux.go4lunch.ui.fragment;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public abstract class BaseFragment extends Fragment {

    protected FirebaseAuth getAuth(){
        return FirebaseAuth.getInstance();
    }

    @Nullable
    protected FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    protected Boolean isCurrentUserLoggedIn(){
        return getCurrentUser() != null;
    }
}
