package com.saram.app.controllers.fragments;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.saram.app.R;
import com.saram.app.controllers.activitys.InicioActivity;
import com.saram.app.models.Alarma;
import com.saram.app.models.general.AlertsDiaog;
import com.saram.app.routes.Rutas;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
// IMPORTAMOS EL JAVA EN DONDE SE ENCUENTRA DECLARADO EL CANAL QUE UTILIZAREMOS PARA LAS NOTIFICACIONES
// import static com.saram.app.canal_notificacion.Canal_ID;

public class Home extends Fragment{
    // SE DECLARAN LOS OBJETOS UTILIZADOS
    private AlertsDiaog alertsDiaog = new AlertsDiaog();
    private Button btnAlarma;
    private TextView tvNombre, tvEstado, tvModelo;
    private ImageView imgEstado;
    private String nombre, latitud, longitud;

    // PARA EL LLAMADO CONTINUO DEL WEB SERVICE
    public static final long PERIODO_MONI = 30000; // 30 segundos (60 * 1000 millisegundos)
    private Handler handler;
    private Runnable runnable;

    // VARIABLES PARA EL CONTADOR PARA EVITAR QUE LOS MENSAJES SE EJECUTEN DE MANERA AUTOMÁTICA SI QUE REALMENTE SEA UN ACCIDENTE
    private static final long PERIODO_ALERTA = 60000; // 5 Minutos --> 300000
    private CountDownTimer countDownTimer;
    private boolean tiempoCorriendo;
    private long miTiempo = PERIODO_ALERTA;

    // OBJETOS PARA LA CONEXIÓN AL SERVIDOR UTILIZANDO VOLLEY
    RequestQueue requestQueue;
    ProgressDialog progressDialog;

    // CREAMOS UNA CADENA LA CUAL CONTENDRÁ LA CADENA DE NUESTRO WEB SERVICE
    String HttpUriCheck = Rutas.getEstado;
    String HttpUriContac = Rutas.getContactos;
    String HttpUriCheckGPS = Rutas.getUbicacion;
    String vtoken, idmoto, modelo;

    public Home() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();

        // EWJECUTAMOS EL CANAL
        muestraNotificacion();

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

        // PARA EJECUTAR LAS TAREAS EN SEGUNDO PLANO
        //tiempo timer = new tiempo();
        //timer.execute();

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

        // SE ACTIVAN LOS OBJETOS DE REQUEST QUEUE Y PROGRESS DIALOG
        requestQueue = Volley.newRequestQueue(getActivity());
        progressDialog = new ProgressDialog(getActivity());

        // EJECUTAMOS EL CANAL
        muestraNotificacion();

        // DAMOS VALOR A LAS COORDENADAS
        obtenerCoordenadas();

        if(modelo.equals("Nada que monitorear")){
            // MOSTRAMOS LA IMAGEN ADECUADA
            imgEstado.setImageDrawable(getResources().getDrawable(R.drawable.nada));

            // COLOCAMOS EL VALOR DE MOTOCICLETA EN BUEN ESTADO
            tvEstado.setText("");
            tvModelo.setText(modelo);
            alertsDiaog.alert_OK(getContext(), "Ve a la sección de motocicletas y selecciona la motocicleta que deseas monitorear", "ENTENDIDO", 0, null).show();
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
                Intent alarma = new Intent(getActivity(), Alarma.class);

                // SE CREA EL OBJETO DEL ALARM MANAGER
                AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                PendingIntent pi = PendingIntent.getBroadcast(getContext(),0,alarma,0);
                am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),pi);

                mandaMensajes();
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

                            if (status == 3) {
                                // MOSTRAMOS LA IMAGEN ADECUADA
                                imgEstado.setImageDrawable(getResources().getDrawable(R.drawable.tache));

                                tvModelo.setText(modelo);
                                // COLOCAMOS EN EL TEXT VIEW QUE EXISTE UN ERROR
                                tvEstado.setText("¡MOTOCICLETA CAÍDA!");

                                // MANDAMOS NOTIFICACIÓN DE QUE LA MOTOCICLETA SE HA CAÍDO
                                sendNotificacion();
                                startAlarma();

                                AlertDialog.Builder VerificaAlerta = new AlertDialog.Builder(getContext());
                                VerificaAlerta.setIcon(R.drawable.ic_baseline_healing_24);
                                VerificaAlerta.setTitle("¡SE HA DETECTADO UN ACCIDENTE!");
                                VerificaAlerta.setMessage("Presiona en DETENER si no deseas que se manden las alertas a tus contactos");
                                VerificaAlerta.setPositiveButton("DETENER", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        // CREAMOS EL MÉTODO PARA RESETEAR EL CONTADOR
                                        resetAlarma();
                                    }
                                });

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
                                imgEstado.setImageDrawable(getResources().getDrawable(R.drawable.nada));

                                tvModelo.setText(modelo);
                                tvEstado.setText("No se detecta\ninformación");
                                //Toast.makeText(getActivity(), "Asegurate de que el dispositivo SARAM de la motocicleta "+modelo+" se encuentre encendido", Toast.LENGTH_LONG).show();
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
                parametros.put("ID_motocicleta", idmoto);
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
        manda_a_numeros();
    }

    private void sendNotificacion(){
        // CREAMOS EL PENDING INTENT PARA REDIRECCIONAR AL INICIO DE LA APP
        Intent resultIntent = new Intent(getContext(), InicioActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getContext());
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


        // CREAMOS LA NOTIFICACION DE ALERTA
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "1")
                .setSmallIcon(R.drawable.saram)
                .setContentTitle("Se detectó CAÍDA de la motocicleta - "+modelo)
                .setContentText("¡Si te encuentras bien cancela las alertas!")
                .setVibrate(new long[] {1000, 2500, 1000, 5000})
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1234, builder.build());
    }

    public void muestraNotificacion() {
        // SE CREA EL CANAL DE NOTIFICACIONES, EN CASO DE FUNCIONAR EN APIS SUPERORES A LA 26
        // Y PARA ELLO SE CREA EL IF PARA DETECTAR LA VERSIÓN DE ANDROD CON SDK_INT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "canal_1";
            String description = "este es el canal 1";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            // CREAMOS LA NOTIFICACION POR EL CANAL PREDEFINIDO
            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /*
    // CREACIÓN DE HILO PARA QUE SE EJECUTE SIEMPRE EN UN DETERMINADO TIEMPO
    public void hilo(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void ejecutar(){
        tiempo timer = new tiempo();
        timer.execute();
    }

    // CREAMOS LA CLASE QUE DARÁ EL TIEMPO DE EJECUCIÓN EN SEGUNDO PLANO
    public class tiempo extends AsyncTask<Void, Integer, Boolean>{
        @Override
        protected Boolean doInBackground(Void... voids) {

            for(int con=1; con<=30; con++){
                hilo();
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            ejecutar();

            // EN ESTA PARTE DEL CÓDIGO EJECUTAMOS TODO EL CÓDIGO QUE SE NECESITA EN SEGUNDO PLANO

        }
    }
     */

    private void manda_a_numeros(){
        // VEWRIFICAMOS QUE SE TENGAN LOS PERMISOS NECESARIOS PARA EL ENVÍO DE MENSAJES DE TEXTO
        int permissionCheckSMS = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS);

        // SE VERIFICA CON LA CONDICIONAL DE QUE LOS PERMISOS SE ENCIENTREN INSTALDOS
        if(permissionCheckSMS == PackageManager.PERMISSION_GRANTED){
            // AQUI SE COLOCA MENSAJES EXTRAS QUE SE QUIERAN COLOCAR SOBRE LA EXPLICACIÓN DE LOS PERMISOS
            // AQUÍ COLOCAREMOS TODO EL CONTENIDO PARA MANDAR MENSAJES
            // SE ACTIVA TODO PARA TRAER TODOS LOS NÚMEROS DE CONTACTOS SARAM
            // MOSTRAMOS EL PROGRESS DIALOG ---- AQUÍ SE COMIENZA EL ARMADO Y LA EJECUCIÓN DEL WEB SERVICE
            progressDialog.setMessage("CARGANDO...");
            progressDialog.show();
            // CREACIÓN DE LA CADENA A EJECUTAR EN EL WEB SERVICE MEDIANTE VOLLEY
            // Objeto de volley
            StringRequest stringRequest = new StringRequest(Request.Method.POST, HttpUriContac,
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
                                String status = obj.getString("status");

                                // INTERPRETAR LOS VALORES
                                if (status.contentEquals("false")) {
                                    Toast.makeText(getContext(), "Error de autenticación, Intentalo más tarde", Toast.LENGTH_LONG).show();
                                    Intent inicio2 = new Intent(getContext(), InicioActivity.class);
                                    startActivity(inicio2);
                                } else {
                                    // CÓDIGO PARA MANDAR LOS MENSAJES DE TEXTO
                                    try {

                                        // SE OBTIENE EL NÚMERO TELEFÓNICO
                                        SmsManager sms = SmsManager.getDefault();

                                        // OBTENER SE OBTIENEN LOS DATOS DEL ARRAY
                                        JSONArray ContactosInfo = obj.getJSONArray("contactos");
                                        final String[] Numero = new String[ContactosInfo.length()];

                                        for (int i = 0; i < ContactosInfo.length(); i++) {
                                            Numero[i] = ContactosInfo.getJSONObject(i).getString("Numero_Tel");
                                            sms.sendTextMessage(
                                                    Numero[i],
                                                    null,
                                                    //"prueba 2",
                                                    "Intenta comunicarte conmigo, mi motocicleta se ha caido aproximadamente en esta zona... https://maps.google.com/?q="+latitud+","+longitud,
                                                    null,
                                                    null);
                                        }

                                        Toast.makeText(getActivity(),"MENSAJES ENVIADOS", Toast.LENGTH_SHORT).show();
                                    }
                                    catch (Exception e){
                                        Toast.makeText(getContext(), "Fallo en envío de mensajes", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getContext(), "NO ENTRA AL TRY", Toast.LENGTH_LONG).show();
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
        }
        else{
            // PEDIRÁ LA ACTIVACIÓN DEL SERVICIO
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS}, 1);
        }
    }

    private void obtenerCoordenadas(){
        // VERIFICA QUE TENGA LA APP LOS PERMISOS NECESARIOS PARA LA UTILIZACIÓN DE LA UBICACIÓN
        int permissionCheckUbicacion = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);

        // SE VERIFICA CON LA CONDICIONAL QUE SE TENGAN LOS PERMISOS INSTALADOS
        if (permissionCheckUbicacion == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                // SE COLOCAN MENSAJES DE EXPLICACIÓN EXTRA DEL PORQUE SE NECESITAN ACTIVAR ESTOS PERMISOS
            } else {
                // PEDIRÁ LA ACTIVACIÓN DEL SERVICIO
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
        else{
            // AQUI SE COLOCA EL CÓDIGO PARA DAR LOS VALORES DE LATITUD Y LONGITUD
            //-------------------------INICIO DE LA LLAMADA AL WEB SERVICE--------------------
            // SE ACTIVA TODO PARA TRAER INFORMACIÓN DE LA COORDENADAS DE LA MOTOCICLETA
            // MOSTRAMOS EL PROGRESS DIALOG ---- AQUÍ SE COMIENZA EL ARMADO Y LA EJECUCIÓN DEL WEB SERVICE
            progressDialog.setMessage("CARGANDO...");
            progressDialog.show();
            // CREACIÓN DE LA CADENA A EJECUTAR EN EL WEB SERVICE MEDIANTE VOLLEY
            // Objeto de volley
            StringRequest stringRequest = new StringRequest(Request.Method.POST, HttpUriCheckGPS,
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
                                String status = obj.getString("status");

                                if(status.contentEquals("true")){
                                    // SE OBTIENEN LOS VALORES DE LATITUD Y LONGITUD
                                    latitud = obj.getString("latitud");
                                    longitud = obj.getString("longitud");
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
                protected Map<String, String> getParams(){
                    Map<String, String> parametros = new HashMap<>();
                    parametros.put("ID_motocicleta", idmoto);
                    return parametros;
                }
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
            //------------------FIN DE LA LLAMADA DEL WEB SERVICE-----------------------------
        }
    }

    // MÉTODOS DE EL CONTADOR DE LA ALARMA PARA PERMITIR AL USUARIO LOGRAR DETENER QUE SE ENVÍEN LAS ALERTAS A SUS CONTACTOS
    private void startAlarma(){
        countDownTimer = new CountDownTimer(miTiempo, 1000) {
            @Override
            public void onTick(long segundosParaFinalizar) {
                // ASUGNAMOS EL VALOR DE MI TIEMPO CON EL VALOR DE CADA SEGUNDO
                miTiempo = segundosParaFinalizar;
            }

            @Override
            public void onFinish() {
                // DAMOS EL VALOR DE FALSO PARA INDICAR QUE SE FINALIZÓ EL TIEMPO
                tiempoCorriendo = false;
                // AL FINALIZAR EL TIEMPO CORREMOS EL MANDAR MENSAJES DE TEXTO
                mandaMensajes();
            }
        }.start();

        tiempoCorriendo = true;
    }

    private void resetAlarma(){
        // VOLVEMOS ASIGNARLE EL VALOR DE 10 MINUTOS PARA QUE SE REINICIE
        miTiempo = PERIODO_ALERTA;
    }
}