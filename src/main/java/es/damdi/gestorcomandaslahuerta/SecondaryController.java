package es.damdi.gestorcomandaslahuerta;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.google.firebase.database.FirebaseDatabase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class SecondaryController {

  FirebaseAuth auth;
  FirebaseDatabase db;

  @FXML
  private TextField nameField;

  @FXML
  private TextField emailField;

  @FXML
  private PasswordField passwordField;

  @FXML
  private Button registerButton;

  @FXML
  private void handleRegisterButton(ActionEvent event) {
      createUser();
  }

  @FXML
    public void createUser() {

        // Initialize Firebase app
        auth = FirebaseAuth.getInstance();
        db= FirebaseDatabase.getInstance("https://proyectofinal-29247-default-rtdb.europe-west1.firebasedatabase.app/");
    
        String email;
        String password;
        String nombre;

        email= emailField.getText();
        password= passwordField.getText();
        nombre= nameField.getText();
    
        try {
          CreateRequest request = new CreateRequest()
              .setEmail(email)
              .setEmailVerified(false)
              .setPassword(password);
          UserRecord userRecord = auth.createUser(request);

          Camarero c= new Camarero(email, nombre.toString(), password, false);

          db.getReference("camareros").child(email.toString().replace(".","-")).setValue(c, null);

          Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Employee Registration");
            Label dialogLabel = new Label("Successfully registered employee " + nombre);
            dialogLabel.setFont(new Font(16));
            Button dialogButton = new Button("OK");
            dialogButton.setOnAction(e -> dialog.close());
            VBox dialogVbox = new VBox(dialogLabel, dialogButton);
            dialogVbox.setAlignment(javafx.geometry.Pos.CENTER);
            Scene dialogScene = new Scene(dialogVbox, 400, 200);
            dialog.setScene(dialogScene);
            dialog.show();

          emailField.setText("");
          nameField.setText("");
          passwordField.setText("");

        } catch (FirebaseAuthException ex) {
          Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Employee Registration");
            Label dialogLabel = new Label("Error creando el usuario");
            dialogLabel.setFont(new Font(16));
            Button dialogButton = new Button("OK");
            dialogButton.setOnAction(e -> dialog.close());
            VBox dialogVbox = new VBox(dialogLabel, dialogButton);
            dialogVbox.setAlignment(javafx.geometry.Pos.CENTER);
            Scene dialogScene = new Scene(dialogVbox, 400, 200);
            dialog.setScene(dialogScene);
            dialog.show();

            ex.printStackTrace();
        }
    }

    @FXML
    private void cancelRegister() throws Throwable {
        this.finalize();
    }
}