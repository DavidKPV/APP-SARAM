package com.saram.app;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class userinfoActivity extends AppCompatActivity {

    // SE DECLARAN LOS OBJETOS UTILIZADOS
    LinearLayout llSexo;
    Spinner spSexo;
    TextInputLayout tilNombre, tilApellidos, tilFecha, tilDireccion, tilTelefono, tilEmail, tilTipoSangre, tilAlergias, tilReligion, tilExtra;
    EditText etNombre, etApellidos, etFecha, etDireccion, etTelefono, etEmail, etTipoSangre, etAlergias, etReligion, etExtra;
    Button btnActualiza;

    // OBJETOS PARA LA CONEXIÓN AL SERVIDOR UTILIZANDO VOLLEY
    RequestQueue requestQueue;
    ProgressDialog progressDialog;

    // PAARA VALIDAR QUE EL SSEXO SOLO SE INTRODUZCA UNA VEZ
    String sexo;
    String nada="";
    String vsexo=null;

    // CREAMOS UNA CADENA LA CUAL CONTENDRÁ LA CADENA DE NUESTRO WEB SERVICE
    String HttpUriUpdate = "http://192.168.43.200:8080/SARAM-API/public/api/updateUser";
    String HttpUriGetUser = "http://192.168.43.200:8080/SARAM-API/public/api/getuser";
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
        setContentView(R.layout.activity_userinfo);
        // PARA ACTIVAR LA FLECHA DE RETORNO
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // SE ENLAZAN LOS CONTROLADORES CON LA VISTA
        tilNombre = (TextInputLayout) findViewById(R.id.tilNombre);
        tilApellidos = (TextInputLayout) findViewById(R.id.tilApellidos);
        tilFecha = (TextInputLayout) findViewById(R.id.tilFecha);
        tilDireccion = (TextInputLayout) findViewById(R.id.tilDireccion);
        tilTelefono = (TextInputLayout) findViewById(R.id.tilTelefono);
        tilEmail = (TextInputLayout) findViewById(R.id.tilEmail);
        tilTipoSangre = (TextInputLayout) findViewById(R.id.tilTipoSangre);
        tilAlergias = (TextInputLayout) findViewById(R.id.tilAlergias);
        tilReligion = (TextInputLayout) findViewById(R.id.tilReligion);
        tilExtra = (TextInputLayout) findViewById(R.id.tilExtra);
        etNombre = (EditText) findViewById(R.id.etNombre);
        etApellidos = (EditText) findViewById(R.id.etApellidos);
        etFecha = (EditText) findViewById(R.id.etFecha);
        etDireccion = (EditText) findViewById(R.id.etDireccion);
        etTelefono = (EditText) findViewById(R.id.etTelefono);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etTipoSangre = (EditText) findViewById(R.id.etTipoSangre);
        etAlergias = (EditText) findViewById(R.id.etAlergias);
        etReligion = (EditText) findViewById(R.id.etReligion);
        etExtra = (EditText) findViewById(R.id.etExtra);
        // ENCONTRAR LOS ELEMENTOS POR ID PARA EL SPINNER
        spSexo = (Spinner) findViewById(R.id.spSexo);
        // ENCONTRAMOS EL LINEAR LAYOUT DE SEXO
        llSexo = (LinearLayout) findViewById(R.id.llSexo);
        btnActualiza = (Button) findViewById(R.id.btnActualiza);

        // CREAR UN ADAPTER  PARA EL SPINNER                                       contexto         values          clase
        ArrayAdapter<CharSequence> adaptador = ArrayAdapter.createFromResource(this, R.array.valores_sexo, android.R.layout.simple_spinner_item);
        spSexo.setAdapter(adaptador);

        // SE ACTIVAN LOS OBJETOS DE REQUEST QUEUE Y PROGRESS DIALOG
        requestQueue = Volley.newRequestQueue(userinfoActivity.this);
        progressDialog = new ProgressDialog(userinfoActivity.this);

        // OBTENEMOS EL TOKEN DEL SHARED
        SharedPreferences sp1 = getSharedPreferences("MisDatos", Context.MODE_PRIVATE);
        vtoken = sp1.getString("token", "");

        // SE ACTIVA TODO PARA TRAER INFORMACIÓN DE USUARIO
        // MOSTRAMOS EL PROGRESS DIALOG ---- AQUÍ SE COMIENZA EL ARMADO Y LA EJECUCIÓN DEL WEB SERVICE
        progressDialog.setMessage("CARGANDO...");
        progressDialog.show();
        // CREACIÓN DE LA CADENA A EJECUTAR EN EL WEB SERVICE MEDIANTE VOLLEY
        // Objeto de volley
        StringRequest stringRequest = new StringRequest(Request.Method.POST, HttpUriGetUser,
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
                            // INTERPRETAR LOS VALORES DEL JSON OBTENIDO DEL WEB SERVICE
                            String nombre = obj.getString("Nombre");
                            String apellidos = obj.getString("Apellidos");
                            String correo = obj.getString("Correo");
                            String fecha = obj.getString("Edad");
                            String direccion = obj.getString("Direccion");
                            String telefono = obj.getString("Telefono");
                            String tiposangre = obj.getString("Tipo_sangre");
                            String alergias = obj.getString("Alergias");
                            String religion = obj.getString("Religion");
                            String extra = obj.getString("Informacion_adicional");
                            sexo = obj.getString("Sexo");

                            if(sexo.equals("null")){
                                // SI ES NULO MOSTRARÁ EL CAMPO DE SEXO
                            }
                            else{
                                // SI YA CONTIENE PARÁMETROS EL CAMPO SEXO EVITAR QUE SE MUESTRE ESA OPCIÓN
                                llSexo.setVisibility(llSexo.GONE);
                            }

                            etNombre.setText(nombre);
                            etApellidos.setText(apellidos);
                            etEmail.setText(correo);

                            if(fecha.equals("null")){
                                etFecha.setText(nada);
                            }else{
                                etFecha.setText(fecha);
                            }
                            if(direccion.equals("null")){
                                etDireccion.setText(nada);
                            }else{
                                etDireccion.setText(direccion);
                            }
                            if(telefono.equals("null")){
                                etTelefono.setText(nada);
                            }else{
                                etTelefono.setText(telefono);
                            }
                            if(tiposangre.equals("null")){
                                etTipoSangre.setText(nada);
                            }else{
                                etTipoSangre.setText(tiposangre);
                            }
                            if(alergias.equals("null")){
                                etAlergias.setText(nada);
                            }else{
                                etAlergias.setText(alergias);
                            }
                            if(religion.equals("null")){
                                etReligion.setText(nada);
                            }else{
                                etReligion.setText(religion);
                            }
                            if(extra.equals("null")){
                                etExtra.setText(nada);
                            }
                            else{
                                etExtra.setText(extra);
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
        // VALIDACIÓN PARA EL CAMPO NOMBRE
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

        // VALIDACIÓN PARA EL CAMPO APELLIDOS
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

        // VALIDACIÓN PARA EL CAMPO EMAIL
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // PARA LIMPIAR LOS ERRORES DENTRO DEL PROCESO DE LLENADO DEL CAMPO
                tilEmail.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // VALIDACIÓN PARA EL CAMPO FECHA
        etFecha.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // PARA LIMPIAR LOS ERRORES DENTRO DEL PROCESO DE LLENADO DEL CAMPO
                tilFecha.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // VALIDACIÓN PARA EL CAMPO DIRECCION
        etDireccion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // PARA LIMPIAR LOS ERRORES DENTRO DEL PROCESO DE LLENADO DEL CAMPO
                tilDireccion.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // VALIDACIÓN PARA EL CAMPO TELEFONO
        etTelefono.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // PARA LIMPIAR LOS ERRORES DENTRO DEL PROCESO DE LLENADO DEL CAMPO
                tilTelefono.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // VALIDACIÓN PARA EL CAMPO TIPO DE SANGRE
        etTipoSangre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // PARA LIMPIAR LOS ERRORES DENTRO DEL PROCESO DE LLENADO DEL CAMPO
                tilTipoSangre.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // VALIDACIÓN PARA EL CAMPO ALERGIAS
        etAlergias.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // PARA LIMPIAR LOS ERRORES DENTRO DEL PROCESO DE LLENADO DEL CAMPO
                tilAlergias.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // VALIDACIÓN PARA EL CAMPO RELIGION
        etReligion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // PARA LIMPIAR LOS ERRORES DENTRO DEL PROCESO DE LLENADO DEL CAMPO
                tilReligion.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // VALIDACIÓN PARA EL CAMPO EXTRA
        etExtra.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // PARA LIMPIAR LOS ERRORES DENTRO DEL PROCESO DE LLENADO DEL CAMPO
                tilExtra.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // OYENTE DEL BOTÓN ACTUALIZAR
        btnActualiza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actualizarDatos();
            }
        });

    }

    // MÉTODO PARA ACTUALIZAR LOS DATOS DEL USUARIO
    private void actualizarDatos(){
        final String nombre = tilNombre.getEditText().getText().toString();
        final String apellidos = tilApellidos.getEditText().getText().toString();
        final String correo = tilEmail.getEditText().getText().toString();
        final String fecha = tilFecha.getEditText().getText().toString();
        final String direccion = tilDireccion.getEditText().getText().toString();
        final String telefono = tilTelefono.getEditText().getText().toString();
        final String tiposangre = tilTipoSangre.getEditText().getText().toString();
        final String alergias = tilAlergias.getEditText().getText().toString();
        final String religion = tilReligion.getEditText().getText().toString();
        final String extra = tilExtra.getEditText().getText().toString();
        if(sexo.equals("null")){
            vsexo = spSexo.getSelectedItem().toString();
        }

        // SE VALIDAN QUE LOS CAMPOS NO ESTEN VACÍOS
        if (nombre.isEmpty() || apellidos.isEmpty() || correo.isEmpty()) {
            Toast.makeText(this, "NO DEBEN DE QUEDAR VACÍOS LOS CAMPOS ESENCIALES (Nombre, apellidos y correo)", Toast.LENGTH_LONG).show();
        } else {
            // SE OBTIENEN LOS RESULTADOS DE LA VALIDACIÓN DE AMBOS CAMPOS
            Boolean vnombre = validaNombre(nombre);
            Boolean vapellidos = validaApellidos(apellidos);
            Boolean vcorreo = validaCorreo(correo);
            Boolean vfecha = validaFecha(fecha);
            Boolean vdireccion = validaDireccion(direccion);
            Boolean vtelefono = validaTelefono(telefono);
            Boolean vtiposangre = validaTipoSangre(tiposangre);
            Boolean valergias = validaAlergias(alergias);
            Boolean vreligion = validaReligion(religion);
            Boolean vextra = validaExtra(extra);

            // SE VALIDA QUE LOS CAMPOS HAYAN PASADO SUS RESPECTIVAS VALIDACIONES
            if (vnombre && vapellidos && vcorreo && vfecha && vdireccion && vtelefono && vtiposangre && valergias && vreligion && vextra) {
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
                                        // REDIRIGE AL ACTIVITY REINICIANDO EL SERVICIO PARA ACTUALIZAR
                                        Intent infoUserupdate = new Intent(getApplicationContext(), userinfoActivity.class);
                                        startActivity(infoUserupdate);
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
                        parametros.put("Correo", correo);
                        parametros.put("Edad", fecha);
                        parametros.put("Telefono", telefono);
                        parametros.put("Direccion", direccion);
                        parametros.put("Tipo_Sangre", tiposangre);
                        parametros.put("Alergias", alergias);
                        parametros.put("Religion", religion);
                        parametros.put("Extras", extra);
                        if(vsexo != null){
                            parametros.put("Sexo", vsexo);
                        }
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
        Pattern patron = Pattern.compile("^[a-zA-Z ]+$");
        // Y NO PASE DEL LÍMTE PERMITIDO
        if (!patron.matcher(apellidos).matches() || apellidos.length() > 30) {
            tilApellidos.setError("Apellidos no válidos, Intentalo de nuevo");
            return false;
        }else{
            return true;
        }
    }

    private boolean validaCorreo(String correo){
        // O SE ASEGURA QUE TENGA DATOS ESPECÍFICOS DE UN EMAIL
        Pattern patronEmail = Patterns.EMAIL_ADDRESS;

        // Y NO PASE DEL LÍMTE PERMITIDO
        if(!patronEmail.matcher(correo).matches() || correo.length() > 50 ) {
            tilEmail.setError("Correo no válido, Intentalo de nuevo");
            return false;
        }else{
            return true;
        }
    }

    private boolean validaFecha(String fecha){
        Pattern patron = Pattern.compile("^\\d{4}([-])(0?[1-9]|1[1-2])\\1(3[01]|[12][0-9]|0?[1-9])$");
        // Y NO PASE DEL LÍMTE PERMITIDO
        if (!patron.matcher(fecha).matches() || fecha.length() > 10) {
            tilFecha.setError("Fecha no válida, Intentalo de nuevo");
            return false;
        }else{
            return true;
        }
    }

    private boolean validaTelefono(String telefono){
        // VALIDA QUE NO PASE DEL LÍMTE PERMITIDO
        if (telefono.length() > 13) {
            tilTelefono.setError("Teléfono no válido, Intentalo de nuevo");
            return false;
        }else{
            tilTelefono.setError(null);
        }
        return true;
    }

    private boolean validaDireccion(String direccion){
        // VALIDA QUE NO PASE DEL LÍMTE PERMITIDO
        if (direccion.length() > 150) {
            tilDireccion.setError("Límte de caracteres sobrepasado, Intentalo de nuevo");
            return false;
        }else{
            tilDireccion.setError(null);
        }
        return true;
    }

    private boolean validaTipoSangre(String tiposangre){
        // VALIDA QUE NO PASE DEL LÍMTE PERMITIDO
        if (tiposangre.length() > 4) {
            tilTipoSangre.setError("Límte de caracteres sobrepasado, Intentalo de nuevo");
            return false;
        }else{
            tilTipoSangre.setError(null);
        }
        return true;
    }

    private boolean validaAlergias(String alergias){
        // VALIDA QUE NO PASE DEL LÍMTE PERMITIDO
        if (alergias.length() > 150) {
            tilAlergias.setError("Límte de caracteres sobrepasado, Intentalo de nuevo");
            return false;
        }else{
            tilAlergias.setError(null);
        }
        return true;
    }

    private boolean validaReligion(String religion){
        // VALIDA QUE NO PASE DEL LÍMTE PERMITIDO
        if (religion.length() > 150) {
            tilReligion.setError("Límte de caracteres sobrepasado, Intentalo de nuevo");
            return false;
        }else{
            tilReligion.setError(null);
        }
        return true;
    }

    private boolean validaExtra(String extra){
        // VALIDA QUE NO PASE DEL LÍMTE PERMITIDO
        if (extra.length() > 150) {
            tilExtra.setError("Límte de caracteres sobrepasado, Intentalo de nuevo");
            return false;
        }else{
            tilExtra.setError(null);
        }
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}