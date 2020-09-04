package com.saram.app.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.saram.app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class registroActivity extends AppCompatActivity {

    // SE DECLARAN LOS OBJETOS UTILIZADOS
    TextInputLayout tilNombre, tilApellidos, tilCorreo, tilPassword, tilRPassword;
    EditText etNombre, etApellidos, etCorreo, etPassword, etRPassword;
    Button btnRegistro;
    TextView tvTerminos;
    CheckBox chbTerminos;

    // OBJETOS PARA LE EJECUCIÓN DE VOLLEY
    RequestQueue requestQueue;
    ProgressDialog progressDialog;

    // CREAMOS UNA CADENA LA CUAL CONTENDRÁ LA CADENA DE NUESTRO WEB SERVICE
    String HttpUri = "http://192.168.43.200:8080/SARAM-API/public/api/registerUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // PARA ACTIVAR LA FLECHA DE RETORNO
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // SE ENLAZAN LOS CONTROLADORES CON LA VISTA
        btnRegistro = (Button) findViewById(R.id.btnRegistro);
        tilNombre = (TextInputLayout) findViewById(R.id.tilNombre);
        tilApellidos = (TextInputLayout) findViewById(R.id.tilApellidos);
        tilCorreo = (TextInputLayout) findViewById(R.id.tilCorreo);
        tilPassword = (TextInputLayout) findViewById(R.id.tilPassword);
        tilRPassword = (TextInputLayout) findViewById(R.id.tilRPassword);
        etNombre = (EditText) findViewById(R.id.etNombre);
        etApellidos = (EditText) findViewById(R.id.etApellidos);
        etCorreo = (EditText) findViewById(R.id.etCorreo);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etRPassword = (EditText) findViewById(R.id.etRPassword);
        chbTerminos = (CheckBox) findViewById(R.id.chbTerminos);

        // SE ACTIVAN LOS OBJETOS DE REQUEST QUEUE Y PROGRESS DIALOG
        requestQueue = Volley.newRequestQueue(registroActivity.this);
        progressDialog = new ProgressDialog(registroActivity.this);

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

        // VALIDACIÓN DEL CAMPO CONTRASEÑA Y REPETIR CONTRASEÑA
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // PARA LIMPIAR LOS ERRORES DENTRO DEL PROCESO DE LLENADO DEL CAMPO
                tilPassword.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        etRPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // PARA LIMPIAR LOS ERRORES DENTRO DEL PROCESO DE LLENADO DEL CAMPO
                tilRPassword.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // SE CREA EL OYENTE DEL BOTON
        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrarDatos();
            }
        });
    }

    private void registrarDatos(){
        if(chbTerminos.isChecked()){
            // SE OBTIENEN LOS VALORES DE LOS CAMPOS
            final String nombre = tilNombre.getEditText().getText().toString();
            final String apellidos = tilApellidos.getEditText().getText().toString();
            final String correo = tilCorreo.getEditText().getText().toString();
            final String password = tilPassword.getEditText().getText().toString();
            final String rpassword = tilRPassword.getEditText().getText().toString();

            // SE VALIDAN QUE LOS CAMPOS NO ESTEN VACÍOS
            if (nombre.isEmpty() || apellidos.isEmpty() || correo.isEmpty() || password.isEmpty() || rpassword.isEmpty()) {
                Toast.makeText(this, "LLENA TODOS LOS CAMPOS", Toast.LENGTH_LONG).show();
            } else {
                // SE OBTIENEN LOS RESULTADOS DE LA VALIDACIÓN DE AMBOS CAMPOS
                Boolean vnombre = validanombre(nombre);
                Boolean vapellidos = validaapellidos(apellidos);
                Boolean vcorreo = validaemail(correo);
                Boolean vpassword = validapasswordyRpassword(password, rpassword);

                // SE VALIDA QUE LOS CAMPOS HAYAN PASADO SUS RESPECTIVAS VALIDACIONES
                if (vnombre && vapellidos && vcorreo && vpassword) {
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
                                        // OBTENER EL MENSAJE
                                        String mensaje = obj.getString("mensaje");
                                        // INTERPRETAR LOS VALORES
                                        if (status.contentEquals("false")){
                                            Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();
                                            // REDIRIGE AL INICIO DE LA APP
                                            finish();
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
                            parametros.put("Contrasena", password);
                            parametros.put("Confirmacion", rpassword);
                            return parametros;
                        }
                    };

                    // SE MANDA A EJECUTAR EL STRING PARA LA LIBRERÍA DE VOLLEY
                    requestQueue.add(stringRequest);
                }
            }
        }else{
            Toast.makeText(this, "ACEPTA LOS TÉRMINOS Y CONDICIONES", Toast.LENGTH_SHORT).show();
        }
    }


    // MÉTODOS DE VALIDACIÓN DE LOS CAMPOS
    private boolean validanombre(String nombre){
        // SE ASEGURA QUE TENGA DATOS ESPECÍFICOS DE UN NOMBRE
        Pattern patron = Pattern.compile("^[a-zA-Z ]+$");
        // Y NO PASE DEL LÍMTE PERMITIDO
        if (!patron.matcher(nombre).matches() || nombre.length() > 30) {
            tilNombre.setError("Nombre no válido, Intentalo de nuevo");
            return false;
        }else{
            return true;
        }
    }

    private boolean validaapellidos(String apellidos){
        // SE ASEGURA QUE TENGA DATOS ESPECÍFICOS DE UN NOMBRE
        Pattern patron = Pattern.compile("^[a-zA-Z ]+$");
        // Y NO PASE DEL LÍMTE PERMITIDO
        if (!patron.matcher(apellidos).matches() || apellidos.length() > 30) {
            tilApellidos.setError("Apellidos no válidos, Intentalo de nuevo");
            return false;
        }else{
            return true;
        }
    }

    private boolean validaemail(String correo){
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

    private boolean validapasswordyRpassword(String password, String Rpassword){
        // SE VERIFICA QUE AMBAS CONTRASEÑAS SEAN IGUALES
        if(password.equals(Rpassword)) {
            // VALIDA QUE NO PASE DEL LÍMTE PERMITIDO
            if (password.length() > 16) {
                tilPassword.setError("Contraseña no válida, Intentalo de nuevo");
                return false;
            } else {
                tilPassword.setError(null);
            }
            return true;
        }else{
            tilPassword.setError("Las Contraseñas no coinciden");
            tilRPassword.setError("Las Contraseñas no coinciden");
            return false;
        }
    }

    // PARA ACTIVAR LA FUNCIONALIDAD DE LA FLECHA DE RETORNO
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
