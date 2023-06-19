package es.damdi.gestorcomandaslahuerta;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.IOException;
import java.io.InputStream;

public class LaunchApp {
    public static void main(String[] args) {

        LaunchApp app= new LaunchApp();

        InputStream serviceAccount = null;
        serviceAccount = app.getClass().getResourceAsStream("key.json");

        FirebaseOptions options = null;
        try {
            options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://proyectofinal-29247-default-rtdb.europe-west1.firebasedatabase.app/")
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        FirebaseApp.initializeApp(options);

        App.main(args);
    }
}
