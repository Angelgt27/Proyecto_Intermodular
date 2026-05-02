package com.example.printergrado.ui.main;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.printergrado.R;
import com.example.printergrado.viewmodel.MainViewModel;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

public class ScannerFragment extends Fragment {

    private DecoratedBarcodeView barcodeScannerView;
    private MainViewModel mainViewModel;
    private String token;

    // Permiso de cámara
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) barcodeScannerView.resume();
                else Toast.makeText(getContext(), "Permiso de cámara denegado", Toast.LENGTH_LONG).show();
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scanner, container, false);

        barcodeScannerView = view.findViewById(R.id.barcode_scanner);
        barcodeScannerView.setStatusText("Escanea el QR de un ticket para validarlo");
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        SharedPreferences prefs = requireActivity().getSharedPreferences("CinePrefs", Context.MODE_PRIVATE);
        token = "Bearer " + prefs.getString("jwt_token", "");

        // Cuando escanea un QR...
        barcodeScannerView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (result.getText() == null || result.getText().isEmpty()) return;

                // Pausamos el escáner para no lanzar 20 peticiones por segundo
                barcodeScannerView.pause();

                // Enviamos el QR a Flask
                mainViewModel.escanearTicket(token, result.getText());
            }
        });

        // Cuando Flask responde, mostramos el Toast y reactivamos la cámara tras 2 segundos
        mainViewModel.getMensajes().observe(getViewLifecycleOwner(), msj -> {
            if (msj != null && msj.contains("✅") || msj != null && msj.contains("❌")) {
                new Handler(Looper.getMainLooper()).postDelayed(() -> barcodeScannerView.resume(), 2500);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            barcodeScannerView.resume();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        barcodeScannerView.pause();
    }
}