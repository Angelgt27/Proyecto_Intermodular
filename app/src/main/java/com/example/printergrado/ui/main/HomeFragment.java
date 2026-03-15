package com.example.printergrado.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.printergrado.R;
import com.example.printergrado.viewmodel.MainViewModel;

public class HomeFragment extends Fragment {

    private MainViewModel mainViewModel;
    private PeliculaAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        RecyclerView rv = view.findViewById(R.id.rvPeliculas);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshHome);

        // Color de la rueda de carga
        swipeRefreshLayout.setColorSchemeResources(R.color.rojo_cine);

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PeliculaAdapter();
        rv.setAdapter(adapter);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        // Evento al deslizar el dedo hacia abajo
        swipeRefreshLayout.setOnRefreshListener(() -> {
            mainViewModel.cargarPeliculas();
        });

        // --- SOLUCIÓN AL PARPADEO ---
        // Solo mostramos la rueda automáticamente si NO tenemos películas guardadas en memoria
        if (mainViewModel.getPeliculas().getValue() == null) {
            swipeRefreshLayout.setRefreshing(true);
        }

        // Observador de películas
        mainViewModel.getPeliculas().observe(getViewLifecycleOwner(), peliculas -> {
            swipeRefreshLayout.setRefreshing(false); // Ocultamos la rueda
            if (peliculas != null) {
                adapter.setPeliculas(peliculas);
            }
        });

        // Observador de errores
        mainViewModel.getMensajes().observe(getViewLifecycleOwner(), msj -> {
            if (msj != null) {
                swipeRefreshLayout.setRefreshing(false); // Ocultamos la rueda si hay error
            }
        });

        return view;
    }
}