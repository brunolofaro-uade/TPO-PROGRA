package uade.edu.ar;

import uade.edu.progra3.AlgoritmoDeBlockchain;
import uade.edu.progra3.model.Bloque;
import uade.edu.progra3.model.Transaccion;

import java.util.ArrayList;
import java.util.List;

public class AlgoritmoDeBlockchainImpl implements AlgoritmoDeBlockchain {

    @Override
    public List<List<Bloque>> construirBlockchain(List<Transaccion> transacciones,
                                                  int maxTamanioBloque,
                                                  int maxValorBloque,
                                                  int maxTransacciones,
                                                  int maxBloques) {
        List<List<Bloque>> soluciones = new ArrayList<>();
        List<Bloque> bloquesActuales = new ArrayList<>();
        List<Transaccion> transaccionesActuales = new ArrayList<>();
        Bloque bloqueActual = new Bloque();

        construirBlockchainRecursivo(transacciones, maxTamanioBloque, maxValorBloque, maxTransacciones,
                0, transaccionesActuales, soluciones, bloquesActuales, bloqueActual);

        return soluciones;
    }

    private void construirBlockchainRecursivo(List<Transaccion> transacciones, int maxTamanioBloque,
  int maxValorBloque, int maxTransacciones, int indice,
  List<Transaccion> transaccionesActuales, List<List<Bloque>> soluciones,
  List<Bloque> bloquesActuales, Bloque bloqueActual) {
        if (esBloqueValido(transaccionesActuales, maxTamanioBloque, maxValorBloque, maxTransacciones)) {
            bloqueActual.setValorTotal(calcularValorTotal(transaccionesActuales));
            bloqueActual.setTamanioTotal(calcularTamanioTotal(transaccionesActuales));
            bloqueActual.setTransacciones(new ArrayList<>(transaccionesActuales));

            Bloque nuevoBloque = new Bloque();
            nuevoBloque.setValorTotal(bloqueActual.getValorTotal());
            nuevoBloque.setTamanioTotal(bloqueActual.getTamanioTotal());
            nuevoBloque.setTransacciones(new ArrayList<>(bloqueActual.getTransacciones()));

            if (!nuevoBloque.getTransacciones().isEmpty()) {
                bloquesActuales.add(nuevoBloque);
                soluciones.add(new ArrayList<>(bloquesActuales));
            }

            for (int i = indice; i < transacciones.size(); i++) {
                Transaccion transaccion = transacciones.get(i);
                if (sePuedeAgregar(transaccion, transaccionesActuales)) {
                    transaccionesActuales.add(transaccion);
                    construirBlockchainRecursivo(transacciones, maxTamanioBloque, maxValorBloque, maxTransacciones,
                i + 1, transaccionesActuales, soluciones, bloquesActuales, bloqueActual);
                    transaccionesActuales.remove(transaccionesActuales.size() - 1);
                }
            }

            if (!bloquesActuales.isEmpty()) {
                bloquesActuales.remove(bloquesActuales.size() - 1);
            }
        }
    }

    private boolean esBloqueValido(List<Transaccion> bloque, int maxTamanioBloque, int maxValorBloque,
                                   int maxTransacciones) {
        int tamanioTotal = calcularTamanioTotal(bloque);
        int valorTotal = calcularValorTotal(bloque);
        int numTransacciones = bloque.size();
        return tamanioTotal <= maxTamanioBloque && valorTotal <= maxValorBloque &&
                numTransacciones <= maxTransacciones && valorTotal % 10 == 0;
    }

    private int calcularTamanioTotal(List<Transaccion> bloque) {
        int tamanioTotal = 0;
        for (Transaccion transaccion : bloque) {
            tamanioTotal += transaccion.getTamanio();
        }
        return tamanioTotal;
    }

    private int calcularValorTotal(List<Transaccion> bloque) {
        int valorTotal = 0;
        for (Transaccion transaccion : bloque) {
            valorTotal += transaccion.getValor();
        }
        return valorTotal;
    }

    private boolean sePuedeAgregar(Transaccion transaccion, List<Transaccion> transacciones) {
        Transaccion dependencia = transaccion.getDependencia();
        if (dependencia != null && !transacciones.contains(dependencia)) {
            return false;
        }
        return transaccion.getFirmasActuales() >= transaccion.getFirmasRequeridas();
    }
}
