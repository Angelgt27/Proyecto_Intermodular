package com.example.printergrado.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.printergrado.R;
import com.example.printergrado.data.model.Pelicula;

import java.util.ArrayList;
import java.util.List;

public class PeliculaAdapter extends RecyclerView.Adapter<PeliculaAdapter.PeliculaViewHolder> {

    private List<Pelicula> listaPeliculas = new ArrayList<>();

    // Método para inyectar la lista de películas desde el MainActivity
    public void setPeliculas(List<Pelicula> peliculas) {
        this.listaPeliculas = peliculas;
        notifyDataSetChanged(); // Avisamos a la lista de que hay datos nuevos
    }

    @NonNull
    @Override
    public PeliculaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflamos el diseño XML de cada fila
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pelicula, parent, false);
        return new PeliculaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PeliculaViewHolder holder, int position) {
        Pelicula pelicula = listaPeliculas.get(position);

        holder.tvTitulo.setText(pelicula.getTitulo());

        // Creamos un texto combinando el género, la duración y la sinopsis
        String info = pelicula.getGenero() + " • " + pelicula.getDuracion() + " min\n" + pelicula.getSinopsis();
        holder.tvDescripcion.setText(info);
    }

    @Override
    public int getItemCount() {
        return listaPeliculas.size();
    }

    // Clase interna que guarda las referencias a las vistas del XML
    static class PeliculaViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvDescripcion;

        public PeliculaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTituloPelicula);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcionPelicula);
        }
    }
}