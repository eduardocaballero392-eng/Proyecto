package com.pen.proyecto;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarioActivity extends AppCompatActivity {

    private ImageButton btnAtras, btnMesAnterior, btnMesSiguiente;
    private TextView tvMesAnio, tvContadorRegistros;
    private GridLayout gridCalendario;
    private LinearLayout layoutRegistros;

    private Calendar calendarioActual;
    private SimpleDateFormat formatoMes = new SimpleDateFormat("MMMM yyyy", new Locale("es", "ES"));
    private MySQLiteHelper dbHelper;
    private List<RegistroBD> registrosBD;
    private int loggedUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);

        dbHelper = new MySQLiteHelper(this);
        loggedUserId = getIntent().getIntExtra("USER_ID", -1);

        btnAtras = findViewById(R.id.btnAtras);
        btnMesAnterior = findViewById(R.id.btnMesAnterior);
        btnMesSiguiente = findViewById(R.id.btnMesSiguiente);
        tvMesAnio = findViewById(R.id.tvMesAnio);
        tvContadorRegistros = findViewById(R.id.tvContadorRegistros);
        gridCalendario = findViewById(R.id.gridCalendario);
        layoutRegistros = findViewById(R.id.layoutRegistros);

        calendarioActual = Calendar.getInstance();
        
        cargarRegistrosDesdeBD();

        btnAtras.setOnClickListener(v -> finish());
        btnMesAnterior.setOnClickListener(v -> {
            calendarioActual.add(Calendar.MONTH, -1);
            actualizarCalendario();
        });
        btnMesSiguiente.setOnClickListener(v -> {
            calendarioActual.add(Calendar.MONTH, 1);
            actualizarCalendario();
        });

        actualizarCalendario();
    }

    private void cargarRegistrosDesdeBD() {
        registrosBD = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        // Cargar solo registros del usuario logueado
        Cursor cursor = db.query(MySQLiteHelper.TABLE_REGISTROS, null, 
                MySQLiteHelper.COLUMN_REG_EMPLEADO_ID + " = ?", 
                new String[]{String.valueOf(loggedUserId)}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                String fecha = cursor.getString(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_REG_FECHA));
                String tipo = cursor.getString(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_REG_DESCRIPCION));
                double peso = cursor.getDouble(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_REG_PESO));
                String hora = cursor.getString(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_REG_HORA));
                
                String[] parts = fecha.split("-");
                int ano = Integer.parseInt(parts[0]);
                int mes = Integer.parseInt(parts[1]) - 1;
                int dia = Integer.parseInt(parts[2]);

                registrosBD.add(new RegistroBD(ano, mes, dia, tipo, peso + " kg", hora));
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void actualizarCalendario() {
        tvMesAnio.setText(formatoMes.format(calendarioActual.getTime()).toLowerCase());
        gridCalendario.removeAllViews();

        int año = calendarioActual.get(Calendar.YEAR);
        int mes = calendarioActual.get(Calendar.MONTH);

        Calendar temp = Calendar.getInstance();
        temp.set(año, mes, 1);
        int primerDiaSemana = temp.get(Calendar.DAY_OF_WEEK);
        int diasEnMes = temp.getActualMaximum(Calendar.DAY_OF_MONTH);
        int primerDia = (primerDiaSemana == Calendar.SUNDAY) ? 0 : primerDiaSemana - 1;

        for (int i = 0; i < primerDia; i++) {
            gridCalendario.addView(new TextView(this));
        }

        for (int dia = 1; dia <= diasEnMes; dia++) {
            TextView celdaDia = new TextView(this);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = 120;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            celdaDia.setLayoutParams(params);
            celdaDia.setGravity(Gravity.CENTER);
            celdaDia.setText(String.valueOf(dia));

            if (tieneRegistrosEnFecha(año, mes, dia)) {
                celdaDia.setBackgroundResource(R.drawable.circle_green); // Círculo verde para días con registros
                celdaDia.setTextColor(Color.WHITE);
            } else {
                celdaDia.setTextColor(Color.BLACK);
            }

            final int d = dia;
            celdaDia.setOnClickListener(v -> mostrarRegistrosDelDia(año, mes, d));
            gridCalendario.addView(celdaDia);
        }
    }

    private boolean tieneRegistrosEnFecha(int año, int mes, int dia) {
        for (RegistroBD r : registrosBD) {
            if (r.ano == año && r.mes == mes && r.dia == dia) return true;
        }
        return false;
    }

    private void mostrarRegistrosDelDia(int año, int mes, int dia) {
        layoutRegistros.removeAllViews();
        List<RegistroBD> delDia = new ArrayList<>();
        for (RegistroBD r : registrosBD) {
            if (r.ano == año && r.mes == mes && r.dia == dia) delDia.add(r);
        }

        tvContadorRegistros.setText(delDia.size() + " registros");
        for (RegistroBD r : delDia) {
            View v = getLayoutInflater().inflate(R.layout.item_registro_calendario, null);
            ((TextView)v.findViewById(R.id.tvTipo)).setText("Residuo");
            ((TextView)v.findViewById(R.id.tvPeso)).setText(r.peso);
            ((TextView)v.findViewById(R.id.tvDescripcion)).setText(r.tipoResiduo);
            ((TextView)v.findViewById(R.id.tvHoraFoto)).setText(r.hora + " - 1 foto");
            layoutRegistros.addView(v);
        }
    }

    private static class RegistroBD {
        int ano, mes, dia;
        String tipoResiduo, peso, hora;
        RegistroBD(int ano, int mes, int dia, String tipoResiduo, String peso, String hora) {
            this.ano = ano; this.mes = mes; this.dia = dia; this.tipoResiduo = tipoResiduo; this.peso = peso; this.hora = hora;
        }
    }
}
