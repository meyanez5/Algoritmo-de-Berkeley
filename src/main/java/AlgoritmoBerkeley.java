import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AlgoritmoBerkeley {

    static class Nodo extends Thread {
        private String nombre;
        private int reloj;
        private int ajuste;

        public Nodo(String nombre, int reloj) {
            this.nombre = nombre;
            this.reloj = reloj;
            this.ajuste = 0;
        }

        public int obtenerReloj() {
            return reloj + ajuste;
        }

        public void ajustarReloj(int ajuste) {
            this.ajuste += ajuste;
        }

        @Override
        public void run() {
            System.out.println(nombre + " iniciado con reloj: " + reloj);
        }
    }

    private static void sincronizarRelojes(List<Nodo> nodos) {
        Nodo maestro = nodos.get(0);
        System.out.println("\nReloj del Maestro: " + maestro.obtenerReloj());

        // Umbral de desincronización máxima aceptable
        final int umbralDesincronizacion = 20;

        // Recopilar las diferencias de tiempo
        int relojMaestro = maestro.obtenerReloj();
        int tiempoTotal = relojMaestro;
        int conteo = 1;

        // Lista para nodos válidos (dentro del umbral)
        List<Nodo> nodosValidos = new ArrayList<>();
        nodosValidos.add(maestro);

        // Iterar sobre los nodos esclavos para recopilar los tiempos
        for (int i = 1; i < nodos.size(); i++) {
            Nodo nodo = nodos.get(i);
            int relojNodo = nodo.obtenerReloj();
            System.out.println(nodo.nombre + " reloj: " + relojNodo);

            int diferencia = Math.abs(relojMaestro - relojNodo);
            if (diferencia <= umbralDesincronizacion) {
                nodosValidos.add(nodo);
                tiempoTotal += relojNodo;
                conteo++;
            } else {
                System.out.println("Nodo " + nodo.nombre + " excluido por diferencia de tiempo excesiva: " + diferencia);
            }
        }

        // Calcular el tiempo promedio
        int tiempoPromedio = tiempoTotal / conteo;
        System.out.println("\nTiempo promedio calculado: " + tiempoPromedio);

        // Ajustar los relojes de los nodos válidos
        for (Nodo nodo : nodosValidos) {
            int ajuste = tiempoPromedio - nodo.obtenerReloj();
            nodo.ajustarReloj(ajuste);
            System.out.println("Ajuste realizado a " + nodo.nombre + ": " + ajuste);
        }
    }

    public static void main(String[] args) {
        // Crear nodos con relojes iniciales aleatorios
        List<Nodo> nodos = new ArrayList<>();
        Random random = new Random();

        // El primer nodo será el maestro
        nodos.add(new Nodo("Maestro", random.nextInt(100)));
        for (int i = 1; i <= 4; i++) {
            nodos.add(new Nodo("Nodo-" + i, random.nextInt(100)));
        }

        // Iniciar los nodos
        nodos.forEach(Thread::start);

        try {
            // Esperar que todos los nodos estén listos
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Sincronizar los relojes utilizando el algoritmo de Berkeley
        sincronizarRelojes(nodos);

        // Imprimir relojes sincronizados
        System.out.println("\nRelojes después de la sincronización:");
        for (Nodo nodo : nodos) {
            System.out.println(nodo.nombre + " reloj: " + nodo.obtenerReloj());
        }
    }


}
