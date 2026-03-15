package com.example.printergrado.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.printergrado.R;

public class QRTicketAdapter extends RecyclerView.Adapter<QRTicketAdapter.QRViewHolder> {

    private final String titulo;
    private final String fecha;
    private final String hora;
    private final int cantidad;

    public QRTicketAdapter(String titulo, String fecha, String hora, int cantidad) {
        this.titulo = titulo;
        this.fecha = fecha;
        this.hora = hora;
        this.cantidad = cantidad;
    }

    @NonNull
    @Override
    public QRViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_qr_ticket, parent, false);
        return new QRViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QRViewHolder holder, int position) {
        holder.tvTitulo.setText(titulo);
        holder.tvInfo.setText("Fecha: " + fecha + "\nHora: " + hora);
        // Indicamos qué número de entrada es (Ej: Entrada 1 de 3)
        holder.tvNumero.setText("Entrada " + (position + 1) + " de " + cantidad);
    }

    @Override
    public int getItemCount() {
        return cantidad; // Creamos tantas filas como cantidad de tickets haya
    }

    static class QRViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvInfo, tvNumero;

        public QRViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvQRTitulo);
            tvInfo = itemView.findViewById(R.id.tvQRInfo);
            tvNumero = itemView.findViewById(R.id.tvQRNumero);
        }
    }
}