package com.pen.proyecto;

<<<<<<< HEAD
import android.graphics.Color;
=======
>>>>>>> 80f27eddca501b840cd41b7324c671f4c7462bf5
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

<<<<<<< HEAD
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.Calendar;
=======
import java.util.ArrayList;
>>>>>>> 80f27eddca501b840cd41b7324c671f4c7462bf5
import java.util.List;

public class RegistrosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RegistrosAdapter adapter;
    private ImageButton btnAtras;
<<<<<<< HEAD
    private BarChart barChart;
=======
>>>>>>> 80f27eddca501b840cd41b7324c671f4c7462bf5

    // Spinners para filtros
    private Spinner spinnerCategoria, spinnerMes;

<<<<<<< HEAD
=======
    // TextViews para estadísticas
    private TextView tvTotalRegistros, tvPesoTotal, tvPesoMes;

>>>>>>> 80f27eddca501b840cd41b7324c671f4c7462bf5
    // Datos completos
    private List<RegistroItem> todosLosRegistros;
    private List<RegistroItem> registrosFiltrados;

    // Arrays para filtros
<<<<<<< HEAD
    private String[] categorias = {"Todo", "Papel/Cartón", "Plástico", "Vidrio", "Orgánico", "Mixto"};
    private String[] meses = {"Todo", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};

    // Filtros seleccionados
    private String categoriaSeleccionada = "Todo";
    private String mesSeleccionado = "Todo";
=======
    private String[] categorias = {"Todos", "Papel/Cartón", "Plástico", "Vidrio", "Orgánico", "Mixto"};
    private String[] meses = {"Todos", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};

    // Filtros seleccionados
    private String categoriaSeleccionada = "Todos";
    private String mesSeleccionado = "Todos";
>>>>>>> 80f27eddca501b840cd41b7324c671f4c7462bf5

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registros);

        // Inicializar vistas
        recyclerView = findViewById(R.id.recyclerView);
        btnAtras = findViewById(R.id.btnAtras);
<<<<<<< HEAD
        barChart = findViewById(R.id.barChart);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        spinnerMes = findViewById(R.id.spinnerMes);

=======

        // Spinners
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        spinnerMes = findViewById(R.id.spinnerMes);

        // Estadísticas
        tvTotalRegistros = findViewById(R.id.tvTotalRegistros);
        tvPesoTotal = findViewById(R.id.tvPesoTotal);
        tvPesoMes = findViewById(R.id.tvPesoMes);

>>>>>>> 80f27eddca501b840cd41b7324c671f4c7462bf5
        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Configurar Spinners
        configurarSpinners();

        // Cargar datos de ejemplo
        cargarRegistrosEjemplo();

        // Aplicar filtros iniciales
        aplicarFiltros();

        // Botón atrás
        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void configurarSpinners() {
        // Adaptador para spinner de categorías
        ArrayAdapter<String> adapterCategoria = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, categorias);
        adapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adapterCategoria);

        // Adaptador para spinner de meses
        ArrayAdapter<String> adapterMes = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, meses);
        adapterMes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMes.setAdapter(adapterMes);

        // Listeners para cambios en los spinners
        spinnerCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoriaSeleccionada = categorias[position];
                aplicarFiltros();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerMes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mesSeleccionado = meses[position];
                aplicarFiltros();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void cargarRegistrosEjemplo() {
        todosLosRegistros = new ArrayList<>();

        // Registros con fechas simuladas (meses)
        // Enero
        todosLosRegistros.add(new RegistroItem("Papel/Cartón", "Puente Piedra", "2.5 kg", "Enero", "Folletos"));
        todosLosRegistros.add(new RegistroItem("Plástico", "Av. San Juan", "1.8 kg", "Enero", "Botellas"));

        // Febrero
        todosLosRegistros.add(new RegistroItem("Papel/Cartón", "Mercado", "3.2 kg", "Febrero", "Cajas"));
        todosLosRegistros.add(new RegistroItem("Vidrio", "Restaurante", "4.0 kg", "Febrero", "Botellas"));
        todosLosRegistros.add(new RegistroItem("Orgánico", "Mercado", "5.0 kg", "Febrero", "Residuos"));

        // Marzo
        todosLosRegistros.add(new RegistroItem("Plástico", "Parque", "2.2 kg", "Marzo", "Envases"));
        todosLosRegistros.add(new RegistroItem("Mixto", "Esquina", "3.5 kg", "Marzo", "Variado"));
        todosLosRegistros.add(new RegistroItem("Papel/Cartón", "Oficina", "1.5 kg", "Marzo", "Papel"));

        // Abril
        todosLosRegistros.add(new RegistroItem("Papel/Cartón", "Colegio", "4.2 kg", "Abril", "Libros"));
        todosLosRegistros.add(new RegistroItem("Plástico", "Tienda", "2.8 kg", "Abril", "Botellas"));
        todosLosRegistros.add(new RegistroItem("Orgánico", "Restaurante", "3.5 kg", "Abril", "Comida"));
        todosLosRegistros.add(new RegistroItem("Vidrio", "Bar", "2.0 kg", "Abril", "Botellas"));

        // Mayo
        todosLosRegistros.add(new RegistroItem("Papel/Cartón", "Empresa", "3.8 kg", "Mayo", "Documentos"));
        todosLosRegistros.add(new RegistroItem("Plástico", "Supermercado", "4.5 kg", "Mayo", "Envases"));
    }

    private void aplicarFiltros() {
        registrosFiltrados = new ArrayList<>();
<<<<<<< HEAD

        // Aplicar filtros a los datos
        for (RegistroItem item : todosLosRegistros) {
            boolean cumpleCategoria = categoriaSeleccionada.equals("Todo") ||
                    item.getTipo().equals(categoriaSeleccionada);
            boolean cumpleMes = mesSeleccionado.equals("Todo") ||
=======
        float pesoTotal = 0;
        float pesoMes = 0;

        // Aplicar filtros a los datos
        for (RegistroItem item : todosLosRegistros) {
            boolean cumpleCategoria = categoriaSeleccionada.equals("Todos") ||
                    item.getTipo().equals(categoriaSeleccionada);
            boolean cumpleMes = mesSeleccionado.equals("Todos") ||
>>>>>>> 80f27eddca501b840cd41b7324c671f4c7462bf5
                    item.getMes().equals(mesSeleccionado);

            if (cumpleCategoria && cumpleMes) {
                registrosFiltrados.add(item);
<<<<<<< HEAD
            }
        }

        // Actualizar RecyclerView
        adapter = new RegistrosAdapter(registrosFiltrados);
        recyclerView.setAdapter(adapter);

        // Actualizar gráfico
        actualizarGrafico();
    }

    private void actualizarGrafico() {
        // Si no hay registros filtrados, mostrar gráfico vacío
        if (registrosFiltrados.isEmpty()) {
            barChart.clear();
            barChart.invalidate();
            return;
        }

        // Contar registros por mes según los filtros actuales
        int[] registrosPorMes = new int[12]; // 0=Enero, 11=Diciembre

        for (RegistroItem item : registrosFiltrados) {
            int indiceMes = obtenerIndiceMes(item.getMes());
            if (indiceMes >= 0) {
                registrosPorMes[indiceMes]++;
            }
        }

        // Crear entradas para el gráfico (solo meses con datos)
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> mesesConDatos = new ArrayList<>();
        String[] nombresMeses = {"Ene", "Feb", "Mar", "Abr", "May", "Jun",
                "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};

        for (int i = 0; i < 12; i++) {
            if (registrosPorMes[i] > 0 || mesSeleccionado.equals("Todo")) {
                // Si el mes está seleccionado o es "Todo", mostrar todos los meses con datos
                if (mesSeleccionado.equals("Todo") && registrosPorMes[i] > 0) {
                    entries.add(new BarEntry(entries.size(), registrosPorMes[i]));
                    mesesConDatos.add(nombresMeses[i]);
                } else if (!mesSeleccionado.equals("Todo") && i == obtenerIndiceMes(mesSeleccionado)) {
                    // Si hay un mes específico seleccionado, mostrar solo ese mes
                    entries.add(new BarEntry(0, registrosPorMes[i]));
                    mesesConDatos.add(nombresMeses[i]);
                }
            }
        }

        // Si no hay datos para mostrar
        if (entries.isEmpty()) {
            barChart.clear();
            barChart.invalidate();
            return;
        }

        // Configurar DataSet
        BarDataSet dataSet = new BarDataSet(entries, "Registros");
        dataSet.setColors(
                Color.parseColor("#2E7D32"),  // Verde
                Color.parseColor("#2196F3"),  // Azul
                Color.parseColor("#9C27B0"),  // Morado
                Color.parseColor("#FF9800"),  // Naranja
                Color.parseColor("#757575"),  // Gris
                Color.parseColor("#F44336")   // Rojo
        );
        dataSet.setValueTextSize(12f);

        // Configurar datos del gráfico
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.5f);

        barChart.setData(barData);
        barChart.setFitBars(true);
        barChart.setDrawValueAboveBar(true);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);

        // Configurar eje X con los meses correspondientes
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(mesesConDatos));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        // Configurar eje Y
        barChart.getAxisLeft().setAxisMinimum(0f);
        barChart.getAxisRight().setEnabled(false);

        // Animar y actualizar
        barChart.animateY(800);
        barChart.invalidate();
    }

    private int obtenerIndiceMes(String nombreMes) {
        switch (nombreMes) {
            case "Enero": return 0;
            case "Febrero": return 1;
            case "Marzo": return 2;
            case "Abril": return 3;
            case "Mayo": return 4;
            case "Junio": return 5;
            case "Julio": return 6;
            case "Agosto": return 7;
            case "Septiembre": return 8;
            case "Octubre": return 9;
            case "Noviembre": return 10;
            case "Diciembre": return 11;
            default: return -1;
        }
=======

                // Calcular pesos
                float peso = Float.parseFloat(item.getPeso().replace(" kg", ""));
                pesoTotal += peso;

                if (item.getMes().equals(mesSeleccionado) || mesSeleccionado.equals("Todos")) {
                    pesoMes += peso;
                }
            }
        }

        // Actualizar estadísticas
        tvTotalRegistros.setText(String.valueOf(registrosFiltrados.size()));
        tvPesoTotal.setText(String.format("%.1f kg", pesoTotal));

        if (mesSeleccionado.equals("Todos")) {
            tvPesoMes.setText(String.format("%.1f kg", pesoMes));
        } else {
            tvPesoMes.setText(String.format("%.1f kg", pesoMes));
        }

        // Actualizar RecyclerView
        adapter = new RegistrosAdapter(registrosFiltrados);
        recyclerView.setAdapter(adapter);
>>>>>>> 80f27eddca501b840cd41b7324c671f4c7462bf5
    }

    // Clase modelo para los registros
    public static class RegistroItem {
        private String tipo;
        private String ubicacion;
        private String peso;
        private String mes;
        private String descripcion;

        public RegistroItem(String tipo, String ubicacion, String peso, String mes, String descripcion) {
            this.tipo = tipo;
            this.ubicacion = ubicacion;
            this.peso = peso;
            this.mes = mes;
            this.descripcion = descripcion;
        }

        public String getTipo() { return tipo; }
        public String getUbicacion() { return ubicacion; }
        public String getPeso() { return peso; }
        public String getMes() { return mes; }
        public String getDescripcion() { return descripcion; }
    }

    // Adaptador para el RecyclerView
    public class RegistrosAdapter extends RecyclerView.Adapter<RegistrosAdapter.ViewHolder> {

        private List<RegistroItem> registros;

        public RegistrosAdapter(List<RegistroItem> registros) {
            this.registros = registros;
        }

        @Override
        public ViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            android.view.View view = getLayoutInflater().inflate(R.layout.item_registro, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            RegistroItem item = registros.get(position);

            holder.tvTipo.setText(item.getTipo());
            holder.tvUbicacion.setText(item.getUbicacion());
            holder.tvPeso.setText(item.getPeso());
            holder.tvTiempo.setText(item.getMes());

            if (item.getDescripcion() == null || item.getDescripcion().isEmpty()) {
                holder.tvDescripcion.setVisibility(View.GONE);
            } else {
                holder.tvDescripcion.setVisibility(View.VISIBLE);
                holder.tvDescripcion.setText(item.getDescripcion());
            }
        }

        @Override
        public int getItemCount() {
            return registros.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTipo, tvUbicacion, tvPeso, tvTiempo, tvDescripcion;

            public ViewHolder(android.view.View itemView) {
                super(itemView);
                tvTipo = itemView.findViewById(R.id.tvTipo);
                tvUbicacion = itemView.findViewById(R.id.tvUbicacion);
                tvPeso = itemView.findViewById(R.id.tvPeso);
                tvTiempo = itemView.findViewById(R.id.tvTiempo);
                tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            }
        }
    }
}