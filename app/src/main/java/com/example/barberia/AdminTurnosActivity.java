package com.example.barberia;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminTurnosActivity extends AppCompatActivity {

    private Button btnVolverAdminTurnos;
    private Button btnCargarTurnosAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_turnos);

        btnVolverAdminTurnos = findViewById(R.id.btnVolverAdminTurnos);
        btnCargarTurnosAdmin = findViewById(R.id.btnCargarTurnosAdmin);

        btnVolverAdminTurnos.setOnClickListener(view -> finish());

        /*
         * Esta pantalla queda preparada para que otro integrante implemente
         * la carga, modificación y cancelación de turnos desde Firestore.
         */
        btnCargarTurnosAdmin.setOnClickListener(view -> {
            Toast.makeText(
                    AdminTurnosActivity.this,
                    "Pendiente: cargar turnos desde Firebase.",
                    Toast.LENGTH_SHORT
            ).show();
        });
    }
}