package com.example.printergrado.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.printergrado.R;
import com.example.printergrado.data.model.Sala;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class AdminSalaAdapter extends RecyclerView.Adapter<AdminSalaAdapter.SalaViewHolder> {

    private List<Sala> listaSalas = new ArrayList<>();
    private final OnSalaActionListener listener;

    public interface OnSalaActionListener {
        void onEditar(Sala sala);
        void onEliminar(int idSala);
    }

    public AdminSalaAdapter(OnSalaActionListener listener) {
        this.listener = listener;
    }

    public void setSalas(List<Sala> salas) {
        this.listaSalas = salas;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SalaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_sala, parent, false);
        return new SalaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SalaViewHolder holder, int position) {
        Sala sala = listaSalas.get(position);
        holder.tvNombre.setText(sala.getNombre());

        holder.btnEditar.setOnClickListener(v -> listener.onEditar(sala));
        holder.btnEliminar.setOnClickListener(v -> listener.onEliminar(sala.getIdSala()));
    }

    @Override
    public int getItemCount() { return listaSalas.size(); }

    static class SalaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;
        MaterialButton btnEditar, btnEliminar;

        public SalaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreSalaItem);
            btnEditar = itemView.findViewById(R.id.btnEditarSalaItem);
            btnEliminar = itemView.findViewById(R.id.btnEliminarSalaItem);
        }
    }
}