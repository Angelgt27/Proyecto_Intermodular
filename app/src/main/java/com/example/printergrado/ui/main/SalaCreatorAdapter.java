package com.example.printergrado.ui.main;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.printergrado.R;
import com.example.printergrado.data.model.ButacaTemporal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SalaCreatorAdapter extends RecyclerView.Adapter<SalaCreatorAdapter.ViewHolder> {

    private final List<ButacaTemporal> matriz;

    public SalaCreatorAdapter(List<ButacaTemporal> matriz) {
        this.matriz = matriz;
        recalcularNumeros(); // Calculamos la primera vez
    }

    // MAGIA EN TIEMPO REAL: Recalcula los números seguidos ignorando huecos
    private void recalcularNumeros() {
        Map<String, Integer> contadoresFila = new HashMap<>();
        for (ButacaTemporal b : matriz) {
            if (b.isActiva()) {
                int num = contadoresFila.getOrDefault(b.getFila(), 1);
                b.setNumeroComercial(num);
                contadoresFila.put(b.getFila(), num + 1);
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_butaca_creator, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ButacaTemporal b = matriz.get(position);

        if (b.isActiva()) {
            // VERDE: Muestra el nombre recalculado (Ej: A-1, A-2...)
            holder.tvButaca.setText(b.getFila() + "-" + b.getNumeroComercial());
            holder.tvButaca.setBackgroundColor(Color.parseColor("#4CAF50"));
        } else {
            // ROJO: Borra el texto porque es un hueco/pasillo
            holder.tvButaca.setText("");
            holder.tvButaca.setBackgroundColor(Color.parseColor("#E53935"));
        }

        holder.tvButaca.setOnClickListener(v -> {
            b.setActiva(!b.isActiva());
            recalcularNumeros(); // Recalcula toda la matriz
            notifyDataSetChanged(); // Refresca toda la vista de golpe
        });
    }

    @Override
    public int getItemCount() { return matriz.size(); }
    public List<ButacaTemporal> getMatriz() { return matriz; }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvButaca;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvButaca = itemView.findViewById(R.id.tvButacaCreator);
        }
    }
}