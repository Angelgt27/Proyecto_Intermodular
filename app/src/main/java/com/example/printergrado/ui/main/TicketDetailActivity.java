package com.example.printergrado.ui.main;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.printergrado.R;

public class TicketDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_ticket_detail);

        // Ajustamos la barra roja para que no se pise con la cámara/batería
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            findViewById(R.id.barraSuperiorDetalle).setPadding(0, insets.top, 0, 0);
            findViewById(R.id.barraSuperiorDetalle).getLayoutParams().height = insets.top + (int)(60 * getResources().getDisplayMetrics().density);
            return windowInsets;
        });

        // Botón Volver
        TextView btnVolver = findViewById(R.id.btnVolverDetalle);
        btnVolver.setOnClickListener(v -> finish());

        // Configuramos la lista
        RecyclerView rv = findViewById(R.id.rvQRTickets);
        rv.setLayoutManager(new LinearLayoutManager(this));

        // Recibimos los datos y creamos el adaptador
        if (getIntent() != null) {
            String titulo = getIntent().getStringExtra("TITULO");
            String fecha = getIntent().getStringExtra("FECHA");
            String hora = getIntent().getStringExtra("HORA");
            int cantidad = getIntent().getIntExtra("CANTIDAD", 1);

            QRTicketAdapter adapter = new QRTicketAdapter(titulo, fecha, hora, cantidad);
            rv.setAdapter(adapter);
        }
    }
}