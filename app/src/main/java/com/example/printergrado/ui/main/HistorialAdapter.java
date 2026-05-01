package com.example.printergrado.ui.main;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.printergrado.R;
import com.example.printergrado.data.model.Ticket;

import java.util.List;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder> {

    private final List<Ticket> listaTickets;

    public HistorialAdapter(List<Ticket> listaTickets) {
        this.listaTickets = listaTickets;
    }

    @NonNull
    @Override
    public HistorialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Enlaza con el diseño item_historial.xml que creamos antes
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_historial, parent, false);
        return new HistorialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistorialViewHolder holder, int position) {
        Ticket ticket = listaTickets.get(position);

        // Ponemos el Título
        holder.tvTitulo.setText(ticket.getTitulo());

        // Creamos un string con Fecha, Hora y Cantidad
        String info = ticket.getFecha() + " • " + ticket.getHora() + " • " + ticket.getCantidadTickets() + " entradas";
        holder.tvInfo.setText(info);

        // Asignamos el estado (Activa / Finalizada)
        String estado = ticket.getEstado();
        holder.tvEstado.setText(estado);

        // --- MAGIA VISUAL: Cambiamos el color de la pastilla según el estado ---
        GradientDrawable background = (GradientDrawable) holder.tvEstado.getBackground().mutate();

        if ("Activa".equalsIgnoreCase(estado)) {
            background.setColor(Color.parseColor("#E8F5E9")); // Verde claro
            holder.tvEstado.setTextColor(Color.parseColor("#2E7D32")); // Verde oscuro
        } else if ("Cancelada".equalsIgnoreCase(estado)) {
            background.setColor(Color.parseColor("#FFEBEE")); // Rojo claro
            holder.tvEstado.setTextColor(Color.parseColor("#C62828")); // Rojo oscuro
        } else {
            background.setColor(Color.parseColor("#EEEEEE")); // Gris claro
            holder.tvEstado.setTextColor(Color.parseColor("#757575")); // Gris oscuro
        }
    }

    @Override
    public int getItemCount() {
        return listaTickets.size();
    }

    static class HistorialViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvInfo, tvEstado;

        public HistorialViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvHistorialTitulo);
            tvInfo = itemView.findViewById(R.id.tvHistorialInfo);
            tvEstado = itemView.findViewById(R.id.tvHistorialEstado);
        }
    }
}