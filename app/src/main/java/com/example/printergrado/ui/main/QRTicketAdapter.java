package com.example.printergrado.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.printergrado.R;

import java.util.List;

public class QRTicketAdapter extends RecyclerView.Adapter<QRTicketAdapter.QRViewHolder> {

    private final String titulo;
    private final String fecha;
    private final String hora;
    private final String cine;
    private final List<String> butacas;

    public QRTicketAdapter(String titulo, String fecha, String hora, String cine, List<String> butacas) {
        this.titulo = titulo;
        this.fecha = fecha;
        this.hora = hora;
        this.cine = cine;
        this.butacas = butacas;
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

        // Ponemos el Cine junto con la Fecha y Hora
        String cineStr = (cine != null && !cine.isEmpty()) ? cine : "Cine no especificado";
        holder.tvInfo.setText(cineStr + "\n" + fecha + " • " + hora);

        // --- MAGIA AQUÍ: Sustituimos el número genérico por la butaca real ---
        String butacaAsignada = butacas.get(position);
        holder.tvNumero.setText("Butaca Asignada: " + butacaAsignada);
    }

    @Override
    public int getItemCount() {
        return butacas.size(); // Creamos tantos QRs como butacas haya
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