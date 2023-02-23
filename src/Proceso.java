import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CyclicBarrier;

public class Proceso extends Thread {
    private int id = 0;
    private Buffer bufferEtapa1;
    private Buffer bufferEtapa2;
    private Buffer bufferEtapaFinal;
    private String tipo;
    static private int cantidadProductos;
    static private ArrayList<Producto> pFinal = new ArrayList<Producto>();
    //Capacidad de procesos
    private Integer capProc;
    static private Identificador identificador = new Identificador();
    private CyclicBarrier barrier;


    public Proceso(int id, String tipo, int cantidadProductos, Buffer bufferEtapa1, Buffer bufferEtapa2, Buffer bufferEtapaFinal, Integer capProc, CyclicBarrier barrier) {
        this.id = id;
        this.tipo = tipo;
        Proceso.cantidadProductos = cantidadProductos;
        this.bufferEtapa1 = bufferEtapa1;
        this.bufferEtapa2 = bufferEtapa2;
        this.bufferEtapaFinal = bufferEtapaFinal;
        this.capProc = capProc;
        this.barrier = barrier;
    }

    private synchronized void etapa1() {

        for (Integer i = 0; i < cantidadProductos; i++) {
            Producto productoNuevo = new Producto(identificador.getId(), tipo, "");
            //Si el tipo del proceso es naranja, se crea un producto naranja
            if (tipo == "naranja") {
                while (!bufferEtapa1.almacenarNaranja(productoNuevo)) 
                {
                    Thread.yield(); //Espera semiactiva
                }
                
            } 
            //Si el tipo del proceso es azul, se crea un producto azul
            else if (tipo == "azul") 
            {
                //Se almacena el producto en el buffer 1
                bufferEtapa1.almacenarAzul(productoNuevo);
            }
            else 
            {
                System.out.println("ERROR: Tipo de producto no reconocido");
            }

            //Información que se va a mostrar en la etapa final
            synchronized (this) {

                String mensaje = "Etapa 1: Proceso " + id + ".";

                String etapa = productoNuevo.getInfo();

                etapa += mensaje;

                productoNuevo.setInfo(etapa);
            }
        }
    }

    private void etapa2()
    {
        Integer temp = cantidadProductos;
        int numeroAleatorio = 0;
        while (temp > 0)
        {
            //Solo procesos naranjas pueden extraer productos naranjas de este buffer
            if (tipo == "naranja")
            {
                Producto productoExtraido;
                while((productoExtraido = bufferEtapa1.extraerNaranja()) == null)
                {
                    Thread.yield(); //Espera semiactiva
                }
                try {
                    //Numerio aleatorio entre 50 y 500 para simular el tiempo de procesamiento
                    numeroAleatorio = (int)(Math.random() * (500 - 50 + 1) + 50);
                    sleep(numeroAleatorio);
                } catch (Exception e) {
                    
                }
                //Se almacena el producto en el buffer 2
                bufferEtapa2.almacenarNaranja(productoExtraido);

                //Información que se va a mostrar en la etapa final
                synchronized (this) {

                    String mensaje = " Etapa 2: Proceso " + id + " proecesado en " + numeroAleatorio + " milisegundos.";
    
                    String etapa = productoExtraido.getInfo();
    
                    etapa += mensaje;
    
                    productoExtraido.setInfo(etapa);
                }
            }
            //Solo procesos azules pueden extraer productos azules de este buffer
            else if (tipo == "azul")
            {
                Producto productoExtraido;
                productoExtraido = bufferEtapa1.extraerAzul();
                try {
                    //Numerio aleatorio entre 50 y 500 para simular el tiempo de procesamiento
                    numeroAleatorio = (int)(Math.random() * (500 - 50 + 1) + 50);
                    sleep(numeroAleatorio);
                } catch (Exception e) {
                    
                }
                //Se almacena el producto en el buffer 2
                bufferEtapa2.almacenarAzul(productoExtraido);

                //Información que se va a mostrar en la etapa final
                synchronized (this) {

                    String mensaje = " Etapa 2: Proceso " + id + " proecesado en " + numeroAleatorio + " milisegundos.";
    
                    String etapa = productoExtraido.getInfo();
    
                    etapa += mensaje;
    
                    productoExtraido.setInfo(etapa);
                }
            }
            else
            {
                System.out.println("ERROR: Tipo de producto no reconocido");
            }
            temp--;

        }
    }

    private void etapa3()
    {
        Integer temp = cantidadProductos;
        int numeroAleatorio = 0;
        while (temp > 0)
        {
            Producto productoExtraido;
            //Se extrae el producto sin importar si es naranja o azul
            while((productoExtraido = bufferEtapa2.extraerRojo()) == null)
            {
                //Nada espera activa
            }
            try {
                //Numerio aleatorio entre 50 y 500 para simular el tiempo de procesamiento
                numeroAleatorio = (int)(Math.random() * (500 - 50 + 1) + 50);
                sleep(numeroAleatorio);
            } catch (Exception e) {
                
            }
            //Se almacena el producto en el buffer final, sin importar si este es naranja o azul
            bufferEtapaFinal.almacenarRojo(productoExtraido);

            //Información que se va a mostrar en la etapa final
            synchronized (this) {

                String mensaje = " Etapa 3: Proceso " + id + " proecesado en " + numeroAleatorio + " milisegundos.";

                String etapa = productoExtraido.getInfo();

                etapa += mensaje;

                productoExtraido.setInfo(etapa);
            }

            temp--;
        }
    }

    public void etapaFinal()
    {
        Integer temp = cantidadProductos * capProc;
        while (temp > 0)
        {
            Producto productoExtraido;
            while((productoExtraido = bufferEtapaFinal.extraerRojo()) == null)
            {
                //Nada espera activa
            }
            try {
                sleep(200);
            } catch (Exception e) {
            }
            pFinal.add(productoExtraido);
            temp--;
        }

        //Se ordenen los productos por el orden en el que fuern creados, el cuale está dado por el identificador
        Collections.sort(pFinal, (p1, p2) -> p1.getId() - p2.getId());
    }

    public void run() {
        if(id == 0)
        {
            etapa1();
        }
        else if(id == 1)
        {
            etapa2();
        }
        else if(id == 2)
        {
            etapa3();
            try {
                //Se espera a que todos los productos terminen las etapas 1, 2 y 3
                barrier.await();
            } catch (Exception e) {
                System.out.println("No se puede hacer el await");
            }
        }
        else if(id == 3)
        {
            etapaFinal();

            //Se muestra la información de los productos de forma sincronizada, para que los mensajes finales no se vean afectados por la concurrencia
            synchronized (this)
            {
                System.out.println("\n------- Etapa 1 -------");
                System.out.println("Productos restantes en el buffer de la etapa 1: " + bufferEtapa1.getTamanio());
                System.out.println("------------------------");

                System.out.println("\n------- Etapa 2 -------");
                System.out.println("Productos restantes en el buffer de la etapa 1: " + bufferEtapa2.getTamanio());
                System.out.println("------------------------");

                System.out.println("\n------- Etapa 3 -------");
                System.out.println("Productos restantes en el buffer de la etapa 3: " + bufferEtapaFinal.getTamanio());
                System.out.println("------------------------");

                System.out.println("\n------- Etapa final -------");
                System.out.println("------------------------");
                System.out.println("Estos son los productos de la etapa final:");

                for (int i = 0; i < pFinal.size(); i++) {
                    int id = pFinal.get(i).getId();
                    System.out.println("\n------- Producto " + id + " --------");
                    System.out.println("Producto " + id + " extraido" + " de tipo " + pFinal.get(i).getTipo());
                    System.out.println(pFinal.get(i).getInfo());
                }
                System.out.println("\nFin de la operación");
            } 
        }
        else
        {
            System.out.println("ERROR: ID de etapa no reconocido");
        }
    }
}
