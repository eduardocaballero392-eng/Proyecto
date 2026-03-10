package com.pen.proyecto;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
<<<<<<< HEAD

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
=======
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Button btnConfirmar;
    private LatLng ubicacionSeleccionada;
>>>>>>> 80f27eddca501b840cd41b7324c671f4c7462bf5

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

<<<<<<< HEAD
        Button btnConfirmar = findViewById(R.id.btnConfirmar);
=======
        btnConfirmar = findViewById(R.id.btnConfirmar);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
>>>>>>> 80f27eddca501b840cd41b7324c671f4c7462bf5

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
<<<<<<< HEAD
                Intent intent = new Intent(MainActivity.this, EvidenciaActivity.class);
                startActivity(intent);
            }
        });
    }
}
=======
                if (ubicacionSeleccionada != null) {
                    Intent intent = new Intent(MainActivity.this, EvidenciaActivity.class);
                    intent.putExtra("latitud", ubicacionSeleccionada.latitude);
                    intent.putExtra("longitud", ubicacionSeleccionada.longitude);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Haz clic en el mapa para seleccionar un punto", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        LatLng puentePiedra = new LatLng(-11.85, -77.0667);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(puentePiedra, 15f));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                ubicacionSeleccionada = latLng;
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).title("Punto de recolección"));
            }
        });

        ubicacionSeleccionada = puentePiedra;
        mMap.addMarker(new MarkerOptions().position(puentePiedra).title("Punto de recolección por defecto"));
    }
}
>>>>>>> 80f27eddca501b840cd41b7324c671f4c7462bf5
