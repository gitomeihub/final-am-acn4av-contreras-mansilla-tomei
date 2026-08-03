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
import java.util.HashMap;
import java.util.Map;

public class AdminServiciosActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private Button btnVolverAdminServicios;
    private Button btnAgregarServicioAdmin;
    private LinearLayout layoutServiciosAdminDinamico;
    private EditText etNombreNuevoServicio;
    private EditText etPrecioNuevoServicio;
    private EditText etDescripcionNuevoServicio;
    private EditText etImagenNuevoServicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_servicios);

        db = FirebaseFirestore.getInstance();
        btnVolverAdminServicios = findViewById(R.id.btnVolverAdminServicios);
        btnAgregarServicioAdmin = findViewById(R.id.btnAgregarServicioAdmin);
        layoutServiciosAdminDinamico = findViewById(R.id.layoutServiciosAdminDinamico);
        etNombreNuevoServicio = findViewById(R.id.etNombreNuevoServicio);
        etPrecioNuevoServicio = findViewById(R.id.etPrecioNuevoServicio);
        etDescripcionNuevoServicio = findViewById(R.id.etDescripcionNuevoServicio);
        etImagenNuevoServicio = findViewById(R.id.etImagenNuevoServicio);

        btnVolverAdminServicios.setOnClickListener(view -> finish());
        btnAgregarServicioAdmin.setOnClickListener(view -> agregarNuevoServicio());

        cargarServicios();
    }

    private void cargarServicios() {
        layoutServiciosAdminDinamico.removeAllViews();
        db.collection("servicios")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        mostrarMensajeSinServicios();
                        crearServiciosIniciales();
                        return;
                    }
                    for (QueryDocumentSnapshot documento : queryDocumentSnapshots) {
                        agregarTarjetaServicio(documento);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AdminServiciosActivity.this, "No se pudieron cargar los servicios.", Toast.LENGTH_LONG).show();
                });
    }

    private void mostrarMensajeSinServicios() {
        TextView txtMensaje = new TextView(this);
        txtMensaje.setText("No había servicios cargados. Se crearán servicios iniciales.");
        txtMensaje.setTextColor(Color.WHITE);
        txtMensaje.setTextSize(15);
        txtMensaje.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
        layoutServiciosAdminDinamico.addView(txtMensaje);
    }

    private void crearServiciosIniciales() {
        crearServicioBase("Corte", "$10.000", "Corte clásico masculino", "");
        crearServicioBase("Corte + Barba", "$14.000", "Corte y perfilado de barba", "");
        crearServicioBase("Color", "$15.000", "Servicio de coloración", "");
        crearServicioBase("Corte + Color", "$22.000", "Corte más coloración", "");
        crearServicioBase("Corte + Barba + Color", "$25.000", "Servicio completo de barbería", "");

        Toast.makeText(this, "Servicios iniciales creados. Tocá volver a entrar o recargar.", Toast.LENGTH_LONG).show();
    }

    private void crearServicioBase(String nombre, String precio, String descripcion, String imagenUrl) {
        Map<String, Object> servicio = new HashMap<>();
        servicio.put("nombre", nombre);
        servicio.put("precio", precio);
        servicio.put("descripcion", descripcion);
        servicio.put("imagenUrl", imagenUrl);
        servicio.put("activo", true);
        db.collection("servicios").add(servicio);
    }

    private void agregarNuevoServicio() {
        String nombre = etNombreNuevoServicio.getText().toString().trim();
        String precio = etPrecioNuevoServicio.getText().toString().trim();
        String descripcion = etDescripcionNuevoServicio.getText().toString().trim();
        String imagenUrl = etImagenNuevoServicio.getText().toString().trim();

        if (nombre.isEmpty() || precio.isEmpty()) {
            Toast.makeText(this, "Completá nombre y precio del servicio.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> nuevoServicio = new HashMap<>();
        nuevoServicio.put("nombre", nombre);
        nuevoServicio.put("precio", precio);
        nuevoServicio.put("descripcion", descripcion);
        nuevoServicio.put("imagenUrl", imagenUrl);
        nuevoServicio.put("activo", true);

        db.collection("servicios")
                .add(nuevoServicio)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(AdminServiciosActivity.this, "Servicio agregado correctamente.", Toast.LENGTH_SHORT).show();
                    etNombreNuevoServicio.setText("");
                    etPrecioNuevoServicio.setText("");
                    etDescripcionNuevoServicio.setText("");
                    etImagenNuevoServicio.setText("");
                    cargarServicios();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AdminServiciosActivity.this, "No se pudo agregar el servicio.", Toast.LENGTH_LONG).show();
                });
    }

    private void agregarTarjetaServicio(QueryDocumentSnapshot documento) {
        String documentold = documento.getId();
        String nombre = obtenerString(documento, "nombre", "");
        String precio = obtenerString(documento, "precio", "");
        String descripcion = obtenerString(documento, "descripcion", "");
        String imagenUrl = obtenerString(documento, "imagenUrl", "");
        Boolean activoBoolean = documento.getBoolean("activo");
        boolean activo = activoBoolean == null || activoBoolean;

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

        TextView txtTitulo = new TextView(this);
        txtTitulo.setText("Editar servicio");
        txtTitulo.setTextColor(Color.parseColor("#D4AF37"));
        txtTitulo.setTextSize(18);
        txtTitulo.setTypeface(null, android.graphics.Typeface.BOLD);

        EditText etNombre = crearEditText("Nombre", nombre);
        EditText etPrecio = crearEditText("Precio", precio);
        EditText etDescripcion = crearEditText("Descripción", descripcion);
        EditText etImagenUrl = crearEditText("URL de imagen", imagenUrl);

        TextView txtEstado = new TextView(this);
        txtEstado.setText(activo ? "Estado: activo" : "Estado: inactivo");
        txtEstado.setTextColor(Color.WHITE);
        txtEstado.setTextSize(14);

        Button btnGuardar = new Button(this);
        btnGuardar.setText("Guardar cambios");
        btnGuardar.setAllCaps(false);
        btnGuardar.setTextColor(Color.BLACK);
        btnGuardar.setBackgroundColor(Color.parseColor("#D4AF37"));

        Button btnCambiarEstado = new Button(this);
        btnCambiarEstado.setText(activo ? "Desactivar servicio" : "Activar servicio");
        btnCambiarEstado.setAllCaps(false);
        btnCambiarEstado.setTextColor(Color.WHITE);
        btnCambiarEstado.setBackgroundColor(Color.parseColor("#444444"));

        Button btnEliminar = new Button(this);
        btnEliminar.setText("Eliminar servicio");
        btnEliminar.setAllCaps(false);
        btnEliminar.setTextColor(Color.WHITE);
        btnEliminar.setBackgroundColor(Color.parseColor("#8B0000"));

        agregarMargenSuperior(etNombre, 12);
        agregarMargenSuperior(etPrecio, 8);
        agregarMargenSuperior(etDescripcion, 8);
        agregarMargenSuperior(etImagenUrl, 8);
        agregarMargenSuperior(txtEstado, 12);
        agregarMargenSuperior(btnGuardar, 14);
        agregarMargenSuperior(btnCambiarEstado, 10);
        agregarMargenSuperior(btnEliminar, 10);

        btnGuardar.setOnClickListener(view -> {
            String nuevoNombre = etNombre.getText().toString().trim();
            String nuevoPrecio = etPrecio.getText().toString().trim();
            String nuevaDescripcion = etDescripcion.getText().toString().trim();
            String nuevaImagenUrl = etImagenUrl.getText().toString().trim();

            if (nuevoNombre.isEmpty() || nuevoPrecio.isEmpty()) {
                Toast.makeText(AdminServiciosActivity.this, "Nombre y precio no pueden quedar vacíos.", Toast.LENGTH_SHORT).show();
                return;
            }

            db.collection("servicios")
                    .document(documentold)
                    .update(
                            "nombre", nuevoNombre,
                            "precio", nuevoPrecio,
                            "descripcion", nuevaDescripcion,
                            "imagenUrl", nuevaImagenUrl
                    )
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(AdminServiciosActivity.this, "Servicio actualizado correctamente.", Toast.LENGTH_SHORT).show();
                        cargarServicios();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AdminServiciosActivity.this, "No se pudo actualizar el servicio.", Toast.LENGTH_LONG).show();
                    });
        });

        btnCambiarEstado.setOnClickListener(view -> {
            db.collection("servicios")
                    .document(documentold)
                    .update("activo", !activo)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(AdminServiciosActivity.this, "Estado del servicio actualizado.", Toast.LENGTH_SHORT).show();
                        cargarServicios();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AdminServiciosActivity.this, "No se pudo cambiar el estado.", Toast.LENGTH_LONG).show();
                    });
        });

        btnEliminar.setOnClickListener(view -> {
            db.collection("servicios")
                    .document(documentold)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(AdminServiciosActivity.this, "Servicio eliminado.", Toast.LENGTH_SHORT).show();
                        cargarServicios();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AdminServiciosActivity.this, "No se pudo eliminar el servicio.", Toast.LENGTH_LONG).show();
                    });
        });

        tarjeta.addView(txtTitulo);
        tarjeta.addView(etNombre);
        tarjeta.addView(etPrecio);
        tarjeta.addView(etDescripcion);
        tarjeta.addView(etImagenUrl);
        tarjeta.addView(txtEstado);
        tarjeta.addView(btnGuardar);
        tarjeta.addView(btnCambiarEstado);
        tarjeta.addView(btnEliminar);

        layoutServiciosAdminDinamico.addView(tarjeta);
    }

    private EditText crearEditText(String hint, String valor) {
        EditText editText = new EditText(this);
        editText.setHint(hint);
        editText.setText(valor);
        editText.setTextColor(Color.WHITE);
        editText.setHintTextColor(Color.GRAY);
        editText.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#D4AF37")));
        return editText;
    }

    private String obtenerString(QueryDocumentSnapshot documento, String campo, String valorPorDefecto) {
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
        float densidad = getResources().getDisplayMetrics().density;
        return (int) (cantidadDp * densidad);
    }
}