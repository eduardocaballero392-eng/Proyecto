package com.pen.proyecto;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
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

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class EvidenciaActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    // TODO: Reemplaza con tu API KEY de Google AI Studio
    private static final String API_KEY = "TU_API_KEY_AQUI";

    private EditText etDescripcion, etPesoEstimado;
    private Button btnFoto, btnGuardar;
    private GenerativeModelFutures model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evidencia);

        etDescripcion = findViewById(R.id.etDescripcion);
        etPesoEstimado = findViewById(R.id.etPesoEstimado);
        btnFoto = findViewById(R.id.btnFoto);
        btnGuardar = findViewById(R.id.btnGuardarRegistro);

        // Inicializar el modelo de IA
        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash", API_KEY);
        model = GenerativeModelFutures.from(gm);

        btnFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirCamaraFoto();
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarRegistro();
            }
        });
    }

    private void abrirCamaraFoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(this, "No se puede abrir la cámara", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                if (imageBitmap != null) {
                    Toast.makeText(this, "Analizando con IA...", Toast.LENGTH_SHORT).show();
                    analizarEvidenciaConIA(imageBitmap);
                }
            }
        }
    }

    private void analizarEvidenciaConIA(Bitmap bitmap) {
        Content content = new Content.Builder()
                .addText("Actúa como un experto en gestión de residuos. " +
                        "Analiza esta imagen y proporciona: " +
                        "1. Una descripción detallada de los residuos. " +
                        "2. Un peso estimado en kilogramos (solo el número). " +
                        "Responde en formato: DESCRIPCION: [texto] PESO: [numero]")
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
                    Toast.makeText(EvidenciaActivity.this, "Error de IA: " + t.getMessage(), Toast.LENGTH_LONG).show()
                );
            }
        }, executor);
    }

    private void procesarRespuestaIA(String respuesta) {
        try {
            if (respuesta.contains("DESCRIPCION:") && respuesta.contains("PESO:")) {
                String descripcion = respuesta.substring(
                        respuesta.indexOf("DESCRIPCION:") + 12,
                        respuesta.indexOf("PESO:")
                ).trim();
                String peso = respuesta.substring(
                        respuesta.indexOf("PESO:") + 5
                ).trim();

                etDescripcion.setText(descripcion);
                etPesoEstimado.setText(peso);
                Toast.makeText(this, "IA: Análisis completado", Toast.LENGTH_SHORT).show();
            } else {
                etDescripcion.setText(respuesta);
            }
        } catch (Exception e) {
            etDescripcion.setText(respuesta);
        }
    }

    private void guardarRegistro() {
        Toast.makeText(this, "✅ Registro guardado correctamente", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(EvidenciaActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
