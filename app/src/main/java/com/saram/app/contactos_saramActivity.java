package com.saram.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Application;
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
import com.saram.app.ui.adapter.MotosAdapter;
import com.saram.app.ui.adapter.contactos_saramAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class contactos_saramActivity extends AppCompatActivity {

    // ESTOS SON DE LA ETIQUETA QUE CREO NO SE OCUPAN
    FloatingActionButton fabMas;
    RecyclerView rvContenedorContactos;
    contactos_saramAdapter Contactos;

    // OBJETOS PARA LA CONEXIÓN AL SERVIDOR UTILIZANDO VOLLEY
    RequestQueue requestQueue;
    ProgressDialog progressDialog;

    // CREAMOS UNA CADENA LA CUAL CONTENDRÁ LA CADENA DE NUESTRO WEB SERVICE
    String HttpUri = "http://192.168.43.200:8080/SARAM-API/public/api/getContactos";
    String HttpUriDel = "http://192.168.43.200:8080/SARAM-API/public/api/delContactos";
    String vtoken;

    // ESTE MÉTODO EVITA QUE SE REGRESE CON LA FLECHA DE RETORNO QUE TODOS TENEMOS
    @Override
    public void onBackPressed() {
        // CUANDO REGRESE CON LA FLECHA DE REGRESO LO QUE HARÁ SERÁ REGRESAR AL INICIO
        Intent inicio = new Intent(this, inicioActivity.class);
        startActivity(inicio);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactos_saram);

        // PARA ACTIVAR LA FLECHA DE RETORNO
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // SE ENLAZAN LOS OBJETOS CON LA VISTA
        fabMas = (FloatingActionButton) findViewById(R.id.fabMas);
        rvContenedorContactos = (RecyclerView) findViewById(R.id.rvContenedorContactos);
        rvContenedorContactos.setHasFixedSize(true);

        //Asociar el RV a un LinearLayoutManager
        LinearLayoutManager LLManager= new LinearLayoutManager(this);
        rvContenedorContactos.setLayoutManager(LLManager);

        // SE ACTIVAN LOS OBJETOS DE REQUEST QUEUE Y PROGRESS DIALOG
        requestQueue = Volley.newRequestQueue(contactos_saramActivity.this);
        progressDialog = new ProgressDialog(contactos_saramActivity.this);

        // OBTENEMOS EL TOKEN DEL SHARED
        SharedPreferences sp1 = getSharedPreferences("MisDatos", Context.MODE_PRIVATE);
        vtoken = sp1.getString("token", "");

        fabMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent agregarContacto =  new Intent(contactos_saramActivity.this, contactosActivity.class);
                startActivity(agregarContacto);
            }
        });

        // SE ACTIVA TODO PARA TRAER INFORMACIÓN DE LOS CONTACTOS
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
                                Intent inicio2 = new Intent(getApplicationContext(), inicioActivity.class);
                                startActivity(inicio2);
                            } else {
                                // OBTENER SE OBTIENEN LOS DATOS DEL ARRAY
                                JSONArray ContactosInfo = obj.getJSONArray("contactos");
                                final String[] Nombre = new String[ContactosInfo.length()];
                                final String[] Numero = new String[ContactosInfo.length()];
                                final int[] ID_contactos = new int[ContactosInfo.length()];
                                for (int i=0; i<ContactosInfo.length(); i++){
                                    Nombre[i]=ContactosInfo.getJSONObject(i).getString("Nombre");
                                    Numero[i]=ContactosInfo.getJSONObject(i).getString("Numero_Tel");
                                    ID_contactos[i]=ContactosInfo.getJSONObject(i).getInt("ID_Usuario");
                                }
                                Contactos = new contactos_saramAdapter(Nombre, Numero, ID_contactos, getApplication());
                                rvContenedorContactos.setAdapter(Contactos);

                                Contactos.setOnItemClickListener(new contactos_saramAdapter.OnItemClickListener() {
                                    @Override
                                    public void onEditClick(int position) {
                                        Intent editcontacto = new Intent(contactos_saramActivity.this, edit_contactosActivity.class);
                                        editcontacto.putExtra("numero", Numero[position]);
                                        startActivity(editcontacto);
                                    }

                                    @Override
                                    public void onDeleteClick(final int position) {
                                        // DAMOS EL VALOR DEL MODELO DE LA MOTOCICLETA A ELIMINAR
                                        final String nombre = Nombre[position];

                                        // DECLARAMOS LAS OPCIONES U OPCIÓN QUE REALIZARÁ LA APP AL MOSTRAR EL ALERT DIALOG
                                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                switch (which){
                                                    case DialogInterface.BUTTON_POSITIVE:
                                                        // SE ACTIVA TODO PARA ELIMINAR INFORMACIÓN DEL CONTACTO SELECCIONADO
                                                        // AQUÍ SE REALIZA EL PROCESO PARA REALIZAR LA INSERCIÓN EN LA BASE DE DATOS
                                                        // MOSTRAMOS EL PROGRESS DIALOG ---- AQUÍ SE COMIENZA EL ARMADO Y LA EJECUCIÓN DEL WEB SERVICE
                                                        progressDialog.setMessage("CARGANDO...");
                                                        progressDialog.show();
                                                        // CREACIÓN DE LA CADENA A EJECUTAR EN EL WEB SERVICE MEDIANTE VOLLEY
                                                        // Objeto de volley
                                                        StringRequest stringRequest = new StringRequest(Request.Method.POST, HttpUriDel,
                                                                new Response.Listener<String>(){
                                                                    @Override
                                                                    public void onResponse(String serverresponse) {
                                                                        // UNA VEZ QUE SE MANDAN TODOS LOS VALORES AL WEB SERVICE
                                                                        // QUITAMOS EL PROGRESS DIALOG PARA QUE UNA VEZ QUE SE MANDEN LOS DATOS
                                                                        // YA SE PUEDA TRABAJAR
                                                                        progressDialog.dismiss();
                                                                        // MANEJO DE ERRORES CON RESPECTO A LA RESPUESTA
                                                                        try {
                                                                            // SE MANEJARÁ COMO ARRAY DE ACUERDO A LO PROGRAMADO POR EL WEB SERVICE
                                                                            JSONArray valores = new JSONArray(serverresponse);
                                                                            String [] mensaje = new String[valores.length()];
                                                                            mensaje [0] = valores.getJSONObject(0).getString("mensaje");
                                                                            Toast.makeText(getApplicationContext(),mensaje[0], Toast.LENGTH_LONG).show();

                                                                            // ACTUALIZAMOS LA LISTA DE CONTACTOS
                                                                            Intent actualiza = new Intent(getApplicationContext(), contactos_saramActivity.class);
                                                                            startActivity(actualiza);
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
                                                                Map<String, String> parametros = new HashMap<>();
                                                                parametros.put("Numero_Tel", Numero[position]);
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
                                        AlertDialog.Builder mensaje = new AlertDialog.Builder( contactos_saramActivity.this);
                                        mensaje.setTitle("¿ELIMINAR CONTACTO?").setPositiveButton("ELIMINAR", dialogClickListener)
                                                .setNegativeButton("CANCELAR", dialogClickListener).setIcon(R.drawable.saram)
                                                .setMessage("El contacto de nombre "+nombre+" será eliminado").show();
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "NO ENTRA AL TRY", Toast.LENGTH_LONG).show();
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