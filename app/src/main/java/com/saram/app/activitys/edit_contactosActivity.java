package com.saram.app.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.saram.app.R;
import com.saram.app.models.rutas;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class edit_contactosActivity extends AppCompatActivity {
    // TODO ESTO ES LO QUE SE ESTÁ TRABAJANDO

    // SE DECLARAN LOS OBJETOS
    TextInputLayout tilNombre, tilApellidos, tilNumero, tilCorreo;
    EditText etNombre, etApellidos, etNumero, etCorreo;
    Button btnActualiza;
    String num_tel;

    // OBJETOS PARA LA CONEXIÓN AL SERVIDOR UTILIZANDO VOLLEY
    RequestQueue requestQueue;
    ProgressDialog progressDialog;

    // CREAMOS UNA CADENA LA CUAL CONTENDRÁ LA CADENA DE NUESTRO WEB SERVICE
    String HttpUriUpdate = rutas.setContactos; // ESTO ES LO QUE SE DEBE DE ACTUALIZAR
    String HttpUriGetContactos = rutas.getContactos;
    String vtoken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contactos);

        // PARA ACTIVAR LA FLECHA DE RETORNO
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // SE ENLAZAN LOS OBJETOS CON LOS CONTROLADORES
        tilNombre = (TextInputLayout) findViewById(R.id.tilNombre);
        tilApellidos = (TextInputLayout) findViewById(R.id.tilApellidos);
        tilNumero = (TextInputLayout) findViewById(R.id.tilNumero);
        tilCorreo = (TextInputLayout) findViewById(R.id.tilCorreo);
        etNombre = (EditText) findViewById(R.id.etNombre);
        etApellidos = (EditText) findViewById(R.id.etApellidos);
        etNumero = (EditText) findViewById(R.id.etNumero);
        etCorreo = (EditText) findViewById(R.id.etCorreo);
        btnActualiza = (Button) findViewById(R.id.btnActualiza);

        // SE ACTIVAN LOS OBJETOS DE REQUEST QUEUE Y PROGRESS DIALOG
        requestQueue = Volley.newRequestQueue(edit_contactosActivity.this);
        progressDialog = new ProgressDialog(edit_contactosActivity.this);

        // OBTENEMOS EL TOKEN DEL SHARED
        SharedPreferences sp1 = getSharedPreferences("MisDatos", Context.MODE_PRIVATE);
        vtoken = sp1.getString("token", "");

        // SE ACTIVA TODO PARA TRAER INFORMACIÓN DE CONTACTO
        // MOSTRAMOS EL PROGRESS DIALOG ---- AQUÍ SE COMIENZA EL ARMADO Y LA EJECUCIÓN DEL WEB SERVICE
        progressDialog.setMessage("CARGANDO...");
        progressDialog.show();
        // CREACIÓN DE LA CADENA A EJECUTAR EN EL WEB SERVICE MEDIANTE VOLLEY
        // Objeto de volley
        StringRequest stringRequest = new StringRequest(Request.Method.POST, HttpUriGetContactos,
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
                                final String[] Apellidos = new String[ContactosInfo.length()];
                                final String[] Numero = new String[ContactosInfo.length()];
                                final String[] Correo = new String[ContactosInfo.length()];

                                // TRAEMOS EL VALOR DEL CONTACTO DEL NUMERO DEL CONTACTO SELECCIONADO
                                Intent viejo = getIntent();
                                num_tel = viejo.getStringExtra("numero");

                                for(int i=0; i<ContactosInfo.length();i++) {
                                    if(ContactosInfo.getJSONObject(i).getString("Numero_Tel")==num_tel) {
                                        etNombre.append(ContactosInfo.getJSONObject(i).getString("Nombre"));

                                        // SE VERIFICA SI LOS VALORES NO ESTAN VACÍOS PARA COLOCARLO EN EL CAMPO
                                        String apellidos = ContactosInfo.getJSONObject(i).getString("Apellidos");
                                        if(apellidos.equals("null")){
                                            etApellidos.append("");
                                        }
                                        else{
                                            etApellidos.append(apellidos);
                                        }

                                        etNumero.append(String.valueOf(ContactosInfo.getJSONObject(i).get("Numero_Tel")));

                                        // SE VERIFICA SI LOS VALORES NO ESTAN VACÍOS PARA COLOCARLO EN EL CAMPO
                                        String correo = ContactosInfo.getJSONObject(i).getString("Apellidos");
                                        if(apellidos.equals("null")){
                                            etApellidos.append("");
                                        }
                                        else{
                                            etApellidos.append(correo);
                                        }
                                    }
                                }
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

        // SE CREAN LAS VALIDACIONES EN TIEMPO REAL
        // VALIDACIÓN DENTRO DEL CAMPO NOMBRE
        etNombre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // PARA LIMPIAR LOS ERRORES DENTRO DEL PROCESO DE LLENADO DEL CAMPO
                tilNombre.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // VALIDACIÓN DEL CAMPO APELLIDOS
        etApellidos.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // PARA LIMPIAR LOS ERRORES DENTRO DEL PROCESO DE LLENADO DEL CAMPO
                tilApellidos.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // VALIDACIÓN PARA EL CAMPO TELEFONO
        etNumero.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // PARA LIMPIAR LOS ERRORES DENTRO DEL PROCESO DE LLENADO DEL CAMPO
                tilNumero.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // VALIDACIÓN DEL CAMPO CORREO
        etCorreo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // PARA LIMPIAR LOS ERRORES DENTRO DEL PROCESO DE LLENADO DEL CAMPO
                tilCorreo.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // SE CREA EL OYENTE DEL BOTÓN
        btnActualiza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                apdateContacto();
            }
        });
    }

    // MÉTODO PARA ACTUALIZAR EL CONTACTO
    private void apdateContacto(){
        final String nombre = tilNombre.getEditText().getText().toString();
        final String apellidos = tilApellidos.getEditText().getText().toString();
        final String numero = tilNumero.getEditText().getText().toString();
        final String correo = tilCorreo.getEditText().getText().toString();

        // SE VALIDAN QUE LOS CAMPOS NO ESTEN VACÍOS
        if (nombre.isEmpty() || numero.isEmpty()) {
            Toast.makeText(this, "ES NECESARIO INGRESAR EL NOMBRE Y EL NÚMERO", Toast.LENGTH_LONG).show();
        } else {
            // SE OBTIENEN LOS RESULTADOS DE LA VALIDACIÓN DE AMBOS CAMPOS
            Boolean vnombre = validaNombre(nombre);
            Boolean vapellidos = validaApellidos(apellidos);
            Boolean vnumero = validaTelefono(numero);
            Boolean vcorreo = validaCorreo(correo);

            // SE VALIDA QUE LOS CAMPOS HAYAN PASADO SUS RESPECTIVAS VALIDACIONES
            if (vnombre && vapellidos && vnumero && vcorreo) {
                // AQUÍ SE REALIZA EL PROCESO PARA REALIZAR LA INSERCIÓN EN LA BASE DE DATOS
                // MOSTRAMOS EL PROGRESS DIALOG ---- AQUÍ SE COMIENZA EL ARMADO Y LA EJECUCIÓN DEL WEB SERVICE
                progressDialog.setMessage("CARGANDO...");
                progressDialog.show();
                // CREACIÓN DE LA CADENA A EJECUTAR EN EL WEB SERVICE MEDIANTE VOLLEY
                // Objeto de volley
                StringRequest stringRequest2 = new StringRequest(Request.Method.POST, HttpUriUpdate,
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
                                    // OBTENER EL MENSAJE
                                    String mensaje = obj.getString("mensaje");
                                    // INTERPRETAR LOS VALORES
                                    if (status.contentEquals("false")) {
                                        Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();
                                        // REDIRIGE A LA INFO DE LOS CONTACTOS YA ACTUALIZADA
                                        // REDIRIGE AL ACTIVITY REINICIANDO EL SERVICIO
                                        Intent infoaupdate = new Intent(getApplicationContext(), contactos_saramActivity.class);
                                        startActivity(infoaupdate);
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
                    protected Map<String, String> getParams() {
                        // RETORNAR LOS VALORES
                        Map<String, String> parametros = new HashMap<>();
                        parametros.put("Nombre", nombre);
                        parametros.put("Apellidos", apellidos);
                        parametros.put("Numero_Tel", numero);
                        parametros.put("Correo", correo);
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
                requestQueue.add(stringRequest2);
            }
        }
    }

    // MÉTODOS QUE VALIDAN LOS CAMPOS
    private boolean validaNombre(String nombre){
        Pattern patron = Pattern.compile("^[a-zA-Z ]+$");
        // Y NO PASE DEL LÍMTE PERMITIDO
        if (!patron.matcher(nombre).matches() || nombre.length() > 30) {
            tilNombre.setError("Nombre no válido, Intentalo de nuevo");
            return false;
        }else{
            return true;
        }
    }

    private boolean validaApellidos(String apellidos){
        if(apellidos.equals("")){
            return true;
        }
        else {
            Pattern patron = Pattern.compile("^[a-zA-Z ]+$");
            // Y NO PASE DEL LÍMTE PERMITIDO
            if (!patron.matcher(apellidos).matches() || apellidos.length() > 30) {
                tilApellidos.setError("Apellidos no válidos, Intentalo de nuevo");
                return false;
            }else{
                return true;
            }
        }
    }

    private boolean validaTelefono(String telefono){
        // VALIDA QUE NO PASE DEL LÍMTE PERMITIDO
        if (telefono.length() > 13) {
            tilNumero.setError("Teléfono no válido, Intentalo de nuevo");
            return false;
        }else{
            tilNumero.setError(null);
        }
        return true;
    }

    private boolean validaCorreo(String correo){
        if(correo.equals("")){
            return true;
        }
        else{
            // O SE ASEGURA QUE TENGA DATOS ESPECÍFICOS DE UN EMAIL
            Pattern patronEmail = Patterns.EMAIL_ADDRESS;

            // Y NO PASE DEL LÍMTE PERMITIDO
            if(!patronEmail.matcher(correo).matches() || correo.length() > 50 ) {
                tilCorreo.setError("Correo no válido, Intentalo de nuevo");
                return false;
            }else{
                return true;
            }
        }
    }

    // PARA ACTIVAR LA FUNCIONALIDAD DE LA FLECHA DE RETORNO
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}