package com.pen.proyecto;

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

import java.util.ArrayList;
import java.util.List;

public class RegistrosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RegistrosAdapter adapter;
    private ImageButton btnAtras;

    // Spinners para filtros
    private Spinner spinnerCategoria, spinnerMes;

    // TextViews para estadísticas
    private TextView tvTotalRegistros, tvPesoTotal, tvPesoMes;

    // Datos completos
    private List<RegistroItem> todosLosRegistros;
    private List<RegistroItem> registrosFiltrados;

    // Arrays para filtros
    private String[] categorias = {"Todos", "Papel/Cartón", "Plástico", "Vidrio", "Orgánico", "Mixto"};
    private String[] meses = {"Todos", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};

    // Filtros seleccionados
    private String categoriaSeleccionada = "Todos";
    private String mesSeleccionado = "Todos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registros);

        // Inicializar vistas
        recyclerView = findViewById(R.id.recyclerView);
        btnAtras = findViewById(R.id.btnAtras);

        // Spinners
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        spinnerMes = findViewById(R.id.spinnerMes);

        // Estadísticas
        tvTotalRegistros = findViewById(R.id.tvTotalRegistros);
        tvPesoTotal = findViewById(R.id.tvPesoTotal);
        tvPesoMes = findViewById(R.id.tvPesoMes);

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
        float pesoTotal = 0;
        float pesoMes = 0;

        // Aplicar filtros a los datos
        for (RegistroItem item : todosLosRegistros) {
            boolean cumpleCategoria = categoriaSeleccionada.equals("Todos") ||
                    item.getTipo().equals(categoriaSeleccionada);
            boolean cumpleMes = mesSeleccionado.equals("Todos") ||
                    item.getMes().equals(mesSeleccionado);

            if (cumpleCategoria && cumpleMes) {
                registrosFiltrados.add(item);

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