import java.util.ArrayList;

public class Buffer {
    private ArrayList<Producto> buff; // Lista que almacena los productos en el buffer

    public Buffer(int tamanio) {
        this.buff = new ArrayList<Producto>();
    }
    
    // Retorna el tamaño actual del buffer
    public synchronized Integer getTamanio() {
        return buff.size();
    }
    
    // Almacena un producto naranja en el buffer
    public synchronized Boolean almacenarNaranja(Producto p) {
        try {
            // Se almacena el producto en el buffer 1
            buff.add(p);
            return true;
        } catch (IllegalStateException e) {
            // El buffer está lleno
            return false;
        }
    }
    
    // Almacena un producto azul en el buffer, notifica a los threads esperando si es necesario
    public synchronized void almacenarAzul(Producto p) {
        try {
            buff.add(p);
        } catch (IllegalStateException e) {
            // El buffer está lleno, no se pudo agregar el elemento.
            System.out.println("El buffer está lleno, no se pudo agregar el elemento.");
            return;
        }
        notify(); //Espera pasiva
    }
    
    // Almacena un producto rojo en el buffer
    public synchronized void almacenarRojo(Producto p) {
        buff.add(p);
    }

    // Extrae un producto rojo del buffer, retorna null si el buffer está vacío
    public synchronized Producto extraerRojo() {
        try {
            Producto i = buff.remove(0);
            return i;
        } catch (IndexOutOfBoundsException e) {
            // El buffer está vacío
            return null;
        }
    }
    
    // Extrae un producto naranja del buffer, retorna null si el buffer está vacío
    public synchronized Producto extraerNaranja() {
        try {
            Producto i = buff.remove(0);
            return i;
        } catch (IndexOutOfBoundsException e) {
            // El buffer está vacío
            return null;
        }
    }
    
    // Extrae un producto azul del buffer, espera si el buffer está vacío y notifica a los threads si es necesario
    public synchronized Producto extraerAzul() {
        Producto i = null;
        try {
            i = buff.remove(0);
        } catch (Exception e) {
            i = null;
        }
        while (i == null) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i = buff.remove(0);
        }
        notify();
        return i;
    }
}    