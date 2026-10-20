package com.example.roomtrip.adminhotel;

import android.content.Context;
import android.util.Log;

import com.example.roomtrip.data.MockData;
import com.example.roomtrip.data.model.Habitacion;
import com.example.roomtrip.data.model.Servicio;
import com.example.roomtrip.data.model.Checkout;
import com.example.roomtrip.utils.StorageHelper;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repositorio para gestionar los datos del Administrador con persistencia local.
 * TODO(Lab6-Firebase): Migrar a Firebase Firestore/Realtime Database.
 */
public class AdminRepository {
    private static final String TAG = "AdminRepository";
    private static final String FILE_HABITACIONES = "admin_habitaciones.json";
    private static final String FILE_SERVICIOS = "admin_servicios.json";
    private static final String FILE_CHECKOUTS = "admin_checkouts.json";

    private static AdminRepository instance;
    private final Context context;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private List<Habitacion> habitaciones = new ArrayList<>();
    private List<Servicio> servicios = new ArrayList<>();
    private List<Checkout> checkouts = new ArrayList<>();

    private AdminRepository(Context context) {
        this.context = context.getApplicationContext();
        hidratar();
    }

    public static synchronized AdminRepository getInstance(Context context) {
        if (instance == null) {
            instance = new AdminRepository(context);
        }
        return instance;
    }

    private void hidratar() {
        // Cargar habitaciones con el tipo correcto para evitar LinkedTreeMap
        Type typeHab = new TypeToken<List<Habitacion>>(){}.getType();
        List<Habitacion> h = StorageHelper.leerJson(context, FILE_HABITACIONES, typeHab);
        if (h == null || h.isEmpty()) {
            habitaciones = MockData.getHabitacionesEjemplo();
            guardarHabitaciones();
        } else {
            habitaciones = h;
        }

        // Cargar servicios
        Type typeServ = new TypeToken<List<Servicio>>(){}.getType();
        List<Servicio> s = StorageHelper.leerJson(context, FILE_SERVICIOS, typeServ);
        if (s == null || s.isEmpty()) {
            servicios = MockData.getServiciosEjemplo();
            guardarServicios();
        } else {
            servicios = s;
        }

        // Cargar checkouts
        Type typeCheck = new TypeToken<List<Checkout>>(){}.getType();
        List<Checkout> c = StorageHelper.leerJson(context, FILE_CHECKOUTS, typeCheck);
        if (c == null || c.isEmpty()) {
            checkouts = MockData.getCheckoutsEjemplo();
            guardarCheckouts();
        } else {
            checkouts = c;
        }
    }

    public List<Habitacion> getHabitaciones() { return new ArrayList<>(habitaciones); }
    public List<Servicio> getServicios() { return new ArrayList<>(servicios); }
    public List<Checkout> getCheckouts() { return new ArrayList<>(checkouts); }

    public void agregarHabitacion(Habitacion h) {
        habitaciones.add(h);
        guardarHabitaciones();
    }

    public void agregarServicio(Servicio s) {
        servicios.add(s);
        guardarServicios();
    }

    public void actualizarCheckout(Checkout c) {
        for (int i = 0; i < checkouts.size(); i++) {
            Checkout item = checkouts.get(i);
            if (item.getNombreHuesped().equals(c.getNombreHuesped()) && item.getHabitacion().equals(c.getHabitacion())) {
                checkouts.set(i, c);
                break;
            }
        }
        guardarCheckouts();
    }

    private void guardarHabitaciones() {
        executor.execute(() -> StorageHelper.guardarJson(context, FILE_HABITACIONES, habitaciones));
    }

    private void guardarServicios() {
        executor.execute(() -> StorageHelper.guardarJson(context, FILE_SERVICIOS, servicios));
    }

    private void guardarCheckouts() {
        executor.execute(() -> StorageHelper.guardarJson(context, FILE_CHECKOUTS, checkouts));
    }
    
    public void limpiarCache() {
        executor.execute(() -> {
            context.deleteFile(FILE_HABITACIONES);
            context.deleteFile(FILE_SERVICIOS);
            context.deleteFile(FILE_CHECKOUTS);
            hidratar();
        });
    }
}
