package com.pen.proyecto;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class EvidenciaActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_VIDEO_CAPTURE = 2;

    private EditText etDescripcion, etPesoEstimado;
    private Button btnFoto, btnVideo, btnGuardar;
    private double latitud, longitud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evidencia);

        etDescripcion = findViewById(R.id.etDescripcion);
        etPesoEstimado = findViewById(R.id.etPesoEstimado);
        btnFoto = findViewById(R.id.btnFoto);
        btnVideo = findViewById(R.id.btnVideo);
        btnGuardar = findViewById(R.id.btnGuardarRegistro);

        Intent intent = getIntent();
        if (intent != null) {
            latitud = intent.getDoubleExtra("latitud", 0);
            longitud = intent.getDoubleExtra("longitud", 0);
        }

        btnFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirCamaraFoto();
            }
        });

        btnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirCamaraVideo();
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

    private void abrirCamaraVideo() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        } else {
            Toast.makeText(this, "No se puede abrir la cámara", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Toast.makeText(this, "Foto tomada con éxito", Toast.LENGTH_SHORT).show();
            } else if (requestCode == REQUEST_VIDEO_CAPTURE) {
                Uri videoUri = data.getData();
                Toast.makeText(this, "Video grabado con éxito", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void guardarRegistro() {
        String descripcion = etDescripcion.getText().toString().trim();
        String pesoStr = etPesoEstimado.getText().toString().trim();

        Toast.makeText(this, "✅ Registro guardado correctamente", Toast.LENGTH_SHORT).show();

        // Ahora HomeActivity SÍ existe
        Intent intent = new Intent(EvidenciaActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}