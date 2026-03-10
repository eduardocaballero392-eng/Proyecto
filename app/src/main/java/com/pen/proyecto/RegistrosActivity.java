package com.pen.proyecto;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
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

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RegistrosActivity extends AppCompatActivity {

    // UI Elements
    private RecyclerView recyclerView;
    private RegistrosAdapter adapter;
    private ImageButton btnAtras;
    private BarChart barChart;
    private Spinner spinnerCategoria, spinnerMes;
    private TextView tvTotalRegistros, tvPesoTotal, tvPesoMes;

    // Database
    private MySQLiteHelper dbHelper;
    private int loggedUserId;

    // Data
    private List<RegistroItem> todosLosRegistros;
    private List<RegistroItem> registrosFiltrados;

    // Filter arrays
    private String[] categorias = {"Todos", "Papel", "Cartón", "Plástico", "Vidrio", "Orgánico", "Mixto"};
    private String[] meses = {"Todos", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};

    // Selected filters
    private String categoriaSeleccionada = "Todos";
    private String mesSeleccionado = "Todos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registros);

        // Initialize database
        dbHelper = new MySQLiteHelper(this);
        loggedUserId = getIntent().getIntExtra("USER_ID", -1);

        // Initialize views
        inicializarVistas();
        
        // Configure spinners
        configurarSpinners();
        
        // Load data
        cargarRegistrosDesdeBD();
        aplicarFiltros();

        // Back button
        btnAtras.setOnClickListener(v -> finish());
    }

    private void inicializarVistas() {
        recyclerView = findViewById(R.id.recyclerView);
        btnAtras = findViewById(R.id.btnAtras);
        barChart = findViewById(R.id.barChart);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        spinnerMes = findViewById(R.id.spinnerMes);
        tvTotalRegistros = findViewById(R.id.tvTotalRegistros);
        tvPesoTotal = findViewById(R.id.tvPesoTotal);
        tvPesoMes = findViewById(R.id.tvPesoMes);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void configurarSpinners() {
        // Categoría Spinner
        ArrayAdapter<String> adapterCat = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, categorias);
        adapterCat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adapterCat);

        // Mes Spinner
        ArrayAdapter<String> adapterMes = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, meses);
        adapterMes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMes.setAdapter(adapterMes);

        // Listener para ambos spinners
        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoriaSeleccionada = spinnerCategoria.getSelectedItem().toString();
                mesSeleccionado = spinnerMes.getSelectedItem().toString();
                aplicarFiltros();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        spinnerCategoria.setOnItemSelectedListener(listener);
        spinnerMes.setOnItemSelectedListener(listener);
    }

    private void cargarRegistrosDesdeBD() {
        todosLosRegistros = new ArrayList<>();
        
        // Si no hay usuario válido, salir
        if (loggedUserId == -1) {
            return;
        }

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
                MySQLiteHelper.COLUMN_REG_FECHA + " DESC"
            );

            if (cursor.moveToFirst()) {
                do {
                    String tipo = cursor.getString(
                        cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_REG_DESCRIPCION)
                    );
                    double peso = cursor.getDouble(
                        cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_REG_PESO)
                    );
                    String fecha = cursor.getString(
                        cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_REG_FECHA)
                    );
                    String hora = cursor.getString(
                        cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_REG_HORA)
                    );

                    String mesNombre = obtenerNombreMesDesdeFecha(fecha);
                    
                    todosLosRegistros.add(new RegistroItem(
                        "Residuo", 
                        tipo, 
                        peso, 
                        mesNombre, 
                        fecha, 
                        hora
                    ));
                    
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
    }

    private String obtenerNombreMesDesdeFecha(String fecha) {
        if (fecha == null || !fecha.contains("-")) return "Todos";
        try {
            String[] parts = fecha.split("-");
            int mesInt = Integer.parseInt(parts[1]);
            if (mesInt >= 1 && mesInt <= 12) {
                return meses[mesInt];
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return "Todos";
    }

    private void aplicarFiltros() {
        registrosFiltrados = new ArrayList<>();
        float pesoTotal = 0;
        float pesoMes = 0;

        for (RegistroItem item : todosLosRegistros) {
            boolean cumpleCategoria = categoriaSeleccionada.equals("Todos") ||
                    item.getMaterial().equalsIgnoreCase(categoriaSeleccionada);
            boolean cumpleMes = mesSeleccionado.equals("Todos") ||
                    item.getMes().equals(mesSeleccionado);

            if (cumpleCategoria && cumpleMes) {
                registrosFiltrados.add(item);

                // Calcular pesos para estadísticas
                pesoTotal += item.getPeso();

                if (item.getMes().equals(mesSeleccionado) || mesSeleccionado.equals("Todos")) {
                    pesoMes += item.getPeso();
                }
            }
        }

        // Actualizar estadísticas
        tvTotalRegistros.setText(String.valueOf(registrosFiltrados.size()));
        tvPesoTotal.setText(String.format(Locale.getDefault(), "%.1f kg", pesoTotal));
        tvPesoMes.setText(String.format(Locale.getDefault(), "%.1f kg", pesoMes));

        // Actualizar RecyclerView
        adapter = new RegistrosAdapter(registrosFiltrados);
        recyclerView.setAdapter(adapter);

        // Actualizar gráfico
        actualizarGrafico();
    }

    private void actualizarGrafico() {
        if (registrosFiltrados.isEmpty()) {
            barChart.clear();
            barChart.invalidate();
            return;
        }

        // Contar registros por mes
        int[] registrosPorMes = new int[12];
        for (RegistroItem item : registrosFiltrados) {
            int idx = obtenerIndiceMes(item.getMes());
            if (idx >= 0) {
                registrosPorMes[idx]++;
            }
        }

        // Preparar datos para el gráfico
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        String[] shortMeses = {"Ene", "Feb", "Mar", "Abr", "May", "Jun", 
                              "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};

        for (int i = 0; i < 12; i++) {
            if (registrosPorMes[i] > 0 || !mesSeleccionado.equals("Todos")) {
                if (mesSeleccionado.equals("Todos") || i == obtenerIndiceMes(mesSeleccionado)) {
                    entries.add(new BarEntry(labels.size(), registrosPorMes[i]));
                    labels.add(shortMeses[i]);
                }
            }
        }

        // Configurar dataset
        BarDataSet dataSet = new BarDataSet(entries, "Registros por mes");
        dataSet.setColor(Color.parseColor("#2E7D32"));
        dataSet.setValueTextColor(Color.BLACK);
        
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.5f);
        
        barChart.setData(barData);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setGranularity(1f);
        barChart.getDescription().setEnabled(false);
        barChart.animateY(1000);
        barChart.invalidate();
    }

    private int obtenerIndiceMes(String nombreMes) {
        for (int i = 1; i < meses.length; i++) {
            if (meses[i].equals(nombreMes)) {
                return i - 1; // Convertir a índice 0-11
            }
        }
        return -1;
    }

    // Modelo de datos
    public static class RegistroItem {
        private String titulo;
        private String material;
        private double peso;
        private String mes;
        private String fecha;
        private String hora;

        public RegistroItem(String titulo, String material, double peso, String mes, String fecha, String hora) {
            this.titulo = titulo;
            this.material = material;
            this.peso = peso;
            this.mes = mes;
            this.fecha = fecha;
            this.hora = hora;
        }

        public String getTitulo() { return titulo; }
        public String getMaterial() { return material; }
        public double getPeso() { return peso; }
        public String getPesoStr() { return String.format(Locale.getDefault(), "%.1f kg", peso); }
        public String getMes() { return mes; }
        public String getFechaHora() { return fecha + " " + hora; }
    }

    // Adaptador del RecyclerView
    public class RegistrosAdapter extends RecyclerView.Adapter<RegistrosAdapter.ViewHolder> {
        private List<RegistroItem> registros;

        public RegistrosAdapter(List<RegistroItem> registros) {
            this.registros = registros;
        }

        @Override
        public ViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_registro_calendario, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            RegistroItem item = registros.get(position);
            holder.tvTipo.setText(item.getTitulo());
            holder.tvPeso.setText(item.getPesoStr());
            holder.tvDescripcion.setText(item.getMaterial());
            holder.tvHoraFoto.setText(item.getFechaHora());
        }

        @Override
        public int getItemCount() { 
            return registros != null ? registros.size() : 0; 
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTipo, tvPeso, tvHoraFoto, tvDescripcion;

            public ViewHolder(View itemView) {
                super(itemView);
                tvTipo = itemView.findViewById(R.id.tvTipo);
                tvPeso = itemView.findViewById(R.id.tvPeso);
                tvHoraFoto = itemView.findViewById(R.id.tvHoraFoto);
                tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            }
        }
    }
}