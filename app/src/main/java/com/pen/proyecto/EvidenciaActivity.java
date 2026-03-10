package com.pen.proyecto;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class EvidenciaActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_GALLERY = 2;

    // IMPORTANTE: Reemplaza con tu API Key de Google AI Studio
    private static final String API_KEY = "TU_API_KEY_AQUI";

    private EditText etDescripcion, etPesoEstimado;
    private Button btnFoto, btnGuardar;
    private GenerativeModelFutures model;
    private MySQLiteHelper dbHelper;
    private int loggedUserId;
    private Bitmap imagenCapturada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evidencia);

        // Inicializar base de datos
        dbHelper = new MySQLiteHelper(this);
        loggedUserId = getIntent().getIntExtra("USER_ID", -1);

        // Inicializar vistas
        etDescripcion = findViewById(R.id.etDescripcion);
        etPesoEstimado = findViewById(R.id.etPesoEstimado);
        btnFoto = findViewById(R.id.btnFoto);
        btnGuardar = findViewById(R.id.btnGuardarRegistro);

        // Inicializar modelo de IA (si hay API Key)
        if (!API_KEY.equals("TU_API_KEY_AQUI")) {
            GenerativeModel gm = new GenerativeModel("gemini-1.5-flash", API_KEY);
            model = GenerativeModelFutures.from(gm);
        }

        // Configurar listeners
        btnFoto.setOnClickListener(v -> mostrarOpcionesImagen());
        btnGuardar.setOnClickListener(v -> guardarRegistroEnBD());
    }

    private void mostrarOpcionesImagen() {
        String[] opciones = {"Tomar Foto", "Elegir de Galería", "Cancelar"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccionar Evidencia");
        builder.setItems(opciones, (dialog, which) -> {
            if (opciones[which].equals("Tomar Foto")) {
                abrirCamaraFoto();
            } else if (opciones[which].equals("Elegir de Galería")) {
                abrirGaleria();
            } else {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void abrirCamaraFoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(this, "No se puede abrir la cámara", Toast.LENGTH_SHORT).show();
        }
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == RESULT_OK && data != null) {
            imagenCapturada = null;
            
            try {
                if (requestCode == REQUEST_IMAGE_CAPTURE) {
                    // Foto de cámara
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        imagenCapturada = (Bitmap) extras.get("data");
                    }
                } else if (requestCode == REQUEST_IMAGE_GALLERY) {
                    // Imagen de galería
                    Uri selectedImage = data.getData();
                    if (selectedImage != null) {
                        imagenCapturada = MediaStore.Images.Media.getBitmap(
                            this.getContentResolver(), 
                            selectedImage
                        );
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
            }

            // Analizar con IA si hay imagen y API Key configurada
            if (imagenCapturada != null) {
                if (model != null) {
                    Toast.makeText(this, "Analizando con IA...", Toast.LENGTH_SHORT).show();
                    analizarEvidenciaConIA(imagenCapturada);
                } else {
                    Toast.makeText(this, "Imagen capturada (IA no configurada)", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void analizarEvidenciaConIA(Bitmap bitmap) {
        Content content = new Content.Builder()
                .addText("Actúa como un experto en gestión de residuos. Analiza esta imagen y proporciona: " +
                        "1. El tipo de residuo (solo una palabra: Papel, Cartón, Plástico, Vidrio, Orgánico o Mixto). " +
                        "2. Un peso estimado en kg (solo el número). " +
                        "Formato: TIPO: [palabra] PESO: [numero]")
                .addImage(bitmap)
                .build();

        Executor executor = Executors.newSingleThreadExecutor();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String text = result.getText();
                runOnUiThread(() -> procesarRespuestaIA(text));
            }

            @Override
            public void onFailure(Throwable t) {
                runOnUiThread(() -> 
                    Toast.makeText(EvidenciaActivity.this, 
                        "Error de IA: " + t.getMessage(), 
                        Toast.LENGTH_SHORT).show()
                );
            }
        }, executor);
    }

    private void procesarRespuestaIA(String respuesta) {
        try {
            if (respuesta.contains("TIPO:") && respuesta.contains("PESO:")) {
                // Extraer tipo
                int tipoStart = respuesta.indexOf("TIPO:") + 5;
                int tipoEnd = respuesta.indexOf("PESO:");
                String tipo = respuesta.substring(tipoStart, tipoEnd).trim();
                
                // Extraer peso
                String peso = respuesta.substring(respuesta.indexOf("PESO:") + 5).trim();
                
                // Actualizar campos
                etDescripcion.setText(tipo);
                etPesoEstimado.setText(peso);
            } else {
                // Si no tiene el formato esperado, mostrar la respuesta completa
                etDescripcion.setText(respuesta);
            }
        } catch (Exception e) {
            e.printStackTrace();
            etDescripcion.setText(respuesta);
        }
    }

    private void guardarRegistroEnBD() {
        // Validar usuario
        if (loggedUserId == -1) {
            Toast.makeText(this, "Error: Usuario no identificado", Toast.LENGTH_SHORT).show();
            return;
        }

        String tipo = etDescripcion.getText().toString().trim();
        String pesoStr = etPesoEstimado.getText().toString().trim();

        // Validar campos
        if (tipo.isEmpty()) {
            etDescripcion.setError("Ingresa una descripción");
            return;
        }

        if (pesoStr.isEmpty()) {
            etPesoEstimado.setError("Ingresa el peso estimado");
            return;
        }

        double peso;
        try {
            peso = Double.parseDouble(pesoStr);
        } catch (NumberFormatException e) {
            etPesoEstimado.setError("Ingresa un número válido");
            return;
        }

        // Obtener fecha y hora actual
        String fecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String hora = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());

        // Guardar en base de datos
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_REG_DESCRIPCION, tipo);
        values.put(MySQLiteHelper.COLUMN_REG_PESO, peso);
        values.put(MySQLiteHelper.COLUMN_REG_FECHA, fecha);
        values.put(MySQLiteHelper.COLUMN_REG_HORA, hora);
        values.put(MySQLiteHelper.COLUMN_REG_EMPLEADO_ID, loggedUserId);

        long id = db.insert(MySQLiteHelper.TABLE_REGISTROS, null, values);
        db.close();

        if (id != -1) {
            Toast.makeText(this, "✅ Registro guardado correctamente", Toast.LENGTH_LONG).show();
            
            // Volver a HomeActivity
            Intent intent = new Intent(EvidenciaActivity.this, HomeActivity.class);
            intent.putExtra("USER_ID", loggedUserId);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "❌ Error al guardar el registro", Toast.LENGTH_SHORT).show();
        }
    }
}