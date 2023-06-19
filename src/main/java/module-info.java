module es.damdi.gestorcomandaslahuerta {
    requires javafx.controls;
    requires javafx.fxml;
    requires firebase.admin;
    requires com.google.auth.oauth2;
    requires java.desktop;
    requires com.google.auth;

    opens es.damdi.gestorcomandaslahuerta to javafx.fxml, firebase.admin;
    exports es.damdi.gestorcomandaslahuerta;
    exports es.damdi.gestorcomandaslahuerta.models;
    opens es.damdi.gestorcomandaslahuerta.models to firebase.admin, javafx.fxml;
    exports es.damdi.gestorcomandaslahuerta.controllers;
    opens es.damdi.gestorcomandaslahuerta.controllers to firebase.admin, javafx.fxml;
}