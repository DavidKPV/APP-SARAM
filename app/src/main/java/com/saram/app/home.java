package com.saram.app;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.w3c.dom.Text;

public class home extends Fragment {

    // SE DECLARAN LOS OBJETOS UTILIZADOS
    Button btnAlarma;
    TextView tvNombre;
    String nombre;

    public home() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        tvNombre = (TextView) getActivity().findViewById(R.id.tvNombre);

        SharedPreferences sp1 = getActivity().getSharedPreferences("MisDatos", Context.MODE_PRIVATE);
        nombre = sp1.getString("nombre", "Se reseteó el shared");

        tvNombre.setText(nombre);

        // ALERT DIALOG PARA INDICAR QUE DEBE DE CONTINUAR LLENANDO SU INFORMACIÓN DESPUÉS DE HABER HECHO LOGIN
        AlertDialog.Builder VerificaAlerta = new AlertDialog.Builder(getContext());
        VerificaAlerta.setIcon(R.drawable.ic_baseline_info_24);
        VerificaAlerta.setTitle("VE AL ÍCONO DE SARAM DEL MENÚ DESPLEGABLE");
        VerificaAlerta.setMessage("Para continuar llenando tu información de usuario");
        VerificaAlerta.setPositiveButton("CONTINUAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent infouser = new Intent(getContext(), userinfoActivity.class);
                startActivity(infouser);
            }
        });
        VerificaAlerta.setNegativeButton("MAS TARDE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        VerificaAlerta.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // SE INSTANCIA UN VIEW PARA PODER UTILIZAR CCÓDIGO JAVA (AUNQUE LAS SENTENCIAS CAMBIAN EN
        // COMPARACIÓN A COMO SE DECLARAN Y SE TRAEN UN ACTIVITY)
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // ENLAZAR LOS OBJETOS CON LA VISTA
        btnAlarma = (Button) view.findViewById(R.id.btnAlarma);

        //SharedPreferences sp1 = getActivity().getSharedPreferences("MisDatos", Context.MODE_PRIVATE);

        //tvNombre.setText("DAVID");

        btnAlarma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ejecutar_alarma();
            }
        });



        return view;
    }

    private void ejecutar_alarma(){
        AlertDialog.Builder VerificaAlerta = new AlertDialog.Builder(getContext());
        VerificaAlerta.setIcon(R.drawable.ic_baseline_healing_24);
        VerificaAlerta.setTitle("SE ACTIVARÁ LA ALARMA MANUAL :C");
        VerificaAlerta.setMessage("Si continua se mandarán las alertas a sus contactos seleccionados");
        VerificaAlerta.setPositiveButton("CONTINUAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getContext(), "SE ESTA ACTIVANDO...", Toast.LENGTH_LONG).show();
                // REALIZAMOS EL CAMBIO A LA CLASE JAVA DE LA ALARMA CON LA AYUDA DE UN INTENT
                Intent alarma = new Intent(getActivity(), com.saram.app.alarma.class);

                // SE CREA EL OBJETO DEL ALARM MANAGER
                AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                PendingIntent pi = PendingIntent.getBroadcast(getContext(),0,alarma,0);
                am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),pi);
            }
        });
        VerificaAlerta.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        VerificaAlerta.show();

    }

}