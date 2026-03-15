package com.example.printergrado.ui.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.printergrado.R;
import com.example.printergrado.viewmodel.MainViewModel;

public class TicketsFragment extends Fragment {

    private MainViewModel mainViewModel;
    private TicketAdapter adapter;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tickets, container, false);

        RecyclerView rv = view.findViewById(R.id.rvTickets);
        progressBar = view.findViewById(R.id.progressBarTickets);

        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        SharedPreferences prefs = requireActivity().getSharedPreferences("CinePrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);

        adapter = new TicketAdapter(idSesion -> {
            // Mostrar rueda al eliminar
            progressBar.setVisibility(View.VISIBLE);
            mainViewModel.eliminarTicket("Bearer " + token, idSesion);
        });

        rv.setAdapter(adapter);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        mainViewModel.getTickets().observe(getViewLifecycleOwner(), tickets -> {
            // Ocultamos la rueda en cuanto llegan los datos
            progressBar.setVisibility(View.GONE);
            if (tickets != null) adapter.setTickets(tickets);
        });

        // Mostrar rueda al solicitar los tickets
        progressBar.setVisibility(View.VISIBLE);
        mainViewModel.cargarTickets("Bearer " + token);

        return view;
    }
}