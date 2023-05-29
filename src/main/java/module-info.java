module es.damdi.gestorcomandaslahuerta {
    requires javafx.controls;
    requires javafx.fxml;
    requires firebase.admin;
    requires com.google.auth.oauth2;
    requires java.desktop;
    requires com.google.auth;

    opens es.damdi.gestorcomandaslahuerta to javafx.fxml, firebase.admin;
    exports es.damdi.gestorcomandaslahuerta;
}