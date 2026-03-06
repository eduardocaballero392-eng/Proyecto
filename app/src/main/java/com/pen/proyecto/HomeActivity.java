package com.pen.proyecto;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private LinearLayout btnNuevoRegistro, layoutInicio, layoutRegistros, layoutCalendario, layoutPerfil;
    private TextView tvInicio, tvRegistros, tvCalendario, tvPerfil, tvVerTodos;
    private TextView tvHoy, tvSemana, tvMes;
    private String nombreUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Inicializar vistas de la barra inferior
        btnNuevoRegistro = findViewById(R.id.btnNuevoRegistro);
        layoutInicio = findViewById(R.id.layoutInicio);
        layoutRegistros = findViewById(R.id.layoutRegistros);
        layoutCalendario = findViewById(R.id.layoutCalendario);
        layoutPerfil = findViewById(R.id.layoutPerfil);

        tvInicio = findViewById(R.id.tvInicio);
        tvRegistros = findViewById(R.id.tvRegistros);
        tvCalendario = findViewById(R.id.tvCalendario);
        tvPerfil = findViewById(R.id.tvPerfil);

        // Otros elementos
        tvVerTodos = findViewById(R.id.tvVerTodos);
        tvHoy = findViewById(R.id.tvHoy);
        tvSemana = findViewById(R.id.tvSemana);
        tvMes = findViewById(R.id.tvMes);

        // Recibir el usuario del Login
        nombreUsuario = getIntent().getStringExtra(LoginActivity.EXTRA_USUARIO);
        if (nombreUsuario == null || nombreUsuario.isEmpty()) {
            nombreUsuario = "EDUARDO";
        }

        // Cargar estadísticas (simuladas)
        cargarEstadisticas();

        // Botón Nuevo Registro (central)
        btnNuevoRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, EvidenciaActivity.class);
                startActivity(intent);
            }
        });

        // "Ver todos" - abre la pantalla de registros
        tvVerTodos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, RegistrosActivity.class);
                startActivity(intent);
            }
        });

        // Navegación inferior - Inicio
        layoutInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cambiar colores para indicar selección
                resetearColores();
                tvInicio.setTextColor(getColor(R.color.colorPrimary));
                tvInicio.setTypeface(tvInicio.getTypeface(), android.graphics.Typeface.BOLD);
                Toast.makeText(HomeActivity.this, "Ya estás en Inicio", Toast.LENGTH_SHORT).show();
            }
        });

        // Navegación inferior - Registros
        layoutRegistros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetearColores();
                tvRegistros.setTextColor(getColor(R.color.colorPrimary));
                tvRegistros.setTypeface(tvRegistros.getTypeface(), android.graphics.Typeface.BOLD);
                Intent intent = new Intent(HomeActivity.this, RegistrosActivity.class);
                startActivity(intent);
            }
        });

        // Navegación inferior - Calendario
        layoutCalendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetearColores();
                tvCalendario.setTextColor(getColor(R.color.colorPrimary));
                tvCalendario.setTypeface(tvCalendario.getTypeface(), android.graphics.Typeface.BOLD);
                Intent intent = new Intent(HomeActivity.this, CalendarioActivity.class);
                startActivity(intent);
            }
        });

        // Navegación inferior - Perfil
        layoutPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetearColores();
                tvPerfil.setTextColor(getColor(R.color.colorPrimary));
                tvPerfil.setTypeface(tvPerfil.getTypeface(), android.graphics.Typeface.BOLD);
                Intent intent = new Intent(HomeActivity.this, PerfilActivity.class);
                intent.putExtra(LoginActivity.EXTRA_USUARIO, nombreUsuario);
                startActivity(intent);
            }
        });
    }

    private void resetearColores() {
        tvInicio.setTextColor(getColor(android.R.color.darker_gray));
        tvRegistros.setTextColor(getColor(android.R.color.darker_gray));
        tvCalendario.setTextColor(getColor(android.R.color.darker_gray));
        tvPerfil.setTextColor(getColor(android.R.color.darker_gray));

        tvInicio.setTypeface(tvInicio.getTypeface(), android.graphics.Typeface.NORMAL);
        tvRegistros.setTypeface(tvRegistros.getTypeface(), android.graphics.Typeface.NORMAL);
        tvCalendario.setTypeface(tvCalendario.getTypeface(), android.graphics.Typeface.NORMAL);
        tvPerfil.setTypeface(tvPerfil.getTypeface(), android.graphics.Typeface.NORMAL);
    }

    private void cargarEstadisticas() {
        // Simulación de datos
        tvHoy.setText("12.5 kg");
        tvSemana.setText("68.3 kg");
        tvMes.setText("245.7 kg");
    }
}