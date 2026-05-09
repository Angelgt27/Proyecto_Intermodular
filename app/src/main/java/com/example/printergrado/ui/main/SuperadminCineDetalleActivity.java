package com.example.printergrado.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.printergrado.R;

public class SuperadminCineDetalleActivity extends AppCompatActivity {

    private int idCine = -1;
    private String nombre, direccion, telefono;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_superadmin_cine_detalle);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            View barra = findViewById(R.id.barraSuperiorDetalleCine);
            barra.setPadding(0, insets.top, 0, 0);
            barra.getLayoutParams().height = insets.top + (int)(60 * getResources().getDisplayMetrics().density);
            return windowInsets;
        });

        findViewById(R.id.btnVolverDetalleCine).setOnClickListener(v -> finish());

        if (getIntent() != null) {
            idCine = getIntent().getIntExtra("ID_CINE", -1);
            nombre = getIntent().getStringExtra("NOMBRE");
            direccion = getIntent().getStringExtra("DIRECCION");
            telefono = getIntent().getStringExtra("TELEFONO");

            TextView tvTitulo = findViewById(R.id.tvTituloDetalleCine);
            tvTitulo.setText(nombre != null ? nombre : "Detalles");
        }

        findViewById(R.id.cardDatosCinePuente).setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminDatosCineActivity.class);
            intent.putExtra("ID_CINE", idCine);
            intent.putExtra("NOMBRE", nombre);
            intent.putExtra("DIRECCION", direccion);
            intent.putExtra("TELEFONO", telefono);
            startActivity(intent);
        });

        findViewById(R.id.cardGestionSalasPuente).setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminSalasListaActivity.class);
            intent.putExtra("ID_CINE", idCine);
            startActivity(intent);
        });
    }
}