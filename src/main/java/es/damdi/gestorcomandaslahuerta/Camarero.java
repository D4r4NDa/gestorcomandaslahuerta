package es.damdi.gestorcomandaslahuerta;

import java.io.Serializable;

public class Camarero implements Serializable {

    private String email;
    private String nombre;
    private String password;
    private Boolean online;

    public Camarero(String email, String nombre, String password, Boolean online) {
        this.email = email;
        this.nombre = nombre;
        this.password = password;
        this.online = online;
    }

    public Camarero() {
        
    }

    public String getEmail() {
        return this.email;
    }

    public String getNombre() {
        return this.nombre;
    }

    public String getPassword() {
        return this.password;
    }

    public Boolean getOnline() {
        return this.online;
    }

    public void setNombre(String nombre) {
        this.nombre= nombre;
    }

    public void setEmail(String email) {
        this.email= email;
    }

    public void setPassword(String password) {
        this.password= password;
    }

    public void setOnline(Boolean online) {
        this.online= online;
    }
}

