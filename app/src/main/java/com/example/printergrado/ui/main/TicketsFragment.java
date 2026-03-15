package com.example.printergrado.ui.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.printergrado.R;
import com.example.printergrado.viewmodel.MainViewModel;

public class TicketsFragment extends Fragment {

    private MainViewModel mainViewModel;
    private TicketAdapter adapter;
    private LinearLayout layoutVacioTickets;
    private RecyclerView rv;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tickets, container, false);

        rv = view.findViewById(R.id.rvTickets);
        layoutVacioTickets = view.findViewById(R.id.layoutVacioTickets);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshTickets);

        swipeRefreshLayout.setColorSchemeResources(R.color.rojo_cine);

        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        SharedPreferences prefs = requireActivity().getSharedPreferences("CinePrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);

        adapter = new TicketAdapter(idSesion -> {
            swipeRefreshLayout.setRefreshing(true);
            mainViewModel.eliminarTicket("Bearer " + token, idSesion);
        });

        rv.setAdapter(adapter);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        // Cuando el usuario desliza, volvemos a pedir los tickets
        swipeRefreshLayout.setOnRefreshListener(() -> {
            mainViewModel.cargarTickets("Bearer " + token);
        });

        mainViewModel.getTickets().observe(getViewLifecycleOwner(), tickets -> {
            swipeRefreshLayout.setRefreshing(false); // Ocultar rueda

            if (tickets != null) {
                adapter.setTickets(tickets);

                if (tickets.isEmpty()) {
                    layoutVacioTickets.setVisibility(View.VISIBLE);
                    rv.setVisibility(View.GONE);
                } else {
                    layoutVacioTickets.setVisibility(View.GONE);
                    rv.setVisibility(View.VISIBLE);
                }
            }
        });

        swipeRefreshLayout.setRefreshing(true);
        layoutVacioTickets.setVisibility(View.GONE);
        rv.setVisibility(View.GONE);

        mainViewModel.cargarTickets("Bearer " + token);

        return view;
    }
}