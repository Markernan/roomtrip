package com.example.roomtrip.data.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** RF-REP-004: el reporte de ingresos por servicios adicionales se ordena de menor a mayor. */
public class IngresoServicioTest {

    private static List<String> nombres(List<IngresoServicio> lista) {
        List<String> nombres = new ArrayList<>();
        for (IngresoServicio ingreso : lista) nombres.add(ingreso.getNombre());
        return nombres;
    }

    @Test
    public void ordenaDeMenorAMayor() {
        List<IngresoServicio> desordenado = Arrays.asList(
                new IngresoServicio("Desayuno", 4250.00),
                new IngresoServicio("Lavandería", 620.50),
                new IngresoServicio("Masaje", 2340.00));

        List<IngresoServicio> ordenado = IngresoServicio.ordenarDeMenorAMayor(desordenado);

        assertEquals(Arrays.asList("Lavandería", "Masaje", "Desayuno"), nombres(ordenado));
    }

    @Test
    public void noModificaLaListaOriginal() {
        IngresoServicio caro = new IngresoServicio("Caro", 900);
        IngresoServicio barato = new IngresoServicio("Barato", 100);
        List<IngresoServicio> original = new ArrayList<>(Arrays.asList(caro, barato));

        List<IngresoServicio> ordenado = IngresoServicio.ordenarDeMenorAMayor(original);

        assertNotSame(original, ordenado);
        assertSame(caro, original.get(0));   // el original sigue en su orden
        assertSame(barato, ordenado.get(0));
    }

    @Test
    public void conMontosIgualesConservaElOrdenOriginal() {
        List<IngresoServicio> lista = Arrays.asList(
                new IngresoServicio("A", 500),
                new IngresoServicio("B", 100),
                new IngresoServicio("C", 500),
                new IngresoServicio("D", 100));

        assertEquals(Arrays.asList("B", "D", "A", "C"),
                nombres(IngresoServicio.ordenarDeMenorAMayor(lista)));
    }

    @Test
    public void listaVaciaOConUnSoloElemento() {
        assertTrue(IngresoServicio.ordenarDeMenorAMayor(new ArrayList<>()).isEmpty());
        assertEquals(1, IngresoServicio.ordenarDeMenorAMayor(
                Arrays.asList(new IngresoServicio("Único", 10))).size());
    }

    @Test
    public void losDatosDeEjemploDeMockDataEstanDesordenadosParaProbarElOrden() {
        // Si MockData ya viniera ordenado, el reporte "funcionaría" aunque el orden no se aplicara.
        List<IngresoServicio> ejemplo = com.example.roomtrip.data.MockData.getIngresosServiciosEjemplo();
        List<IngresoServicio> ordenado = IngresoServicio.ordenarDeMenorAMayor(ejemplo);

        assertTrue("los datos de ejemplo deben venir desordenados", !nombres(ejemplo).equals(nombres(ordenado)));
        for (int i = 1; i < ordenado.size(); i++) {
            assertTrue(ordenado.get(i - 1).getMonto() <= ordenado.get(i).getMonto());
        }
    }
}
