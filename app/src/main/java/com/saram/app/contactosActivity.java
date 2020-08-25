package com.saram.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class contactosActivity extends AppCompatActivity {

    // SE DECLARAN LOS OBJETOS
    ListView listaContactos;
    // OBJETOS PARA LA CONEXIÓN AL SERVIDOR UTILIZANDO VOLLEY
    RequestQueue requestQueue;
    ProgressDialog progressDialog;

    // CREAMOS UNA CADENA LA CUAL CONTENDRÁ LA CADENA DE NUESTRO WEB SERVICE
    String HttpUriSave = "http://192.168.43.200:8080/SARAM-API/public/api/setContactos";
    String vtoken, nombre, numero;

    // ESTE MÉTODO EVITA QUE SE REGRESE CON LA FLECHA DE RETORNO QUE TODOS TENEMOS
    @Override
    public void onBackPressed() {
        // CUANDO REGRESE CON LA FLECHA DE REGRESO LO QUE HARÁ SERÁ REGRESAR AL INICIO
        Intent inicio = new Intent(this, contactos_saramActivity.class);
        startActivity(inicio);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactos);
        // PARA ACTIVAR LA FLECHA DE RETORNO
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // SE ACTIVAN LOS OBJETOS DE REQUEST QUEUE Y PROGRESS DIALOG
        requestQueue = Volley.newRequestQueue(this);
        progressDialog = new ProgressDialog(this);

        // OBTENEMOS EL TOKEN DEL SHARED
        SharedPreferences sp1 = getSharedPreferences("MisDatos", Context.MODE_PRIVATE);
        vtoken = sp1.getString("token", "");

        // SE ENLAZAN LOS OBJETOS CON LA VISTA
        listaContactos = (ListView) findViewById(R.id.listaContactos);


        // SE EJECUTA EL MÉTODO QUE OBTIENE EL CONTENIDO DE LOS CONTACTOS
        final Cursor contactosN = getContentResolver().query(
                // EL CONTEN_URI ES UNA DIRECCIÓN QUE PUEDE INGRESAR A LA RUTA DE LOS CONTACTOS
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.Data.DISPLAY_NAME + " ASC"
        );
        // SE INICIA EL CURSOR
        startManagingCursor(contactosN);


        while(contactosN.moveToNext()){
            // SE OBTIENEN LOS DATOS DEL NOMBRE Y NÚMERO DE LOS CONTACTOS ASÍ COMO SU ID
            final String[] desde = {
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone._ID
            };

            // SE CREA UN ARREGLO ENCONTRANDO LOS TEXTVIEW DE CADA ITERACIÓN DEL ARCHIVO XML (ITEM_CONTACTOS.XML)
            int[] a = {
                    R.id.tvNombreContacto,
                    R.id.tvNumeroContacto,
                    R.id.tvIdContacto
            };

            // SE CREA EL ADAPTER PARA INSERTAR LOS NOMBRE Y NÚMEROS DE CADA CONTACTO
            final SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this,
                    R.layout.item_contactos, contactosN, desde, a);
            listaContactos.setAdapter(simpleCursorAdapter);

            listaContactos.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> adapterView, final View view, int position, long id) {

                    // SE BUSCA LA REFERENCIA DEL TEXTVIEW EN LA VISTA.
                    final TextView tvNombre = (TextView) view.findViewById(R.id.tvNombreContacto);
                    final TextView tvNumero = (TextView) view.findViewById(R.id.tvNumeroContacto);
                    // SE OBTIENE EL TEXTO DENTRO DEL CAMPO.
                    nombre  = tvNombre.getText().toString();
                    numero  = tvNumero.getText().toString();

                    // DECLARAMOS LAS OPCIONES U OPCIÓN QUE REALIZARÁ LA APP AL MOSTRAR EL ALERT DIALOG
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    //guardarContacto();
                                    // SE GUARDA EL CONTACTO EN LA BASE DE DATOS DE SARAM
                                    // MOSTRAMOS EL PROGRESS DIALOG ---- AQUÍ SE COMIENZA EL ARMADO Y LA EJECUCIÓN DEL WEB SERVICE
                                    progressDialog.setMessage("CARGANDO...");
                                    progressDialog.show();
                                    // CREACIÓN DE LA CADENA A EJECUTAR EN EL WEB SERVICE MEDIANTE VOLLEY
                                    // Objeto de volley
                                    StringRequest stringRequest = new StringRequest(Request.Method.POST, HttpUriSave,
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
                                                        // JSONObject obj = new JSONObject(serverresponse);
                                                        // INTERPRETAR EL VALOR DEL JSON OBTENIDO DEL WEB SERVICE
                                                        // String mensaje = obj.getString("mensaje");
                                                        // INTERPRETAR LOS VALORES

                                                        // SE MANEJARÁ COMO ARRAY DE ACUERDO A LO PROGRAMADO POR EL WEB SERVICE
                                                        JSONArray valores = new JSONArray(serverresponse);
                                                        String [] mensaje = new String[valores.length()];
                                                        mensaje [0] = valores.getJSONObject(0).getString("mensaje");
                                                        Toast.makeText(getApplicationContext(),mensaje[0], Toast.LENGTH_LONG).show();

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
                                        protected Map<String, String> getParams() {
                                            // RETORNAR LOS VALORES
                                            Map<String, String> parametros = new HashMap<>();
                                            parametros.put("Nombre", nombre);
                                            parametros.put("Numero_Tel", numero);
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
                                    break;
                                default:
                                    break;
                            }
                        }
                    };

                    // SE CREA EL MENSAJE DE CONFIRMACIÓN
                    AlertDialog.Builder mensaje = new AlertDialog.Builder(contactosActivity.this);
                    mensaje.setTitle("¿SE AGREGARÁ ESTE CONTACTO A SARAM?").setPositiveButton("ACEPTAR", dialogClickListener)
                            .setNegativeButton("CANCELAR", dialogClickListener).setIcon(R.drawable.saram)
                            .setMessage("Desde ahora "+nombre+" recibirá los mensajes de alerta por parte de SARAM").show();
                }
            });
        }
    }


    // PARA ACTIVAR LA FUNCIONALIDAD DE LA FLECHA DE RETORNO
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}