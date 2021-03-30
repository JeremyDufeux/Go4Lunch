package com.jeremydufeux.go4lunch.injection;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ViewModelComponent;

@Module
@InstallIn(ViewModelComponent.class)
public class ExecutorModule {

    @Provides
    Executor provideExecutor() {
        return Executors.newSingleThreadExecutor();
    }
}
