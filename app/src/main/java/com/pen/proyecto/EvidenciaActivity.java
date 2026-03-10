package com.pen.proyecto;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EvidenciaActivity extends AppCompatActivity {

    private EditText etPesoEstimado, etSucursal, etComentarios;
    private Spinner spinnerTipoResiduo;
    private TextView tvFechaHora, btnVolver;
    private Button btnGuardar;
    private MySQLiteHelper dbHelper;
    private int loggedUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evidencia);

        dbHelper = new MySQLiteHelper(this);
        loggedUserId = getIntent().getIntExtra("USER_ID", -1);

        spinnerTipoResiduo = findViewById(R.id.spinnerTipoResiduo);
        etPesoEstimado = findViewById(R.id.etPesoEstimado);
        etSucursal = findViewById(R.id.etSucursal);
        etComentarios = findViewById(R.id.etComentarios);
        tvFechaHora = findViewById(R.id.tvFechaHora);
        btnVolver = findViewById(R.id.btnVolver);
        btnGuardar = findViewById(R.id.btnGuardarRegistro);

        configurarSpinner();
        configurarFechaHora();

        btnVolver.setOnClickListener(v -> finish());
        btnGuardar.setOnClickListener(v -> guardarRegistroEnBD());
    }

    private void configurarSpinner() {
        String[] tipos = {"Seleccionar...", "Papel", "Cartón", "Plástico", "Vidrio", "Orgánico", "Metales", "Otros"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tipos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoResiduo.setAdapter(adapter);
    }

    private void configurarFechaHora() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        tvFechaHora.setText(sdf.format(new Date()));
    }

    private void guardarRegistroEnBD() {
        String tipo = spinnerTipoResiduo.getSelectedItem().toString();
        String pesoStr = etPesoEstimado.getText().toString().trim();
        String sucursal = etSucursal.getText().toString().trim();
        String comentario = etComentarios.getText().toString().trim();

        if (tipo.equals("Seleccionar...") || pesoStr.isEmpty() || sucursal.isEmpty()) {
            Toast.makeText(this, "Por favor completa los campos obligatorios (*)", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double peso = Double.parseDouble(pesoStr.replace(",", "."));
            String fecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            String hora = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(MySQLiteHelper.COLUMN_REG_DESCRIPCION, tipo);
            values.put(MySQLiteHelper.COLUMN_REG_PESO, peso);
            values.put(MySQLiteHelper.COLUMN_REG_FECHA, fecha);
            values.put(MySQLiteHelper.COLUMN_REG_HORA, hora);
            values.put(MySQLiteHelper.COLUMN_REG_SUCURSAL, sucursal);
            values.put(MySQLiteHelper.COLUMN_REG_COMENTARIO, comentario);
            values.put(MySQLiteHelper.COLUMN_REG_EMPLEADO_ID, loggedUserId);

            long id = db.insert(MySQLiteHelper.TABLE_REGISTROS, null, values);

            if (id != -1) {
                Toast.makeText(this, "✅ Registro guardado exitosamente", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error al guardar en la base de datos", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Ingrese una cantidad válida", Toast.LENGTH_SHORT).show();
        }
    }
}
