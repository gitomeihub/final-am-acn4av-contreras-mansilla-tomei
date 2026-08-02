package com.example.barberia;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmailLogin;
    private EditText etPasswordLogin;
    private Button btnLogin;
    private Button btnCrearCuenta;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmailLogin = findViewById(R.id.etEmailLogin);
        etPasswordLogin = findViewById(R.id.etPasswordLogin);
        btnLogin = findViewById(R.id.btnLogin);
        btnCrearCuenta = findViewById(R.id.btnCrearCuenta);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnLogin.setOnClickListener(v -> iniciarSesion());

        btnCrearCuenta.setOnClickListener(v -> registrarCliente());
    }

    private void iniciarSesion() {
        String email = etEmailLogin.getText().toString().trim();
        String password = etPasswordLogin.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completá email y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = auth.getCurrentUser().getUid();

                    db.collection("usuarios")
                            .document(uid)
                            .get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    String rol = documentSnapshot.getString("rol");

                                    if ("admin".equals(rol)) {
                                        Toast.makeText(this, "Bienvenido admin", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                                        startActivity(intent);
                                        finish();

                                    } else {
                                        Toast.makeText(this, "Bienvenido cliente", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(LoginActivity.this, InicioActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }

                                } else {
                                    Toast.makeText(this, "No se encontraron datos del usuario", Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error al leer el rol del usuario", Toast.LENGTH_LONG).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Email o contraseña incorrectos", Toast.LENGTH_LONG).show();
                });
    }

    private void registrarCliente() {
        String email = etEmailLogin.getText().toString().trim();
        String password = etPasswordLogin.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completá email y contraseña para registrarte", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = auth.getCurrentUser().getUid();

                    Map<String, Object> usuario = new HashMap<>();
                    usuario.put("nombre", "Cliente");
                    usuario.put("email", email);
                    usuario.put("rol", "cliente");

                    db.collection("usuarios")
                            .document(uid)
                            .set(usuario)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Cuenta creada correctamente", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(LoginActivity.this, InicioActivity.class);
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error al guardar datos del usuario", Toast.LENGTH_LONG).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "No se pudo crear la cuenta: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}