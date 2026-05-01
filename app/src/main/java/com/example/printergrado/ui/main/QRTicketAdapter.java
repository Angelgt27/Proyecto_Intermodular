package com.example.printergrado.ui.main;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.printergrado.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.List;

public class QRTicketAdapter extends RecyclerView.Adapter<QRTicketAdapter.QRViewHolder> {

    private final String titulo, fecha, hora, cine;
    private final List<String> butacas;
    private final List<String> qrs;

    public QRTicketAdapter(String titulo, String fecha, String hora, String cine, List<String> butacas, List<String> qrs) {
        this.titulo = titulo;
        this.fecha = fecha;
        this.hora = hora;
        this.cine = cine;
        this.butacas = butacas;
        this.qrs = qrs;
    }

    @NonNull
    @Override
    public QRViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_qr_ticket, parent, false);
        return new QRViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QRViewHolder holder, int position) {
        holder.tvTitulo.setText(titulo);
        String cineStr = (cine != null && !cine.isEmpty()) ? cine : "Cine no especificado";
        holder.tvInfo.setText(cineStr + "\n" + fecha + " • " + hora);
        holder.tvNumero.setText("Butaca Asignada: " + butacas.get(position));

        String contenidoQR = qrs.get(position);
        Bitmap qrBitmap = generarQR(contenidoQR);
        if (qrBitmap != null) {
            holder.ivQR.setImageBitmap(qrBitmap);
        }
    }

    @Override
    public int getItemCount() {
        return butacas.size();
    }

    private Bitmap generarQR(String content) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bmp;
        } catch (Exception e) {
            return null;
        }
    }

    static class QRViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvInfo, tvNumero;
        ImageView ivQR;

        public QRViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvQRTitulo);
            tvInfo = itemView.findViewById(R.id.tvQRInfo);
            tvNumero = itemView.findViewById(R.id.tvQRNumero);
            ivQR = itemView.findViewById(R.id.ivQRWIP);
        }
    }
}