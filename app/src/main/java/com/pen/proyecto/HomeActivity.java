package com.pen.proyecto;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;  // ← ESTE IMPORT FALTABA

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private Button btnNuevoRegistro;
    private TextView tvInicio, tvRegistros, tvCalendario, tvPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnNuevoRegistro = findViewById(R.id.btnNuevoRegistro);
        tvInicio = findViewById(R.id.tvInicio);
        tvRegistros = findViewById(R.id.tvRegistros);
        tvCalendario = findViewById(R.id.tvCalendario);
        tvPerfil = findViewById(R.id.tvPerfil);

        // Botón para nuevo registro
        btnNuevoRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, EvidenciaActivity.class);
                startActivity(intent);
            }
        });

        // Navegación inferior
        tvInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this, "Ya estás en Inicio", Toast.LENGTH_SHORT).show();
            }
        });

        tvRegistros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this, "Ver todos los registros", Toast.LENGTH_SHORT).show();
                // Aquí iría una actividad de lista de registros
            }
        });

        tvCalendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this, "Calendario", Toast.LENGTH_SHORT).show();
                // Aquí iría una actividad de calendario
            }
        });

        tvPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this, "Perfil de usuario", Toast.LENGTH_SHORT).show();
                // Aquí iría una actividad de perfil
            }
        });
    }
}