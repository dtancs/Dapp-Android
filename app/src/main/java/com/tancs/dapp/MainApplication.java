package com.tancs.dapp;

/**
 * Created by tancs on 4/27/17.
 */

import utils.VolleySingleton;
import android.app.Application;

public class MainApplication extends Application {
    // Called when the application is starting, before any other application objects have been created.
    // Overriding this method is totally optional!
    private static VolleySingleton mVolleySingleton;

    @Override
    public void onCreate() {
        super.onCreate();
        // Required initialization logic here!
        mVolleySingleton = VolleySingleton.getInstance(this);
    }
}