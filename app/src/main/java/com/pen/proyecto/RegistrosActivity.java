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

    private RecyclerView recyclerView;
    private RegistrosAdapter adapter;
    private ImageButton btnAtras;
    private BarChart barChart;
    private MySQLiteHelper dbHelper;

    private Spinner spinnerCategoria, spinnerMes;
    private List<RegistroItem> todosLosRegistros;
    private List<RegistroItem> registrosFiltrados;

    private String[] categorias = {"Todo", "Papel", "Cartón", "Plástico", "Vidrio", "Orgánico", "Metales", "Otros"};
    private String[] meses = {"Todo", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};

    private String categoriaSeleccionada = "Todo";
    private String mesSeleccionado = "Todo";
    private int loggedUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registros);

        dbHelper = new MySQLiteHelper(this);
        loggedUserId = getIntent().getIntExtra("USER_ID", -1);

        recyclerView = findViewById(R.id.recyclerView);
        btnAtras = findViewById(R.id.btnAtras);
        barChart = findViewById(R.id.barChart);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        spinnerMes = findViewById(R.id.spinnerMes);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        configurarSpinners();
        cargarRegistrosDesdeBD();
    }

    private void configurarSpinners() {
        ArrayAdapter<String> adapterCat = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categorias);
        adapterCat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adapterCat);

        ArrayAdapter<String> adapterMes = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, meses);
        adapterMes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMes.setAdapter(adapterMes);

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
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        Cursor cursor = db.query(MySQLiteHelper.TABLE_REGISTROS, null, 
                MySQLiteHelper.COLUMN_REG_EMPLEADO_ID + " = ?", 
                new String[]{String.valueOf(loggedUserId)}, null, null, MySQLiteHelper.COLUMN_REG_FECHA + " DESC");

        if (cursor.moveToFirst()) {
            do {
                String tipo = cursor.getString(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_REG_DESCRIPCION));
                double peso = cursor.getDouble(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_REG_PESO));
                String fecha = cursor.getString(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_REG_FECHA));
                String hora = cursor.getString(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_REG_HORA));
                String sucursal = cursor.getString(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_REG_SUCURSAL));
                
                String mesNombre = obtenerNombreMesDesdeFecha(fecha);
                todosLosRegistros.add(new RegistroItem(tipo, sucursal != null ? sucursal : "Sin sucursal", peso, mesNombre, fecha, hora));
            } while (cursor.moveToNext());
        }
        cursor.close();
        aplicarFiltros();
    }

    private String obtenerNombreMesDesdeFecha(String fecha) {
        if (fecha == null || !fecha.contains("-")) return "Todo";
        try {
            String[] parts = fecha.split("-");
            int mesInt = Integer.parseInt(parts[1]);
            if (mesInt >= 1 && mesInt <= 12) {
                return meses[mesInt];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Todo";
    }

    private void aplicarFiltros() {
        registrosFiltrados = new ArrayList<>();
        for (RegistroItem item : todosLosRegistros) {
            boolean cumpleCat = categoriaSeleccionada.equals("Todo") || item.getMaterial().equalsIgnoreCase(categoriaSeleccionada);
            boolean cumpleMes = mesSeleccionado.equals("Todo") || item.getMes().equals(mesSeleccionado);
            
            if (cumpleCat && cumpleMes) {
                registrosFiltrados.add(item);
            }
        }
        adapter = new RegistrosAdapter(registrosFiltrados);
        recyclerView.setAdapter(adapter);
        actualizarGrafico();
    }

    private void actualizarGrafico() {
        if (registrosFiltrados == null || registrosFiltrados.isEmpty()) {
            barChart.clear();
            return;
        }

        float[] pesoPorMes = new float[12];
        for (RegistroItem item : registrosFiltrados) {
            int idx = obtenerIndiceMes(item.getMes());
            if (idx >= 0) pesoPorMes[idx] += item.getPeso();
        }

        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        String[] shortMeses = {"Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};

        int count = 0;
        for (int i = 0; i < 12; i++) {
            if (mesSeleccionado.equals("Todo") || i == obtenerIndiceMes(mesSeleccionado)) {
                entries.add(new BarEntry(count++, pesoPorMes[i]));
                labels.add(shortMeses[i]);
            }
        }

        // CAMBIO: Ahora la leyenda muestra la categoría seleccionada
        String labelLeyenda = categoriaSeleccionada.equals("Todo") ? "Residuos Totales" : categoriaSeleccionada;
        BarDataSet dataSet = new BarDataSet(entries, labelLeyenda);
        
        dataSet.setColor(Color.parseColor("#2E7D32"));
        BarData barData = new BarData(dataSet);
        barChart.setData(barData);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setGranularity(1f);
        barChart.getDescription().setEnabled(false);
        barChart.invalidate();
    }

    private int obtenerIndiceMes(String nombreMes) {
        for (int i = 1; i < meses.length; i++) {
            if (meses[i].equalsIgnoreCase(nombreMes)) return i - 1;
        }
        return -1;
    }

    public static class RegistroItem {
        private String titulo, material, mes, fecha, hora;
        private double peso;
        public RegistroItem(String material, String sucursal, double peso, String mes, String fecha, String hora) {
            this.material = material; this.titulo = sucursal; this.peso = peso; this.mes = mes; this.fecha = fecha; this.hora = hora;
        }
        public String getTitulo() { return titulo; }
        public String getMaterial() { return material; }
        public double getPeso() { return peso; }
        public String getPesoStr() { return String.format(Locale.getDefault(), "%.1f kg", peso); }
        public String getMes() { return mes; }
        public String getFechaHora() { return fecha + " " + hora; }
    }

    public class RegistrosAdapter extends RecyclerView.Adapter<RegistrosAdapter.ViewHolder> {
        private List<RegistroItem> registros;
        public RegistrosAdapter(List<RegistroItem> registros) { this.registros = registros; }
        @Override
        public ViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_registro_calendario, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            RegistroItem item = registros.get(position);
            // CAMBIO: El título principal ahora es la categoría (Cartón, Papel, etc.)
            holder.tvTipo.setText(item.getMaterial()); 
            holder.tvPeso.setText(item.getPesoStr());
            // CAMBIO: La descripción ahora muestra la sucursal
            holder.tvDescripcion.setText("Sucursal: " + item.getTitulo());
            holder.tvHoraFoto.setText(item.getFechaHora());
        }
        @Override
        public int getItemCount() { return registros.size(); }
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
