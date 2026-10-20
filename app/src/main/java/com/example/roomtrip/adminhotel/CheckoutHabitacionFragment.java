package com.example.roomtrip.adminhotel;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.roomtrip.R;
import com.example.roomtrip.data.model.Checkout;
import com.example.roomtrip.data.model.CobroAdicional;
import com.example.roomtrip.data.model.EstadoCheckout;
import com.example.roomtrip.databinding.FragmentCheckoutHabitacionBinding;

import java.util.EnumSet;

public class CheckoutHabitacionFragment extends Fragment {

    private FragmentCheckoutHabitacionBinding binding;
    private AdminRepository repository;
    private String idCheckout;

    public CheckoutHabitacionFragment() {
        super(R.layout.fragment_checkout_habitacion);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repository = AdminRepository.getInstance(requireContext());
        if (getArguments() != null) {
            idCheckout = getArguments().getString("id_checkout");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentCheckoutHabitacionBinding.bind(view);

        // Flecha atrás funcional
        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        // Si vinimos por deep link, buscamos el checkout específico
        cargarDatosCheckout();

        binding.btnProcesarTransaccion.setOnClickListener(v -> procesarTransaccion());
    }

    private void cargarDatosCheckout() {
        if (idCheckout == null) return;
        
        // Simulación: buscar el checkout en el repo por habitación
        for (Checkout c : repository.getCheckouts()) {
            if (c.getHabitacion().equals(idCheckout)) {
                binding.tvGuestName.setText(c.getNombreHuesped());
                binding.tvRoomNumber.setText(getString(R.string.checkout_hab_checkout_habitacion_con_numero, c.getHabitacion()));
                break;
            }
        }
    }

    /** RN-012: si se escribe un cobro adicional, monto, motivo y observación son obligatorios. */
    private void procesarTransaccion() {
        String motivo = texto(binding.etDamageDescription);
        String montoStr = texto(binding.etDamageAmount);
        String observacion = texto(binding.etDamageObservation);

        binding.etDamageDescription.setError(null);
        binding.etDamageAmount.setError(null);
        binding.etDamageObservation.setError(null);

        EnumSet<CobroAdicional.Error> errores = CobroAdicional.validar(motivo, montoStr, observacion);
        if (!errores.isEmpty()) {
            if (errores.contains(CobroAdicional.Error.MOTIVO_OBLIGATORIO)) {
                binding.etDamageDescription.setError(getString(R.string.cobro_error_motivo));
            }
            if (errores.contains(CobroAdicional.Error.MONTO_OBLIGATORIO)) {
                binding.etDamageAmount.setError(getString(R.string.cobro_error_monto_obligatorio));
            }
            if (errores.contains(CobroAdicional.Error.MONTO_INVALIDO)) {
                binding.etDamageAmount.setError(getString(R.string.cobro_error_monto_invalido));
            }
            if (errores.contains(CobroAdicional.Error.OBSERVACION_OBLIGATORIA)) {
                binding.etDamageObservation.setError(getString(R.string.cobro_error_observacion));
            }
            return;
        }

        Double montoAdicional = CobroAdicional.parsearMonto(montoStr);
        double totalExtra = (montoAdicional != null ? montoAdicional : 0.0);

        // Actualizar en repositorio persistente
        String huesped = binding.tvGuestName.getText().toString();
        String hab = idCheckout != null ? idCheckout : "???";
        
        // El monto total sería el base (simulado) + el extra
        Checkout actualizado = new Checkout(huesped, hab, "Finalizado", EstadoCheckout.PROCESADO, 510.0 + totalExtra);
        repository.actualizarCheckout(actualizado);

        // TODO(Lab6-Firebase): Registrar en Firestore
        
        if (totalExtra == 0) {
            Toast.makeText(requireContext(), R.string.checkout_procesado, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(),
                    getString(R.string.checkout_procesado_con_cargo, totalExtra),
                    Toast.LENGTH_LONG).show();
        }

        Navigation.findNavController(requireView()).navigateUp();
    }

    private static String texto(EditText campo) {
        return campo.getText().toString().trim();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
