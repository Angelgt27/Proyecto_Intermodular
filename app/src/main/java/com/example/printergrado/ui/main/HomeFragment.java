package com.example.printergrado.ui.main;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.printergrado.R;
import com.example.printergrado.data.model.Pelicula;
import com.example.printergrado.viewmodel.MainViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private MainViewModel mainViewModel;
    private PeliculaAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private List<Pelicula> todasLasPeliculas = new ArrayList<>();

    private TextInputEditText etFiltroNombre;
    private TextInputEditText etFiltroFecha;
    private AutoCompleteTextView spinnerFiltroCine;
    private FloatingActionButton fabAgregarPelicula;

    // Lo hacemos variable de clase para usarlo en onResume
    private boolean isAdmin = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        RecyclerView rv = view.findViewById(R.id.rvPeliculas);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshHome);
        etFiltroNombre = view.findViewById(R.id.etFiltroNombre);
        etFiltroFecha = view.findViewById(R.id.etFiltroFecha);
        spinnerFiltroCine = view.findViewById(R.id.spinnerFiltroCine);
        fabAgregarPelicula = view.findViewById(R.id.fabAgregarPelicula);

        SharedPreferences prefs = requireActivity().getSharedPreferences("CinePrefs", Context.MODE_PRIVATE);
        String rol = prefs.getString("rol", "Usuario");
        isAdmin = "Admin".equals(rol);

        swipeRefreshLayout.setColorSchemeResources(R.color.rojo_cine);

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PeliculaAdapter();
        adapter.setRole(rol);
        rv.setAdapter(adapter);

        String[] cines = new String[]{"Cine Yelmo Ideal", "Cinesa Las Rozas", "Kinepolis Ciudad de la Imagen", "Todos"};
        ArrayAdapter<String> adapterCines = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, cines);
        spinnerFiltroCine.setAdapter(adapterCines);
        spinnerFiltroCine.setText("Todos", false);

        etFiltroFecha.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(requireContext(), (vista, year, month, dayOfMonth) -> {
                String fecha = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                etFiltroFecha.setText(fecha);
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        });

        etFiltroNombre.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) { filtrarPeliculas(); }
        });

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        if (isAdmin) {
            View tilCine = view.findViewById(R.id.tilFiltroCine);
            if (tilCine != null) {
                tilCine.setVisibility(View.GONE);
            }
            fabAgregarPelicula.setVisibility(View.VISIBLE);
            fabAgregarPelicula.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), AdminFormularioActivity.class);
                startActivity(intent);
            });
        }

        // Configurar Observadores
        mainViewModel.getPeliculas().observe(getViewLifecycleOwner(), peliculas -> {
            Log.d("DEPURACION", "4. Observer disparado en HomeFragment.");
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
            if (peliculas != null) {
                todasLasPeliculas = peliculas;
                filtrarPeliculas();
            }
        });

        mainViewModel.getMensajes().observe(getViewLifecycleOwner(), msj -> {
            if (msj != null && swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            mainViewModel.cargarPeliculas(isAdmin);
        });

        return view;
    }

    // --- CLAVE: Cargamos datos cada vez que el fragmento vuelve a ser visible ---
    @Override
    public void onResume() {
        super.onResume();
        Log.d("DEPURACION", "HomeFragment onResume: Recargando peliculas...");

        // Ponemos la animación de carga
        swipeRefreshLayout.post(() -> swipeRefreshLayout.setRefreshing(true));

        // Pedimos los datos (esto detectará cambios hechos en AdminFormularioActivity)
        mainViewModel.cargarPeliculas(isAdmin);
    }

    private void filtrarPeliculas() {
        String textoBuscado = etFiltroNombre.getText() != null ? etFiltroNombre.getText().toString().toLowerCase().trim() : "";
        List<Pelicula> listaFiltrada = new ArrayList<>();

        for (Pelicula pelicula : todasLasPeliculas) {
            String titulo = pelicula.getTitulo();
            if (titulo != null && titulo.toLowerCase().contains(textoBuscado)) {
                listaFiltrada.add(pelicula);
            }
        }

        Log.d("DEPURACION", "5. Dibujando " + listaFiltrada.size() + " peliculas en pantalla.");
        adapter.setPeliculas(listaFiltrada);
    }
}