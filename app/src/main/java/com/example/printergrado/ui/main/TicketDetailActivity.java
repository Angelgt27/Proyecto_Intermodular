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

import java.util.ArrayList;

public class TicketDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_ticket_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            findViewById(R.id.barraSuperiorDetalle).setPadding(0, insets.top, 0, 0);
            findViewById(R.id.barraSuperiorDetalle).getLayoutParams().height = insets.top + (int)(60 * getResources().getDisplayMetrics().density);
            return windowInsets;
        });

        TextView btnVolver = findViewById(R.id.btnVolverDetalle);
        btnVolver.setOnClickListener(v -> finish());

        RecyclerView rv = findViewById(R.id.rvQRTickets);
        rv.setLayoutManager(new LinearLayoutManager(this));

        if (getIntent() != null) {
            String titulo = getIntent().getStringExtra("TITULO");
            String fecha = getIntent().getStringExtra("FECHA");
            String hora = getIntent().getStringExtra("HORA");
            // --- AÑADIDO: Recibimos los nuevos datos ---
            String cine = getIntent().getStringExtra("CINE");
            ArrayList<String> butacas = getIntent().getStringArrayListExtra("BUTACAS");

            if (butacas == null) butacas = new ArrayList<>();

            QRTicketAdapter adapter = new QRTicketAdapter(titulo, fecha, hora, cine, butacas);
            rv.setAdapter(adapter);
        }
    }
}