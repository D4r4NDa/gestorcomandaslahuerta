package es.damdi.gestorcomandaslahuerta;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import com.google.firebase.database.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.awt.EventQueue;



public class PrimaryController {

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
    private TextField passwordField;

    @FXML
    private ImageView ivPerfil;

    private ObservableList<Employee> employeeData = FXCollections.observableArrayList();

    public void initialize() throws IOException, InterruptedException {

        FileInputStream serviceAccount = new FileInputStream("key.json");

        FirebaseOptions options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .setDatabaseUrl("https://proyectofinal-29247-default-rtdb.europe-west1.firebasedatabase.app/")
            .build();

        FirebaseApp.initializeApp(options);

        db= FirebaseDatabase.getInstance();

        ArrayList<Camarero> empleados = new ArrayList<Camarero>();


        //*********************************************************************************************************************************************************************************************
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

    @FXML
    private void showAddEmployeeDialog() {
        Dialog<Employee> dialog = new Dialog<>();
        dialog.setTitle("Add Employee");
        dialog.setHeaderText("Please enter employee details");

        // Load the secondary FXML and controller
        FXMLLoader loader = new FXMLLoader(getClass().getResource("secondary.fxml"));
        SecondaryController controller = new SecondaryController();
        loader.setController(controller);

        try {
            dialog.getDialogPane().setContent(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Show the dialog and wait for user input
        dialog.show();
    }

    @FXML
    private void deleteEmployee() {
        auth = FirebaseAuth.getInstance();
        Employee selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();

        if (selectedEmployee != null) {
            try {
                auth.deleteUser(selectedEmployee.getUid());
            } catch (FirebaseAuthException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error en la eliminación");
                alert.setHeaderText(null);
                alert.setContentText("No se ha podido eliminar al camarero correctamente, intentalo de nuevo.\nSi el error persiste contacta al administrador");
                alert.showAndWait();
            }
            db.getReference("camareros").child(selectedEmployee.getEmail().replace(".","-")).removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError error, DatabaseReference ref) {
                    if(error != null) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error en la eliminación");
                        alert.setHeaderText(null);
                        alert.setContentText("No se ha podido eliminar al camarero correctamente, intentalo de nuevo.\nSi el error persiste contacta al administrador");
                        alert.showAndWait();
                    }else {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Eliminación correcta");
                        alert.setHeaderText(null);
                        alert.setContentText("Se ha dado de baja al camarero.");
                        alert.showAndWait();
                    }


                }
            });
        }else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Selecciona un camarero");
            alert.setHeaderText(null);
            alert.setContentText("Debes seleccionar un camarero para poder eliminarlo.");
            alert.showAndWait();
        }
    }

}
