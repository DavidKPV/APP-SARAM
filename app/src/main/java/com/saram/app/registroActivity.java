package com.saram.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

public class registroActivity extends AppCompatActivity {

    // SE DECLARAN LOS OBJETOS UTILIZADOS
    TextInputLayout tilNombre, tilEmail, tilPassword, tilRPassword, tilEdad;
    EditText etNombre, etEmail, etPassword, etRPassword, etEdad;
    Button btnRegistro;
    TextView tvTerminos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // PARA ACTIVAR LA FLECHA DE RETORNO
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // SE ENLAZAN LOS CONTROLADORES CON LA VISTA
        btnRegistro = (Button) findViewById(R.id.btnRegistro);

        // SE CREA EL OYENTE DEL BOTON
        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // AQUÍ SE COLOCAN TODAS LAS INSTRUCCIONES PARA REGISTRAR EL USUARIO
            }
        });
    }

    // PARA ACTIVAR LA FUNCIONALIDAD DE LA FLECHA DE RETORNO
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
