package com.saram.app.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
import com.saram.app.adapters.contactos_saramAdapter;
import com.saram.app.models.rutas;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class contactos_saramActivity extends AppCompatActivity {
    // ESTOS SON DE LA ETIQUETA QUE CREO NO SE OCUPAN
    FloatingActionButton fabMas;
    RecyclerView rvContenedorContactos;
    contactos_saramAdapter Contactos = null;
    // OBJETOS PARA LA CONEXIÓN AL SERVIDOR UTILIZANDO VOLLEY
    RequestQueue requestQueue;
    ProgressDialog progressDialog;

    // CREAMOS UNA CADENA LA CUAL CONTENDRÁ LA CADENA DE NUESTRO WEB SERVICE
    String HttpUri = rutas.getContactos;
    String HttpUriDel = rutas.delContactos;
    String HttpUriSave = rutas.setContactos;
    String vtoken;
    static final int PICK_CONTACT = 1;
    String nombre, telefono;

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

        // ASOCIAR EL RV A UN LINEAR LAYOUT MANAGER
        LinearLayoutManager LLManager= new LinearLayoutManager(this);
        rvContenedorContactos.setLayoutManager(LLManager);
        // REGISTRAR EL RECYCLE VIEW PARA EL CONTEXT VIEW
        registerForContextMenu(rvContenedorContactos);

        // SE ACTIVAN LOS OBJETOS DE REQUEST QUEUE Y PROGRESS DIALOG
        requestQueue = Volley.newRequestQueue(contactos_saramActivity.this);
        progressDialog = new ProgressDialog(contactos_saramActivity.this);

        // OBTENEMOS EL TOKEN DEL SHARED
        SharedPreferences sp1 = getSharedPreferences("MisDatos", Context.MODE_PRIVATE);
        vtoken = sp1.getString("token", "");

        fabMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* REDIRIGE A LA ACTIVITY QUE CONTIENE UN CONTENT PROVIDER (OEERO DUPLICA LOS DATOS)
                Intent agregarContacto =  new Intent(contactos_saramActivity.this, contactosActivity.class);
                startActivity(agregarContacto);
                 */
                // ACCEDEMOS A LOS CONTACTOS DEL DISPOSITIVO
                Intent intent1 = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent1, PICK_CONTACT);
            }
        });

        // MÉTODO QUE MOSTRARÁ TODOS LOS CONTACTOS ENCONTRADOS DESDE LA APP
        mostrarDatosCompletos();
    }

    // MÉTODO QUE OBTIENE TODOS LOS REGISTROS DE LOS CONTACTOS EN LA BASE DE DATOS
    private void mostrarDatosCompletos(){
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
                                    ID_contactos[i]=ContactosInfo.getJSONObject(i).getInt("id_contacto");
                                }
                                Contactos = new contactos_saramAdapter(Nombre, Numero, ID_contactos, getApplication());
                                rvContenedorContactos.setAdapter(Contactos);
                                // REGISTRAMOS EL CONTEXT MENU DENTRO DEL RECYCLE VIEW
                                registerForContextMenu(rvContenedorContactos);

                                Contactos.setOnItemClickListener(new contactos_saramAdapter.OnItemClickListener() {
                                    @Override
                                    public void onEditClick(int position) {
                                        Intent editcontacto = new Intent(contactos_saramActivity.this, edit_contactosActivity.class);
                                        editcontacto.putExtra("ID", ID_contactos[position]);
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
                                                                            // SE ACTUALIZA EL LISTADO
                                                                            mostrarDatosCompletos();
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

    // CREAMOS EL CONTEXT MENU, EL CUÁL SE MOSTRARÁ AL DEJAR PRESIONADO EL CONTACTO SELECCIONADO
    // SE INFLA EL LAYOUT DEL MENÚ DE OPCIONES
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        // INFLAMOS EL MENÚ
        MenuInflater menuInflater = getMenuInflater();

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle("SE ELIMINARÁ RL CONTACTO SELECCIONADO");
        menuInflater.inflate(R.menu.context_menu, menu);
    }

    // MÉTODO ENCARGADO DE VERIFICAR QUE CONTACTO HA SIDO SELECCIONADO
    // SE MANEJA EL EVENTO DE CADA OPCIÓN
    @Override
    public boolean onContextItemSelected(MenuItem item){
        // CREAMOS EL SWITCH ENCARGADO DE LEER LAS OPCIONES
        switch(item.getItemId()){
            case R.id.delContacto:
                Toast.makeText(contactos_saramActivity.this, "Contacto Eliminado", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    // ESTE MÉTODO ES FORZOSO LLAMARSE ASÍ
    public void onActivityResult(int reqCode, int resultCode, Intent datos){
        super.onActivityResult(reqCode, resultCode, datos);
        if(reqCode == PICK_CONTACT){
            if(resultCode == Activity.RESULT_OK){
                Uri contactos = datos.getData(); // OBTENEMOS LA URI DE LOS CONTACTOS
                // OBTENEMOS TODA LA INFORMACIÓN DENTRO DEL CURSOR
                Cursor cursorContactos = managedQuery(contactos, null, null, null, null);
                if(cursorContactos.moveToFirst()){
                    // OBTENEMOS EL NOMBRE DEL TELÉFONO Y SU ID
                    nombre = cursorContactos.getString(cursorContactos.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    String id_contacto = cursorContactos.getString(cursorContactos.getColumnIndex(ContactsContract.Contacts._ID));

                    // OBTENEMOS MEDIANTE EL ID EL NÚMERO DEL TELÉFONO
                    Cursor cursorTel = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +"="+id_contacto,
                            null,
                            null);

                    // POSICIONAMOS EL CURSOR EN EL PRIMER REGISTRO
                    cursorTel.moveToFirst();
                    // CON EL CICLO FOR OBTENEMOS EL VALOR DEL O LOS TELÉFONOS ENCONTRADOS DE ACUERDO AL ID DEL USUARIO
                    telefono = cursorTel.getString(cursorTel.getColumnIndex("data1"));

                    // GUARDAMOS EL CONTACTO
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
                                                        // SE MANEJARÁ COMO ARRAY DE ACUERDO A LO PROGRAMADO POR EL WEB SERVICE
                                                        JSONArray valores = new JSONArray(serverresponse);
                                                        String [] mensaje = new String[valores.length()];
                                                        mensaje [0] = valores.getJSONObject(0).getString("mensaje");
                                                        Toast.makeText(getApplicationContext(),mensaje[0], Toast.LENGTH_LONG).show();
                                                        // ACTUALIZAR EL RECYCLE VIEW
                                                        mostrarDatosCompletos();
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
                                            parametros.put("Numero_Tel", telefono);
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
                    AlertDialog.Builder mensaje = new AlertDialog.Builder(contactos_saramActivity.this);
                    mensaje.setTitle("¿SE AGREGARÁ EL CONTACTO A SARAM?").setPositiveButton("ACEPTAR", dialogClickListener)
                            .setNegativeButton("CANCELAR", dialogClickListener).setIcon(R.drawable.saram)
                            .setMessage("Desde ahora "+nombre+" recibirá los mensajes de alerta por parte de SARAM").show();
                }
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