package com.example.printergrado.ui.main;

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

public class HomeFragment extends Fragment {

    private MainViewModel mainViewModel;
    private PeliculaAdapter adapter;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        RecyclerView rv = view.findViewById(R.id.rvPeliculas);
        progressBar = view.findViewById(R.id.progressBarPeliculas); // Enlazamos la rueda

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PeliculaAdapter();
        rv.setAdapter(adapter);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        // 1. Mostramos la rueda de carga por defecto
        progressBar.setVisibility(View.VISIBLE);

        // 2. Si llegan las películas con éxito, ocultamos la rueda y mostramos la lista
        mainViewModel.getPeliculas().observe(getViewLifecycleOwner(), peliculas -> {
            progressBar.setVisibility(View.GONE);
            if (peliculas != null) adapter.setPeliculas(peliculas);
        });

        // 3. Si hay un error de conexión, también ocultamos la rueda para que no gire infinitamente
        mainViewModel.getMensajes().observe(getViewLifecycleOwner(), msj -> {
            if (msj != null) {
                progressBar.setVisibility(View.GONE);
            }
        });

        return view;
    }
}