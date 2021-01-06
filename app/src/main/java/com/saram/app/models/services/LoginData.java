package com.saram.app.models.services;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.ImageView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.saram.app.controllers.activitys.InicioActivity;
import com.saram.app.models.Images;
import com.saram.app.models.Userbd;
import com.saram.app.routes.Rutas;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class LoginData {
    private Context contexto;
    private ProgressDialog progressDialog;
    private String HttpUri;
    private Userbd userbd;
    private Images imagenes = new Images();
    private RequestQueue requestQueue;
    private ImageView imageView;

    public LoginData(Context contexto, ImageView ivlogo) {
        this.contexto = contexto;
        this.progressDialog = new ProgressDialog(contexto);
        this.HttpUri = Rutas.login;
        this.userbd = new Userbd(contexto);
        this.requestQueue = Volley.newRequestQueue(contexto);
        this.imageView = ivlogo;
    }


    // MÉTODO DE INGRESO AL INICIO DE LA APP
    public void serviceIngresarData(final String correo, final String pass) {
        // MOSTRAMOS EL PROGRESS DIALOG ---- AQUÍ SE COMIENZA EL ARMADO Y LA EJECUCIÓN DEL WEB SERVICE
        progressDialog.setMessage("CARGANDO...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        // CREACIÓN DE LA CADENA A EJECUTAR EN EL WEB SERVICE MEDIANTE VOLLEY
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

                            // INTERPRETAR VALORES
                            if (status.contentEquals("false")) {
                                Toast.makeText(contexto, mensaje, Toast.LENGTH_LONG).show();
                            } else {
                                // SE OBTIENE EL TOKEN
                                String token = obj.getString("Token");
                                // SE OBTIENE EL NOMBRE
                                String nombre = obj.getString("Nombre");
                                // SE OBTIENE EL NOMBRE
                                String apellidos = obj.getString("Apellidos");

                                Toast.makeText(contexto, mensaje + " " + nombre, Toast.LENGTH_LONG).show();
                                // ACTIVAMOS EL EDITOR DEL SHARED
                                SharedPreferences sp1 = contexto.getSharedPreferences("MisDatos", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp1.edit();
                                editor.putString("nombre", nombre);
                                editor.putString("token", token);
                                editor.apply();
                                // VERIFICAMOS QUE NO EXISTA REGISTRO EN LA BD
                                String[] datos = userbd.getData("1");
                                if (datos[0] == null) {
                                    userbd.setData(1, nombre, imagenes.transformarImagenAByte(imageView));
                                }

                                // REDIRIGE AL INICIO DE LA APP
                                Intent intent_inicio = new Intent(contexto, InicioActivity.class);
                                contexto.startActivity(intent_inicio);
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
                Toast.makeText(contexto, error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            // MAPEO DE LOS VALORES QUE MANDAMOS AL WEB SERVICE
            protected Map<String, String> getParams() {
                // RETORNAR LOS VALORES
                Map<String, String> parametros = new HashMap<>();
                parametros.put("Correo", correo);
                parametros.put("Contrasena", pass);
                return parametros;
            }
        };
        // SE MANDA A EJECUTAR EL STRING PARA LA LIBRERÍA DE VOLLEY
        requestQueue.add(stringRequest);
    }

    public void serviceIngresarQr(final String Qrtoken){
        progressDialog.setMessage("CARGANDO...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        // CREACIÓN DE LA CADENA A EJECUTAR EN EL WEB SERVICE MEDIANTE VOLLEY
        StringRequest stringRequest = new StringRequest(Request.Method.POST, HttpUri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String serverresponse) {
                        progressDialog.dismiss();
                        // MANEJO DE ERRORES CON RESPECTO A LA RESPUESTA
                        try {
                            // CREAR UN OBJETO DE TIPO JSON PARA OBTENER EL ARCHIVO QUE MANDARÁ EL WEB SERVICE
                            JSONObject obj = new JSONObject(serverresponse);
                            // INTERPRETAR EL VALOR DEL JSON OBTENIDO DEL WEB SERVICE
                            String status = obj.getString("status");
                            // OBTENER EL MENSAJE
                            String mensaje = obj.getString("mensaje");

                            // INTERPRETAR VALORES
                            if(status.contentEquals("false")){
                                Toast.makeText(contexto, mensaje, Toast.LENGTH_LONG).show();
                            }
                            else{
                                String nombre = obj.getString("Nombre");

                                Toast.makeText(contexto, mensaje+" "+nombre, Toast.LENGTH_LONG).show();

                                // REDIRIGE AL INICIO DE LA APP
                                Intent intent_inicio = new Intent(contexto, InicioActivity.class);
                                contexto.startActivity(intent_inicio);
                            }
                        }catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                    // ESTE SE EJECUTA SI HAY UN ERROR EN LA RESPUESTA
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // SE OCULTA EL PROGRESS DIALOG
                progressDialog.dismiss();
                Toast.makeText(contexto, error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            // MAPEO DE LOS VALORES QUE MANDAMOS AL WEB SERVICE
            protected Map<String, String> getParams(){
                // RETORNAR LOS VALORES
                Map<String, String> parametros = new HashMap<>();
                parametros.put("Token", Qrtoken);
                return parametros;
            }
        };
        // SE MANDA A EJECUTAR EL STRING PARA LA LIBRERÍA DE VOLLEY
        requestQueue.add(stringRequest);
    }
}

