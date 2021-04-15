package com.jeremydufeux.go4lunch;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

@HiltAndroidTest
public class SomeTest {

    @Rule
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Before
    public void init() {
        hiltRule.inject();
    }

    @Test
    public void happyPath() {
        // Can already use analyticsAdapter here.
    }
}
