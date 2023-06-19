package es.damdi.gestorcomandaslahuerta.models;

import java.io.Serializable;

public class BebidasPedidas implements Serializable {
    private Bebida bebida;
    private Long cantidad;

    public Bebida getBebida() {
        return bebida;
    }

    public void setBebida(Bebida bebida) {
        this.bebida = bebida;
    }

    public Long getCantidad() {
        return cantidad;
    }

    public void setCantidad(Long cantidad) {
        this.cantidad = cantidad;
    }
}
