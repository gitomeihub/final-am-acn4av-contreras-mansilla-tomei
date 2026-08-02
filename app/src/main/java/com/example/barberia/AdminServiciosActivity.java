package com.example.barberia;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminServiciosActivity extends AppCompatActivity {

    private Button btnVolverAdminServicios;
    private Button btnAgregarServicioAdmin;

    private Button btnEditarCorte;
    private Button btnEditarCorteBarba;
    private Button btnEditarColor;
    private Button btnEditarCorteColor;
    private Button btnEditarCompleto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_servicios);

        btnVolverAdminServicios = findViewById(R.id.btnVolverAdminServicios);
        btnAgregarServicioAdmin = findViewById(R.id.btnAgregarServicioAdmin);

        btnEditarCorte = findViewById(R.id.btnEditarCorte);
        btnEditarCorteBarba = findViewById(R.id.btnEditarCorteBarba);
        btnEditarColor = findViewById(R.id.btnEditarColor);
        btnEditarCorteColor = findViewById(R.id.btnEditarCorteColor);
        btnEditarCompleto = findViewById(R.id.btnEditarCompleto);

        btnVolverAdminServicios.setOnClickListener(view -> finish());

        btnAgregarServicioAdmin.setOnClickListener(view -> {
            Toast.makeText(
                    AdminServiciosActivity.this,
                    "Pendiente: agregar nuevo servicio.",
                    Toast.LENGTH_SHORT
            ).show();
        });

        btnEditarCorte.setOnClickListener(view -> mostrarPendiente("Corte"));

        btnEditarCorteBarba.setOnClickListener(view -> mostrarPendiente("Corte + Barba"));

        btnEditarColor.setOnClickListener(view -> mostrarPendiente("Color"));

        btnEditarCorteColor.setOnClickListener(view -> mostrarPendiente("Corte + Color"));

        btnEditarCompleto.setOnClickListener(view -> mostrarPendiente("Corte + Barba + Color"));
    }

    private void mostrarPendiente(String servicio) {
        Toast.makeText(
                AdminServiciosActivity.this,
                "Pendiente: editar " + servicio,
                Toast.LENGTH_SHORT
        ).show();
    }
}