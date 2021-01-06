package com.saram.app.models;

import android.app.Application;
import android.os.SystemClock;

public class Retardo_splash extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // DORMIMOS LA APP DURANTE 3 SEGUNDOS
        SystemClock.sleep(500);
    }
}
