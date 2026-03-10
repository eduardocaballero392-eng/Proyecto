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

    // Elementos de navegación
    private LinearLayout navInicio, navRegistros, navCalendario, navPerfil;
    private FrameLayout btnNuevo;
    
    // Elementos de estadísticas
    private TextView tvPesoHoy, tvPesoSemana, tvPesoMes, tvVerTodos;
    private LinearLayout containerRecientes;
    
    // Base de datos
    private MySQLiteHelper dbHelper;
    private int loggedUserId;
    private String loggedUserDni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Inicializar base de datos
        dbHelper = new MySQLiteHelper(this);

        // Recuperar datos del usuario logueado
        loggedUserId = getIntent().getIntExtra("USER_ID", -1);
        loggedUserDni = getIntent().getStringExtra("USUARIO");
        
        // Si viene de LoginActivity con EXTRA_USUARIO
        if (loggedUserDni == null || loggedUserDni.isEmpty()) {
            loggedUserDni = getIntent().getStringExtra(LoginActivity.EXTRA_USUARIO);
        }

        // Vincular vistas
        inicializarVistas();
        
        // Configurar listeners
        configurarListeners();
    }

    private void inicializarVistas() {
        // Navegación
        navInicio = findViewById(R.id.navInicio);
        navRegistros = findViewById(R.id.navRegistros);
        btnNuevo = findViewById(R.id.btnNuevo);
        navCalendario = findViewById(R.id.navCalendario);
        navPerfil = findViewById(R.id.navPerfil);

        // Estadísticas
        tvPesoHoy = findViewById(R.id.tvPesoHoy);
        tvPesoSemana = findViewById(R.id.tvPesoSemana);
        tvPesoMes = findViewById(R.id.tvPesoMes);
        tvVerTodos = findViewById(R.id.tvVerTodos);
        containerRecientes = findViewById(R.id.containerRecientes);
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
            intent.putExtra("USUARIO", loggedUserDni);
            startActivity(intent);
        });

        // Navegación - Registros
        navRegistros.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, RegistrosActivity.class);
            intent.putExtra("USER_ID", loggedUserId);
            startActivity(intent);
        });

        // Navegación - Calendario
        navCalendario.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CalendarioActivity.class);
            intent.putExtra("USER_ID", loggedUserId);
            startActivity(intent);
        });

        // Navegación - Perfil
        navPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, PerfilActivity.class);
            intent.putExtra("USER_ID", loggedUserId);
            intent.putExtra("USUARIO", loggedUserDni);
            startActivity(intent);
        });

        // Ver todos los registros
        tvVerTodos.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, RegistrosActivity.class);
            intent.putExtra("USER_ID", loggedUserId);
            startActivity(intent);
        });

        // Navegación - Inicio (ya estamos aquí)
        navInicio.setOnClickListener(v -> 
            Toast.makeText(this, "Ya estás en Inicio", Toast.LENGTH_SHORT).show()
        );
    }

    private void cargarEstadisticas() {
        // Validar que el usuario existe
        if (loggedUserId == -1) {
            tvPesoHoy.setText("0.0 kg");
            tvPesoSemana.setText("0.0 kg");
            tvPesoMes.setText("0.0 kg");
            return;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String hoy = sdf.format(new Date());

        try {
            // Peso de hoy
            double totalHoy = getSumaPeso(db, 
                MySQLiteHelper.COLUMN_REG_EMPLEADO_ID + " = ? AND fecha = ?", 
                new String[]{String.valueOf(loggedUserId), hoy});
            tvPesoHoy.setText(String.format(Locale.getDefault(), "%.1f kg", totalHoy));

            // Peso del mes (mes actual)
            String mesPrefijo = hoy.substring(0, 7) + "%";
            double totalMes = getSumaPeso(db, 
                MySQLiteHelper.COLUMN_REG_EMPLEADO_ID + " = ? AND fecha LIKE ?", 
                new String[]{String.valueOf(loggedUserId), mesPrefijo});
            tvPesoMes.setText(String.format(Locale.getDefault(), "%.1f kg", totalMes));

            // Peso de la última semana
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -7);
            String haceUnaSemana = sdf.format(cal.getTime());
            double totalSemana = getSumaPeso(db, 
                MySQLiteHelper.COLUMN_REG_EMPLEADO_ID + " = ? AND fecha >= ?", 
                new String[]{String.valueOf(loggedUserId), haceUnaSemana});
            tvPesoSemana.setText(String.format(Locale.getDefault(), "%.1f kg", totalSemana));
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    private double getSumaPeso(SQLiteDatabase db, String whereClause, String[] whereArgs) {
        double total = 0;
        Cursor cursor = null;
        
        try {
            cursor = db.rawQuery(
                "SELECT SUM(" + MySQLiteHelper.COLUMN_REG_PESO + ") FROM " + 
                MySQLiteHelper.TABLE_REGISTROS + " WHERE " + whereClause, 
                whereArgs
            );
            
            if (cursor.moveToFirst() && !cursor.isNull(0)) {
                total = cursor.getDouble(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }
        
        return total;
    }

    private void cargarRegistrosRecientes() {
        if (containerRecientes == null || loggedUserId == -1) return;
        
        containerRecientes.removeAllViews();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(
                MySQLiteHelper.TABLE_REGISTROS, 
                null, 
                MySQLiteHelper.COLUMN_REG_EMPLEADO_ID + " = ?", 
                new String[]{String.valueOf(loggedUserId)}, 
                null, 
                null, 
                MySQLiteHelper.COLUMN_REG_ID + " DESC", 
                "5"  // Mostrar 5 registros recientes
            );

            if (cursor.moveToFirst()) {
                do {
                    String tipo = cursor.getString(
                        cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_REG_DESCRIPCION)
                    );
                    String fecha = cursor.getString(
                        cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_REG_FECHA)
                    );
                    double peso = cursor.getDouble(
                        cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_REG_PESO)
                    );

                    View itemView = getLayoutInflater().inflate(
                        R.layout.item_registro_home, 
                        containerRecientes, 
                        false
                    );
                    
                    TextView tvTitulo = itemView.findViewById(R.id.tvTituloReciente);
                    TextView tvSubtitulo = itemView.findViewById(R.id.tvSubtituloReciente);

                    // Mostrar el tipo (categoría) y los kg
                    tvTitulo.setText(tipo + ": " + String.format(Locale.getDefault(), "%.2f kg", peso));
                    tvSubtitulo.setText("Fecha: " + fecha);

                    containerRecientes.addView(itemView);
                    
                } while (cursor.moveToNext());
            } else {
                // Mostrar mensaje si no hay registros
                TextView tvVacio = new TextView(this);
                tvVacio.setText("No hay registros recientes");
                tvVacio.setPadding(16, 16, 16, 16);
                tvVacio.setTextColor(getColor(android.R.color.darker_gray));
                containerRecientes.addView(tvVacio);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
    }
}