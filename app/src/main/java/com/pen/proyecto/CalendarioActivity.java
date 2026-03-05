package com.pen.proyecto;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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

    // Datos de ejemplo
    private List<RegistroCalendario> registrosEjemplo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);

        // Inicializar vistas
        btnAtras = findViewById(R.id.btnAtras);
        btnMesAnterior = findViewById(R.id.btnMesAnterior);
        btnMesSiguiente = findViewById(R.id.btnMesSiguiente);
        tvMesAnio = findViewById(R.id.tvMesAnio);
        tvContadorRegistros = findViewById(R.id.tvContadorRegistros);
        gridCalendario = findViewById(R.id.gridCalendario);
        layoutRegistros = findViewById(R.id.layoutRegistros);

        // Inicializar calendario
        calendarioActual = Calendar.getInstance();
        calendarioActual.set(2026, Calendar.MARCH, 1); // Marzo 2026 como ejemplo

        // Cargar datos de ejemplo
        cargarRegistrosEjemplo();

        // Configurar listeners
        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnMesAnterior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarioActual.add(Calendar.MONTH, -1);
                actualizarCalendario();
            }
        });

        btnMesSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarioActual.add(Calendar.MONTH, 1);
                actualizarCalendario();
            }
        });

        // Mostrar calendario inicial
        actualizarCalendario();
    }

    private void cargarRegistrosEjemplo() {
        registrosEjemplo = new ArrayList<>();

        // Registro 1 - 15 de marzo
        registrosEjemplo.add(new RegistroCalendario(
                2026, 2, 15, // Marzo (0=Ene, 1=Feb, 2=Mar)
                "Mixto",
                "0 kg",
                "Puente Piedra, Lima, Lima Metropolitana, Lima, 15118, Perú",
                "03:28 p. m. - 1 foto",
                "",
                ""
        ));

        // Registro 2 - 15 de marzo
        registrosEjemplo.add(new RegistroCalendario(
                2026, 2, 15,
                "Papel/Cartón",
                "0 kg",
                "Puente Piedra, Lima, Lima Metropolitana, Lima, 15118, Perú",
                "",
                "Gran cantidad de folletos, volantes y publicidad política impresos en papel esparcidos por el suelo.",
                "Recoger y depositar en el contenedor azul para reciclaje. Asegurarse de que el papel no esté excesivamente contaminado con materia orgánica humedad para facilitar el proceso."
        ));

        // Registro 3 - 20 de marzo
        registrosEjemplo.add(new RegistroCalendario(
                2026, 2, 20,
                "Plástico",
                "2.5 kg",
                "Av. San Juan, Puente Piedra",
                "10:15 a. m. - 2 fotos",
                "Botellas PET y envases",
                ""
        ));

        // Registro 4 - 20 de marzo
        registrosEjemplo.add(new RegistroCalendario(
                2026, 2, 20,
                "Vidrio",
                "3.0 kg",
                "Restaurante El Buen Sabor",
                "12:30 p. m. - 1 foto",
                "Botellas de vidrio",
                ""
        ));

        // Registro 5 - 25 de marzo
        registrosEjemplo.add(new RegistroCalendario(
                2026, 2, 25,
                "Orgánico",
                "4.2 kg",
                "Mercado Central",
                "09:45 a. m. - 3 fotos",
                "Residuos de frutas y verduras",
                ""
        ));
    }

    private void actualizarCalendario() {
        // Actualizar título del mes
        tvMesAnio.setText(formatoMes.format(calendarioActual.getTime()));

        // Limpiar grid
        gridCalendario.removeAllViews();

        // Obtener información del mes
        int año = calendarioActual.get(Calendar.YEAR);
        int mes = calendarioActual.get(Calendar.MONTH);

        Calendar temp = Calendar.getInstance();
        temp.set(año, mes, 1);
        int primerDiaSemana = temp.get(Calendar.DAY_OF_WEEK); // 1=domingo
        int diasEnMes = temp.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Ajustar: en nuestra grid, domingo es posición 0
        int primerDia = (primerDiaSemana == Calendar.SUNDAY) ? 0 : primerDiaSemana - 1;

        // Crear celdas vacías para días anteriores
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = 120;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED);

        for (int i = 0; i < primerDia; i++) {
            TextView celdaVacia = new TextView(this);
            celdaVacia.setLayoutParams(params);
            celdaVacia.setGravity(Gravity.CENTER);
            gridCalendario.addView(celdaVacia);
        }

        // Crear celdas para cada día del mes
        for (int dia = 1; dia <= diasEnMes; dia++) {
            TextView celdaDia = new TextView(this);
            GridLayout.LayoutParams paramsDia = new GridLayout.LayoutParams();
            paramsDia.width = 0;
            paramsDia.height = 120;
            paramsDia.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            paramsDia.rowSpec = GridLayout.spec(GridLayout.UNDEFINED);
            celdaDia.setLayoutParams(paramsDia);

            celdaDia.setGravity(Gravity.CENTER);
            celdaDia.setText(String.valueOf(dia));
            celdaDia.setTextSize(16);
            celdaDia.setPadding(8, 8, 8, 8);

            // Verificar si hay registros para este día
            boolean tieneRegistros = tieneRegistrosEnFecha(año, mes, dia);
            if (tieneRegistros) {
                celdaDia.setBackground(ContextCompat.getDrawable(this, R.drawable.dia_con_registro));
                celdaDia.setTextColor(Color.WHITE);
            } else {
                celdaDia.setBackground(ContextCompat.getDrawable(this, R.drawable.dia_sin_registro));
                celdaDia.setTextColor(Color.BLACK);
            }

            final int diaSeleccionado = dia;
            celdaDia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mostrarRegistrosDelDia(año, mes, diaSeleccionado);
                }
            });

            gridCalendario.addView(celdaDia);
        }
    }

    private boolean tieneRegistrosEnFecha(int año, int mes, int dia) {
        for (RegistroCalendario registro : registrosEjemplo) {
            if (registro.ano == año && registro.mes == mes && registro.dia == dia) {
                return true;
            }
        }
        return false;
    }

    private void mostrarRegistrosDelDia(int año, int mes, int dia) {
        // Limpiar registros anteriores
        layoutRegistros.removeAllViews();

        List<RegistroCalendario> registrosDelDia = new ArrayList<>();
        for (RegistroCalendario registro : registrosEjemplo) {
            if (registro.ano == año && registro.mes == mes && registro.dia == dia) {
                registrosDelDia.add(registro);
            }
        }

        // Actualizar contador
        tvContadorRegistros.setText(registrosDelDia.size() + " registros");

        // Mostrar cada registro
        for (RegistroCalendario registro : registrosDelDia) {
            View itemView = getLayoutInflater().inflate(R.layout.item_registro_calendario, null);

            TextView tvTipo = itemView.findViewById(R.id.tvTipo);
            TextView tvPeso = itemView.findViewById(R.id.tvPeso);
            TextView tvUbicacion = itemView.findViewById(R.id.tvUbicacion);
            TextView tvHoraFoto = itemView.findViewById(R.id.tvHoraFoto);
            TextView tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            TextView tvRecomendacion = itemView.findViewById(R.id.tvRecomendacion);

            tvTipo.setText(registro.tipo);
            tvPeso.setText(registro.peso);
            tvUbicacion.setText(registro.ubicacion);

            if (registro.horaFoto.isEmpty()) {
                tvHoraFoto.setVisibility(View.GONE);
            } else {
                tvHoraFoto.setVisibility(View.VISIBLE);
                tvHoraFoto.setText(registro.horaFoto);
            }

            if (registro.descripcion.isEmpty()) {
                tvDescripcion.setVisibility(View.GONE);
            } else {
                tvDescripcion.setVisibility(View.VISIBLE);
                tvDescripcion.setText(registro.descripcion);
            }

            if (registro.recomendacion.isEmpty()) {
                tvRecomendacion.setVisibility(View.GONE);
            } else {
                tvRecomendacion.setVisibility(View.VISIBLE);
                tvRecomendacion.setText(registro.recomendacion);
            }

            layoutRegistros.addView(itemView);
        }
    }

    // Clase modelo para registros del calendario
    private static class RegistroCalendario {
        int ano, mes, dia;
        String tipo, peso, ubicacion, horaFoto, descripcion, recomendacion;

        RegistroCalendario(int ano, int mes, int dia, String tipo, String peso,
                           String ubicacion, String horaFoto, String descripcion, String recomendacion) {
            this.ano = ano;
            this.mes = mes;
            this.dia = dia;
            this.tipo = tipo;
            this.peso = peso;
            this.ubicacion = ubicacion;
            this.horaFoto = horaFoto;
            this.descripcion = descripcion;
            this.recomendacion = recomendacion;
        }
    }
}