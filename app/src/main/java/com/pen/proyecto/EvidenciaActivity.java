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

    private static final String API_KEY = "TU_API_KEY_AQUI";

    private EditText etDescripcion, etPesoEstimado;
    private Button btnFoto, btnGuardar;
    private GenerativeModelFutures model;
    private MySQLiteHelper dbHelper;
    private int loggedUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evidencia);

        dbHelper = new MySQLiteHelper(this);
        loggedUserId = getIntent().getIntExtra("USER_ID", -1);

        etDescripcion = findViewById(R.id.etDescripcion);
        etPesoEstimado = findViewById(R.id.etPesoEstimado);
        btnFoto = findViewById(R.id.btnFoto);
        btnGuardar = findViewById(R.id.btnGuardarRegistro);

        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash", API_KEY);
        model = GenerativeModelFutures.from(gm);

        btnFoto.setOnClickListener(v -> mostrarOpcionesImagen());
        btnGuardar.setOnClickListener(v -> guardarRegistroEnBD());
    }

    private void mostrarOpcionesImagen() {
        String[] opciones = {"Tomar Foto", "Elegir de Galería", "Cancelar"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccionar Evidencia");
        builder.setItems(opciones, (dialog, which) -> {
            if (opciones[which].equals("Tomar Foto")) abrirCamaraFoto();
            else if (opciones[which].equals("Elegir de Galería")) abrirGaleria();
            else dialog.dismiss();
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
            Bitmap imageBitmap = null;
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bundle extras = data.getExtras();
                imageBitmap = (Bitmap) extras.get("data");
            } else if (requestCode == REQUEST_IMAGE_GALLERY) {
                Uri selectedImage = data.getData();
                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (imageBitmap != null) {
                Toast.makeText(this, "Analizando con IA...", Toast.LENGTH_SHORT).show();
                analizarEvidenciaConIA(imageBitmap);
            }
        }
    }

    private void analizarEvidenciaConIA(Bitmap bitmap) {
        Content content = new Content.Builder()
                .addText("Actúa como un experto en gestión de residuos. Analiza esta imagen y proporciona: 1. El tipo de residuo (solo una palabra: Papel, Cartón, Plástico, Vidrio, Orgánico o Mixto). 2. Un peso estimado en kg (solo el número). Formato: TIPO: [palabra] PESO: [numero]")
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
                runOnUiThread(() -> Toast.makeText(EvidenciaActivity.this, "Error de IA", Toast.LENGTH_SHORT).show());
            }
        }, executor);
    }

    private void procesarRespuestaIA(String respuesta) {
        try {
            if (respuesta.contains("TIPO:") && respuesta.contains("PESO:")) {
                String tipo = respuesta.substring(respuesta.indexOf("TIPO:") + 5, respuesta.indexOf("PESO:")).trim();
                String peso = respuesta.substring(respuesta.indexOf("PESO:") + 5).trim();
                etDescripcion.setText(tipo); // Ahora solo el tipo (Papel, Cartón, etc.)
                etPesoEstimado.setText(peso);
            } else {
                etDescripcion.setText(respuesta);
            }
        } catch (Exception e) {
            etDescripcion.setText(respuesta);
        }
    }

    private void guardarRegistroEnBD() {
        String tipo = etDescripcion.getText().toString().trim();
        String pesoStr = etPesoEstimado.getText().toString().trim();

        if (tipo.isEmpty() || pesoStr.isEmpty()) {
            Toast.makeText(this, "Por favor completa los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        double peso = Double.parseDouble(pesoStr);
        String fecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String hora = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date()); // Hora actual

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_REG_DESCRIPCION, tipo);
        values.put(MySQLiteHelper.COLUMN_REG_PESO, peso);
        values.put(MySQLiteHelper.COLUMN_REG_FECHA, fecha);
        values.put(MySQLiteHelper.COLUMN_REG_HORA, hora); // Guardar hora
        values.put(MySQLiteHelper.COLUMN_REG_EMPLEADO_ID, loggedUserId);

        long id = db.insert(MySQLiteHelper.TABLE_REGISTROS, null, values);

        if (id != -1) {
            Toast.makeText(this, "✅ Registro guardado", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
        }
    }
}
