package com.example.printergrado.ui.reserva;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.printergrado.R;
import com.example.printergrado.data.model.Butaca;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class ButacaAdapter extends RecyclerView.Adapter<ButacaAdapter.ButacaViewHolder> {

    private List<Butaca> listaButacas;
    private List<Integer> seleccionadas = new ArrayList<>();
    private OnButacaSeleccionadaListener listener;

    public interface OnButacaSeleccionadaListener {
        void onSeleccionCambiada(int cantidad);
    }

    public ButacaAdapter(List<Butaca> listaButacas, OnButacaSeleccionadaListener listener) {
        this.listaButacas = listaButacas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ButacaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_butaca, parent, false);
        return new ButacaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ButacaViewHolder holder, int position) {
        Butaca butaca = listaButacas.get(position);

        // Si es un pasillo/hueco (eliminado por el admin), ocultar
        if (butaca == null) {
            holder.itemView.setVisibility(View.INVISIBLE);
            holder.itemView.setOnClickListener(null);
            return;
        }

        holder.itemView.setVisibility(View.VISIBLE);

        // TEXTO: Usamos el guion para separar, ej: "1-A"
        holder.tvNumeroButaca.setText(butaca.getFila() + "-" + butaca.getColumna());

        // --- LÓGICA DE COLORES CORREGIDA ---
        if (butaca.isOcupada()) {
            // Ya está vendida a alguien
            holder.cardButaca.setCardBackgroundColor(Color.parseColor("#9E9E9E")); // GRIS (Ocupado)
            holder.tvNumeroButaca.setTextColor(Color.WHITE);
            holder.itemView.setOnClickListener(null);
        } else if (seleccionadas.contains(butaca.getIdButaca())) {
            // El usuario la ha tocado para comprarla
            holder.cardButaca.setCardBackgroundColor(Color.parseColor("#E53935")); // ROJO (Seleccionada)
            holder.tvNumeroButaca.setTextColor(Color.WHITE);
            holder.itemView.setOnClickListener(v -> {
                seleccionadas.remove(Integer.valueOf(butaca.getIdButaca()));
                notifyItemChanged(position);
                listener.onSeleccionCambiada(seleccionadas.size());
            });
        } else {
            // Está libre para ser comprada (Estado por defecto)
            holder.cardButaca.setCardBackgroundColor(Color.parseColor("#4CAF50")); // VERDE (Libre)
            holder.tvNumeroButaca.setTextColor(Color.WHITE);
            holder.itemView.setOnClickListener(v -> {
                seleccionadas.add(butaca.getIdButaca());
                notifyItemChanged(position);
                listener.onSeleccionCambiada(seleccionadas.size());
            });
        }
    }

    @Override
    public int getItemCount() {
        return listaButacas.size();
    }

    public List<Integer> getButacasSeleccionadas() {
        return seleccionadas;
    }

    static class ButacaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumeroButaca;
        MaterialCardView cardButaca;

        public ButacaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumeroButaca = itemView.findViewById(R.id.tvNumeroButaca);
            cardButaca = itemView.findViewById(R.id.cardButaca);
        }
    }
}