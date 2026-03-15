package com.example.printergrado.ui.main;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.printergrado.R;
import com.example.printergrado.data.model.Pelicula;
import com.example.printergrado.ui.reserva.ReservaActivity;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class PeliculaAdapter extends RecyclerView.Adapter<PeliculaAdapter.PeliculaViewHolder> {

    private List<Pelicula> listaPeliculas = new ArrayList<>();

    public void setPeliculas(List<Pelicula> peliculas) {
        this.listaPeliculas = peliculas;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PeliculaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pelicula, parent, false);
        return new PeliculaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PeliculaViewHolder holder, int position) {
        Pelicula pelicula = listaPeliculas.get(position);

        holder.tvTitulo.setText(pelicula.getTitulo());

        String info = pelicula.getGenero() + " • " + pelicula.getDuracion() + " min\n" + pelicula.getSinopsis();
        holder.tvDescripcion.setText(info);

        holder.btnReservar.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, ReservaActivity.class);

            // --- AÑADIDO: Enviamos el ID real de la película ---
            intent.putExtra("ID_PELICULA", pelicula.getIdPelicula());
            intent.putExtra("TITULO", pelicula.getTitulo());
            intent.putExtra("GENERO", pelicula.getGenero());
            intent.putExtra("DURACION", pelicula.getDuracion());
            intent.putExtra("SINOPSIS", pelicula.getSinopsis());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaPeliculas.size();
    }

    static class PeliculaViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvDescripcion;
        MaterialButton btnReservar;

        public PeliculaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTituloPelicula);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcionPelicula);
            btnReservar = itemView.findViewById(R.id.btnReservarItem);
        }
    }
}