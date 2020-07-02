package com.saram.app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class infomotoActivity extends AppCompatActivity {

    ListView lvMotos;
    // ESTOS SON DE LA ETIQUETA QUE CREO NO SE OCUPAN
    FloatingActionButton fabMas;
    ImageView imgEdita;
    TextView tvModelo, tvMarca, tvCilindraje, tvPlaca, tvSaram;

    // OBJETOS PARA LA CONEXIÓN AL SERVIDOR UTILIZANDO VOLLEY
    RequestQueue requestQueue;
    ProgressDialog progressDialog;

    // CREAMOS UNA CADENA LA CUAL CONTENDRÁ LA CADENA DE NUESTRO WEB SERVICE
    String HttpUri = "http://192.168.43.200:8080/SARAM-API/public/api/getmotos";
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
        setContentView(R.layout.activity_infomoto);
        // PARA ACTIVAR LA FLECHA DE RETORNO
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // SE ENLAZAN LOS OBJETOS CON LA VISTA
        fabMas = (FloatingActionButton) findViewById(R.id.fabMas);
        imgEdita = (ImageView) findViewById(R.id.imgEditar);
        tvModelo = (TextView) findViewById(R.id.tvModelo);
        tvMarca = (TextView) findViewById(R.id.tvMarca);
        tvCilindraje = (TextView) findViewById(R.id.tvCilindraje);
        tvPlaca = (TextView) findViewById(R.id.tvPlaca);
        tvSaram = (TextView) findViewById(R.id.tvSaram);

        //lvMotos = (ListView) findViewById(R.id.lvMotos);

        // SE ACTIVAN LOS OBJETOS DE REQUEST QUEUE Y PROGRESS DIALOG
        requestQueue = Volley.newRequestQueue(infomotoActivity.this);
        progressDialog = new ProgressDialog(infomotoActivity.this);

        // OBTENEMOS EL TOKEN DEL SHARED
        SharedPreferences sp1 = getSharedPreferences("MisDatos", Context.MODE_PRIVATE);
        vtoken = sp1.getString("token", "");


        imgEdita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //en esta se debe mandar a llamar para la sig pantalla
                Intent agregar2 =  new Intent(infomotoActivity.this, editmotoActivity.class);
                startActivity(agregar2);
            }
        });


        fabMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent agregar =  new Intent(infomotoActivity.this, addmotoActivity.class);
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
                                Intent inicio2 = new Intent(getApplicationContext(), inicioActivity.class);
                                startActivity(inicio2);
                            } else {
                                // OBTENER SE OBTIENEN LOS DATOS DEL ARRAY
                                JSONArray infomotos = obj.getJSONArray("motos");

                                if(infomotos.getJSONObject(0).getString("Modelo")==null){
                                    Toast.makeText(getApplicationContext(), "Ingresa una motocicleta :D", Toast.LENGTH_LONG).show();
                                }else{
                                    for(int i=0; i<infomotos.length();i++) {
                                        tvModelo.append(infomotos.getJSONObject(i).getString("Modelo"));
                                        tvMarca.append(infomotos.getJSONObject(i).getString("Marca"));
                                        tvCilindraje.append(String.valueOf(infomotos.getJSONObject(i).get("Cilindraje")));
                                        tvPlaca.append(infomotos.getJSONObject(i).getString("Placa"));
                                        tvSaram.append(infomotos.getJSONObject(i).getString("ID_saram"));
                                    }
                                    // OCULTAMOS EL BOTÓN PARA QUE NO PUEDAN AGREGAR MÁS MOTOCICLETAS
                                    fabMas.hide();
                                }
                                /*
                                TODOS ESTOS CÓDIGOS COMENTADOS FUE MI ÚLTIMO INTENTO POR CONSEGUIR REPETIR LA PLANTILLA
                                POR CADA MOTOCICLETA, SIN EMBARGO EL DETALLE SE ENCUENTRA EN QUE DEBO DE PASAR EL
                                JSONARRAY (PARTE DE ARRIBA) A UN OBJETO DE TIPO CURSOR PARA PODER COMPLETAR
                                EL SET ADAPTER DEL FINAL
                                // SE OBTIENEN LOS DATOS DEL NOMBRE Y NÚMERO DE LOS CONTACTOS ASÍ COMO SU ID
                                String[] desde = {
                                        "Modelo",
                                        "Marca",
                                        "Cilindraje",
                                        "Placa",
                                        "ID_saram"
                                };

                                // SE CREA UN ARREGLO ENCONTRANDO LOS TEXTVIEW DE CADA ITERACIÓN DEL ARCHIVO XML (ITEM_CONTACTOS.XML)
                                int[] a = {
                                        R.id.tvModelo,
                                        R.id.tvMarca,
                                        R.id.tvCilindraje,
                                        R.id.tvPlaca,
                                        R.id.tvSaram
                                };

                                // SE CREA EL ADAPTER PARA INSERTAR LOS NOMBRE Y NÚMEROS DE CADA CONTACTO
                                final SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(
                                        getApplicationContext(),
                                        R.layout.tarjeta_info_moto,
                                        (Cursor) list,
                                        desde,
                                        a);
                                lvMotos.setAdapter(simpleCursorAdapter);
                                 */
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