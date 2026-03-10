package com.pen.proyecto;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private LinearLayout navInicio, navRegistros, navCalendario, navPerfil;
    private FrameLayout btnNuevo;
    private TextView tvPesoHoy, tvPesoSemana, tvPesoMes, tvVerTodos;
    private LinearLayout containerRecientes;
    private MySQLiteHelper dbHelper;
    private int loggedUserId;
    private String loggedUserDni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        dbHelper = new MySQLiteHelper(this);

        // Recuperar datos del usuario logueado
        loggedUserId = getIntent().getIntExtra("USER_ID", -1);
        loggedUserDni = getIntent().getStringExtra("USUARIO");

        // Vincular vistas de navegación (IDs actualizados del nuevo diseño)
        navInicio = findViewById(R.id.navInicio);
        navRegistros = findViewById(R.id.navRegistros);
        btnNuevo = findViewById(R.id.btnNuevo);
        navCalendario = findViewById(R.id.navCalendario);
        navPerfil = findViewById(R.id.navPerfil);

        // Vincular vistas de estadísticas
        tvPesoHoy = findViewById(R.id.tvPesoHoy);
        tvPesoSemana = findViewById(R.id.tvPesoSemana);
        tvPesoMes = findViewById(R.id.tvPesoMes);
        tvVerTodos = findViewById(R.id.tvVerTodos);
        containerRecientes = findViewById(R.id.containerRecientes);

        configurarListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarEstadisticas();
        cargarRegistrosRecientes();
    }

    private void configurarListeners() {
        // Botón central "Nuevo"
        btnNuevo.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, EvidenciaActivity.class);
            intent.putExtra("USER_ID", loggedUserId);
            startActivity(intent);
        });

        // Navegación
        navRegistros.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, RegistrosActivity.class);
            intent.putExtra("USER_ID", loggedUserId);
            startActivity(intent);
        });

        navCalendario.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CalendarioActivity.class);
            intent.putExtra("USER_ID", loggedUserId);
            startActivity(intent);
        });

        navPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, PerfilActivity.class);
            intent.putExtra("USER_ID", loggedUserId);
            intent.putExtra("USUARIO", loggedUserDni);
            startActivity(intent);
        });

        tvVerTodos.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, RegistrosActivity.class);
            intent.putExtra("USER_ID", loggedUserId);
            startActivity(intent);
        });

        navInicio.setOnClickListener(v -> Toast.makeText(this, "Ya estás en Inicio", Toast.LENGTH_SHORT).show());
    }

    private void cargarEstadisticas() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String hoy = sdf.format(new Date());

        // Filtrar estadísticas por el usuario logueado
        String selection = MySQLiteHelper.COLUMN_REG_EMPLEADO_ID + " = ? AND ";

        // Hoy
        double totalHoy = getSumaPeso(db, selection + "fecha = ?", new String[]{String.valueOf(loggedUserId), hoy});
        tvPesoHoy.setText(String.format(Locale.getDefault(), "%.1f kg", totalHoy));

        // Mes
        String mesPrefijo = hoy.substring(0, 7) + "%";
        double totalMes = getSumaPeso(db, selection + "fecha LIKE ?", new String[]{String.valueOf(loggedUserId), mesPrefijo});
        tvPesoMes.setText(String.format(Locale.getDefault(), "%.1f kg", totalMes));

        // Semana
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -7);
        String haceUnaSemana = sdf.format(cal.getTime());
        double totalSemana = getSumaPeso(db, selection + "fecha >= ?", new String[]{String.valueOf(loggedUserId), haceUnaSemana});
        tvPesoSemana.setText(String.format(Locale.getDefault(), "%.1f kg", totalSemana));
    }

    private double getSumaPeso(SQLiteDatabase db, String selection, String[] args) {
        Cursor cursor = db.rawQuery("SELECT SUM(" + MySQLiteHelper.COLUMN_REG_PESO + ") FROM " + MySQLiteHelper.TABLE_REGISTROS + " WHERE " + selection, args);
        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }

    private void cargarRegistrosRecientes() {
        if (containerRecientes == null) return;
        containerRecientes.removeAllViews();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(MySQLiteHelper.TABLE_REGISTROS, null, 
                MySQLiteHelper.COLUMN_REG_EMPLEADO_ID + " = ?", 
                new String[]{String.valueOf(loggedUserId)}, 
                null, null, MySQLiteHelper.COLUMN_REG_ID + " DESC", "3");

        if (cursor.moveToFirst()) {
            do {
                String tipo = cursor.getString(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_REG_DESCRIPCION));
                String fecha = cursor.getString(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_REG_FECHA));
                double peso = cursor.getDouble(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_REG_PESO));

                View itemView = getLayoutInflater().inflate(R.layout.item_registro_home, containerRecientes, false);
                TextView tvTitulo = itemView.findViewById(R.id.tvTituloReciente);
                TextView tvSubtitulo = itemView.findViewById(R.id.tvSubtituloReciente);

                // Mostrar el tipo (categoría) y los kg
                tvTitulo.setText(tipo + ": " + String.format(Locale.getDefault(), "%.2f kg", peso));
                tvSubtitulo.setText("Fecha: " + fecha);

                containerRecientes.addView(itemView);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }
}
