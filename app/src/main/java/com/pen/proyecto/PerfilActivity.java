package com.pen.proyecto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PerfilActivity extends AppCompatActivity {

    // Elementos de UI
    private TextView tvInicial, tvNombreUsuario;
    private TextView tvTotalRegistros, tvKgTotal, tvKgMes;
    private TextView tvMixto, tvPapel, tvPlastico, tvVidrio, tvOrganico;
    private Button btnCerrarSesion;
    
    // Base de datos y usuario
    private MySQLiteHelper dbHelper;
    private int loggedUserId;
    private String loggedUserDni;
    private String nombreUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        // Inicializar base de datos
        dbHelper = new MySQLiteHelper(this);

        // Recuperar datos del usuario
        loggedUserId = getIntent().getIntExtra("USER_ID", -1);
        loggedUserDni = getIntent().getStringExtra("USUARIO");
        
        // Si viene de LoginActivity con EXTRA_USUARIO
        String usuarioExtra = getIntent().getStringExtra(LoginActivity.EXTRA_USUARIO);
        if (usuarioExtra != null && !usuarioExtra.isEmpty()) {
            nombreUsuario = usuarioExtra.toUpperCase();
        } else if (loggedUserDni != null && !loggedUserDni.isEmpty()) {
            nombreUsuario = loggedUserDni.toUpperCase();
        } else {
            nombreUsuario = "USUARIO";
        }

        // Inicializar vistas
        inicializarVistas();
        
        // Configurar listeners
        configurarListeners();
        
        // Cargar datos del perfil
        cargarDatosPerfil();
    }

    private void inicializarVistas() {
        tvInicial = findViewById(R.id.tvInicialPerfil);
        tvNombreUsuario = findViewById(R.id.tvNombrePerfil);
        tvTotalRegistros = findViewById(R.id.tvTotalRegistros);
        tvKgTotal = findViewById(R.id.tvKgTotal);
        tvKgMes = findViewById(R.id.tvKgMes);
        tvMixto = findViewById(R.id.tvKgMixto);
        tvPapel = findViewById(R.id.tvKgPapel);
        tvPlastico = findViewById(R.id.tvKgPlastico);
        tvVidrio = findViewById(R.id.tvKgVidrio);
        tvOrganico = findViewById(R.id.tvKgOrganico);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
    }

    private void configurarListeners() {
        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarSesion();
            }
        });
    }

    private void cargarDatosPerfil() {
        // Configurar nombre e inicial
        tvNombreUsuario.setText(nombreUsuario);
        tvInicial.setText(String.valueOf(nombreUsuario.charAt(0)));

        // Si no hay usuario válido, mostrar datos vacíos
        if (loggedUserId == -1) {
            tvTotalRegistros.setText("0");
            tvKgTotal.setText("0");
            tvKgMes.setText("0");
            tvMixto.setText("0 kg");
            tvPapel.setText("0 kg");
            tvPlastico.setText("0 kg");
            tvVidrio.setText("0 kg");
            tvOrganico.setText("0 kg");
            return;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String userIdStr = String.valueOf(loggedUserId);

        try {
            // 1. Total de registros
            Cursor c1 = db.rawQuery(
                "SELECT COUNT(*) FROM " + MySQLiteHelper.TABLE_REGISTROS + 
                " WHERE " + MySQLiteHelper.COLUMN_REG_EMPLEADO_ID + "=?", 
                new String[]{userIdStr}
            );
            if (c1.moveToFirst()) {
                tvTotalRegistros.setText(String.valueOf(c1.getInt(0)));
            }
            c1.close();

            // 2. Peso total (kg)
            Cursor c2 = db.rawQuery(
                "SELECT SUM(" + MySQLiteHelper.COLUMN_REG_PESO + ") FROM " + 
                MySQLiteHelper.TABLE_REGISTROS + " WHERE " + 
                MySQLiteHelper.COLUMN_REG_EMPLEADO_ID + "=?", 
                new String[]{userIdStr}
            );
            if (c2.moveToFirst()) {
                double total = c2.getDouble(0);
                tvKgTotal.setText(String.format(Locale.getDefault(), "%.1f", total));
            }
            c2.close();

            // 3. Peso del mes actual
            String mesActual = new SimpleDateFormat("yyyy-MM", Locale.getDefault())
                .format(new Date()) + "%";
            Cursor c3 = db.rawQuery(
                "SELECT SUM(" + MySQLiteHelper.COLUMN_REG_PESO + ") FROM " + 
                MySQLiteHelper.TABLE_REGISTROS + " WHERE " + 
                MySQLiteHelper.COLUMN_REG_EMPLEADO_ID + "=? AND " + 
                MySQLiteHelper.COLUMN_REG_FECHA + " LIKE ?", 
                new String[]{userIdStr, mesActual}
            );
            if (c3.moveToFirst()) {
                double totalMes = c3.getDouble(0);
                tvKgMes.setText(String.format(Locale.getDefault(), "%.1f", totalMes));
            }
            c3.close();

            // 4. Desglose por tipos de residuo
            actualizarPesoPorTipo(db, userIdStr, "Mixto", tvMixto);
            actualizarPesoPorTipo(db, userIdStr, "Papel", tvPapel);
            actualizarPesoPorTipo(db, userIdStr, "Plástico", tvPlastico);
            actualizarPesoPorTipo(db, userIdStr, "Vidrio", tvVidrio);
            actualizarPesoPorTipo(db, userIdStr, "Orgánico", tvOrganico);
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    private void actualizarPesoPorTipo(SQLiteDatabase db, String userId, String tipo, TextView tv) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(
                "SELECT SUM(" + MySQLiteHelper.COLUMN_REG_PESO + ") FROM " + 
                MySQLiteHelper.TABLE_REGISTROS + " WHERE " + 
                MySQLiteHelper.COLUMN_REG_EMPLEADO_ID + "=? AND " + 
                MySQLiteHelper.COLUMN_REG_DESCRIPCION + " LIKE ?", 
                new String[]{userId, "%" + tipo + "%"}
            );
            
            if (cursor.moveToFirst()) {
                double peso = cursor.getDouble(0);
                tv.setText(String.format(Locale.getDefault(), "%.1f kg", peso));
            } else {
                tv.setText("0.0 kg");
            }
        } catch (Exception e) {
            e.printStackTrace();
            tv.setText("0.0 kg");
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    private void cerrarSesion() {
        // Limpiar SharedPreferences (sesión)
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

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar datos cuando la actividad vuelve a primer plano
        cargarDatosPerfil();
    }
}