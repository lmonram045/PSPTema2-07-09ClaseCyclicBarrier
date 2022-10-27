package principal;

import java.util.concurrent.CyclicBarrier;

/***********************************************************************************************************************
 * Suma el total de 10 tandas de números dispuestos en una matriz. Para obtener la suma de cada tanda o fila, se lanza
 * un hilo controlado por una barrera CyclicBarrier de 5 hilos. Cada tanda de la matriz representa los valores
 * recaudados por un cobrador.
 *
 * El propósito de la barrera es desencadenar un procedimiento que suma los totales de cada tanda. En este ejemplo,
 * supondremos que interesa ir acumulando los valores recaudados cada 5 cobradores, 5 tandas de números.
 *
 * Como tenemos 10 hilos auxiliares, la barrera desencadenará este procedimiento 2 veces, lo que implica que se
 * ejecutará de forma cíclica.
 *
 * La primera vez que lo hace, solo hay 5 hilos finalizados. Luego la suma obtenida, será únicamente una parte del total
 * buscado (cada hilo no finalizado contribuye con un 0).
 *
 * Sin embargo, la segunda vez todos los hilos habrán terminado. En este caso, la suma obtenida será el total buscado.
 */
public class Principal {

    // Matriz de 10 tandas o filas de numeros
    private static int tabla[][] = {
            {1},
            {1, 1},
            {1, 2, 1},
            {1, 3, 3, 1},
            {1, 4, 6, 4, 1},
            {1, 5, 10, 10, 5, 1},
            {1, 6, 15, 20, 15, 6, 1},
            {1, 7, 21, 35, 35, 21, 7, 1},
            {1, 8, 28, 56, 70, 56, 28, 8, 1},
            {1, 9, 36, 84, 126, 126, 84, 36, 9, 1}
    };

    private static int resultadoTanda[]; // Resultado de la suma de los elementos de cada tanda

    /*******************************************************************************************************************
     * Clase que define el hilo auxiliar, cuyo método run() se encarga de sumar los elementos de la tanda de números
     * recibida por su constructor.
     *
     * El constructor recibe también un objeto CyclicBarrier, que se usa para sincronizar los hilos auxiliares.
     */
    private static class SumaTanda extends Thread {
        int t; // Índice de la tanda de números a sumar (en este caso un entero de 0 a 4)
        CyclicBarrier barreraCiclica; // Barrera cíclica que se usa para sincronizar los hilos auxiliares

        /**
         * Constructor de la clase SumaTanda.
         *
         */
        SumaTanda(CyclicBarrier barreraCiclica, int t) {
            this.barreraCiclica = barreraCiclica;
            this.t = t;
        }

        /**
         * Método run() que suma los elementos de la tanda recibida por el constructor.
         *
         * Cuando finaliza esta suma y se almacena el valor, se incrementa en una unidad el número de hilos en espera
         * dentro de la barrera.
         *
         * Cuando ese número de elementos en espera sea el indicado más abajo por el constructor de la barrera (5 en
         * este caso), se desencadenará el procedimiento que obtiene la suma de todos ellos
         */
        @Override
        public void run() {
            int elementos = tabla[t].length; // Número de elementos de la tanda

            int sumaParcial = 0; // Suma parcial de los elementos de la tanda

            for (int i = 0; i < elementos; i++) {
                sumaParcial += tabla[t][i];
            }

            resultadoTanda[t] = sumaParcial; // Almacena el resultado de la suma de la tanda

            // Muestra un mensaje en la consola
            System.out.println("La suma de los elementos de la tanda " + t + " es " + sumaParcial);

            try {
                // Un hilo m'as que ha completado su trabajo y por tanto en espera dentro de la barrera
                barreraCiclica.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * Realiza la suma total de los elementos de la matriz, mediante el método sumaParcial de un objeto CyclicBarrier.
         */
        public static void main(String[] args) {

            final int NUM_TANDAS = tabla.length; // Número total de tandas (10, en este caso)
            resultadoTanda = new int[NUM_TANDAS]; // Inicializa el array (vector) de sumas de cada tanda

            /**
             * procedimiento de suma parcial que se ejecutará cada vez que se complete la barrera, implementando mediante
             * la clase Runnable.
             */
            Runnable sumaParcial = new Runnable() {

                int totalAcumulado; // Total acumulado de la suma de los elementos de la matriz

                // Suma los resultados de cada tanda (las que no hayan terminado sumaran 0)
                @Override
                public void run() {
                    totalAcumulado = 0; // reinicia el total

                    for (int i = 0; i < NUM_TANDAS; i++) {
                        totalAcumulado += resultadoTanda[i];
                    }

                    // imprime la suma total
                    System.out.println("\nBarrera completada. Total acumulado: " + totalAcumulado + "\n");
                }
            };

            // Crea una Barrera de Control que desencadenará un procedimiento sumaParcial, cuando el número de elementos
            // en espera dentro de ella sea 5. Este procedimiento será disparado por el último hilo desde el que se
            // invoque al método await() de la barrera.
            CyclicBarrier barreraCiclica = new CyclicBarrier(5, sumaParcial);

            // Lanza un nuevo hilo para cada tanda
            for (int i = 0; i < NUM_TANDAS; i++) {
                // Cada nuevo hilo recibe la Barrera Cíclica de control, y el índice de la tanda sobre la que actuar.
                new SumaTanda(barreraCiclica, i).start();
            }
        }
    }


}
