package com.saram.app.controllers.activitys;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.saram.app.R;

public class RecuperarContra extends AppCompatActivity {
    Button btnCambiarPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_contra);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnCambiarPass = (Button) findViewById(R.id.btnCambiarPass);

        btnCambiarPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Intent agregar =  new Intent(getApplicationContext(), motoInfoActivity.class);
                startActivity(agregar);
                */
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}