package com.example.roomtrip.superadmin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.roomtrip.R;
import com.example.roomtrip.data.MockData;
import com.example.roomtrip.data.model.LogEvento;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

public class AuditoriaActivity extends AppCompatActivity {

    private RecyclerView rvLogs;
    private ImageButton btnBack;
    private LogAdapter adapter;
    private List<LogEvento> listaLogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_superadmin_auditoria);

        rvLogs = findViewById(R.id.rvLogs);
        btnBack = findViewById(R.id.btnBack);

        rvLogs.setLayoutManager(new LinearLayoutManager(this));

        cargarDatosMock();

        adapter = new LogAdapter(this, listaLogs);
        rvLogs.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        setupBottomNavigation();
    }

    private void cargarDatosMock() {
        listaLogs = cargarLogsDesdeCache();
        if (listaLogs == null || listaLogs.isEmpty()) {
            listaLogs = MockData.getLogsEjemplo();
            guardarLogsEnCache(listaLogs);
        }
    }

    private void guardarLogsEnCache(List<LogEvento> logs) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(logs);
            FileOutputStream fos = openFileOutput("auditoria_logs.json", Context.MODE_PRIVATE);
            FileWriter writer = new FileWriter(fos.getFD());
            writer.write(json);
            writer.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<LogEvento> cargarLogsDesdeCache() {
        List<LogEvento> logs = null;
        try {
            FileInputStream fis = openFileInput("auditoria_logs.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            Gson gson = new Gson();
            Type type = new TypeToken<List<LogEvento>>(){}.getType();
            logs = gson.fromJson(reader, type);
            reader.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return logs;
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_auditoria);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_inicio) {
                startActivity(new Intent(this, DashboardActivity.class));
                return true;
            } else if (id == R.id.nav_hoteles) {
                startActivity(new Intent(this, HotelesActivity.class));
                return true;
            } else if (id == R.id.nav_usuarios) {
                startActivity(new Intent(this, UsuariosActivity.class));
                return true;
            } else if (id == R.id.nav_auditoria) {
                return true;
            }
            return false;
        });
    }
}
