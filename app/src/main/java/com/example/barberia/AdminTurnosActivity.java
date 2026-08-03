package com.example.barberia;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class AdminTurnosActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    private Button btnVolverAdminTurnos;
    private Button btnActualizarTurnosAdmin;
    private LinearLayout layoutTurnosAdminDinamico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_turnos);

        db = FirebaseFirestore.getInstance();

        btnVolverAdminTurnos = findViewById(R.id.btnVolverAdminTurnos);
        btnActualizarTurnosAdmin = findViewById(R.id.btnActualizarTurnosAdmin);
        layoutTurnosAdminDinamico = findViewById(R.id.layoutTurnosAdminDinamico);

        btnVolverAdminTurnos.setOnClickListener(view -> finish());
        btnActualizarTurnosAdmin.setOnClickListener(view -> cargarTurnos());

        cargarTurnos();
    }

    private void cargarTurnos() {
        layoutTurnosAdminDinamico.removeAllViews();

        db.collection("turnos")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        mostrarMensajeVacio();
                        return;
                    }

                    for (QueryDocumentSnapshot documento : queryDocumentSnapshots) {
                        agregarTarjetaTurno(documento);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(
                            AdminTurnosActivity.this,
                            "No se pudieron cargar los turnos.",
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void mostrarMensajeVacio() {
        TextView txtVacio = new TextView(this);
        txtVacio.setText("No hay turnos cargados.");
        txtVacio.setTextColor(Color.WHITE);
        txtVacio.setTextSize(16);
        txtVacio.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);

        layoutTurnosAdminDinamico.addView(txtVacio);
    }

    private void agregarTarjetaTurno(QueryDocumentSnapshot documento) {
        String documentoId = documento.getId();

        String nombreCliente = obtenerString(documento, "nombreCliente", "Cliente no informado");
        String dniCliente = obtenerString(documento, "dniCliente", "-");
        String telefonoCliente = obtenerString(documento, "telefonoCliente", "-");
        String servicio = obtenerString(documento, "servicio", "Servicio no informado");
        String precio = obtenerString(documento, "precio", "-");
        String fecha = obtenerString(documento, "fecha", "");
        String horario = obtenerString(documento, "horario", "");
        String medioPago = obtenerString(documento, "medioPago", "No informado");
        String estado = obtenerString(documento, "estado", "reservado");

        LinearLayout tarjeta = new LinearLayout(this);
        tarjeta.setOrientation(LinearLayout.VERTICAL);
        tarjeta.setPadding(dp(18), dp(18), dp(18), dp(18));
        tarjeta.setBackgroundColor(Color.parseColor("#2A2A2A"));

        LinearLayout.LayoutParams paramsTarjeta = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        paramsTarjeta.setMargins(0, 0, 0, dp(18));
        tarjeta.setLayoutParams(paramsTarjeta);

        TextView txtServicio = new TextView(this);
        txtServicio.setText(servicio);
        txtServicio.setTextColor(Color.WHITE);
        txtServicio.setTextSize(20);
        txtServicio.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView txtCliente = new TextView(this);
        txtCliente.setText(
                "Cliente: " + nombreCliente +
                        "\nDNI: " + dniCliente +
                        "\nTeléfono: " + telefonoCliente
        );
        txtCliente.setTextColor(Color.parseColor("#C8C8C8"));
        txtCliente.setTextSize(14);

        TextView txtPrecio = new TextView(this);
        txtPrecio.setText("Total: " + precio);
        txtPrecio.setTextColor(Color.parseColor("#D4AF37"));
        txtPrecio.setTextSize(16);
        txtPrecio.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView txtPagoEstado = new TextView(this);
        txtPagoEstado.setText(
                "Pago: " + medioPago +
                        "\nEstado: " + estado
        );
        txtPagoEstado.setTextColor(Color.parseColor("#C8C8C8"));
        txtPagoEstado.setTextSize(14);

        EditText etFecha = new EditText(this);
        etFecha.setText(fecha);
        etFecha.setHint("Fecha");
        etFecha.setTextColor(Color.WHITE);
        etFecha.setHintTextColor(Color.GRAY);
        etFecha.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#D4AF37")));

        EditText etHorario = new EditText(this);
        etHorario.setText(horario);
        etHorario.setHint("Horario");
        etHorario.setTextColor(Color.WHITE);
        etHorario.setHintTextColor(Color.GRAY);
        etHorario.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#D4AF37")));

        Button btnGuardarCambios = new Button(this);
        btnGuardarCambios.setText("Guardar cambios");
        btnGuardarCambios.setAllCaps(false);
        btnGuardarCambios.setTextColor(Color.BLACK);
        btnGuardarCambios.setBackgroundColor(Color.parseColor("#D4AF37"));

        Button btnCancelarTurno = new Button(this);
        btnCancelarTurno.setText("Cancelar turno");
        btnCancelarTurno.setAllCaps(false);
        btnCancelarTurno.setTextColor(Color.WHITE);
        btnCancelarTurno.setBackgroundColor(Color.parseColor("#8B0000"));

        Button btnMarcarReservado = new Button(this);
        btnMarcarReservado.setText("Marcar como reservado");
        btnMarcarReservado.setAllCaps(false);
        btnMarcarReservado.setTextColor(Color.WHITE);
        btnMarcarReservado.setBackgroundColor(Color.parseColor("#444444"));

        agregarMargenSuperior(txtCliente, 12);
        agregarMargenSuperior(txtPrecio, 12);
        agregarMargenSuperior(txtPagoEstado, 12);
        agregarMargenSuperior(etFecha, 12);
        agregarMargenSuperior(etHorario, 8);
        agregarMargenSuperior(btnGuardarCambios, 14);
        agregarMargenSuperior(btnCancelarTurno, 10);
        agregarMargenSuperior(btnMarcarReservado, 10);

        btnGuardarCambios.setOnClickListener(view -> {
            String nuevaFecha = etFecha.getText().toString().trim();
            String nuevoHorario = etHorario.getText().toString().trim();

            if (nuevaFecha.isEmpty() || nuevoHorario.isEmpty()) {
                Toast.makeText(
                        AdminTurnosActivity.this,
                        "Completá fecha y horario.",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            verificarDisponibilidadYActualizar(
                    documentoId,
                    nuevaFecha,
                    nuevoHorario
            );
        });

        btnCancelarTurno.setOnClickListener(view -> actualizarEstadoTurno(
                documentoId,
                "cancelado"
        ));

        btnMarcarReservado.setOnClickListener(view -> actualizarEstadoTurno(
                documentoId,
                "reservado"
        ));

        tarjeta.addView(txtServicio);
        tarjeta.addView(txtCliente);
        tarjeta.addView(txtPrecio);
        tarjeta.addView(txtPagoEstado);
        tarjeta.addView(etFecha);
        tarjeta.addView(etHorario);
        tarjeta.addView(btnGuardarCambios);
        tarjeta.addView(btnCancelarTurno);
        tarjeta.addView(btnMarcarReservado);

        layoutTurnosAdminDinamico.addView(tarjeta);
    }

    private void verificarDisponibilidadYActualizar(
            String documentoId,
            String nuevaFecha,
            String nuevoHorario
    ) {
        db.collection("turnos")
                .whereEqualTo("fecha", nuevaFecha)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    boolean horarioOcupado = false;

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String idActual = doc.getId();
                        String horarioExistente = doc.getString("horario");
                        String estadoExistente = doc.getString("estado");

                        if (!idActual.equals(documentoId)
                                && nuevoHorario.equals(horarioExistente)
                                && "reservado".equals(estadoExistente)) {
                            horarioOcupado = true;
                            break;
                        }
                    }

                    if (horarioOcupado) {
                        Toast.makeText(
                                AdminTurnosActivity.this,
                                "Ese horario ya está reservado.",
                                Toast.LENGTH_LONG
                        ).show();
                        return;
                    }

                    db.collection("turnos")
                            .document(documentoId)
                            .update(
                                    "fecha", nuevaFecha,
                                    "horario", nuevoHorario
                            )
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(
                                        AdminTurnosActivity.this,
                                        "Turno actualizado correctamente.",
                                        Toast.LENGTH_SHORT).show();
                                cargarTurnos();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(
                                        AdminTurnosActivity.this,
                                        "No se pudo actualizar el turno.",
                                        Toast.LENGTH_LONG
                                ).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(
                            AdminTurnosActivity.this,
                            "No se pudo verificar disponibilidad.",
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void actualizarEstadoTurno(String documentoId, String nuevoEstado) {
        db.collection("turnos")
                .document(documentoId)
                .update("estado", nuevoEstado)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(
                            AdminTurnosActivity.this,
                            "Estado actualizado: " + nuevoEstado,
                            Toast.LENGTH_SHORT
                    ).show();

                    cargarTurnos();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(
                            AdminTurnosActivity.this,
                            "No se pudo actualizar el estado.",
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private String obtenerString(
            QueryDocumentSnapshot documento,
            String campo,
            String valorPorDefecto
    ) {
        String valor = documento.getString(campo);

        if (valor == null || valor.isEmpty()) {
            return valorPorDefecto;
        }

        return valor;
    }

    private void agregarMargenSuperior(android.view.View vista, int margenDp) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, dp(margenDp), 0, 0);
        vista.setLayoutParams(params);
    }

    private int dp(int cantidadDp) {
        float densidad = getResources()
                .getDisplayMetrics()
                .density;

        return (int) (cantidadDp * densidad);
    }
}