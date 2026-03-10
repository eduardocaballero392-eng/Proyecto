package com.pen.proyecto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PerfilActivity extends AppCompatActivity {

    private TextView tvInicial, tvNombreUsuario;
    private TextView tvTotalRegistros, tvKgTotal, tvKgMes;
    private TextView tvMixto, tvPapel, tvPlastico, tvVidrio, tvOrganico;
    private Button btnCerrarSesion;

    // Datos del usuario (simulados)
    private String nombreUsuario = "EDUARDO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        // Inicializar vistas
        tvInicial = findViewById(R.id.tvInicial);
        tvNombreUsuario = findViewById(R.id.tvNombreUsuario);
        tvTotalRegistros = findViewById(R.id.tvTotalRegistros);
        tvKgTotal = findViewById(R.id.tvKgTotal);
        tvKgMes = findViewById(R.id.tvKgMes);
        tvMixto = findViewById(R.id.tvMixto);
        tvPapel = findViewById(R.id.tvPapel);
        tvPlastico = findViewById(R.id.tvPlastico);
        tvVidrio = findViewById(R.id.tvVidrio);
        tvOrganico = findViewById(R.id.tvOrganico);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        // Obtener el nombre de usuario del Intent (desde Login)
        String usuario = getIntent().getStringExtra(LoginActivity.EXTRA_USUARIO);
        if (usuario != null && !usuario.isEmpty()) {
            nombreUsuario = usuario.toUpperCase();
        }

        // Configurar datos del perfil
        configurarPerfil();

        // Cargar estadísticas (simuladas)
        cargarEstadisticas();

        // Botón cerrar sesión
        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarSesion();
            }
        });
    }

    private void configurarPerfil() {
        // Mostrar inicial del nombre
        tvInicial.setText(String.valueOf(nombreUsuario.charAt(0)));
        tvNombreUsuario.setText(nombreUsuario);
    }

    private void cargarEstadisticas() {
        // Aquí deberías cargar datos reales desde tu base de datos
        // Por ahora usamos datos de ejemplo

        // Total de registros
        tvTotalRegistros.setText("24");

        // Kg totales
        tvKgTotal.setText("156.5");

        // Kg este mes (usando mes actual)
        String[] meses = {"Ene", "Feb", "Mar", "Abr", "May", "Jun",
                "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};
        SimpleDateFormat sdf = new SimpleDateFormat("MM", Locale.getDefault());
        int mesActual = Integer.parseInt(sdf.format(new Date())) - 1;
        tvKgMes.setText("42.3");

        // Tipos por categoría
        tvMixto.setText("45.2 kg");
        tvPapel.setText("52.8 kg");
        tvPlastico.setText("28.3 kg");
        tvVidrio.setText("18.5 kg");
        tvOrganico.setText("11.7 kg");
    }

    private void cerrarSesion() {
        // Limpiar cualquier dato de sesión guardado
        SharedPreferences prefs = getSharedPreferences("sesion", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        // Ir al Login y limpiar el stack de actividades
        Intent intent = new Intent(PerfilActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}