package com.saram.app;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import static android.view.View.GONE;

public class home extends Fragment {

    // SE DECLARAN LOS OBJETOS UTILIZADOS
    Button btnAlarma;
    TextView tvNombre, tvEstado, tvModelo;
    ImageView imgEstado;
    String nombre;
    int vAlarma;

    public static final long PERIODO_MONI = 30000; // 30 segundos (60 * 1000 millisegundos)
    public static final long PERIODO_ALARMA = 120000; // 30 segundos (60 * 1000 millisegundos)
    private Handler handler;
    private Runnable runnable;

    // OBJETOS PARA LA CONEXIÓN AL SERVIDOR UTILIZANDO VOLLEY
    RequestQueue requestQueue;
    ProgressDialog progressDialog;

    // CREAMOS UNA CADENA LA CUAL CONTENDRÁ LA CADENA DE NUESTRO WEB SERVICE
    String HttpUriCheck = "http://192.168.43.200:8080/SARAM-API/public/api/getEstado";
    String vtoken, idmoto, modelo;

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

        if(modelo.equals("Nada que monitorear")) {
            // SI NO HAY NADA QUE MONITOREAR NO REALIZARÁ EL PROCESO DE LLAMADA CONTINUA AL SERVIDOR
        }
        else{
            handler = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {
                    checaEstado();
                    handler.postDelayed(this, PERIODO_MONI);
                }
            };
            handler.postDelayed(runnable, PERIODO_MONI);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // SE INSTANCIA UN VIEW PARA PODER UTILIZAR CCÓDIGO JAVA (AUNQUE LAS SENTENCIAS CAMBIAN EN
        // COMPARACIÓN A COMO SE DECLARAN Y SE TRAEN UN ACTIVITY)
        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        // ENLAZAR LOS OBJETOS CON LA VISTA
        btnAlarma = (Button) view.findViewById(R.id.btnAlarma);
        tvEstado = (TextView) view.findViewById(R.id.tvEstado);
        imgEstado = (ImageView) view.findViewById(R.id.imgEstado);
        tvModelo = (TextView) view.findViewById(R.id.tvModeloMoto);

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
        idmoto = sp1.getString("moto","");
        modelo = sp1.getString("modelo","");
        vAlarma = sp1.getInt("alarma",0);

        // SE ACTIVAN LOS OBJETOS DE REQUEST QUEUE Y PROGRESS DIALOG
        requestQueue = Volley.newRequestQueue(getActivity());
        progressDialog = new ProgressDialog(getActivity());

        if(modelo.equals("Nada que monitorear")){
            // MOSTRAMOS LA IMAGEN ADECUADA
            imgEstado.setImageDrawable(getResources().getDrawable(R.drawable.nada));

            // COLOCAMOS EL VALOR DE MOTOCICLETA EN BUEN ESTADO
            tvEstado.setText("");
            tvModelo.setText(modelo);
            Toast.makeText(getActivity(), "Ve a la sección de información y selecciona el ícono de SARAM de la motocicleta que deseas monitorear", Toast.LENGTH_LONG).show();
        }
        else{
            checaEstado();
        }

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

    private void checaEstado(){
        // SE VERIFICA CON LA BASE DE DATOS SI LOS DATOS ESENCIALES DEL USUARIO ESTAN COMPLETOS
        // MOSTRAMOS EL PROGRESS DIALOG ---- AQUÍ SE COMIENZA EL ARMADO Y LA EJECUCIÓN DEL WEB SERVICE
        //progressDialog.setMessage("CARGANDO...");
        //progressDialog.show();
        // CREACIÓN DE LA CADENA A EJECUTAR EN EL WEB SERVICE MEDIANTE VOLLEY
        // Objeto de volley
        StringRequest stringRequest = new StringRequest(Request.Method.POST, HttpUriCheck,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String serverresponse) {
                        // UNA VEZ QUE SE MANDAN TODOS LOS VALORES AL WEB SERVICE
                        // QUITAMOS EL PROGRESS DIALOG PARA QUE UNA VEZ QUE SE MANDEN LOS DATOS
                        // YA SE PUEDA TRABAJAR
                        // progressDialog.dismiss();
                        // MANEJO DE ERRORES CON RESPECTO A LA RESPUESTA
                        try {
                            // CREAR UN OBJETO DE TIPO JSON PARA OBTENER EL ARCHIVO QUE MANDARÁ EL WEB SERVICE
                            JSONObject obj = new JSONObject(serverresponse);
                            // INTERPRETAR EL VALOR DEL JSON OBTENIDO DEL WEB SERVICE
                            int status = obj.getInt("Estado");
                            // INTERPRETAR LOS VALORES
                            if(vAlarma==0) {
                                if (status == 3) {
                                    // MOSTRAMOS LA IMAGEN ADECUADA
                                    imgEstado.setImageDrawable(getResources().getDrawable(R.drawable.tache));

                                    tvModelo.setText(modelo);
                                    // COLOCAMOS EN EL TEXT VIEW QUE EXISTE UN ERROR
                                    tvEstado.setText("¡MOTOCICLETA CAÍDA!");

                                    // ALERT DIALOG PARA INDICAR QUE DEBE DE CONTINUAR LLENANDO SU INFORMACIÓN DESPUÉS DE HABER HECHO LOGIN
                                    AlertDialog.Builder VerificaAlerta = new AlertDialog.Builder(getContext());
                                    VerificaAlerta.setIcon(R.drawable.ic_baseline_healing_24);
                                    VerificaAlerta.setTitle("¡SE HA DETECTADO UN ACCIDENTE!");
                                    VerificaAlerta.setMessage("Presiona en DETENER si no deseas que se manden las alertas a tus contactos");
                                    VerificaAlerta.setPositiveButton("DETENER", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            SharedPreferences sp1 = getActivity().getSharedPreferences("MisDatos", Context.MODE_PRIVATE);
                                            // SE MODIFICA EL VALOR DEL SHARED INICIALIZANDO EL EDITOR DEL SHARED
                                            SharedPreferences.Editor editor = sp1.edit();
                                            editor.putInt("alarma", 1);
                                            editor.commit();
                                        }
                                    });

                                    // TIMERS
                                    handler = new Handler();
                                    runnable = new Runnable(){
                                        @Override
                                        public void run(){
                                            mandaMensajes();
                                            handler.postDelayed(this, PERIODO_ALARMA);
                                        }
                                    };
                                    handler.postDelayed(runnable, PERIODO_ALARMA);
                                    /*
                                    VerificaAlerta.setNegativeButton("MAS TARDE", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                        }
                                    });
                                    */
                                    VerificaAlerta.show();


                                }
                                if(status==2){
                                    // MOSTRAMOS LA IMAGEN ADECUADA
                                    imgEstado.setImageDrawable(getResources().getDrawable(R.drawable.palomaverde));

                                    tvModelo.setText(modelo);
                                    tvEstado.setText("EXCELENTE");
                                }
                                // CUANDO SARAM NO TENGA ESTADO
                                if(status==1){
                                    // MOSTRAMOS LA IMAGEN ADECUADA
                                    imgEstado.setImageDrawable(getResources().getDrawable(R.drawable.saram_nada));

                                    tvModelo.setText(modelo);
                                    tvEstado.setText("No se detecta información");
                                    //Toast.makeText(getActivity(), "Asegurate de que el dispositivo SARAM de la motocicleta "+modelo+" se encuentre encendido", Toast.LENGTH_LONG).show();
                                }
                            }
                            else{
                                // SI EL VALOR DE LA ALARMA TIENE UN VALOR DE 1 QUIERE DECIR QUE SE PAGÓ LA ALARMA O SE ENVIARON
                                // LOS MENSAJES
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
            // MAPEO DE LOS VALORES QUE MANDAMOS AL WEB SERVICE
            protected Map<String, String> getParams() {
                // RETORNAR LOS VALORES
                Map<String, String> parametros = new HashMap<>();
                parametros.put("ID_motocicleta", "18");
                return parametros;
            }
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
    }

    private void mandaMensajes(){
        Toast.makeText(getActivity(),"MANDANDO MENSAJES", Toast.LENGTH_SHORT).show();
    }

}