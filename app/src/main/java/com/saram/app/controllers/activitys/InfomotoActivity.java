package com.saram.app.controllers.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.saram.app.R;
import com.saram.app.controllers.adapters.MotosAdapter;
import com.saram.app.routes.Rutas;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class InfomotoActivity extends AppCompatActivity {

    // ESTOS SON DE LA ETIQUETA QUE CREO NO SE OCUPAN
    FloatingActionButton fabMas;
    RecyclerView rvContenedorMotos;
    MotosAdapter Motocicletas;

    // OBJETOS PARA LA CONEXIÓN AL SERVIDOR UTILIZANDO VOLLEY
    RequestQueue requestQueue;
    ProgressDialog progressDialog;

    // CREAMOS UNA CADENA LA CUAL CONTENDRÁ LA CADENA DE NUESTRO WEB SERVICE
    String HttpUri = Rutas.getMotos;
    String HttpUriDel = Rutas.delMoto;
    String vtoken;

    // ESTE MÉTODO EVITA QUE SE REGRESE CON LA FLECHA DE RETORNO QUE TODOS TENEMOS
    @Override
    public void onBackPressed() {
        // CUANDO REGRESE CON LA FLECHA DE REGRESO LO QUE HARÁ SERÁ REGRESAR AL INICIO
        Intent inicio = new Intent(this, InicioActivity.class);
        startActivity(inicio);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infomoto);
        // PARA ACTIVAR LA FLECHA DE RETORNO
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // SE ENLAZAN LOS OBJETOS CON LA VISTA
        fabMas = (FloatingActionButton) findViewById(R.id.fabMas);
        rvContenedorMotos = (RecyclerView) findViewById(R.id.rvContenedorMotos);
        rvContenedorMotos.setHasFixedSize(true);

        //Asociar el RV a un LinearLayoutManager
        LinearLayoutManager LLManager= new LinearLayoutManager(this);
        rvContenedorMotos.setLayoutManager(LLManager);

        // SE ACTIVAN LOS OBJETOS DE REQUEST QUEUE Y PROGRESS DIALOG
        requestQueue = Volley.newRequestQueue(InfomotoActivity.this);
        progressDialog = new ProgressDialog(InfomotoActivity.this);

        // OBTENEMOS EL TOKEN DEL SHARED
        SharedPreferences sp1 = getSharedPreferences("MisDatos", Context.MODE_PRIVATE);
        vtoken = sp1.getString("token", "");


        fabMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent agregar =  new Intent(InfomotoActivity.this, AddmotoActivity.class);
                startActivity(agregar);
            }
        });

        // SE ACTIVA TODO PARA TRAER INFORMACIÓN DE LA MOTOCICLETA
        // AQUÍ SE REALIZA EL PROCESO PARA REALIZAR LA INSERCIÓN EN LA BASE DE DATOS
        // MOSTRAMOS EL PROGRESS DIALOG ---- AQUÍ SE COMIENZA EL ARMADO Y LA EJECUCIÓN DEL WEB SERVICE
        progressDialog.setMessage("CARGANDO...");
        progressDialog.show();
        // CREACIÓN DE LA CADENA A EJECUTAR EN EL WEB SERVICE MEDIANTE VOLLEY
        // Objeto de volley
        StringRequest stringRequest = new StringRequest(Request.Method.POST, HttpUri,
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
                                Toast.makeText(getApplicationContext(), "Error de autenticación, Intentalo más tarde", Toast.LENGTH_LONG).show();
                                Intent inicio2 = new Intent(getApplicationContext(), InicioActivity.class);
                                startActivity(inicio2);
                            } else {
                                // OBTENER SE OBTIENEN LOS DATOS DEL ARRAY
                                JSONArray MotosInfo = obj.getJSONArray("motos");
                                final String[] Modelos = new String[MotosInfo.length()];
                                String[] Marcas = new String[MotosInfo.length()];
                                String[] Cilindraje = new String[MotosInfo.length()];
                                String[] Placas = new String[MotosInfo.length()];
                                String[] SARAM = new String[MotosInfo.length()];
                                final int[] ID_Motocicleta = new int[MotosInfo.length()];
                                for (int i=0; i<MotosInfo.length(); i++){
                                    Modelos[i]=MotosInfo.getJSONObject(i).getString("Modelo");
                                    Marcas[i]=MotosInfo.getJSONObject(i).getString("Marca");
                                    Cilindraje[i]=MotosInfo.getJSONObject(i).getString("Cilindraje");
                                    SARAM[i]=MotosInfo.getJSONObject(i).getString("ID_saram");
                                    Placas[i]=MotosInfo.getJSONObject(i).getString("Placa");
                                    ID_Motocicleta[i]=MotosInfo.getJSONObject(i).getInt("ID_Motocicleta");
                                }
                                Motocicletas = new MotosAdapter(Modelos, Marcas, Cilindraje, Placas, SARAM, ID_Motocicleta, getApplication());
                                rvContenedorMotos.setAdapter(Motocicletas);

                                Motocicletas.setOnItemClickListener(new MotosAdapter.OnItemClickListener() {
                                    @Override
                                    public void onEditClick(int position) {
                                        Intent editmoto = new Intent(InfomotoActivity.this, EditmotoActivity.class);
                                        editmoto.putExtra("ID", ID_Motocicleta[position]);
                                        startActivity(editmoto);
                                    }

                                    @Override
                                    public void onDeleteClick(final int position) {
                                        // DAMOS EL VALOR DEL MODELO DE LA MOTOCICLETA A ELIMINAR
                                        final String modelo = Modelos[position];

                                        // DECLARAMOS LAS OPCIONES U OPCIÓN QUE REALIZARÁ LA APP AL MOSTRAR EL ALERT DIALOG
                                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                switch (which){
                                                    case DialogInterface.BUTTON_POSITIVE:
                                                        // SE ACTIVA TODO PARA ELIMINAR INFORMACIÓN DE LA MOTOCICLETA
                                                        // AQUÍ SE REALIZA EL PROCESO PARA REALIZAR LA INSERCIÓN EN LA BASE DE DATOS
                                                        // MOSTRAMOS EL PROGRESS DIALOG ---- AQUÍ SE COMIENZA EL ARMADO Y LA EJECUCIÓN DEL WEB SERVICE
                                                        progressDialog.setMessage("CARGANDO...");
                                                        progressDialog.show();
                                                        // CREACIÓN DE LA CADENA A EJECUTAR EN EL WEB SERVICE MEDIANTE VOLLEY
                                                        // Objeto de volley
                                                        StringRequest stringRequest = new StringRequest(Request.Method.POST, HttpUriDel,
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
                                                                            String mensaje = obj.getString("mensaje");
                                                                            if(status.contentEquals("true")){
                                                                                Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();
                                                                                // ACTUALIZAMOS EL ACTIVITY
                                                                                Intent infomoto = new Intent(getApplicationContext(), InfomotoActivity.class);
                                                                                startActivity(infomoto);
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
                                                                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                                                            }
                                                        }) {
                                                            // MAPEO DE LOS VALORES QUE MANDAMOS AL WEB SERVICE
                                                            protected Map<String, String> getParams(){
                                                                // RETORNAR LOS VALORES
                                                                String id_moto = String.valueOf(ID_Motocicleta[position]);
                                                                Map<String, String> parametros = new HashMap<>();
                                                                parametros.put("ID_Moto", id_moto);
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
                                                }
                                            }
                                        };

                                        // SE CREA EL MENSAJE DE CONFIRMACIÓN
                                        AlertDialog.Builder mensaje = new AlertDialog.Builder(InfomotoActivity.this);
                                        mensaje.setTitle("¿ELIMINAR MOTOCICLETA?").setPositiveButton("ELIMINAR", dialogClickListener)
                                                .setNegativeButton("CANCELAR", dialogClickListener).setIcon(R.drawable.saram)
                                                .setMessage("La motocicleta "+modelo+" será eliminada").show();
                                    }

                                    // CUANDO SE LE DE EN EL ÍCONO DE SARAM DE LA MOTOCICLETA SERÁ LA ACCIÓN PARA SELECCIONAR
                                    // EL MONITOREO DE LA MISMA
                                    @Override
                                    public void onSaramClick(int position) {
                                        // ACTIVAMOS EL EDITOR DEL SHARED
                                        final SharedPreferences sp1 = getSharedPreferences("MisDatos", Context.MODE_PRIVATE);
                                        // DAMOS EL VALOR DEL ID QUE MONITOREARÁ
                                        final String id_moto = String.valueOf(ID_Motocicleta[position]);
                                        final String modelo = Modelos[position];

                                        // DECLARAMOS LAS OPCIONES U OPCIÓN QUE REALIZARÁ LA APP AL MOSTRAR EL ALERT DIALOG
                                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                switch (which){
                                                    case DialogInterface.BUTTON_POSITIVE:
                                                        SharedPreferences.Editor editor = sp1.edit();
                                                        editor.putString("moto", id_moto);
                                                        editor.putString("modelo", modelo);
                                                        editor.commit();

                                                        Toast.makeText(getApplicationContext(), "CAMBIANDO A MOTOCICLETA "+modelo, Toast.LENGTH_SHORT).show();
                                                        Toast.makeText(getApplicationContext(), "MONITOREANDO... ", Toast.LENGTH_LONG).show();

                                                        //CAMBIAMOS AL INTENT DEL INICIO
                                                        Intent inicio = new Intent(getApplicationContext(), InicioActivity.class);
                                                        startActivity(inicio);
                                                }
                                            }
                                        };

                                        // SE CREA EL MENSAJE DE CONFIRMACIÓN
                                        AlertDialog.Builder mensaje = new AlertDialog.Builder(InfomotoActivity.this);
                                        mensaje.setTitle("¿CAMBIAR DE MOTOCICLETA?").setPositiveButton("CAMBIAR", dialogClickListener)
                                                .setNegativeButton("CANCELAR", dialogClickListener).setIcon(R.drawable.saram)
                                                .setMessage("Desde ahora la motocicleta "+modelo+" será monitoreada por la APP SARAM").show();
                                    }
                                });
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
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
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

    // PARA ACTIVAR LA FUNCIONALIDAD DE LA FLECHA DE RETORNO
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}