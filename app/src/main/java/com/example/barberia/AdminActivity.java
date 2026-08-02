package com.example.barberia;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class AdminActivity extends AppCompatActivity {

    private Button btnAdminTurnos;
    private Button btnAdminServicios;
    private Button btnCerrarSesionAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        btnAdminTurnos = findViewById(R.id.btnAdminTurnos);
        btnAdminServicios = findViewById(R.id.btnAdminServicios);
        btnCerrarSesionAdmin = findViewById(R.id.btnCerrarSesionAdmin);

        btnAdminTurnos.setOnClickListener(view -> {
            Intent intent = new Intent(
                    AdminActivity.this,
                    AdminTurnosActivity.class
            );

            startActivity(intent);
        });

        btnAdminServicios.setOnClickListener(view -> {
            Intent intent = new Intent(
                    AdminActivity.this,
                    AdminServiciosActivity.class
            );

            startActivity(intent);
        });

        btnCerrarSesionAdmin.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(
                    AdminActivity.this,
                    LoginActivity.class
            );

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
            finish();
        });
    }
}