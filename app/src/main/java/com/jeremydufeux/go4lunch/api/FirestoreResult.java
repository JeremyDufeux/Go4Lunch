package com.jeremydufeux.go4lunch.api;

import org.jetbrains.annotations.Nullable;

public class FirestoreResult {
    Boolean success;
    @Nullable
    Exception error;

    public FirestoreResult(Boolean success) {
        this.success = success;
    }

    public FirestoreResult(Boolean success, @Nullable Exception error) {
        this.success = success;
        this.error = error;
    }

    public Boolean getSuccess() {
        return success;
    }

    @Nullable
    public Exception getError() {
        return error;
    }
}
