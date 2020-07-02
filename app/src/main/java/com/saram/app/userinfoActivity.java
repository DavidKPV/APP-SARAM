package com.saram.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.material.textfield.TextInputLayout;

public class userinfoActivity extends AppCompatActivity {

    // SE DECLARAN LOS OBJETOS UTILIZADOS
    Spinner spSexo;
    TextInputLayout til

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        // PARA ACTIVAR LA FLECHA DE RETORNO
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // ENCONTRAR LOS ELEMENTOS POR ID
        spSexo = (Spinner) findViewById(R.id.spSexo);

        // CREAR UN ADAPTER  PARA EL SPINNER                                       contexto         values          clase
        ArrayAdapter<CharSequence> adaptador = ArrayAdapter.createFromResource(this, R.array.valores_sexo, android.R.layout.simple_spinner_item);
        spSexo.setAdapter(adaptador);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}