package com.example.workmanagerdemo;

import android.content.Context;
import android.util.Log;


import androidx.test.platform.app.InstrumentationRegistry;
import androidx.work.Configuration;
import androidx.work.WorkManager;
import androidx.work.testing.SynchronousExecutor;
import androidx.work.testing.WorkManagerTestInitHelper;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class WorkManagerTestRule extends TestWatcher {
    private Context targetContext;
    private Context testContext;
    private Configuration configuration;
    private WorkManager workManager;

    @Override
    protected void starting(Description description) {
        super.starting(description);
        targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        testContext = InstrumentationRegistry.getInstrumentation().getContext();

        configuration = new Configuration.Builder()
                //Set log level to Log.DEBUG to make it easier to debug
                .setMinimumLoggingLevel(Log.DEBUG)
                // Use Synchronous executor here to make it easier to test
                .setExecutor(new SynchronousExecutor()).build();
        // Initialize work manager for instrumentation Test
        WorkManagerTestInitHelper.initializeTestWorkManager(targetContext,configuration);
        workManager = WorkManager.getInstance(targetContext);
    }
}
