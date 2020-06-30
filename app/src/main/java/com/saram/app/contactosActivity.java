package com.saram.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
        Cursor cursor = getContentResolver().query(
                // EL CONTEN_URI ES UNA DIRECCIÓN QUE PUEDE INGRESAR A LA RUTA DE LOS CONTACTOS
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.Data.DISPLAY_NAME + " ASC"
        );
        // SE INICIA EL CURSOR
        startManagingCursor(cursor);

        // SE OBTIENEN LOS DATOS DEL NOMBRE Y NÚMERO DE LOS CONTACTOS ASÍ COMO SU ID
        String[] desde = {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone._ID
        };

        // SE CREA UN ARREGLO ENCONTRANDO LOS TEXTVIEW DE CADA ITERACIÓN DEL ARCHIVO XML (ITEM_CONTACTOS.XML)
        int[] a = {
                R.id.tvNombreContacto,
                R.id.tvNumeroContacto,
        };

        // SE CREA EL ADAPTER PARA INSERTAR LOS NOMBRE Y NÚMEROS DE CADA CONTACTO
        final SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this,R.layout.item_contactos,cursor,desde,a);
        listaContactos.setAdapter(simpleCursorAdapter);
        listaContactos.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

    }

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