package com.example.roomtrip.taxista;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.roomtrip.data.MockData;
import com.example.roomtrip.data.model.PedidoTaxi;
import com.example.roomtrip.databinding.ActivitySolicitadoBinding;
import com.example.roomtrip.utils.Constants;
import com.example.roomtrip.utils.NavegacionHelper;

import java.util.List;

public class SolicitadoActivity extends AppCompatActivity {

    private ActivitySolicitadoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySolicitadoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavegacionHelper.configurarBarra(this, "TRABAJO");

        // Data estática (Lab 4). En el Lab 6/7 vendrá de Firebase.
        List<PedidoTaxi> pedidos = MockData.getPedidosTaxiEjemplo();

        binding.rvPedidos.setLayoutManager(new LinearLayoutManager(this));
        binding.rvPedidos.setAdapter(new PedidoTaxiAdapter(pedidos, pedido -> {
            Intent intent = new Intent(this, EnCaminoActivity.class);
            intent.putExtra(Constants.EXTRA_PEDIDO_TAXI, pedido);
            startActivity(intent);
        }));

        binding.tvSinPedidos.setVisibility(pedidos.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
