package com.example.printergrado.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.printergrado.R;
import com.example.printergrado.data.model.Cine;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class SuperadminCineAdapter extends RecyclerView.Adapter<SuperadminCineAdapter.ViewHolder> {

    private List<Cine> listaCines = new ArrayList<>();
    private final OnCineActionListener listener;

    public interface OnCineActionListener {
        void onDetalles(Cine cine);
        void onEliminar(int idCine);
    }

    public SuperadminCineAdapter(OnCineActionListener listener) {
        this.listener = listener;
    }

    public void setCines(List<Cine> cines) {
        this.listaCines = cines;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_superadmin_cine, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cine cine = listaCines.get(position);
        holder.tvNombre.setText(cine.getNombre());

        String dir = cine.getDireccion();
        holder.tvDireccion.setText((dir == null || dir.isEmpty()) ? "Sin dirección configurada" : dir);

        holder.btnDetalles.setOnClickListener(v -> listener.onDetalles(cine));
        holder.btnEliminar.setOnClickListener(v -> listener.onEliminar(cine.getIdCine()));
    }

    @Override
    public int getItemCount() { return listaCines.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDireccion;
        MaterialButton btnDetalles, btnEliminar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreCineItem);
            tvDireccion = itemView.findViewById(R.id.tvDireccionCineItem);
            btnDetalles = itemView.findViewById(R.id.btnDetallesCine);
            btnEliminar = itemView.findViewById(R.id.btnEliminarCine);
        }
    }
}