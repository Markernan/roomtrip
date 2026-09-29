package com.example.roomtrip.cliente;

import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomtrip.R;
import com.example.roomtrip.data.MockData;
import com.example.roomtrip.data.model.Mensaje;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private final List<Mensaje> mensajes = new ArrayList<>();
    private MensajeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        findViewById(R.id.btnVolverChat).setOnClickListener(v -> finish());

        mensajes.addAll(MockData.getMensajesChatEjemplo());

        RecyclerView rvMensajes = findViewById(R.id.rvMensajes);
        rvMensajes.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MensajeAdapter(mensajes);
        rvMensajes.setAdapter(adapter);

        EditText etMensaje = findViewById(R.id.etMensaje);
        findViewById(R.id.btnEnviarMensaje).setOnClickListener(v -> {
            String texto = etMensaje.getText().toString().trim();
            if (!texto.isEmpty()) {
                mensajes.add(new Mensaje(texto, "Ahora", true));
                adapter.notifyItemInserted(mensajes.size() - 1);
                rvMensajes.scrollToPosition(mensajes.size() - 1);
                etMensaje.setText("");
            }
        });
    }
}