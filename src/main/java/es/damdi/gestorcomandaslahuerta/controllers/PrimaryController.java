package es.damdi.gestorcomandaslahuerta.controllers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import es.damdi.gestorcomandaslahuerta.App;
import es.damdi.gestorcomandaslahuerta.models.Camarero;
import es.damdi.gestorcomandaslahuerta.models.Employee;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import com.google.firebase.database.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.awt.EventQueue;
import java.util.regex.Pattern;


public class PrimaryController {

    private App mainApp;

    FirebaseDatabase db;
    FirebaseAuth auth;
    

    @FXML
    private TableView<Employee> employeeTable;

    @FXML
    private TableColumn<Employee, String> emailColumn;

    @FXML
    private TableColumn<Employee, String> nameColumn;

    @FXML
    private TextField emailField;

    @FXML
    private TextField nameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ImageView ivLogo;

    private ObservableList<Employee> employeeData = FXCollections.observableArrayList();

    public void initialize() throws IOException, InterruptedException {

        auth = FirebaseAuth.getInstance();
        db= FirebaseDatabase.getInstance("https://proyectofinal-29247-default-rtdb.europe-west1.firebasedatabase.app/");

        ArrayList<Camarero> empleados = new ArrayList<Camarero>();

        nameField= new TextField();
        emailField= new TextField();
        passwordField= new PasswordField();

        mainApp= new App();
        Image icon= new Image(mainApp.getClass().getResourceAsStream("icon.png"));
        ivLogo= new ImageView(icon);


        //*********************************************************************************************************************************************************************************************
        /**
         * Este hilo se encarga de comprobar constantemente si hay algún cambio en la base de datos, si lo hay actualiza los datos en local y la tabla
         * de mesas para representar la infromación actualizada.
         */
        new Thread(new Runnable() {
            @Override
            public void run() {

                db.getReference("camareros").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        empleados.clear();
                        employeeData.clear();
                        
                        for (DataSnapshot i : snapshot.getChildren()) {
                            try {
                                Map<String, Object> data = (Map<String, Object>) i.getValue();
                                Camarero c = new Camarero();
                                c.setNombre((String) data.get("nombre"));
                                c.setEmail((String) data.get("email"));
                                c.setPassword((String) data.get("password"));
                                c.setOnline((Boolean) data.get("online"));
                                c.setUid((String) data.get("uid"));
    
                                empleados.add(c);
                                System.out.println(c.getNombre());
                            }catch(DatabaseException e) {
                                System.err.println("Error al recuperar el camarero: " + e.getMessage());
                            }
                        }
                        // Update the UI on the EDT
                        EventQueue.invokeLater(new Runnable() {
                            public void run() {
                                // Update the UI here
                                emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
                                nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

                                for(Camarero e : empleados) {
                                    Employee emp= new Employee(e.getEmail(), e.getNombre(), e.getPassword(), e.getOnline(), e.getUid());
                                    employeeData.add(emp);

                                }
                                employeeTable.setItems(employeeData);
                                employeeTable.refresh();
                            }
                        });
                    }
                
                    @Override
                    public void onCancelled(DatabaseError error) {
                        System.out.print("ERROR ERROR ERROR");
                    }
                });
            }
        }).start();        


        //*************************************************************************************************************************************************************************************************************
    }

    /**
     * Este método se encarga de mostrar el formulario para rellenar los datos
     * de un nuevo camarero que se vaya a dar de alta.
     */
    @FXML
    private void showAddEmployeeDialog() {
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);

            Image icon= new Image(mainApp.getClass().getResourceAsStream("icon.png"));
            dialogStage.getIcons().add(icon);

            GridPane dialogRoot = new GridPane();
            dialogRoot.setAlignment(Pos.CENTER);
            dialogRoot.setHgap(10);
            dialogRoot.setVgap(10);
            dialogRoot.setPadding(new Insets(10));

            Label nameLabel = new Label("Nombre:");

            Label emailLabel = new Label("Email:");

            Label passwordLabel = new Label("Contraseña:");

            Button confirmButton = new Button("Confirmar");
            confirmButton.setOnAction(event -> {
                if(addEmployee()) {
                    dialogStage.close();
                }
            });

            Button cancelButton = new Button("Cancelar");
            cancelButton.setOnAction(event -> dialogStage.close());

            dialogRoot.add(nameLabel, 0, 0);
            dialogRoot.add(nameField, 1, 0);
            dialogRoot.add(emailLabel, 0, 1);
            dialogRoot.add(emailField, 1, 1);
            dialogRoot.add(passwordLabel, 0, 2);
            dialogRoot.add(passwordField, 1, 2);
            dialogRoot.add(confirmButton, 0, 3);
            dialogRoot.add(cancelButton, 1, 3);

            dialogStage.setScene(new Scene(dialogRoot));
            dialogStage.setTitle("Introduce los datos");
            dialogStage.setWidth(300);
            dialogStage.showAndWait();

    }

    /**
     * Este método es llamado por el formulario de introducción de datos del empleado
     * y es el que se encarga de añadirlo a la base de datos.
     * Si la alta a sido exitosa devuelve true, en caso contrario devuelve false.
     * @return
     */
    private boolean addEmployee() {
        String email;
        String password;
        String nombre;
        String uid;
        String error= "ALERTA:\n";
        boolean mostrarError= false;

        email= emailField.getText();
        password= passwordField.getText();
        nombre= nameField.getText();

        if(email.isEmpty() || !Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", email)) {
            error= error + "-- El email debe tener formato aaaaa@bbb.ccc\n";
            mostrarError= true;
        }else if(nombre.length()<=0) {
            error= error + "-- El nombre no puede estar vacio\n";
            mostrarError= true;
        }else if(password.length()<6) {
            error= error + "-- La contraseña debe tener mínimo 6 caracteres\n";
            mostrarError= true;
        }

        if(mostrarError) {
            Stage dialog = new Stage();
            Image icon= new Image(mainApp.getClass().getResourceAsStream("icon.png"));
            dialog.getIcons().add(icon);
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle(null);
            Label dialogLabel = new Label(error);
            dialogLabel.setFont(new Font(16));
            Button dialogButton = new Button("OK");
            dialogButton.setOnAction(e -> dialog.close());
            VBox dialogVbox = new VBox(dialogLabel, dialogButton);
            dialogVbox.setAlignment(javafx.geometry.Pos.CENTER);
            Scene dialogScene = new Scene(dialogVbox, 400, 200);
            dialog.setScene(dialogScene);
            dialog.show();
        }else {
            try {
                UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                        .setEmail(email)
                        .setEmailVerified(false)
                        .setPassword(password);
                UserRecord userRecord = auth.createUser(request);
                uid= userRecord.getUid();
                System.out.println(uid);

                Camarero c= new Camarero(email, nombre.toString(), password, false, uid.toString());

                db.getReference("camareros").child(email.toString().replace(".","-")).setValue(c, null);

                Stage dialog = new Stage();
                Image icon= new Image(mainApp.getClass().getResourceAsStream("icon.png"));
                dialog.getIcons().add(icon);
                dialog.initModality(Modality.APPLICATION_MODAL);
                dialog.setTitle(null);
                Label dialogLabel = new Label("Se ha registrado con exito al empleado");
                dialogLabel.setFont(new Font(16));
                Button dialogButton = new Button("Ok");
                dialogButton.setOnAction(e -> dialog.close());
                VBox dialogVbox = new VBox(dialogLabel, dialogButton);
                dialogVbox.setAlignment(javafx.geometry.Pos.CENTER);
                Scene dialogScene = new Scene(dialogVbox, 200, 200);
                dialog.setScene(dialogScene);
                dialog.show();

                emailField.setText("");
                nameField.setText("");
                passwordField.setText("");

                return true;

            } catch (FirebaseAuthException ex) {
                Stage dialog = new Stage();
                Image icon= new Image(mainApp.getClass().getResourceAsStream("icon.png"));
                dialog.getIcons().add(icon);
                dialog.initModality(Modality.APPLICATION_MODAL);
                dialog.setTitle("ERROR");
                Label dialogLabel = new Label("Algo salió mal al dar de alta al empleado.\nSi el eror persiste contacta al técnico");
                dialogLabel.setFont(new Font(16));
                Button dialogButton = new Button("OK");
                dialogButton.setOnAction(e -> dialog.close());
                VBox dialogVbox = new VBox(dialogLabel, dialogButton);
                dialogVbox.setAlignment(javafx.geometry.Pos.CENTER);
                Scene dialogScene = new Scene(dialogVbox, 400, 200);
                dialog.setScene(dialogScene);
                dialog.show();

                ex.printStackTrace();
                return false;
            }

        }
        return false;
    }

    /**
     * Este método se encarga de gestionar la baja de un empleado, cuando se ejecuta pide confirmación
     * de si se esta seguro y si se acepta borra al empleado de la base de datos.
     */
    @FXML
    private void deleteEmployee() {
        auth = FirebaseAuth.getInstance();
        Employee selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();

        if (selectedEmployee != null) {

            Alert alert= new Alert(Alert.AlertType.CONFIRMATION);
            Image icon= new Image(mainApp.getClass().getResourceAsStream("icon.png"));
            Stage dialogStage = (Stage) alert.getDialogPane().getScene().getWindow();
            dialogStage.getIcons().add(icon);
            alert.setContentText("¿Seguro que deseas dar de baja a este camarero?");
            alert.setHeaderText(null);
            alert.setWidth(300);

            alert.setResultConverter(buttonType -> {
                if (buttonType == ButtonType.OK) {

                    try {
                        auth.deleteUser(selectedEmployee.getUid());
                    } catch (FirebaseAuthException e) {
                        Alert alertE = new Alert(Alert.AlertType.ERROR);
                        dialogStage.getIcons().add(icon);
                        alertE.setTitle("Error en la eliminación");
                        alertE.setHeaderText(null);
                        alertE.setContentText("No se ha podido eliminar al camarero correctamente, intentalo de nuevo.\nSi el error persiste contacta al administrador");
                        alertE.showAndWait();
                    }
                    db.getReference("camareros").child(selectedEmployee.getEmail().replace(".","-")).removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError error, DatabaseReference ref) {
                            if(error != null) {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                Image icon= new Image(mainApp.getClass().getResourceAsStream("icon.png"));
                                Stage dialogStage = (Stage) alert.getDialogPane().getScene().getWindow();
                                dialogStage.getIcons().add(icon);
                                alert.setTitle("Error en la eliminación");
                                alert.setHeaderText(null);
                                alert.setContentText("No se ha podido eliminar al camarero correctamente, intentalo de nuevo.\nSi el error persiste contacta al administrador");
                                alert.showAndWait();
                            }else {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                Image icon= new Image(mainApp.getClass().getResourceAsStream("icon.png"));
                                Stage dialogStage = (Stage) alert.getDialogPane().getScene().getWindow();
                                dialogStage.getIcons().add(icon);
                                alert.setTitle("Eliminación correcta");
                                alert.setHeaderText(null);
                                alert.setContentText("Se ha dado de baja al camarero.");
                                alert.showAndWait();
                            }


                        }
                    });
                }

                return null;
            });

            alert.showAndWait();

        }else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            Image icon= new Image(mainApp.getClass().getResourceAsStream("icon.png"));
            Stage dialogStage = (Stage) alert.getDialogPane().getScene().getWindow();
            dialogStage.getIcons().add(icon);
            alert.setTitle("Selecciona un camarero");
            alert.setHeaderText(null);
            alert.setContentText("Debes seleccionar un camarero para poder eliminarlo.");
            alert.showAndWait();
        }
    }

    /**
     * Este método se encarga de volver a la pantalla de gestión de mesas
     * al ejecutarse.
     */
    @FXML
    private void volverGestor() {
        Alert alert= new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("¿Seguro que deseas salir del perfil de administrador?");
        alert.setHeaderText(null);
        Image icon= new Image(mainApp.getClass().getResourceAsStream("icon.png"));
        Stage dialogStage = (Stage) alert.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(icon);

        alert.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                mainApp.initLayout();
            }

            return null;
        });

        alert.showAndWait();
    }

    /**
     * Este método se encarga de abrir un cuadro que muestra
     * las insterucciones para realizar las acciones dentro del programa.
     */
   @FXML
    private void abrirAyuda() {
       Dialog<String> dialog= new Dialog<>();
       WebView webView = new WebView();
       WebEngine webEngine = webView.getEngine();

       Image icon= new Image(mainApp.getClass().getResourceAsStream("icon.png"));
       Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
       dialogStage.getIcons().add(icon);
       URL url = mainApp.getClass().getResource("html/help.html");
       URL urlCss = mainApp.getClass().getResource("html/style.css");

       webEngine.load(url.toExternalForm());
       webEngine.setUserStyleSheetLocation(urlCss.toExternalForm());


       dialog.getDialogPane().setContent(webView);
       dialog.initModality(Modality.APPLICATION_MODAL);
       dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
       dialog.setResultConverter(buttonType -> {
           return null;
       });
       dialog.show();
    }

    public void setMainApp(App mainApp) {
        this.mainApp = mainApp;

    }


}
