package uade.edu.ar;

import uade.edu.ar.util.TransaccionUtils;
import uade.edu.progra3.AlgoritmoDeBlockchain;
import uade.edu.progra3.model.Bloque;
import uade.edu.progra3.model.Transaccion;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        int cantTransaccionesSimples = 5000;
        int cantTransaccionesDependencias = 5000;

        AlgoritmoDeBlockchain algoritmoDeBlockchain = new AlgoritmoDeBlockchainImpl();

        List<Transaccion> transacciones = TransaccionUtils.crearTransaccionesMixtas(cantTransaccionesSimples,cantTransaccionesDependencias,10,100,10);

        TransaccionUtils.firmarTransacciones(transacciones);

        long inicio = System.currentTimeMillis();
        List<List<Bloque>> blockchain = algoritmoDeBlockchain.construirBlockchain(transacciones, 1000, 100, 3, 5);
        long fin = System.currentTimeMillis();

        long tiempo = fin - inicio;

        TransaccionUtils.ImprimirBloques(blockchain);

        System.out.println("Entradas: ");
        System.out.println("   transacciones: " + cantTransaccionesSimples);
        System.out.println("   transacciones con dependencias:  " + cantTransaccionesDependencias);
        System.out.println("   total de transacciones: " + cantTransaccionesDependencias + cantTransaccionesSimples);
        System.out.println("El tiempo que tomó la ejecucion es : " + tiempo + "ms");
    }
}

