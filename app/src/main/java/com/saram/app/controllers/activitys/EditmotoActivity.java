package com.saram.app.controllers.activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.saram.app.models.Images;
import com.saram.app.models.Userbd;
import com.saram.app.routes.Rutas;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditmotoActivity extends AppCompatActivity {
    // IMAGENES
    Images imagenes = new Images();
    Userbd userbd = new Userbd(this);

    // SE DECLARAN LOS OBJETOS
    TextInputLayout tilModelo, tilMarca, tilCilindraje, tilPlaca, tilSARAM;
    EditText etModelo, etMarca, etCilindraje, etPlaca, etSARAM;
    Button btnActualiza;
    ImageView ivLogo;
    int id_moto;

    private final static int CODE_GALLERY = 123;

    // OBJETOS PARA LA CONEXIÓN AL SERVIDOR UTILIZANDO VOLLEY
    RequestQueue requestQueue;
    ProgressDialog progressDialog;

    // CREAMOS UNA CADENA LA CUAL CONTENDRÁ LA CADENA DE NUESTRO WEB SERVICE
    String HttpUriUpdate = Rutas.updateMoto;
    String HttpUriGetMoto = Rutas.getMotos;
    String vtoken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editmoto);
        // PARA ACTIVAR LA FLECHA DE RETORNO
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // SE ENLAZAN LOS OBJETOS CON LOS CONTROLADORES
        tilModelo = (TextInputLayout) findViewById(R.id.tilModelo);
        tilMarca = (TextInputLayout) findViewById(R.id.tilMarca);
        tilCilindraje = (TextInputLayout) findViewById(R.id.tilCilindraje);
        tilPlaca = (TextInputLayout) findViewById(R.id.tilPlaca);
        tilSARAM = (TextInputLayout) findViewById(R.id.tilSARAM);
        etModelo = (EditText) findViewById(R.id.etModelo);
        etMarca = (EditText) findViewById(R.id.etMarca);
        etCilindraje = (EditText) findViewById(R.id.etCilindraje);
        etPlaca = (EditText) findViewById(R.id.etPlaca);
        etSARAM = (EditText) findViewById(R.id.etSARAM);
        btnActualiza = (Button) findViewById(R.id.btnActualiza);
        ivLogo = (ImageView) findViewById(R.id.ivLogo);

        // SE ACTIVAN LOS OBJETOS DE REQUEST QUEUE Y PROGRESS DIALOG
        requestQueue = Volley.newRequestQueue(EditmotoActivity.this);
        progressDialog = new ProgressDialog(EditmotoActivity.this);

        // OBTENEMOS EL TOKEN DEL SHARED
        SharedPreferences sp1 = getSharedPreferences("MisDatos", Context.MODE_PRIVATE);
        vtoken = sp1.getString("token", "");

        // SE ACTIVA TODO PARA TRAER INFORMACIÓN DE LA MOTOCICLETA
        // MOSTRAMOS EL PROGRESS DIALOG ---- AQUÍ SE COMIENZA EL ARMADO Y LA EJECUCIÓN DEL WEB SERVICE
        progressDialog.setMessage("CARGANDO...");
        progressDialog.show();
        // CREACIÓN DE LA CADENA A EJECUTAR EN EL WEB SERVICE MEDIANTE VOLLEY
        // Objeto de volley
        StringRequest stringRequest = new StringRequest(Request.Method.POST, HttpUriGetMoto,
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
                                JSONArray infomotos = obj.getJSONArray("motos");
                                Intent viejo = getIntent();
                                id_moto = viejo.getIntExtra("ID", -1);
                                for(int i=0; i<infomotos.length();i++) {
                                    if(infomotos.getJSONObject(i).getInt("ID_Motocicleta")==id_moto) {
                                        etModelo.append(infomotos.getJSONObject(i).getString("Modelo"));
                                        etMarca.append(infomotos.getJSONObject(i).getString("Marca"));
                                        etCilindraje.append(String.valueOf(infomotos.getJSONObject(i).get("Cilindraje")));
                                        etPlaca.append(infomotos.getJSONObject(i).getString("Placa"));
                                        etSARAM.append(infomotos.getJSONObject(i).getString("ID_saram"));
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
        // VALIDACIÓN PARA EL CAMPO MODELO
        etModelo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // PARA LIMPIAR LOS ERRORES DENTRO DEL PROCESO DE LLENADO DEL CAMPO
                tilModelo.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // VALIDACIÓN PARA EL CAMPO MARCA
        etMarca.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // PARA LIMPIAR LOS ERRORES DENTRO DEL PROCESO DE LLENADO DEL CAMPO
                tilMarca.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // VALIDACIÓN PARA EL CAMPO CILINDRAJE
        etCilindraje.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // PARA LIMPIAR LOS ERRORES DENTRO DEL PROCESO DE LLENADO DEL CAMPO
                tilCilindraje.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // VALIDACIÓN PARA EL CAMPO PLACA
        etPlaca.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // PARA LIMPIAR LOS ERRORES DENTRO DEL PROCESO DE LLENADO DEL CAMPO
                tilPlaca.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // VALIDACIÓN PARA EL CAMPO SARAM
        etSARAM.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // PARA LIMPIAR LOS ERRORES DENTRO DEL PROCESO DE LLENADO DEL CAMPO
                tilSARAM.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // SE CREA EL OYENTE DEL BOTÓN
        btnActualiza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatemoto();
            }
        });

        ivLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(
                        EditmotoActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        CODE_GALLERY
                );
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CODE_GALLERY){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // LANZAMOS EL ITENT IMPLÍCITO DE LA GALERÍA
                Intent galeria = new Intent(Intent.ACTION_PICK);
                galeria.setType("image/*");
                startActivityForResult(galeria, CODE_GALLERY);
            }
            else{
                Toast.makeText(getApplicationContext(), "DEBES CONCEDER EL PERMISO PARA ACCEDER A LA GALERÍA", Toast.LENGTH_LONG).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == CODE_GALLERY && resultCode == RESULT_OK && data != null){
            Uri uri = data.getData();
            Picasso.get()
                    .load(uri)
                    .noPlaceholder()
                    .error(R.drawable.saram)
                    .into(ivLogo);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // MÉTODO PARA ACTUALIZAR LA MOTOCICLETA
    private void updatemoto(){
        final String modelo = tilModelo.getEditText().getText().toString();
        final String marca = tilMarca.getEditText().getText().toString();
        final String cilindraje = tilCilindraje.getEditText().getText().toString();
        final String placa = tilPlaca.getEditText().getText().toString();
        final String saram = tilSARAM.getEditText().getText().toString();

        // SE VALIDAN QUE LOS CAMPOS NO ESTEN VACÍOS
        if (modelo.isEmpty() || marca.isEmpty() || cilindraje.isEmpty() || placa.isEmpty() || saram.isEmpty()) {
            Toast.makeText(this, "LLENA TODOS LOS CAMPOS", Toast.LENGTH_LONG).show();
        } else {
            // PASAMOS LOS DATOS QUE NECESITAN SER DE TIPO ENTERO PARA ALMACENARSE EN LA BD
            final int intcilindraje = Integer.parseInt(cilindraje);
            final int intsaram = Integer.parseInt(saram);

            // SE OBTIENEN LOS RESULTADOS DE LA VALIDACIÓN DE AMBOS CAMPOS
            Boolean vmodelo = validaModelo(modelo);
            Boolean vmarca = validaMarca(marca);
            Boolean vcilindraje = validaCilindraje(intcilindraje);
            Boolean vplaca = validaPlaca(placa);
            Boolean vsaram = validaSARAM(intsaram);

            // SE VALIDA QUE LOS CAMPOS HAYAN PASADO SUS RESPECTIVAS VALIDACIONES
            if (vmodelo && vmarca && vcilindraje && vplaca && vsaram) {
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
                                        // REDIRIGE A LA INFO DE LA MOTOCICLETA YA ACTUALIZADA
                                        // REDIRIGE AL ACTIVITY REINICIANDO EL SERVICIO
                                        Intent infoaupdate = new Intent(getApplicationContext(), InfomotoActivity.class);
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
                        parametros.put("Modelo", modelo);
                        parametros.put("Marca", marca);
                        parametros.put("Cilindraje", cilindraje);
                        parametros.put("SARAM", saram);
                        parametros.put("Placa", placa);
                        parametros.put("ID_Moto", String.valueOf(id_moto));
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

    // MÉTODOS QUE VALIDAN LÍMTE DE CAMPOS
    private boolean validaModelo(String modelo){
        if(modelo.length() > 30 ) {
            tilModelo.setError("Modelo no válido, Intentalo de nuevo");
            return false;
        }else{
            return true;
        }
    }

    private boolean validaMarca(String marca){
        if(marca.length() > 10 ) {
            tilMarca.setError("Marca no válida, Intentalo de nuevo");
            return false;
        }else{
            return true;
        }
    }

    private boolean validaCilindraje(int cilindraje){
        if(cilindraje > 99999 && cilindraje < 49 ) {
            tilCilindraje.setError("Cilindraje no válido, Intentalo de nuevo");
            return false;
        }else{
            return true;
        }
    }

    private boolean validaPlaca(String placa){
        if(placa.length() > 5 ) {
            tilPlaca.setError("Placa no válida, Intentalo de nuevo");
            return false;
        }else{
            return true;
        }
    }

    private boolean validaSARAM(int saram){
        if(saram > 9999 && saram < 0 ) {
            tilSARAM.setError("Número de SARAM no válido, Intentalo de nuevo");
            return false;
        }else{
            return true;
        }
    }

    // PARA ACTIVAR LA FUNCIONALIDAD DE LA FLECHA DE RETORNO
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}