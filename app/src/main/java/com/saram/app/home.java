package com.saram.app;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class home extends Fragment {

    // SE DECLARAN LOS OBJETOS UTILIZADOS
    Button btnAlarma;
    TextView tvNombre;
    String nombre;

    // OBJETOS PARA LA CONEXIÓN AL SERVIDOR UTILIZANDO VOLLEY
    RequestQueue requestQueue;
    ProgressDialog progressDialog;

    // CREAMOS UNA CADENA LA CUAL CONTENDRÁ LA CADENA DE NUESTRO WEB SERVICE
    String HttpUriCheck = "http://192.168.43.200:8080/SARAM-API/public/api/userisReady";
    String vtoken;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // SE INSTANCIA UN VIEW PARA PODER UTILIZAR CCÓDIGO JAVA (AUNQUE LAS SENTENCIAS CAMBIAN EN
        // COMPARACIÓN A COMO SE DECLARAN Y SE TRAEN UN ACTIVITY)
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // ENLAZAR LOS OBJETOS CON LA VISTA
        btnAlarma = (Button) view.findViewById(R.id.btnAlarma);
        // SE ACTIVAN LOS OBJETOS DE REQUEST QUEUE Y PROGRESS DIALOG
        requestQueue = Volley.newRequestQueue(getActivity());
        progressDialog = new ProgressDialog(getActivity());

        btnAlarma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ejecutar_alarma();
            }
        });

        // OBTENEMOS EL TOKEN DEL SHARED
        SharedPreferences sp1 = getActivity().getSharedPreferences("MisDatos", Context.MODE_PRIVATE);
        vtoken = sp1.getString("token", "");

        // SE VERIFICA CON LA BASE DE DATOS SI LOS DATOS ESENCIALES DEL USUARIO ESTAN COMPLETOS
        // MOSTRAMOS EL PROGRESS DIALOG ---- AQUÍ SE COMIENZA EL ARMADO Y LA EJECUCIÓN DEL WEB SERVICE
        progressDialog.setMessage("CARGANDO...");
        progressDialog.show();
        // CREACIÓN DE LA CADENA A EJECUTAR EN EL WEB SERVICE MEDIANTE VOLLEY
        // Objeto de volley
        StringRequest stringRequest = new StringRequest(Request.Method.POST, HttpUriCheck,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String serverresponse) {
                        // UNA VEZ QUE SE MANDAN TODOS LOS VALORES AL WEB SERVICE
                        // QUITAMOS EL PROGRESS DIALOG PARA QUE UNA VEZ QUE SE MANDEN LOS DATOS
                        // YA SE PUEDA TRABAJAR
                        progressDialog.dismiss();
                        // MANEJO DE ERRORES CON RESPECTO A LA RESPUESTA
                        try {
                            // CREAR UN OBJETO DE TIPO JSON PARA OBTENER EL ARCHIVO QUE MANDARÁ EL WEB SERVICE
                            JSONObject obj = new JSONObject(serverresponse);
                            // INTERPRETAR EL VALOR DEL JSON OBTENIDO DEL WEB SERVICE
                            String status = obj.getString("ready");
                            // INTERPRETAR LOS VALORES
                            if (status.contentEquals("false")) {
                                // ALERT DIALOG PARA INDICAR QUE DEBE DE CONTINUAR LLENANDO SU INFORMACIÓN DESPUÉS DE HABER HECHO LOGIN
                                AlertDialog.Builder VerificaAlerta = new AlertDialog.Builder(getContext());
                                VerificaAlerta.setIcon(R.drawable.ic_baseline_info_24);
                                VerificaAlerta.setTitle("¡COMPLETA TU DATOS DE PERFIL!");
                                VerificaAlerta.setMessage("Puedes entrar a la información de perfil dando clic en el ícono de SARAM dentro del menú desplegable o ¿Deseas continuar llenando tu información de perfil?");
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
                            } else {
                                // NO SE MANDARÁ MENSAJE DE QUE SE NECESITA LLENAR INFORMACIÓN
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    // ESTE SE EJECUTA SI HAY UN ERROR EN LA RESPUESTA
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // SE OCULTA EL PROGRESS DIALOG
                progressDialog.dismiss();
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            // SOLO SE MANDA EL TOKEN
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                params.put("Authorization", vtoken);
                return params;
            }
        };

        // SE MANDA A EJECUTAR EL STRING PARA LA LIBRERÍA DE VOLLEY
        requestQueue.add(stringRequest);

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