package com.saram.app.modules;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.widget.Toast;

import com.saram.app.R;

public class alarma extends BroadcastReceiver {

    MediaPlayer mediaPlayer;

    @Override
    public void onReceive(Context context, Intent intent) {
        // SOLO QUEDA CONTINUAR MEJORANDO LA INTERFAZ DE LA ALARMA

        // PRUEBA CON UN TOAST
        Toast.makeText(context, "ACTIVANDO PROCESOS...", Toast.LENGTH_LONG).show();

        // SE ACTIVA LA VIBRACIÓN DEL CELULAR
        Vibrator v = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
        v.vibrate(5000);

        // SE CONSTRUYE EL MEDIA PLAYER Y SE LE DA EL SONIDO
        mediaPlayer = MediaPlayer.create(context, R.raw.sportsbike);
        mediaPlayer.start();
    }
}
