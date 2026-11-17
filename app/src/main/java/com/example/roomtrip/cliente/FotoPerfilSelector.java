package com.example.roomtrip.cliente;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.roomtrip.R;
import com.example.roomtrip.cliente.data.PerfilClienteRepository;
import com.example.roomtrip.data.firebase.FotosRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Foto de perfil del cliente: la elige con el selector de fotos del sistema (no pide permisos),
 * se guarda en almacenamiento interno y se muestra en el avatar. Lo usan Registro y Perfil, que
 * comparten los ids avatarFoto, tvElegirFoto, ivAvatarIcono e ivFotoPerfil.
 * Debe crearse en onCreate, antes de que la Activity llegue a STARTED.
 */
public class FotoPerfilSelector {

    private final AppCompatActivity activity;
    private final PerfilClienteRepository repositorio;
    private final ActivityResultLauncher<PickVisualMediaRequest> selector;
    private boolean fotoElegida;

    public FotoPerfilSelector(AppCompatActivity activity, PerfilClienteRepository repositorio) {
        this.activity = activity;
        this.repositorio = repositorio;
        this.selector = activity.registerForActivityResult(
                new ActivityResultContracts.PickVisualMedia(), this::alElegir);

        View.OnClickListener abrir = v -> selector.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
        activity.findViewById(R.id.avatarFoto).setOnClickListener(abrir);
        activity.findViewById(R.id.tvElegirFoto).setOnClickListener(abrir);

        mostrarGuardada();
    }

    /** True si en esta pantalla se eligió una foto nueva. */
    public boolean seElegioFoto() {
        return fotoElegida;
    }

    private void alElegir(Uri uri) {
        if (uri == null) {
            return;
        }
        if (repositorio.guardarFoto(uri)) {
            fotoElegida = true;
            mostrarGuardada();
            subirAStorage(uri);
        } else {
            Toast.makeText(activity, R.string.cliente_foto_error, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Copia la foto a Firebase Storage ({@code perfiles/{uid}.jpg}) y guarda su URL en {@code usuarios/{uid}.fotoUrl}.
     * Si falla (sin sesión, sin red, el perfil aún no existe durante el registro) queda la copia local.
     */
    private void subirAStorage(Uri uri) {
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        if (usuario == null) {
            return;
        }
        new FotosRepository().subirFotoPerfil(activity, usuario.getUid(), uri,
                url -> FirebaseFirestore.getInstance().collection("usuarios").document(usuario.getUid())
                        .update("fotoUrl", url)
                        .addOnFailureListener(e -> Log.w("FotoPerfil", "No se guardó fotoUrl", e)),
                e -> Log.w("FotoPerfil", "No se subió la foto de perfil a Storage", e));
    }

    private void mostrarGuardada() {
        Bitmap foto = repositorio.cargarFoto();
        if (foto == null) {
            return;
        }
        ImageView ivFoto = activity.findViewById(R.id.ivFotoPerfil);
        ivFoto.setImageBitmap(foto);
        ivFoto.setVisibility(View.VISIBLE);
        activity.findViewById(R.id.ivAvatarIcono).setVisibility(View.GONE);
    }
}
