package com.example.printergrado.ui.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.printergrado.R;
import com.example.printergrado.data.api.ApiClient;
import com.example.printergrado.data.api.ApiService;
import com.example.printergrado.data.model.ReservaResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminFormularioActivity extends AppCompatActivity {

    private TextInputEditText etTitulo, etGenero, etDuracion, etSinopsis;
    private MaterialButton btnGuardar, btnEliminar;
    private TextView tvTituloToolbar;
    private ImageView ivPreview;

    private int peliculaId = -1; // -1 significa crear nueva
    private String imagenBase64 = null; // Guardará la foto convertida
    private ApiService apiService;
    private String token;

    // Lanzador para abrir la galería de fotos
    private final ActivityResultLauncher<String> selectorImagen = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) processImage(uri);
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_admin_formulario);

        // Configuración visual (padding notch/cámara)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            View barraSuperior = findViewById(R.id.barraSuperiorForm);
            barraSuperior.setPadding(0, insets.top, 0, 0);
            barraSuperior.getLayoutParams().height = insets.top + (int)(60 * getResources().getDisplayMetrics().density);
            return windowInsets;
        });

        // Referencias UI
        etTitulo = findViewById(R.id.etTituloPelicula);
        etGenero = findViewById(R.id.etGeneroPelicula);
        etDuracion = findViewById(R.id.etDuracionPelicula);
        etSinopsis = findViewById(R.id.etSinopsisPelicula);
        btnGuardar = findViewById(R.id.btnGuardarPelicula);
        btnEliminar = findViewById(R.id.btnEliminarPelicula);
        tvTituloToolbar = findViewById(R.id.tvTituloToolbar);
        ivPreview = findViewById(R.id.ivPreviewPelicula);

        findViewById(R.id.btnVolverForm).setOnClickListener(v -> finish());
        findViewById(R.id.btnSeleccionarImagen).setOnClickListener(v -> selectorImagen.launch("image/*"));

        apiService = ApiClient.getClient().create(ApiService.class);
        SharedPreferences prefs = getSharedPreferences("CinePrefs", Context.MODE_PRIVATE);
        token = "Bearer " + prefs.getString("jwt_token", "");

        // Cargar datos si estamos en modo Edición
        if (getIntent() != null && getIntent().hasExtra("ID_PELICULA")) {
            peliculaId = getIntent().getIntExtra("ID_PELICULA", -1);
            tvTituloToolbar.setText("Editar Película");
            etTitulo.setText(getIntent().getStringExtra("TITULO"));
            etGenero.setText(getIntent().getStringExtra("GENERO"));
            etDuracion.setText(String.valueOf(getIntent().getIntExtra("DURACION", 0)));
            etSinopsis.setText(getIntent().getStringExtra("SINOPSIS"));

            // Si la peli ya tenía imagen, la decodificamos
            String imgActual = getIntent().getStringExtra("IMAGEN");
            if (imgActual != null && !imgActual.isEmpty()) {
                try {
                    byte[] decoded = Base64.decode(imgActual, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
                    ivPreview.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            btnEliminar.setVisibility(View.VISIBLE); // Mostrar botón eliminar
        }

        btnGuardar.setOnClickListener(v -> guardarPelicula());
        btnEliminar.setOnClickListener(v -> eliminarPelicula());
    }

    private void processImage(Uri uri) {
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(is);

            // Redimensionamos la imagen a tamaño "cartel" para no colapsar la BD
            Bitmap resized = Bitmap.createScaledBitmap(bitmap, 300, 450, true);
            ivPreview.setImageBitmap(resized);

            // Convertimos a Base64
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            resized.compress(Bitmap.CompressFormat.JPEG, 70, baos); // Calidad 70%
            byte[] imageBytes = baos.toByteArray();
            imagenBase64 = Base64.encodeToString(imageBytes, Base64.NO_WRAP);

        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private void guardarPelicula() {
        Map<String, Object> datos = new HashMap<>();
        datos.put("titulo", etTitulo.getText().toString());
        datos.put("genero", etGenero.getText().toString());
        datos.put("sinopsis", etSinopsis.getText().toString());

        if (imagenBase64 != null) {
            datos.put("imagen", imagenBase64); // Se envía solo si ha cambiado
        }

        try {
            datos.put("duracion", Integer.parseInt(etDuracion.getText().toString()));
        } catch (NumberFormatException e) {
            Toast.makeText(this, "La duración debe ser un número", Toast.LENGTH_SHORT).show();
            return;
        }

        btnGuardar.setEnabled(false);

        if (peliculaId == -1) {
            // CREAR NUEVA
            apiService.crearPelicula(token, datos).enqueue(new Callback<ReservaResponse>() {
                @Override
                public void onResponse(Call<ReservaResponse> call, Response<ReservaResponse> response) {
                    gestionarRespuesta(response);
                }
                @Override public void onFailure(Call<ReservaResponse> call, Throwable t) { errorConexion(t); }
            });
        } else {
            apiService.actualizarPelicula(token, peliculaId, datos).enqueue(new Callback<ReservaResponse>() {
                @Override
                public void onResponse(Call<ReservaResponse> call, Response<ReservaResponse> response) {
                    gestionarRespuesta(response);
                }
                @Override public void onFailure(Call<ReservaResponse> call, Throwable t) { errorConexion(t); }
            });
        }
    }

    private void eliminarPelicula() {
        btnEliminar.setEnabled(false);
        apiService.eliminarPelicula(token, peliculaId).enqueue(new Callback<ReservaResponse>() {
            @Override
            public void onResponse(Call<ReservaResponse> call, Response<ReservaResponse> response) {
                gestionarRespuesta(response);
            }
            @Override public void onFailure(Call<ReservaResponse> call, Throwable t) { errorConexion(t); }
        });
    }

    private void gestionarRespuesta(Response<ReservaResponse> response) {
        if (response.isSuccessful() && response.body() != null) {
            Toast.makeText(this, response.body().getMensaje(), Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error en la operación. Revisa los datos.", Toast.LENGTH_LONG).show();
            btnGuardar.setEnabled(true);
            btnEliminar.setEnabled(true);
        }
    }

    private void errorConexion(Throwable t) {
        Toast.makeText(this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
        btnGuardar.setEnabled(true);
        btnEliminar.setEnabled(true);
    }
}