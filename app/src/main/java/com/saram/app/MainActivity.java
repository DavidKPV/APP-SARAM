package com.saram.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // SE DECLARAN LOS OBJETOS UTILIZADOS
    TextView tvOlvido, tvRegistro;
    Button btnIngreso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        // AÚN NO SE PROGRAMA NADA, SIN EMBARGO ESTO ES PARA PROBAR EL INICIO DE LA APP
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
}
