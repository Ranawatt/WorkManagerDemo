package com.example.workmanagerdemo;

import android.app.Application;
import android.util.Log;

import androidx.work.Configuration;

import timber.log.Timber;

public class BlurApplication extends Application implements Configuration.Provider {

    public Configuration getWorkManagerConfiguration(){

        if (BuildConfig.DEBUG){
            return new Configuration.Builder().setMinimumLoggingLevel(Log.DEBUG).build();
        }else{
            return new Configuration.Builder().setMinimumLoggingLevel(Log.ERROR).build();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

    }
}
