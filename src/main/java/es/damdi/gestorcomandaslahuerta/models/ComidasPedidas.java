package es.damdi.gestorcomandaslahuerta.models;

import java.io.Serializable;

public class ComidasPedidas implements Serializable {
    private Comida comida;
    private Long cantidad;

    public Comida getComida() {
        return comida;
    }

    public void setComida(Comida comida) {
        this.comida = comida;
    }

    public Long getCantidad() {
        return cantidad;
    }

    public void setCantidad(Long cantidad) {
        this.cantidad = cantidad;
    }
}
