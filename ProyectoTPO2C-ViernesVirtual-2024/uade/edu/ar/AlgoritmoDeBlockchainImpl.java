package uade.edu.ar;

import uade.edu.progra3.AlgoritmoDeBlockchain;
import uade.edu.progra3.model.Bloque;
import uade.edu.progra3.model.Transaccion;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AlgoritmoDeBlockchainImpl implements AlgoritmoDeBlockchain {

    @Override
    public List<List<Bloque>> construirBlockchain(List<Transaccion> transacciones,
                                                  int maxTamanioBloque,
                                                  int maxValorBloque,
                                                  int maxTransacciones,
                                                  int maxBloques) {
        List<List<Bloque>> soluciones = new ArrayList<>();
        List<Bloque> bloquesActuales = new ArrayList<>();
        Set<Transaccion> transaccionesUsadas = new HashSet<>();

        construirBlockchainRecursivo(transacciones, maxTamanioBloque, maxValorBloque, maxTransacciones, maxBloques,
                0, transaccionesUsadas, bloquesActuales, soluciones);

        return soluciones;
    }

    private void construirBlockchainRecursivo(List<Transaccion> transacciones, int maxTamanioBloque,
                                              int maxValorBloque, int maxTransacciones, int maxBloques,
                                              int indice, Set<Transaccion> transaccionesUsadas,
                                              List<Bloque> bloquesActuales, List<List<Bloque>> soluciones) {
        // Si se alcanza el límite de bloques, se evalúa la solución actual
        if (bloquesActuales.size() > maxBloques) {
            return;
        }

        // Agregar la solución si todos los bloques son válidos
        if (!bloquesActuales.isEmpty()) {
            soluciones.add(new ArrayList<>(bloquesActuales));
        }

        // Intentar construir más bloques a partir de las transacciones restantes
        for (int i = indice; i < transacciones.size(); i++) {
            Transaccion transaccion = transacciones.get(i);

            // Si la transacción ya se usó, se omite
            if (transaccionesUsadas.contains(transaccion)) {
                continue;
            }

            // Crear un nuevo bloque y agregar la transacción
            Bloque bloque = new Bloque();
            List<Transaccion> transaccionesEnBloque = new ArrayList<>();
            transaccionesEnBloque.add(transaccion);

            // Rastrear la transacción como usada
            transaccionesUsadas.add(transaccion);

            // Continuar agregando transacciones al bloque mientras sea válido
            for (int j = i + 1; j < transacciones.size(); j++) {
                Transaccion siguienteTransaccion = transacciones.get(j);

                // Verificar si la transacción se puede agregar
                if (!transaccionesUsadas.contains(siguienteTransaccion) &&
                        sePuedeAgregar(siguienteTransaccion, transaccionesEnBloque, maxTamanioBloque, maxValorBloque, maxTransacciones)) {
                    transaccionesEnBloque.add(siguienteTransaccion);
                    transaccionesUsadas.add(siguienteTransaccion);
                }
            }

            // Completar el bloque y agregarlo a la lista actual
            bloque.setTransacciones(new ArrayList<>(transaccionesEnBloque));
            bloque.setValorTotal(calcularValorTotal(transaccionesEnBloque));
            bloque.setTamanioTotal(calcularTamanioTotal(transaccionesEnBloque));
            bloquesActuales.add(bloque);

            // Llamada recursiva con el siguiente índice
            construirBlockchainRecursivo(transacciones, maxTamanioBloque, maxValorBloque, maxTransacciones, maxBloques,
                    i + 1, transaccionesUsadas, bloquesActuales, soluciones);

            // Backtracking: revertir los cambios
            bloquesActuales.remove(bloquesActuales.size() - 1);
            transaccionesUsadas.removeAll(transaccionesEnBloque);
        }
    }

    private boolean sePuedeAgregar(Transaccion transaccion, List<Transaccion> bloqueActual,
                                   int maxTamanioBloque, int maxValorBloque, int maxTransacciones) {
        // Verificar dependencia
        if (transaccion.getDependencia() != null && !bloqueActual.contains(transaccion.getDependencia())) {
            return false;
        }

        // Calcular el estado actual del bloque con la nueva transacción
        int nuevoTamanio = calcularTamanioTotal(bloqueActual) + transaccion.getTamanio();
        int nuevoValor = calcularValorTotal(bloqueActual) + transaccion.getValor();
        int nuevoNumeroTransacciones = bloqueActual.size() + 1;

        // Validar las restricciones del bloque
        return nuevoTamanio <= maxTamanioBloque &&
                nuevoValor <= maxValorBloque &&
                nuevoNumeroTransacciones <= maxTransacciones &&
                transaccion.getFirmasActuales() >= transaccion.getFirmasRequeridas();
    }

    private int calcularTamanioTotal(List<Transaccion> bloque) {
        return bloque.stream().mapToInt(Transaccion::getTamanio).sum();
    }

    private int calcularValorTotal(List<Transaccion> bloque) {
        return bloque.stream().mapToInt(Transaccion::getValor).sum();
    }
}
