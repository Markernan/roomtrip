package com.example.roomtrip.adminhotel;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.roomtrip.R;
import com.example.roomtrip.data.firebase.ReservaRepository;
import com.example.roomtrip.data.model.CobroAdicional;
import com.example.roomtrip.data.model.EstadoCheckout;
import com.example.roomtrip.data.model.Reserva;
import com.example.roomtrip.data.model.RolUsuario;
import com.example.roomtrip.databinding.FragmentCheckoutHabitacionBinding;
import com.example.roomtrip.utils.ErroresFirebase;
import com.example.roomtrip.utils.RegistroEventos;

import java.util.EnumSet;

/**
 * Cobro del checkout (Lab 6): el cliente ya hizo checkout desde la app y el hotel cobra la tarjeta
 * registrada (simulada) más un cobro adicional por daños si lo hay (RN-012). La reserva queda
 * FINALIZADA y el cliente recibe el aviso de cobro.
 */
public class CheckoutHabitacionFragment extends Fragment {

    private static final String TAG = "CheckoutHabitacion";

    private FragmentCheckoutHabitacionBinding binding;
    private final ReservaRepository repositorio = new ReservaRepository();
    private String reservaId;
    private Reserva reserva;

    public CheckoutHabitacionFragment() {
        super(R.layout.fragment_checkout_habitacion);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            reservaId = getArguments().getString("id_checkout");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentCheckoutHabitacionBinding.bind(view);

        // Flecha atrás funcional
        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        binding.btnProcesarTransaccion.setEnabled(false);
        cargarDatosCheckout();

        binding.btnProcesarTransaccion.setOnClickListener(v -> procesarTransaccion());
    }

    private void cargarDatosCheckout() {
        if (reservaId == null) return;
        repositorio.obtener(reservaId, r -> {
            if (binding == null) return;
            if (r == null) {
                Toast.makeText(requireContext(), R.string.checkout_no_encontrado, Toast.LENGTH_LONG).show();
                return;
            }
            reserva = r;
            mostrar(r);
        }, this::mostrarError);
    }

    private void mostrar(Reserva r) {
        binding.tvGuestName.setText(r.getClienteNombre());
        binding.tvRoomNumber.setText(getString(R.string.checkout_hab_checkout_habitacion_con_numero, r.getHabitacionTipo()));
        binding.tvFechasEstadia.setText(getString(R.string.checkout_fechas_estadia, r.getFechaEntrada(), r.getFechaSalida()));
        binding.tvConceptoAlojamiento.setText(getResources().getQuantityString(
                R.plurals.checkout_alojamiento_noches, r.getNoches(), r.getNoches()));
        binding.tvMontoAlojamiento.setText(getString(R.string.superadmin_monto_soles, r.getMontoTotal()));
        binding.tvConcepto2.setText(R.string.checkout_valoracion_cliente);
        binding.tvMonto2.setText(r.getValoracion() > 0 ? getString(R.string.checkout_estrellas, r.getValoracion())
                : getString(R.string.superadmin_sin_dato));
        binding.tvConcepto3.setText(R.string.checkout_taxi_aeropuerto);
        binding.tvMonto3.setText(r.isTaxiSolicitado() ? R.string.checkout_taxi_solicitado : R.string.checkout_taxi_no);

        boolean pendiente = r.getEstadoCheckout() == EstadoCheckout.PENDIENTE;
        binding.tvEstadoCheckout.setText(r.getEstadoCheckout() != null ? r.getEstadoCheckout().getEtiqueta()
                : getString(R.string.checkout_sin_solicitar));
        binding.tvHoraCheckout.setText(r.getComentario() != null ? r.getComentario() : "");
        binding.tvTotalAcumulado.setText(getString(R.string.superadmin_monto_soles,
                r.getMontoCobrado() > 0 ? r.getMontoCobrado() : r.getMontoTotal()));
        binding.btnProcesarTransaccion.setEnabled(pendiente);
    }

    /** RN-012: si se escribe un cobro adicional, monto, motivo y observación son obligatorios. */
    private void procesarTransaccion() {
        if (reserva == null) return;
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
        boolean hayCargo = totalExtra > 0;

        binding.btnProcesarTransaccion.setEnabled(false);
        repositorio.procesarCheckout(reserva.getId(), totalExtra, hayCargo ? motivo : null, hayCargo ? observacion : null,
                cobrada -> {
                    if (cobrada == null) {
                        mensaje(getString(R.string.checkout_ya_procesado));
                        return;
                    }
                    RegistroEventos.registrar(RegistroEventos.COBRO_REALIZADO, RolUsuario.ADMIN_HOTEL,
                            "Cobro de la reserva " + cobrada.getId() + " por S/ " + cobrada.getMontoCobrado());
                    mensaje(hayCargo ? getString(R.string.checkout_procesado_con_cargo, totalExtra)
                            : getString(R.string.checkout_procesado));
                    if (binding != null) Navigation.findNavController(binding.getRoot()).navigateUp();
                }, e -> {
                    if (binding != null) binding.btnProcesarTransaccion.setEnabled(true);
                    mostrarError(e);
                });
    }

    private void mensaje(String texto) {
        if (isAdded()) Toast.makeText(requireContext(), texto, Toast.LENGTH_LONG).show();
    }

    private void mostrarError(Exception e) {
        Log.e(TAG, "Error de Firestore", e);
        if (isAdded()) mensaje(ErroresFirebase.mensaje(requireContext(), e));
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
