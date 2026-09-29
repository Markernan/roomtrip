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
import com.example.roomtrip.data.model.CobroAdicional;
import com.example.roomtrip.databinding.FragmentCheckoutHabitacionBinding;

import java.util.EnumSet;

public class CheckoutHabitacionFragment extends Fragment {

    private FragmentCheckoutHabitacionBinding binding;

    public CheckoutHabitacionFragment() {
        super(R.layout.fragment_checkout_habitacion);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentCheckoutHabitacionBinding.bind(view);

        binding.btnProcesarTransaccion.setOnClickListener(v -> procesarTransaccion());
    }

    /** RN-012: si se escribe un cobro adicional, monto, motivo y observación son obligatorios. */
    private void procesarTransaccion() {
        String motivo = texto(binding.etDamageDescription);
        String monto = texto(binding.etDamageAmount);
        String observacion = texto(binding.etDamageObservation);

        binding.etDamageDescription.setError(null);
        binding.etDamageAmount.setError(null);
        binding.etDamageObservation.setError(null);

        EnumSet<CobroAdicional.Error> errores = CobroAdicional.validar(motivo, monto, observacion);
        if (!errores.isEmpty()) {
            // Se señala cada campo afectado en el propio campo (RNF-USA-005)
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

        // TODO: registrar el cobro y el cambio de estado en Firestore (Lab 6)
        if (CobroAdicional.estaVacio(motivo, monto, observacion)) {
            Toast.makeText(requireContext(), R.string.checkout_procesado, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(),
                    getString(R.string.checkout_procesado_con_cargo, CobroAdicional.parsearMonto(monto)),
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
