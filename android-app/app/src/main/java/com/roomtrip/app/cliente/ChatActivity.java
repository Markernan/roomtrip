package com.roomtrip.app.cliente;

import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.roomtrip.app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Chat privado entre el cliente y el hotel, disponible desde la confirmación de
 * la reserva hasta el fin del checkout (RF-CL-07). Los mensajes persisten con
 * fecha y hora (RF-GN-04).
 */
public class ChatActivity extends AppCompatActivity {

    private MensajeAdapter adapter;
    private final List<Mensaje> mensajes = new ArrayList<>();
    private RecyclerView rvMensajes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        findViewById(R.id.btnVolverChat).setOnClickListener(v -> finish());

        mensajes.add(new Mensaje("¡Bienvenido! ¿A qué hora llego?", "12:00", false));
        mensajes.add(new Mensaje("Llegarás a las 3:00 PM. Gracias.", "12:02", true));

        rvMensajes = findViewById(R.id.rvMensajes);
        rvMensajes.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MensajeAdapter(mensajes);
        rvMensajes.setAdapter(adapter);

        EditText etMensaje = findViewById(R.id.etMensaje);
        findViewById(R.id.btnEnviarMensaje).setOnClickListener(v -> {
            String texto = etMensaje.getText().toString().trim();
            if (texto.isEmpty()) return;
            // TODO: persistir en Realtime Database con fecha y hora reales (Lab 6).
            mensajes.add(new Mensaje(texto, "Ahora", true));
            adapter.notifyItemInserted(mensajes.size() - 1);
            rvMensajes.scrollToPosition(mensajes.size() - 1);
            etMensaje.setText("");
        });
    }
}
