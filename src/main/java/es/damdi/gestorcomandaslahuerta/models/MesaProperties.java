package es.damdi.gestorcomandaslahuerta.models;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class MesaProperties {

    private final LongProperty numMesa;
    private final LongProperty estado;
    private final ObjectProperty camarero;
    private final ListProperty cPedidas;
    private final ListProperty bPedidas;

    public MesaProperties(Long numMesa, Long estado, Camarero camarero, List<ComidasPedidas> cPedidas, List<BebidasPedidas> bPedidas) {
        this.numMesa = new SimpleLongProperty(numMesa);
        this.estado = new SimpleLongProperty(estado);
        this.camarero = new SimpleObjectProperty(camarero);
        this.cPedidas= new SimpleListProperty(FXCollections.observableArrayList(cPedidas));
        this.bPedidas= new SimpleListProperty(FXCollections.observableArrayList(bPedidas));
    }

    public Long getNumMesa() {
        return numMesa.get();
    }

    public void setNumMesa(Long numMesa) {
        this.numMesa.set(numMesa);
    }

    public LongProperty numMesaProperty() {
        return numMesa;
    }

    public Camarero getCamarero() {
        return (Camarero) camarero.get();
    }

    public void setCamarero(Camarero camarero) {
        this.camarero.set(camarero);
    }

    public ObjectProperty<Camarero> camareroProperty() {
        return camarero;
    }

    public Long getEstado() {
        return estado.get();
    }

    public void setEstado(Long estado) {
        this.estado.set(estado);
    }

    public LongProperty estadoProperty() {
        return estado;
    }

    public List<ComidasPedidas> getCPedidas() {
        return (List<ComidasPedidas>) cPedidas.get();
    }

    public void setCPedidas(List<ComidasPedidas> cPedidas) {
        this.cPedidas.set(FXCollections.observableArrayList(cPedidas));
    }

    public ListProperty<ComidasPedidas> cPedidasProperty() {
        return cPedidas;
    }

    public List<BebidasPedidas> getBPedidas() {
        return (List<BebidasPedidas>) bPedidas.get();
    }

    public void setBPedidas(List<BebidasPedidas> bPedidas) {
        this.bPedidas.set(FXCollections.observableArrayList(bPedidas));
    }

    public ListProperty<BebidasPedidas> bPedidasProperty() {
        return bPedidas;
    }

}
