package es.damdi.gestorcomandaslahuerta;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Employee {

    private final StringProperty email;
    private final StringProperty name;
    private final StringProperty password;
    private final BooleanProperty online;
    private final StringProperty uid;

    public Employee(String email, String name, String password, Boolean online, String uid) {
        this.email = new SimpleStringProperty(email);
        this.name = new SimpleStringProperty(name);
        this.password = new SimpleStringProperty(password);
        this.online= new SimpleBooleanProperty(online);
        this.uid= new SimpleStringProperty(uid);
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public StringProperty emailProperty() {
        return email;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getPassword() {
        return password.get();
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public Boolean getOnline() {
        return online.get();
    }

    public void setOnline(Boolean online) {
        this.online.set(online);
    }

    public BooleanProperty onlineProperty() {
        return online;
    }

    public String getUid() {
        return uid.get();
    }

    public void setUid(String uid) {
        this.uid.set(uid);
    }

    public StringProperty uidProperty() {
        return uid;
    }

}

