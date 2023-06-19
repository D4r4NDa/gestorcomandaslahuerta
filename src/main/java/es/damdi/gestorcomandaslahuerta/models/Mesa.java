package es.damdi.gestorcomandaslahuerta.models;

import java.io.Serializable;
import java.util.List;

public class Mesa implements Serializable {
    private Long numMesa;
    private Camarero camarero;
    private Long estado;
    private List<ComidasPedidas> cPedidas;
    private List<BebidasPedidas> bPedidas;

    public Long getNumMesa() {
        return numMesa;
    }

    public void setNumMesa(Long numMesa) {
        this.numMesa = numMesa;
    }

    public Camarero getCamarero() {
        return camarero;
    }

    public void setCamarero(Camarero camarero) {
        this.camarero = camarero;
    }

    public Long getEstado() {
        return estado;
    }

    public void setEstado(Long estado) {
        this.estado = estado;
    }

    public List<ComidasPedidas> getCPedidas() {
        return cPedidas;
    }

    public void setCPedidas(List<ComidasPedidas> cPedidas) {
        this.cPedidas = cPedidas;
    }

    public List<BebidasPedidas> getBPedidas() {
        return bPedidas;
    }

    public void setBPedidas(List<BebidasPedidas> bPedidas) {
        this.bPedidas = bPedidas;
    }
}
