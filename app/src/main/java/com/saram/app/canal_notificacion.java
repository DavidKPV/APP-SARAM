package com.saram.app;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class canal_notificacion extends Application {

    // DECLARAMOS LA VARIABLE DEL CANAL
    public static final String Canal_ID = "canal_1";

    @Override
    public void onCreate() {
        super.onCreate();
        // ACTIVAMOS EL MÉTODO QUE MANDARÁ LAS NOTIFICACIONES POR EL CANAL
        lanzarNotificaciones();
    }

    private void lanzarNotificaciones() {

        // HACEMOS LA COMPARATIVA PARA MANDAR AL CANAL
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel canal1 = new NotificationChannel(
                    Canal_ID,
                    "Canal_1",
                    NotificationManager.IMPORTANCE_HIGH
            );
            //canal1.setDescription("This is Channel 1");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(canal1);
        }
    }
}
