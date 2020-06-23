package com.saram.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // SE DECLARAN LOS OBJETOS UTILIZADOS
    TextView tvOlvido, tvRegistro;
    Button btnIngreso;

    // ESTE MÉTODO EVITA QUE SE REGRESE CON LA FLECHA DE RETORNO QUE TODOS TENEMOS
    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // VERIFICA SI EXISTE O NO UNA SESION INICIADA
        verifica();

        // SE ACTIVA EL MÉTODO PARA CREAR EL SHARED PREFERENCES
        CrearSharedPreferences();

        // SE ENLANZAN LOS CONTROLADORES CON LA VISTA
        tvOlvido = (TextView) findViewById(R.id.tvOlvido);
        tvRegistro = (TextView) findViewById(R.id.tvRegistro);
        btnIngreso = (Button) findViewById(R.id.btnIngreso);

        // OYENTE DEL TEXTO DE RECUPERAR LA CONTRASEÑA
        tvOlvido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nuevaContrasena();
            }
        });

        // OYENTE PARA UN NUEVO REGISTRO
        tvRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nuevoRegistro();
            }
        });

        // SE CREA EL OYENTE PARA EL BOTÓN DE INGRESO
        btnIngreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ingresar();
            }
        });
    }

    // MÉTODO DE INGRESO AL INICIO DE LA APP
    private void ingresar(){
        // AÚN NO SE PROGRAMA NADA CON VOLLEY, SIN EMBARGO ESTO ES PARA PROBAR EL INICIO DE LA APP
        Intent intent_inicio = new Intent(this, inicioActivity.class);
        startActivity(intent_inicio);
    }

    // MÉTODO DE LA ACCIÓN DEL OYENTE PARA LA RECUPERACIÓN DE LA CONTRASEÑA
    private void nuevaContrasena(){
        Intent intentcontra = new Intent(this, recuperarContra.class);
        startActivity(intentcontra);
    }

    // MÉTODO DE LA ACCIÓN DEL OYENTE PARA UN NUEVO REGISTRO
    private void nuevoRegistro(){
        // SE INTANCIA EL INTENT
        Intent intentregistro = new Intent(this, registroActivity.class);
        // SE INICIA LA NUEVA ACTIVITY
        startActivity(intentregistro);
    }

    // SE CREA LA FUNCIÓN DEL SHARED PREFERENCES
    private void CrearSharedPreferences(){
        // SE INSTANCIA EL SHARED                   Nombre del archivo,   forma en como se abrirá
        SharedPreferences prefer1 = getSharedPreferences("MisDatos", Context.MODE_PRIVATE);

        // SE ACTIVA LA EDICIÓN DEL ARCHIVO
        SharedPreferences.Editor editor = prefer1.edit();

        // SE MANDAN LOS VALORES QUE QUEREMOS ALMACENAR
        // Nombre de la variable, valor de la variable
        editor.putBoolean("sesion", false);

        // SE MANDA UNA INSTRUCCIÓN COMMIT PARA QUE SE GUARDEN LOS CAMBIOS
        editor.commit();
    }

    private void verifica(){
        // ACTIVAMOS EL EDITOR DEL SHARED
        SharedPreferences sp1 = getSharedPreferences("MisDatos", Context.MODE_PRIVATE);

        // TRAEMOS EL VALOR DE LA SESION
        boolean val = sp1.getBoolean("sesion", false);

        // SE INSTANCIA EL ACTIVITY DEL INICIO DE LA APP    contexto, nombre de la clase compilada
        Intent intent1 = new Intent(this, inicioActivity.class);

        if(val == true) {
            // SE MANDA A EJECUTAR EL ACTIVITY DE INICIO DE LA APP
            startActivity(intent1);
        }
    }
}
