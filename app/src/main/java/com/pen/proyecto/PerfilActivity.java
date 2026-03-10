package com.pen.proyecto;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PerfilActivity extends AppCompatActivity {

    private Button btnCerrarSesion;
    private TextView tvNombre, tvInicial, tvTotalRegistros, tvKgTotal, tvKgMes;
    private TextView tvKgMixto, tvKgPapel, tvKgPlastico, tvKgVidrio, tvKgOrganico;
    private MySQLiteHelper dbHelper;
    private int loggedUserId;
    private String loggedUserDni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        dbHelper = new MySQLiteHelper(this);
        loggedUserId = getIntent().getIntExtra("USER_ID", -1);
        loggedUserDni = getIntent().getStringExtra("USUARIO");

        // Vincular vistas
        tvNombre = findViewById(R.id.tvNombrePerfil);
        tvInicial = findViewById(R.id.tvInicialPerfil);
        tvTotalRegistros = findViewById(R.id.tvTotalRegistros);
        tvKgTotal = findViewById(R.id.tvKgTotal);
        tvKgMes = findViewById(R.id.tvKgMes);
        
        tvKgMixto = findViewById(R.id.tvKgMixto);
        tvKgPapel = findViewById(R.id.tvKgPapel);
        tvKgPlastico = findViewById(R.id.tvKgPlastico);
        tvKgVidrio = findViewById(R.id.tvKgVidrio);
        tvKgOrganico = findViewById(R.id.tvKgOrganico);
        
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        btnCerrarSesion.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(PerfilActivity.this, LoginActivity.class);
            intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        cargarDatosPerfil();
    }

    private void cargarDatosPerfil() {
        if (loggedUserDni != null) {
            tvNombre.setText(loggedUserDni);
            tvInicial.setText(loggedUserDni.substring(0, 1).toUpperCase());
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String userIdStr = String.valueOf(loggedUserId);

        // 1. Total Registros
        Cursor c1 = db.rawQuery("SELECT COUNT(*) FROM " + MySQLiteHelper.TABLE_REGISTROS + " WHERE " + MySQLiteHelper.COLUMN_REG_EMPLEADO_ID + "=?", new String[]{userIdStr});
        if (c1.moveToFirst()) tvTotalRegistros.setText(String.valueOf(c1.getInt(0)));
        c1.close();

        // 2. Kg Total
        Cursor c2 = db.rawQuery("SELECT SUM(" + MySQLiteHelper.COLUMN_REG_PESO + ") FROM " + MySQLiteHelper.TABLE_REGISTROS + " WHERE " + MySQLiteHelper.COLUMN_REG_EMPLEADO_ID + "=?", new String[]{userIdStr});
        if (c2.moveToFirst()) tvKgTotal.setText(String.format(Locale.getDefault(), "%.1f", c2.getDouble(0)));
        c2.close();

        // 3. Kg Mes Actual
        String mesActual = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(new Date()) + "%";
        Cursor c3 = db.rawQuery("SELECT SUM(" + MySQLiteHelper.COLUMN_REG_PESO + ") FROM " + MySQLiteHelper.TABLE_REGISTROS + " WHERE " + MySQLiteHelper.COLUMN_REG_EMPLEADO_ID + "=? AND " + MySQLiteHelper.COLUMN_REG_FECHA + " LIKE ?", new String[]{userIdStr, mesActual});
        if (c3.moveToFirst()) tvKgMes.setText(String.format(Locale.getDefault(), "%.1f", c3.getDouble(0)));
        c3.close();

        // 4. Desglose por tipos (Basado en la descripción por ahora, o puedes añadir columna TIPO a la BD)
        actualizarPesoPorTipo(db, userIdStr, "Mixto", tvKgMixto);
        actualizarPesoPorTipo(db, userIdStr, "Papel", tvKgPapel);
        actualizarPesoPorTipo(db, userIdStr, "Plástico", tvKgPlastico);
        actualizarPesoPorTipo(db, userIdStr, "Vidrio", tvKgVidrio);
        actualizarPesoPorTipo(db, userIdStr, "Orgánico", tvKgOrganico);
    }

    private void actualizarPesoPorTipo(SQLiteDatabase db, String userId, String tipo, TextView tv) {
        Cursor cursor = db.rawQuery("SELECT SUM(" + MySQLiteHelper.COLUMN_REG_PESO + ") FROM " + MySQLiteHelper.TABLE_REGISTROS + " WHERE " + MySQLiteHelper.COLUMN_REG_EMPLEADO_ID + "=? AND " + MySQLiteHelper.COLUMN_REG_DESCRIPCION + " LIKE ?", new String[]{userId, "%" + tipo + "%"});
        if (cursor.moveToFirst()) {
            tv.setText(String.format(Locale.getDefault(), "%.1f kg", cursor.getDouble(0)));
        }
        cursor.close();
    }
}
