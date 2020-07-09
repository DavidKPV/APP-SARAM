package com.saram.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.saram.app.ui.adapter.MotosAdapter;

import java.util.ArrayList;

public class contactosActivity extends AppCompatActivity {

    // SE DECLARAN LOS OBJETOS
    ListView listaContactos;

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
        setContentView(R.layout.activity_contactos);
        // PARA ACTIVAR LA FLECHA DE RETORNO
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
            final SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.item_contactos, contactosN, desde, a);
            listaContactos.setAdapter(simpleCursorAdapter);

            listaContactos.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> adapterView, final View view, int position, long id) {

                    // SE BUSCA LA REFERENCIA DEL TEXTVIEW EN LA VISTA.
                    TextView tvNombre = (TextView) view.findViewById(R.id.tvNombreContacto);
                    TextView tvNumero = (TextView) view.findViewById(R.id.tvNombreContacto);
                    // SE OBTIENE EL TEXTO DENTRO DEL CAMPO.
                    final String nombre  = tvNombre.getText().toString();
                    String numero  = tvNumero.getText().toString();

                    // DECLARAMOS LAS OPCIONES U OPCIÓN QUE REALIZARÁ LA APP AL MOSTRAR EL ALERT DIALOG
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    Toast.makeText(getApplicationContext(), "CONTACTO AGREGADO "+nombre, Toast.LENGTH_LONG).show();
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

    // NUEVO CÓDIGO PARA OBTENER LOS CONTACTOS DE MANERA MÁS PRÁCTICAS
    // METODO PARA OBTENER LOS CONTACTOS DEL TELEFONO
    public class contactos_android{
        public String nombre_contacto ="";
        public String numero_contacto ="";
        public int id_contacto=0;
    }

    //-----------------FIN DEL NUEVO CÓDIGO------------------------

    private void obtenerContactos(){
        /* ESTA PARTE ES UN CÓDIGO EN EL CUÁL INTENTÉ FILTRAR LOS RESULTADOS DE LOS CONTACTOS PERO AÚN LO
        PUEDO NECESITAR XD
        // SE DECLARA LA CADENA QUE OBTENDRÁ TODA LA INFORMACIÓN DE LOS CONTACTOS
        String[] proyeccion = new String[]{
                // SON TABLAS DONDE SE ALMACENAN LOS DATOS GENÉRICOS DENTRO DEL DISPOSITIVO MÓVIL
                ContactsContract.Data._ID,
                ContactsContract.Data.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.TYPE
        };
        // SE DECLARA LA SELECCIÓN DE CONTACTOS
        String seleccion =
                ContactsContract.Data.MIMETYPE + "='" +
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "' AND" +
                        ContactsContract.CommonDataKinds.Phone.NUMBER + "IS NOT NULL";
        // PARA COLOCAR EL ORDEN DE COMO SE MOSTRARÁN LOS CONTACTOS
        String orden = ContactsContract.Data.DISPLAY_NAME + " ASC";

        // SE LE ASIGNAN TODOS LOS PARÁMETROS ANTERIORES A LA PETICIÓN DEL CONTENT PROVIDER DE CONTACTOS
        Cursor cursor = getContentResolver().query(
                ContactsContract.Data.CONTENT_URI,
                proyeccion,
                seleccion,
                null,
                orden
        );

        // HACEMOS EL CICLO PARA QUE NOS IMPRIMA TODOS LOS CONTACTO DEL MÓVIL
        while (cursor.moveToNext()){
            textView.append(
                    "Identificador -" + cursor.getString(0)
                            +"Nombre -" + cursor.getString(1)
                            +"Número -" + cursor.getString(2)
                            +"Tipo -" + cursor.getString(3)
            );
        }

        // SE CIERRA EL PROCESO
        cursor.close();
        */
    }

    // PARA ACTIVAR LA FUNCIONALIDAD DE LA FLECHA DE RETORNO
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}