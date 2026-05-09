package com.example.printergrado.ui.main;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.example.printergrado.data.api.ApiClient;
import com.example.printergrado.data.api.ApiService;
import com.example.printergrado.data.model.Pelicula;
import com.example.printergrado.viewmodel.MainViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private MainViewModel mainViewModel;
    private PeliculaAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private List<Pelicula> todasLasPeliculas = new ArrayList<>();
    private Map<String, Integer> mapaCinesFiltro = new HashMap<>(); 


    private TextInputEditText etFiltroNombre;
    private TextInputEditText etFiltroFecha;
    private AutoCompleteTextView spinnerFiltroCine;
    private FloatingActionButton fabAgregarPelicula;
    private MaterialButton btnLimpiarFiltros;

    private boolean isAdmin = false;
    private boolean isSuperAdmin = false;

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
        btnLimpiarFiltros = view.findViewById(R.id.btnLimpiarFiltros);

        SharedPreferences prefs = requireActivity().getSharedPreferences("CinePrefs", Context.MODE_PRIVATE);
        String rol = prefs.getString("rol", "Usuario");
        isAdmin = "Admin".equals(rol);
        isSuperAdmin = "Superadmin".equals(rol);

        swipeRefreshLayout.setColorSchemeResources(R.color.rojo_cine);

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PeliculaAdapter();
        adapter.setRole(rol);
        rv.setAdapter(adapter);

        

        if (isSuperAdmin) {
            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            apiService.getTodosLosCines().enqueue(new Callback<List<Map<String, Object>>>() {
                @Override
                public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<String> nombresCines = new ArrayList<>();
                        nombresCines.add("Todos");
                        mapaCinesFiltro.put("Todos", -1);

                        for (Map<String, Object> c : response.body()) {
                            String nombre = String.valueOf(c.get("nombre"));
                            int id = ((Double) c.get("id_cine")).intValue();
                            nombresCines.add(nombre);
                            mapaCinesFiltro.put(nombre, id);
                        }

                        ArrayAdapter<String> adapterCines = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, nombresCines);
                        spinnerFiltroCine.setAdapter(adapterCines);
                        spinnerFiltroCine.setText("Todos", false);
                    }
                }
                @Override public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {}
            });
        }

        etFiltroNombre.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) { filtrarPeliculas(); }
        });

        spinnerFiltroCine.setOnItemClickListener((parent, view1, position, id) -> filtrarPeliculas());

        etFiltroFecha.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(requireContext(), (vista, year, month, dayOfMonth) -> {
                String fecha = String.format(Locale.getDefault(), "%02d-%02d-%04d", dayOfMonth, month + 1, year);
                etFiltroFecha.setText(fecha);
                aplicarFiltroDeRed();
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        });

        btnLimpiarFiltros.setOnClickListener(v -> {
            etFiltroNombre.setText("");
            etFiltroFecha.setText("");
            if (isSuperAdmin) spinnerFiltroCine.setText("Todos", false);
            aplicarFiltroDeRed();
        });

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        if (isAdmin || isSuperAdmin) {
            if (isAdmin && !isSuperAdmin) {
                View tilCine = view.findViewById(R.id.tilFiltroCine);
                if (tilCine != null) tilCine.setVisibility(View.GONE);
            }
            fabAgregarPelicula.setVisibility(View.VISIBLE);
            fabAgregarPelicula.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), AdminFormularioActivity.class);
                startActivity(intent);
            });
        }

        mainViewModel.getPeliculas().observe(getViewLifecycleOwner(), peliculas -> {
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            if (peliculas != null) {
                todasLasPeliculas = peliculas;
                filtrarPeliculas();
            }
        });

        mainViewModel.getMensajes().observe(getViewLifecycleOwner(), msj -> {
            if (msj != null && swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
        });

        swipeRefreshLayout.setOnRefreshListener(this::aplicarFiltroDeRed);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        aplicarFiltroDeRed();
    }

    private void aplicarFiltroDeRed() {
        String fechaSeleccionada = etFiltroFecha.getText() != null ? etFiltroFecha.getText().toString().trim() : "";
        if (fechaSeleccionada.isEmpty()) fechaSeleccionada = null;

        swipeRefreshLayout.post(() -> swipeRefreshLayout.setRefreshing(true));
        mainViewModel.cargarPeliculas(isAdmin || isSuperAdmin, fechaSeleccionada);
    }

    private void filtrarPeliculas() {
        String textoBuscado = etFiltroNombre.getText() != null ? etFiltroNombre.getText().toString().toLowerCase().trim() : "";
        String cineSeleccionado = spinnerFiltroCine.getText().toString();

        Integer cineId = null;
        if (isSuperAdmin && mapaCinesFiltro.containsKey(cineSeleccionado)) {
            int id = mapaCinesFiltro.get(cineSeleccionado);
            if (id != -1) cineId = id;
        }

        List<Pelicula> listaFiltrada = new ArrayList<>();

        for (Pelicula pelicula : todasLasPeliculas) {
            boolean matchNombre = pelicula.getTitulo() != null && pelicula.getTitulo().toLowerCase().contains(textoBuscado);
            boolean matchCine = (cineId == null || pelicula.getFkCine() == cineId);

            if (matchNombre && matchCine) {
                listaFiltrada.add(pelicula);
            }
        }
        adapter.setPeliculas(listaFiltrada);
    }
}