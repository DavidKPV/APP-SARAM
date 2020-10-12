package com.saram.app.modules;

import android.app.Application;
import android.os.SystemClock;

public class retardo_splash extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // DORMIMOS LA APP DURANTE 3 SEGUNDOS
        SystemClock.sleep(500);
    }
}
