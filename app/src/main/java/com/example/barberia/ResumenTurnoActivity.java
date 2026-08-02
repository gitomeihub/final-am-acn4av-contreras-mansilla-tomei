package com.example.barberia;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class ResumenTurnoActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Vincula esta activity con activity_resumen_turno.xml.
        setContentView(R.layout.activity_resumen_turno);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Referencias a los datos del cliente.
        TextView txtNombreClienteResumen =
                findViewById(R.id.txtNombreClienteResumen);

        TextView txtDniClienteResumen =
                findViewById(R.id.txtDniClienteResumen);

        TextView txtTelefonoClienteResumen =
                findViewById(R.id.txtTelefonoClienteResumen);

        // Referencias a los datos de la reserva.
        TextView txtServicioResumen =
                findViewById(R.id.txtServicioResumen);

        TextView txtFechaResumen =
                findViewById(R.id.txtFechaResumen);

        TextView txtHorarioResumen =
                findViewById(R.id.txtHorarioResumen);

        TextView txtTotalResumen =
                findViewById(R.id.txtTotalResumen);

        // Botones de navegación y confirmación.
        Button btnVolverReserva =
                findViewById(R.id.btnVolverReserva);

        Button btnConfirmarReserva =
                findViewById(R.id.btnConfirmarReserva);

        // Grupo de opciones de pago.
        RadioGroup radioGroupMedioPago =
                findViewById(R.id.radioGroupMedioPago);

        /*
         * Recupera todos los datos enviados desde MainActivity:
         * cliente y detalle de la reserva.
         */
        Intent intentRecibido = getIntent();

        String nombreCliente =
                intentRecibido.getStringExtra("NOMBRE_CLIENTE");

        String dniCliente =
                intentRecibido.getStringExtra("DNI_CLIENTE");

        String telefonoCliente =
                intentRecibido.getStringExtra("TELEFONO_CLIENTE");

        String servicio =
                intentRecibido.getStringExtra("SERVICIO");

        String precio =
                intentRecibido.getStringExtra("PRECIO");

        String fecha =
                intentRecibido.getStringExtra("FECHA");

        String horario =
                intentRecibido.getStringExtra("HORARIO");

        /*
         * Valores de respaldo para evitar que la pantalla quede vacía
         * si se abre directamente desde Android Studio durante una prueba.
         */
        if (nombreCliente == null || nombreCliente.isEmpty()) {
            nombreCliente = getString(R.string.valor_cliente_ejemplo);
        }

        if (dniCliente == null || dniCliente.isEmpty()) {
            dniCliente = getString(R.string.valor_dni_ejemplo);
        }

        if (telefonoCliente == null || telefonoCliente.isEmpty()) {
            telefonoCliente = getString(R.string.valor_telefono_ejemplo);
        }

        if (servicio == null || servicio.isEmpty()) {
            servicio = getString(R.string.valor_servicio_ejemplo);
        }

        if (precio == null || precio.isEmpty()) {
            precio = getString(R.string.valor_total_ejemplo);
        }

        if (fecha == null || fecha.isEmpty()) {
            fecha = getString(R.string.valor_fecha_ejemplo);
        }

        if (horario == null || horario.isEmpty()) {
            horario = getString(R.string.valor_horario_ejemplo);
        }

        // Muestra todos los datos en la pantalla.
        txtNombreClienteResumen.setText(nombreCliente);
        txtDniClienteResumen.setText(dniCliente);
        txtTelefonoClienteResumen.setText(telefonoCliente);

        txtServicioResumen.setText(servicio);
        txtFechaResumen.setText(fecha);
        txtHorarioResumen.setText(horario);
        txtTotalResumen.setText(precio);

        /*
         * Copias finales para usar los valores dentro
         * del evento Confirmar reserva.
         */
        final String nombreFinal = nombreCliente;
        final String dniFinal = dniCliente;
        final String telefonoFinal = telefonoCliente;

        final String servicioFinal = servicio;
        final String precioFinal = precio;
        final String fechaFinal = fecha;
        final String horarioFinal = horario;

        // Regresa a Reserva para modificar las elecciones.
        btnVolverReserva.setOnClickListener(v -> finish());

        /*
         * Valida el método de pago, verifica si el horario está libre
         * y guarda el turno en Firestore.
         */
        btnConfirmarReserva.setOnClickListener(v -> {

            if (radioGroupMedioPago.getCheckedRadioButtonId() == -1) {
                Toast.makeText(
                        ResumenTurnoActivity.this,
                        getString(R.string.mensaje_error_medio_pago),
                        Toast.LENGTH_LONG
                ).show();

                return;
            }

            RadioButton radioPagoSeleccionado = findViewById(
                    radioGroupMedioPago.getCheckedRadioButtonId()
            );

            String medioPago = radioPagoSeleccionado
                    .getText()
                    .toString();

            /*
             * Evita que el usuario toque varias veces confirmar
             * mientras Firebase está procesando.
             */
            btnConfirmarReserva.setEnabled(false);
            btnConfirmarReserva.setText("Guardando...");

            guardarTurnoEnFirestore(
                    nombreFinal,
                    dniFinal,
                    telefonoFinal,
                    servicioFinal,
                    precioFinal,
                    fechaFinal,
                    horarioFinal,
                    medioPago,
                    btnConfirmarReserva
            );
        });
    }

    /*
     * Guarda un turno en Firestore.
     * Antes de guardar, verifica que no exista otro turno reservado
     * con la misma fecha y horario.
     */
    private void guardarTurnoEnFirestore(
            String nombreCliente,
            String dniCliente,
            String telefonoCliente,
            String servicio,
            String precio,
            String fecha,
            String horario,
            String medioPago,
            Button btnConfirmarReserva
    ) {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(
                    this,
                    "Tenés que iniciar sesión para reservar un turno.",
                    Toast.LENGTH_LONG
            ).show();

            Intent intentLogin = new Intent(
                    ResumenTurnoActivity.this,
                    LoginActivity.class
            );

            startActivity(intentLogin);
            finish();

            return;
        }

        String usuarioId = auth.getCurrentUser().getUid();

        /*
         * Primero busca turnos de la misma fecha.
         * Después compara horario y estado.
         * Si encuentra uno reservado con ese horario, no permite guardar.
         */
        db.collection("turnos")
                .whereEqualTo("fecha", fecha)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    boolean horarioOcupado = false;

                    for (QueryDocumentSnapshot documento : queryDocumentSnapshots) {
                        String horarioExistente = documento.getString("horario");
                        String estadoExistente = documento.getString("estado");

                        if (horario.equals(horarioExistente)
                                && "reservado".equals(estadoExistente)) {
                            horarioOcupado = true;
                            break;
                        }
                    }

                    if (horarioOcupado) {
                        Toast.makeText(
                                ResumenTurnoActivity.this,
                                "Ese horario ya está reservado. Volvé a elegir otro.",
                                Toast.LENGTH_LONG
                        ).show();

                        btnConfirmarReserva.setEnabled(true);
                        btnConfirmarReserva.setText("Confirmar reserva");

                        /*
                         * Vuelve automáticamente a la pantalla anterior
                         * para que el cliente pueda elegir otra fecha u horario.
                         */
                        btnConfirmarReserva.postDelayed(() -> {
                            finish();
                        }, 1800);

                        return;
                    }

                    Map<String, Object> nuevoTurno = new HashMap<>();

                    nuevoTurno.put("usuarioId", usuarioId);
                    nuevoTurno.put("nombreCliente", nombreCliente);
                    nuevoTurno.put("dniCliente", dniCliente);
                    nuevoTurno.put("telefonoCliente", telefonoCliente);

                    nuevoTurno.put("servicio", servicio);
                    nuevoTurno.put("precio", precio);
                    nuevoTurno.put("fecha", fecha);
                    nuevoTurno.put("horario", horario);

                    nuevoTurno.put("medioPago", medioPago);
                    nuevoTurno.put("estado", "reservado");
                    nuevoTurno.put("fechaCreacion", FieldValue.serverTimestamp());

                    db.collection("turnos")
                            .add(nuevoTurno)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(
                                        ResumenTurnoActivity.this,
                                        getString(R.string.mensaje_turno_confirmado),
                                        Toast.LENGTH_SHORT
                                ).show();

                                Intent intentMisTurnos = new Intent(
                                        ResumenTurnoActivity.this,
                                        MisTurnosActivity.class
                                );

                                startActivity(intentMisTurnos);
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(
                                        ResumenTurnoActivity.this,
                                        "No se pudo guardar el turno en Firebase.",
                                        Toast.LENGTH_LONG
                                ).show();

                                btnConfirmarReserva.setEnabled(true);
                                btnConfirmarReserva.setText("Confirmar reserva");
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(
                            ResumenTurnoActivity.this,
                            "No se pudo verificar si el horario estaba disponible.",
                            Toast.LENGTH_LONG
                    ).show();

                    btnConfirmarReserva.setEnabled(true);
                    btnConfirmarReserva.setText("Confirmar reserva");
                });
    }
}