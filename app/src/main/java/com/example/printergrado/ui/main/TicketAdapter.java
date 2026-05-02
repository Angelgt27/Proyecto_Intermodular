package com.example.printergrado.ui.main;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.printergrado.R;
import com.example.printergrado.data.model.Ticket;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {

    private List<Ticket> listaTickets = new ArrayList<>();
    private final OnTicketActionListener listener;

    public interface OnTicketActionListener {
        void onEliminar(int idSesion);
    }

    public TicketAdapter(OnTicketActionListener listener) {
        this.listener = listener;
    }

    public void setTickets(List<Ticket> tickets) {
        this.listaTickets = tickets;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ticket, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        Ticket ticket = listaTickets.get(position);

        String tituloFinal = ticket.getTitulo();
        if (ticket.getCantidadTickets() > 1) {
            tituloFinal += " (" + ticket.getCantidadTickets() + ")";
        }
        holder.tvTitulo.setText(tituloFinal);

        String info = "Fecha: " + ticket.getFecha() + " • Hora: " + ticket.getHora();
        holder.tvDescripcion.setText(info);

        // --- NUEVO: Decodificar y mostrar el póster ---
        if (ticket.getImagen() != null && !ticket.getImagen().isEmpty()) {
            try {
                byte[] decoded = Base64.decode(ticket.getImagen(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
                holder.ivCartel.setImageBitmap(bitmap);
            } catch (Exception e) {
                holder.ivCartel.setImageResource(R.drawable.ic_imagen);
            }
        } else {
            holder.ivCartel.setImageResource(R.drawable.ic_imagen);
        }

        holder.btnVer.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(v.getContext(), TicketDetailActivity.class);
            intent.putExtra("TITULO", ticket.getTitulo());
            intent.putExtra("FECHA", ticket.getFecha());
            intent.putExtra("HORA", ticket.getHora());
            intent.putExtra("CINE", ticket.getCine());
            intent.putStringArrayListExtra("BUTACAS", new ArrayList<>(ticket.getButacas()));
            intent.putStringArrayListExtra("QRS", new ArrayList<>(ticket.getQrCodes()));
            v.getContext().startActivity(intent);
        });

        holder.btnEliminar.setOnClickListener(v -> {
            listener.onEliminar(ticket.getIdSesion());
        });
    }

    @Override
    public int getItemCount() {
        return listaTickets.size();
    }

    static class TicketViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvDescripcion;
        MaterialButton btnVer, btnEliminar;
        ImageView ivCartel;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTituloTicket);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcionTicket);
            btnVer = itemView.findViewById(R.id.btnVerTicket);
            btnEliminar = itemView.findViewById(R.id.btnEliminarTicket);
            ivCartel = itemView.findViewById(R.id.ivTicketWIP);
        }
    }
}