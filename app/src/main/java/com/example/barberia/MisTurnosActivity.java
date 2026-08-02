package com.example.barberia;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MisTurnosActivity extends AppCompatActivity {

    private LinearLayout layoutEstadoVacio;
    private LinearLayout layoutTurnosDinamico;
    private TextView txtTituloListaTurnos;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private String uidUsuarioActual = "";
    private boolean esAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mis_turnos);

        Button btnVolverInicio =
                findViewById(R.id.btnVolverInicioMisTurnos);

        layoutEstadoVacio =
                findViewById(R.id.layoutEstadoVacio);

        layoutTurnosDinamico =
                findViewById(R.id.layoutTurnosDinamico);

        txtTituloListaTurnos =
                findViewById(R.id.txtTituloListaTurnos);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        verificarRolYCargarTurnos();

        btnVolverInicio.setOnClickListener(v -> {
            Intent intentInicio;

            if (esAdmin) {
                intentInicio = new Intent(
                        MisTurnosActivity.this,
                        AdminActivity.class
                );
            } else {
                intentInicio = new Intent(
                        MisTurnosActivity.this,
                        InicioActivity.class
                );
            }

            intentInicio.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_SINGLE_TOP
            );

            startActivity(intentInicio);
            finish();
        });
    }

    private void verificarRolYCargarTurnos() {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(
                    this,
                    "Tenés que iniciar sesión para ver tus turnos.",
                    Toast.LENGTH_LONG
            ).show();

            Intent intentLogin = new Intent(
                    MisTurnosActivity.this,
                    LoginActivity.class
            );

            startActivity(intentLogin);
            finish();

            return;
        }

        uidUsuarioActual = auth.getCurrentUser().getUid();

        db.collection("usuarios")
                .document(uidUsuarioActual)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String rol = documentSnapshot.getString("rol");

                        esAdmin = "admin".equals(rol);

                        if (esAdmin) {
                            txtTituloListaTurnos.setText("TODOS LOS TURNOS");
                        } else {
                            txtTituloListaTurnos.setText("PRÓXIMAS RESERVAS");
                        }

                        cargarTurnosDesdeFirestore();
                    } else {
                        Toast.makeText(
                                this,
                                "No se encontró el perfil del usuario.",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(
                            this,
                            "No se pudo verificar el rol del usuario.",
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void cargarTurnosDesdeFirestore() {
        layoutTurnosDinamico.removeAllViews();

        if (esAdmin) {
            db.collection("turnos")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        int turnosMostrados = 0;

                        for (QueryDocumentSnapshot documento : queryDocumentSnapshots) {
                            String estado = obtenerString(documento, "estado", "reservado");

                            if ("cancelado".equals(estado)) {
                                continue;
                            }

                            agregarTarjetaDesdeDocumento(documento);
                            turnosMostrados++;
                        }

                        actualizarEstadoVisual(turnosMostrados);
                    })
                    .addOnFailureListener(e -> mostrarErrorCarga());

        } else {
            db.collection("turnos")
                    .whereEqualTo("usuarioId", uidUsuarioActual)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        int turnosMostrados = 0;

                        for (QueryDocumentSnapshot documento : queryDocumentSnapshots) {
                            String estado = obtenerString(documento, "estado", "reservado");

                            if ("cancelado".equals(estado)) {
                                continue;
                            }

                            agregarTarjetaDesdeDocumento(documento);
                            turnosMostrados++;
                        }

                        actualizarEstadoVisual(turnosMostrados);
                    })
                    .addOnFailureListener(e -> mostrarErrorCarga());
        }
    }

    private void agregarTarjetaDesdeDocumento(QueryDocumentSnapshot documento) {
        String documentoId = documento.getId();

        String nombreCliente = obtenerString(
                documento,
                "nombreCliente",
                "Cliente no informado"
        );

        String dniCliente = obtenerString(
                documento,
                "dniCliente",
                "-"
        );

        String telefonoCliente = obtenerString(
                documento,
                "telefonoCliente",
                "-"
        );

        String servicio = obtenerString(
                documento,
                "servicio",
                ""
        );

        String precio = obtenerString(
                documento,
                "precio",
                ""
        );

        String fecha = obtenerString(
                documento,
                "fecha",
                ""
        );

        String horario = obtenerString(
                documento,
                "horario",
                ""
        );

        String medioPago = obtenerString(
                documento,
                "medioPago",
                "No informado"
        );

        agregarTarjetaTurno(
                documentoId,
                nombreCliente,
                dniCliente,
                telefonoCliente,
                servicio,
                precio,
                fecha,
                horario,
                medioPago
        );
    }

    private void agregarTarjetaTurno(
            String documentoId,
            String nombreCliente,
            String dniCliente,
            String telefonoCliente,
            String servicio,
            String precio,
            String fecha,
            String horario,
            String medioPago
    ) {
        LinearLayout tarjetaTurno = new LinearLayout(this);

        tarjetaTurno.setOrientation(LinearLayout.VERTICAL);
        tarjetaTurno.setPadding(dp(20), dp(20), dp(20), dp(20));
        tarjetaTurno.setBackgroundColor(Color.parseColor("#2A2A2A"));

        LinearLayout.LayoutParams parametrosTarjeta =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        parametrosTarjeta.setMargins(0, 0, 0, dp(18));
        tarjetaTurno.setLayoutParams(parametrosTarjeta);

        TextView txtServicio = new TextView(this);

        txtServicio.setText(servicio);
        txtServicio.setTextColor(Color.WHITE);
        txtServicio.setTextSize(20);
        txtServicio.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        TextView txtCliente = new TextView(this);

        txtCliente.setText(
                getString(
                        R.string.detalle_cliente_turno,
                        nombreCliente,
                        dniCliente,
                        telefonoCliente
                )
        );

        txtCliente.setTextColor(Color.parseColor("#C8C8C8"));
        txtCliente.setTextSize(14);

        LinearLayout.LayoutParams parametrosCliente =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        parametrosCliente.setMargins(0, dp(12), 0, 0);
        txtCliente.setLayoutParams(parametrosCliente);

        TextView txtFechaHorario = new TextView(this);

        txtFechaHorario.setText(
                getString(
                        R.string.detalle_fecha_horario_turno,
                        fecha,
                        horario
                )
        );

        txtFechaHorario.setTextColor(Color.parseColor("#C8C8C8"));
        txtFechaHorario.setTextSize(15);

        LinearLayout.LayoutParams parametrosFecha =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        parametrosFecha.setMargins(0, dp(12), 0, 0);
        txtFechaHorario.setLayoutParams(parametrosFecha);

        TextView txtPrecio = new TextView(this);

        txtPrecio.setText(
                getString(
                        R.string.detalle_total_turno,
                        precio
                )
        );

        txtPrecio.setTextColor(Color.parseColor("#D4AF37"));
        txtPrecio.setTextSize(18);
        txtPrecio.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        LinearLayout.LayoutParams parametrosPrecio =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        parametrosPrecio.setMargins(0, dp(12), 0, 0);
        txtPrecio.setLayoutParams(parametrosPrecio);

        TextView txtMedioPago = new TextView(this);

        txtMedioPago.setText(
                getString(
                        R.string.detalle_pago_turno,
                        medioPago
                )
        );

        txtMedioPago.setTextColor(Color.parseColor("#C8C8C8"));
        txtMedioPago.setTextSize(14);

        LinearLayout.LayoutParams parametrosMedioPago =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        parametrosMedioPago.setMargins(0, dp(12), 0, 0);
        txtMedioPago.setLayoutParams(parametrosMedioPago);

        Button btnCancelarTurno = new Button(this);

        btnCancelarTurno.setText(R.string.boton_cancelar_turno);
        btnCancelarTurno.setAllCaps(false);
        btnCancelarTurno.setTextColor(Color.WHITE);
        btnCancelarTurno.setBackgroundColor(Color.parseColor("#444444"));

        LinearLayout.LayoutParams parametrosBoton =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        parametrosBoton.setMargins(0, dp(18), 0, 0);
        btnCancelarTurno.setLayoutParams(parametrosBoton);

        btnCancelarTurno.setOnClickListener(v -> cancelarTurno(documentoId));

        tarjetaTurno.addView(txtServicio);
        tarjetaTurno.addView(txtCliente);
        tarjetaTurno.addView(txtFechaHorario);
        tarjetaTurno.addView(txtPrecio);
        tarjetaTurno.addView(txtMedioPago);
        tarjetaTurno.addView(btnCancelarTurno);

        layoutTurnosDinamico.addView(tarjetaTurno);
    }

    private void cancelarTurno(String documentoId) {
        db.collection("turnos")
                .document(documentoId)
                .update("estado", "cancelado")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(
                            MisTurnosActivity.this,
                            "Turno cancelado correctamente.",
                            Toast.LENGTH_SHORT
                    ).show();

                    cargarTurnosDesdeFirestore();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(
                            MisTurnosActivity.this,
                            "No se pudo cancelar el turno.",
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void actualizarEstadoVisual(int turnosMostrados) {
        if (turnosMostrados == 0) {
            layoutEstadoVacio.setVisibility(View.VISIBLE);
            txtTituloListaTurnos.setVisibility(View.GONE);
        } else {
            layoutEstadoVacio.setVisibility(View.GONE);
            txtTituloListaTurnos.setVisibility(View.VISIBLE);
        }
    }

    private void mostrarErrorCarga() {
        layoutEstadoVacio.setVisibility(View.VISIBLE);
        txtTituloListaTurnos.setVisibility(View.GONE);

        Toast.makeText(
                this,
                "No se pudieron cargar los turnos.",
                Toast.LENGTH_LONG
        ).show();
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

    private int dp(int cantidadDp) {
        float densidad = getResources()
                .getDisplayMetrics()
                .density;

        return (int) (cantidadDp * densidad);
    }
}