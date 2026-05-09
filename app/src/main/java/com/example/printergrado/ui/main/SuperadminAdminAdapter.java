package com.example.printergrado.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.printergrado.R;
import com.example.printergrado.data.model.Usuario;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class SuperadminAdminAdapter extends RecyclerView.Adapter<SuperadminAdminAdapter.ViewHolder> {

    private List<Usuario> listaAdmins = new ArrayList<>();
    private final OnAdminActionListener listener;

    public interface OnAdminActionListener {
        void onEditar(Usuario admin);
        void onEliminar(int idAdmin);
    }

    public SuperadminAdminAdapter(OnAdminActionListener listener) {
        this.listener = listener;
    }

    public void setAdmins(List<Usuario> admins) {
        this.listaAdmins = admins;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_superadmin_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Usuario admin = listaAdmins.get(position);
        holder.tvNombre.setText(admin.getNombre());
        holder.tvEmail.setText(admin.getEmail());

        String cine = admin.getNombreCine();
        holder.tvCine.setText(cine != null ? cine : "Sin asignar");

        holder.btnEditar.setOnClickListener(v -> listener.onEditar(admin));
        holder.btnEliminar.setOnClickListener(v -> listener.onEliminar(admin.getIdUsuario()));
    }

    @Override
    public int getItemCount() { return listaAdmins.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvEmail, tvCine;
        MaterialButton btnEditar, btnEliminar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreAdminItem);
            tvEmail = itemView.findViewById(R.id.tvEmailAdminItem);
            tvCine = itemView.findViewById(R.id.tvCineAdminItem);
            btnEditar = itemView.findViewById(R.id.btnEditarAdmin);
            btnEliminar = itemView.findViewById(R.id.btnEliminarAdmin);
        }
    }
}