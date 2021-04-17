package com.jeremydufeux.go4lunch.utils;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

public class RxJavaSchedulersTestRule2 implements TestRule {
    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                resetPlugins();
                RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
                RxAndroidPlugins.setMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());

                base.evaluate();

                resetPlugins();
            }
        };
    }

    private void resetPlugins() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }
}