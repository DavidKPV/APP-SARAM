package com.saram.app.controllers.activitys;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // VERIFICA SI EXISTE O NO UNA SESION INICIADA
        verifica();
    }

    private void verifica(){
        // ACTIVAMOS EL EDITOR DEL SHARED
        SharedPreferences sp1 = getSharedPreferences("MisDatos", Context.MODE_PRIVATE);

        // TRAEMOS EL VALOR DE LA SESION
        boolean val = sp1.getBoolean("sesion", false);

        if(val == true) {
            // SE MANDA A EJECUTAR EL ACTIVITY DE INICIO DE LA APP
            // SE INSTANCIA EL ACTIVITY DEL INICIO DE LA APP
            Intent intent = new Intent(this, InicioActivity.class);
            startActivity(intent);
        }else{
            // CREAMOS NUEVAMENTE EL SHARED PREFERENCES
            CrearSharedPreferences();
            // MANDAMOS AL LÓGIN
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    // SE CREA LA FUNCIÓN DEL SHARED PREFERENCES
    private void CrearSharedPreferences(){
        // SE INSTANCIA EL SHARED                   Nombre del archivo,   forma en como se abrirá
        SharedPreferences sp1 = getSharedPreferences("MisDatos", Context.MODE_PRIVATE);

        // SE ACTIVA LA EDICIÓN DEL ARCHIVO

        SharedPreferences.Editor editor = sp1.edit();

        boolean sesion = sp1.getBoolean("sesion", false);
        if(!sesion) {
            // SE MANDAN LOS VALORES QUE QUEREMOS ALMACENAR
            // Nombre de la variable, valor de la variable
            editor.putBoolean("sesion", false);
            editor.putString("nombre", "");
            editor.putString("token", "");
            editor.putString("moto","");
            editor.putString("modelo","Nada que monitorear");
            editor.putInt("Alarma",0);
            editor.putInt("mensajes", 0);

            // SE MANDA UNA INSTRUCCIÓN COMMIT PARA QUE SE GUARDEN LOS CAMBIOS
            editor.commit();
        }
    }
}
