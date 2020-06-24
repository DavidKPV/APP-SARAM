package com.saram.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class home extends Fragment {

    // SE DECLARAN LOS OBJETOS UTILIZADOS
    Button btnAlarma;


    public home() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // SE INSTANCIA UN VIEW PARA PODER UTILIZAR CCÓDIGO JAVA (AUNQUE LAS SENTENCIAS CAMBIAN EN
        // COMPARACIÓN A COMO SE DECLARAN Y SE TRAEN UN ACTIVITY)
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // ENLAZAR LOS OBJETOS CON LA VISTA
        btnAlarma = (Button) view.findViewById(R.id.btnAlarma);



        // SE CREA EL OYENTE DEL BOTÓN QUE ACTIVA LA ALARMA
        btnAlarma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ejecutar_alarma();
            }
        });

        return view;
    }

    private void ejecutar_alarma(){
        Toast.makeText(getContext(), "SE ESTA ACTIVANDO...", Toast.LENGTH_LONG).show();
        // REALIZAMOS EL CAMBIO A LA CLASE JAVA DE LA ALARMA CON LA AYUDA DE UN INTENT
        Intent alarma = new Intent(getActivity(), com.saram.app.alarma.class);

        // SE CREA EL OBJETO DEL ALARM MANAGER
        AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        PendingIntent pi = PendingIntent.getBroadcast(getContext(),0,alarma,0);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),pi);

    }
}