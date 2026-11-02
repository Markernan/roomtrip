package com.example.roomtrip.cliente;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomtrip.R;
import com.example.roomtrip.data.firebase.ChatRepository;
import com.example.roomtrip.data.model.Mensaje;
import com.example.roomtrip.data.model.MensajeChat;
import com.example.roomtrip.data.model.RolUsuario;
import com.example.roomtrip.utils.ErroresFirebase;
import com.example.roomtrip.utils.SesionFirebase;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

/**
 * Chat privado cliente–hotel de una reserva (Lab 6). Lo usan el cliente (desde Mis reservas) y el
 * administrador de hotel (desde Chats, con {@link #EXTRA_COMO_HOTEL}). Los mensajes llegan en tiempo
 * real desde {@code reservas/{reservaId}/mensajes} y persisten con fecha y hora.
 */
public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";

    public static final String EXTRA_RESERVA_ID = "reservaId";
    public static final String EXTRA_COMO_HOTEL = "comoHotel";
    public static final String EXTRA_TITULO = "tituloChat";

    private final List<Mensaje> mensajes = new ArrayList<>();
    private final ChatRepository repositorio = new ChatRepository();
    private MensajeAdapter adapter;
    private RecyclerView rvMensajes;
    private ListenerRegistration registro;
    private String uid;
    private String reservaId;
    private RolUsuario rol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uid = SesionFirebase.uidOVolverAlLogin(this);
        if (uid == null) return;
        setContentView(R.layout.activity_chat);

        reservaId = getIntent().getStringExtra(EXTRA_RESERVA_ID);
        rol = getIntent().getBooleanExtra(EXTRA_COMO_HOTEL, false) ? RolUsuario.ADMIN_HOTEL : RolUsuario.CLIENTE;
        String titulo = getIntent().getStringExtra(EXTRA_TITULO);
        if (titulo != null) {
            ((TextView) findViewById(R.id.tvNombreHotelChat)).setText(titulo);
        }

        findViewById(R.id.btnVolverChat).setOnClickListener(v -> finish());

        rvMensajes = findViewById(R.id.rvMensajes);
        rvMensajes.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MensajeAdapter(mensajes);
        rvMensajes.setAdapter(adapter);

        EditText etMensaje = findViewById(R.id.etMensaje);
        findViewById(R.id.btnEnviarMensaje).setOnClickListener(v -> {
            String texto = etMensaje.getText().toString().trim();
            if (texto.isEmpty() || reservaId == null) return;
            etMensaje.setText("");
            // El listener muestra el mensaje en cuanto se escribe (también sin conexión)
            repositorio.enviar(reservaId, texto, uid, rol, ignorado -> { }, e -> {
                Log.e(TAG, "No se pudo enviar el mensaje", e);
                Toast.makeText(this, ErroresFirebase.mensaje(this, e), Toast.LENGTH_LONG).show();
            });
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (uid == null || reservaId == null) return;
        registro = repositorio.escuchar(reservaId, lista -> {
            mensajes.clear();
            for (MensajeChat m : lista) mensajes.add(m.aVista(uid));
            adapter.notifyDataSetChanged();
            if (!mensajes.isEmpty()) rvMensajes.scrollToPosition(mensajes.size() - 1);
        }, e -> {
            Log.e(TAG, "No se pudo escuchar el chat", e);
            Toast.makeText(this, ErroresFirebase.mensaje(this, e), Toast.LENGTH_LONG).show();
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (registro != null) {
            registro.remove();
            registro = null;
        }
    }
}
