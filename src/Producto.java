public class Producto {
    private Integer id;
    private String tipo;
    //Info contiene las etapas, tiempos de espera y procesos por los cuales pasó el producto
    private String info;

    public Producto(Integer id, String tipo, String info) {
        this.id = id;
        this.tipo = tipo;
        this.info = info;
    }

    public Integer getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
