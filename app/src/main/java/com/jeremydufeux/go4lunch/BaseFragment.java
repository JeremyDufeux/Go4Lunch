package com.jeremydufeux.go4lunch;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.facebook.FacebookSdk.getApplicationContext;

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

    protected OnFailureListener onFailureListener(){
        return e -> {
            Toast.makeText(getApplicationContext(), getString(R.string.error_unknown_error), Toast.LENGTH_LONG).show();
            Log.d("Debug", "onFailureListener: e.toString() : " + e.toString());
        };
    }
}
