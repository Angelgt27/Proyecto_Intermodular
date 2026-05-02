package com.example.printergrado.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.printergrado.R;
import com.example.printergrado.data.model.Sesion;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class AdminSesionAdapter extends RecyclerView.Adapter<AdminSesionAdapter.SesionViewHolder> {

    private List<Sesion> listaSesiones = new ArrayList<>();
    private final OnSesionActionListener listener;

    public interface OnSesionActionListener {
        void onEditar(Sesion sesion);
        void onEliminar(int idSesion);
    }

    public AdminSesionAdapter(OnSesionActionListener listener) {
        this.listener = listener;
    }

    public void setSesiones(List<Sesion> sesiones) {
        this.listaSesiones = sesiones;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SesionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_sesion, parent, false);
        return new SesionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SesionViewHolder holder, int position) {
        Sesion sesion = listaSesiones.get(position);
        holder.tvFechaHora.setText(sesion.getFecha() + " | " + sesion.getHora());
        holder.tvDetalles.setText("Sala: " + sesion.getFkSala() + " • Precio: " + sesion.getPrecio() + "€");

        holder.btnEditar.setOnClickListener(v -> listener.onEditar(sesion));
        holder.btnEliminar.setOnClickListener(v -> listener.onEliminar(sesion.getIdSesion()));
    }

    @Override
    public int getItemCount() {
        return listaSesiones.size();
    }

    static class SesionViewHolder extends RecyclerView.ViewHolder {
        TextView tvFechaHora, tvDetalles;
        MaterialButton btnEditar, btnEliminar;

        public SesionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFechaHora = itemView.findViewById(R.id.tvFechaHoraSesion);
            tvDetalles = itemView.findViewById(R.id.tvDetallesSesion);
            btnEditar = itemView.findViewById(R.id.btnEditarSesion);
            btnEliminar = itemView.findViewById(R.id.btnEliminarSesion);
        }
    }
}