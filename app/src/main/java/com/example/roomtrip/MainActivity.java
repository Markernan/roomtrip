package com.example.roomtrip;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtener el NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            NavGraph navGraph = navController.getNavInflater().inflate(R.navigation.nav_graph);

            // Leer el rol proveniente de LoginActivity
            String rolUsuario = getIntent().getStringExtra("ROL_USUARIO");

            if (rolUsuario != null) {
                switch (rolUsuario) {
                    case "SuperAdmin":
                        // Pantalla inicial para SuperAdmin
                        navGraph.setStartDestination(R.id.reportesFragment);
                        break;

                    case "Admin Hotel":
                    case "Cliente":
                    default:
                        // Pantalla inicial para Admin de Hotel
                        navGraph.setStartDestination(R.id.inicioFragment);
                        break;
                }
            }

            // Aplicar el grafo actualizado
            navController.setGraph(navGraph);

            // Conectar el BottomNavigationView con el NavController
            BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
            if (bottomNav != null) {
                NavigationUI.setupWithNavController(bottomNav, navController);
            }
        }
    }
}