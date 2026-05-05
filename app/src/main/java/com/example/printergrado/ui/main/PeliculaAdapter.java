package com.example.printergrado.ui.main;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.printergrado.R;
import com.example.printergrado.data.model.Pelicula;
import com.example.printergrado.ui.reserva.ReservaActivity;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PeliculaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_PELICULA = 1;

    private List<Pelicula> listaPeliculasOriginal = new ArrayList<>();
    private List<ListItem> listaDisplay = new ArrayList<>();

    private String userRole = "Usuario";
    private Set<Integer> cinesExpandidos = new HashSet<>();

    public void setRole(String role) {
        this.userRole = role;
        construirListaDisplay();
    }

    public void setPeliculas(List<Pelicula> peliculas) {
        this.listaPeliculasOriginal = peliculas;
        construirListaDisplay();
    }

    private void construirListaDisplay() {
        listaDisplay.clear();

        if ("Superadmin".equals(userRole)) {
            Map<Integer, List<Pelicula>> peliculasPorCine = new HashMap<>();
            Map<Integer, String> nombresRealesCines = new HashMap<>();

            for (Pelicula p : listaPeliculasOriginal) {
                if (!peliculasPorCine.containsKey(p.getFkCine())) {
                    peliculasPorCine.put(p.getFkCine(), new ArrayList<>());
                    nombresRealesCines.put(p.getFkCine(), p.getNombreCine()); // Usamos el nombre real
                }
                peliculasPorCine.get(p.getFkCine()).add(p);
            }

            for (Map.Entry<Integer, List<Pelicula>> entrada : peliculasPorCine.entrySet()) {
                int cineId = entrada.getKey();
                String nombreReal = nombresRealesCines.get(cineId);

                listaDisplay.add(new ListItem(TYPE_HEADER, nombreReal != null ? nombreReal : "Cine Desconocido", cineId, null));

                if (cinesExpandidos.contains(cineId)) {
                    for (Pelicula p : entrada.getValue()) {
                        listaDisplay.add(new ListItem(TYPE_PELICULA, null, cineId, p));
                    }
                }
            }
        } else {
            for (Pelicula p : listaPeliculasOriginal) {
                listaDisplay.add(new ListItem(TYPE_PELICULA, null, p.getFkCine(), p));
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return listaDisplay.get(position).type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cine_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pelicula, parent, false);
            return new PeliculaViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ListItem item = listaDisplay.get(position);

        if (holder.getItemViewType() == TYPE_HEADER) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.tvNombreCine.setText(item.cineNombre);
            headerHolder.tvIcono.setText(cinesExpandidos.contains(item.cineId) ? "▲" : "▼");

            headerHolder.itemView.setOnClickListener(v -> {
                if (cinesExpandidos.contains(item.cineId)) cinesExpandidos.remove(item.cineId);
                else cinesExpandidos.add(item.cineId);
                construirListaDisplay();
            });

        } else {
            PeliculaViewHolder peliculaHolder = (PeliculaViewHolder) holder;
            Pelicula pelicula = item.pelicula;

            peliculaHolder.tvTitulo.setText(pelicula.getTitulo());
            String info = pelicula.getGenero() + " • " + pelicula.getDuracion() + " min\n" + pelicula.getSinopsis();
            peliculaHolder.tvDescripcion.setText(info);

            if (pelicula.getImagen() != null && !pelicula.getImagen().isEmpty()) {
                try {
                    byte[] decodedString = android.util.Base64.decode(pelicula.getImagen(), android.util.Base64.DEFAULT);
                    android.graphics.Bitmap decodedByte = android.graphics.BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    peliculaHolder.ivPelicula.setImageBitmap(decodedByte);
                } catch (Exception e) { peliculaHolder.ivPelicula.setImageResource(R.drawable.ic_imagen); }
            } else {
                peliculaHolder.ivPelicula.setImageResource(R.drawable.ic_imagen);
            }

            if ("Admin".equals(userRole) || "Superadmin".equals(userRole)) {
                peliculaHolder.btnReservar.setVisibility(View.GONE);
                peliculaHolder.btnEditar.setVisibility(View.VISIBLE);
                peliculaHolder.btnSesiones.setVisibility(View.VISIBLE);

                peliculaHolder.btnEditar.setOnClickListener(v -> {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, AdminFormularioActivity.class);
                    intent.putExtra("ID_PELICULA", pelicula.getIdPelicula());
                    intent.putExtra("TITULO", pelicula.getTitulo());
                    intent.putExtra("GENERO", pelicula.getGenero());
                    intent.putExtra("DURACION", pelicula.getDuracion());
                    intent.putExtra("SINOPSIS", pelicula.getSinopsis());
                    intent.putExtra("IMAGEN", pelicula.getImagen());
                    context.startActivity(intent);
                });

                peliculaHolder.btnSesiones.setOnClickListener(v -> {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, AdminSesionesActivity.class);
                    intent.putExtra("ID_PELICULA", pelicula.getIdPelicula());
                    intent.putExtra("TITULO_PELICULA", pelicula.getTitulo());
                    context.startActivity(intent);
                });
            } else {
                peliculaHolder.btnEditar.setVisibility(View.GONE);
                peliculaHolder.btnSesiones.setVisibility(View.GONE);
                peliculaHolder.btnReservar.setVisibility(View.VISIBLE);

                peliculaHolder.btnReservar.setOnClickListener(v -> {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, ReservaActivity.class);
                    intent.putExtra("ID_PELICULA", pelicula.getIdPelicula());
                    intent.putExtra("TITULO", pelicula.getTitulo());
                    intent.putExtra("GENERO", pelicula.getGenero());
                    intent.putExtra("DURACION", pelicula.getDuracion());
                    intent.putExtra("SINOPSIS", pelicula.getSinopsis());
                    intent.putExtra("IMAGEN", pelicula.getImagen());
                    context.startActivity(intent);
                });
            }
        }
    }

    @Override public int getItemCount() { return listaDisplay.size(); }

    static class ListItem {
        int type; String cineNombre; int cineId; Pelicula pelicula;
        ListItem(int type, String cineNombre, int cineId, Pelicula pelicula) {
            this.type = type; this.cineNombre = cineNombre; this.cineId = cineId; this.pelicula = pelicula;
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreCine, tvIcono;
        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreCine = itemView.findViewById(R.id.tvCineHeaderNombre);
            tvIcono = itemView.findViewById(R.id.tvCineHeaderIcono);
        }
    }

    static class PeliculaViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvDescripcion;
        MaterialButton btnReservar, btnEditar, btnSesiones;
        ImageView ivPelicula;

        public PeliculaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTituloPelicula);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcionPelicula);
            btnReservar = itemView.findViewById(R.id.btnReservarItem);
            btnEditar = itemView.findViewById(R.id.btnEditarItem);
            btnSesiones = itemView.findViewById(R.id.btnSesionesItem);
            ivPelicula = itemView.findViewById(R.id.ivPeliculaWIP);
        }
    }
}