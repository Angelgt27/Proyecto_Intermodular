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
        

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_historial, parent, false);
        return new HistorialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistorialViewHolder holder, int position) {
        Ticket ticket = listaTickets.get(position);

        

        holder.tvTitulo.setText(ticket.getTitulo());

        

        String info = ticket.getFecha() + " • " + ticket.getHora() + " • " + ticket.getCantidadTickets() + " entradas";
        holder.tvInfo.setText(info);

        

        String estado = ticket.getEstado();
        holder.tvEstado.setText(estado);

        

        GradientDrawable background = (GradientDrawable) holder.tvEstado.getBackground().mutate();

        if ("Activa".equalsIgnoreCase(estado)) {
            background.setColor(Color.parseColor("#E8F5E9")); 

            holder.tvEstado.setTextColor(Color.parseColor("#2E7D32")); 

        } else if ("Cancelada".equalsIgnoreCase(estado)) {
            background.setColor(Color.parseColor("#FFEBEE")); 

            holder.tvEstado.setTextColor(Color.parseColor("#C62828")); 

        } else {
            background.setColor(Color.parseColor("#EEEEEE")); 

            holder.tvEstado.setTextColor(Color.parseColor("#757575")); 

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