import java.util.concurrent.CyclicBarrier;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("INICIO");
        
        int cantidadProcesos = 7;
        int cantidadProductos = 3;
        int tamanioBuffer = 5;

        Buffer bufferInicial = new Buffer(tamanioBuffer);
        Buffer bufferMitad = new Buffer(tamanioBuffer);
        Buffer bufferFinal = new Buffer(2000000);

        CyclicBarrier barrier = new CyclicBarrier(cantidadProcesos + 1);

        Proceso procesoNaranjaUno = new Proceso(0, "naranja", cantidadProductos, bufferInicial, bufferMitad, bufferFinal, cantidadProcesos, barrier);
        Proceso procesoNaranjaDos = new Proceso(1, "naranja", cantidadProductos, bufferInicial, bufferMitad, bufferFinal, cantidadProcesos, barrier);
        Proceso procesoNaranjaTres = new Proceso(2, "naranja", cantidadProductos, bufferInicial, bufferMitad, bufferFinal, cantidadProcesos, barrier);
        procesoNaranjaUno.start();
        procesoNaranjaDos.start();
        procesoNaranjaTres.start();

        for (int i = 1; i < cantidadProcesos; i++) {
            Proceso procesoAzulUno = new Proceso(0, "azul", cantidadProductos, bufferInicial, bufferMitad, bufferFinal, cantidadProcesos, barrier);
            Proceso procesoAzulDos = new Proceso(1, "azul", cantidadProductos, bufferInicial, bufferMitad, bufferFinal, cantidadProcesos, barrier);
            Proceso procesoAzulTres = new Proceso(2, "azul", cantidadProductos, bufferInicial, bufferMitad, bufferFinal, cantidadProcesos, barrier);
            procesoAzulUno.start();
            procesoAzulDos.start();
            procesoAzulTres.start();
        }

        Proceso etaFinal = new Proceso(3, "final", cantidadProductos, bufferInicial, bufferMitad, bufferFinal, cantidadProcesos, barrier);
        etaFinal.start();

        barrier.await();

    }
}
