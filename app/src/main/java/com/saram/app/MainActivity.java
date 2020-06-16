package com.saram.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    TextView tvOlvido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvOlvido = (TextView) findViewById(R.id.tvOlvido);

        tvOlvido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nuevaContraseña();
            }
        });
    }

    private void nuevaContraseña(){
        Intent intent = new Intent(this, recuperarContra.class);
        startActivity(intent);
    }
}
