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

    private final List<Butaca> listaButacas;
    private final OnButacaSeleccionadaListener listener;

    public interface OnButacaSeleccionadaListener {
        void onSeleccionCambiada(int cantidadSeleccionada);
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
        String textoAsiento = butaca.getFila() + "-" + butaca.getNumero();
        holder.tvNumero.setText(textoAsiento);

        // LÓGICA DE COLORES
        if (butaca.isOcupada()) {
            holder.card.setCardBackgroundColor(Color.parseColor("#E0E0E0")); // Gris (Ocupada)
            holder.tvNumero.setTextColor(Color.parseColor("#9E9E9E"));
            holder.itemView.setOnClickListener(null); // No se puede pulsar
        } else if (butaca.isSeleccionada()) {
            holder.card.setCardBackgroundColor(Color.parseColor("#D32F2F")); // Rojo (Tus asientos)
            holder.tvNumero.setTextColor(Color.WHITE);
            holder.itemView.setOnClickListener(v -> toggleSeleccion(position));
        } else {
            holder.card.setCardBackgroundColor(Color.parseColor("#388E3C")); // Verde (Libres)
            holder.tvNumero.setTextColor(Color.WHITE);
            holder.itemView.setOnClickListener(v -> toggleSeleccion(position));
        }
    }

    private void toggleSeleccion(int position) {
        Butaca butaca = listaButacas.get(position);
        // Evitamos que compre más de 10 entradas de golpe (límite de la app)
        if (!butaca.isSeleccionada() && getButacasSeleccionadas().size() >= 10) {
            return;
        }
        butaca.setSeleccionada(!butaca.isSeleccionada());
        notifyItemChanged(position); // Refresca el color
        listener.onSeleccionCambiada(getButacasSeleccionadas().size()); // Avisa a ReservaActivity
    }

    public List<Integer> getButacasSeleccionadas() {
        List<Integer> seleccionadas = new ArrayList<>();
        for (Butaca b : listaButacas) {
            if (b.isSeleccionada()) seleccionadas.add(b.getIdButaca());
        }
        return seleccionadas;
    }

    @Override
    public int getItemCount() { return listaButacas.size(); }

    static class ButacaViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView card;
        TextView tvNumero;
        public ButacaViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.cardButaca);
            tvNumero = itemView.findViewById(R.id.tvNumeroButaca);
        }
    }
}