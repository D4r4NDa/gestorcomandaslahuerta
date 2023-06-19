package es.damdi.gestorcomandaslahuerta.controllers;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import es.damdi.gestorcomandaslahuerta.App;
import es.damdi.gestorcomandaslahuerta.models.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainController {

    private App mainApp;

    FirebaseDatabase db;
    FirebaseAuth auth;

    @FXML
    private TableView<MesaProperties> mesaTable;
    @FXML
    private TableColumn<MesaProperties, Long> mesaColumn;
    @FXML
    private TextArea taComidas;
    @FXML
    private TextArea taBebidas;
    @FXML
    private Label lNombre;
    private ObservableList<MesaProperties> mesaData = FXCollections.observableArrayList();

    public void initialize() throws IOException, InterruptedException {

        db= FirebaseDatabase.getInstance();

        ArrayList<Mesa> mesas = new ArrayList<Mesa>();

        mesaColumn.setCellFactory(column -> {
            TableCell<MesaProperties, Long> cell = new TableCell<MesaProperties, Long>() {
                protected void updateItem(Long item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(item.toString());
                    }
                }
            };

            cell.setOnMouseClicked(event -> {
                if (!cell.isEmpty()) {
                    selectMesa();
                }
            });

            return cell;
        });

        //*********************************************************************************************************************************************************************************************
        new Thread(new Runnable() {
            @Override
            public void run() {
                db.getReference("mesas").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        List<Mesa> tempMesas = new ArrayList<>();

                        for (DataSnapshot i : snapshot.getChildren()) {
                            try {
                                Map<String, Object> data = (Map<String, Object>) i.getValue();
                                Mesa m = new Mesa();
                                m.setNumMesa((Long) data.get("numMesa"));
                                try {
                                    Map<String, Object> camareroData = (Map<String, Object>) data.get("camarero");
                                    if (camareroData != null) {
                                        Camarero c = new Camarero();
                                        c.setNombre((String) camareroData.get("nombre"));
                                        c.setEmail((String) camareroData.get("email"));
                                        c.setPassword((String) camareroData.get("password"));
                                        c.setOnline((Boolean) camareroData.get("online"));
                                        c.setUid((String) camareroData.get("uid"));

                                        m.setCamarero(c);
                                    }
                                }catch (Exception e) {
                                    e.printStackTrace();
                                }
                                m.setEstado((Long) data.get("estado"));
                                m.setCPedidas((List<ComidasPedidas>) data.get("cpedidas"));
                                System.out.println(m.getCPedidas());
                                m.setBPedidas((List<BebidasPedidas>) data.get("bpedidas"));

                                tempMesas.add(m);
                                System.out.println(m.getNumMesa());
                                selectMesa();
                            }catch(DatabaseException e) {
                                e.printStackTrace();
                            }
                        }
                        // Update the UI on the EDT
                        Platform.runLater(() -> {
                            mesas.clear();
                            mesaData.clear();
                            mesas.addAll(tempMesas);

                            mesaColumn.setCellValueFactory(cellData -> Bindings.createObjectBinding(() -> cellData.getValue().getNumMesa(), cellData.getValue().numMesaProperty()));

                            for (Mesa m : mesas) {

                                List<ComidasPedidas> cFiller= new ArrayList<ComidasPedidas>();
                                List<BebidasPedidas> bFiller= new ArrayList<BebidasPedidas>();

                                if(m.getCPedidas()==null) {
                                    m.setCPedidas(cFiller);
                                }

                                if(m.getBPedidas()==null) {
                                    m.setBPedidas(bFiller);
                                }

                                MesaProperties mp = new MesaProperties(m.getNumMesa(), m.getEstado(), m.getCamarero(), m.getCPedidas(), m.getBPedidas());
                                mesaData.add(mp);
                            }

                            mesaTable.setItems(mesaData);
                            mesaTable.refresh();
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
    private void irAdmin() {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle(null);
        dialog.setHeaderText("Clave de administrador:");

        Label messageLabel = new Label("Clave:");
        PasswordField passwordField = new PasswordField();

        VBox content = new VBox(messageLabel, passwordField);
        content.setSpacing(10);
        content.setPadding(new Insets(10));

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                String enteredPassword = passwordField.getText();
                return enteredPassword.equals("admin");
            }
            
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            if (result) {
                mainApp.setPrimary();

            } else if(!result) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Clave incorrecta. Prueba de nuevo");
                alert.showAndWait();
            } else if (result == null) {

            }
        });
    }


    @FXML
    private void selectMesa() {
        if(mesaTable.getSelectionModel().getSelectedItem()!=null) {
            MesaProperties selectedMesa = mesaTable.getSelectionModel().getSelectedItem();
            try {
                if(selectedMesa.getCamarero()!=null) {
                    lNombre.setText(selectedMesa.getCamarero().getNombre());
                }else {
                    lNombre.setText("MESA LIBRE");
                }

            }catch (Exception e) {
                e.printStackTrace();
            }


            List<ComidasPedidas> cPedidas = new ArrayList<>();
            for(int i=0; i<selectedMesa.getCPedidas().size(); i++) {
                cPedidas.add((ComidasPedidas) selectedMesa.getCPedidas().get(i));
            }

            selectedMesa.setCPedidas(cPedidas);

            String comidas= "PLATOS\n---------------------------";
            if(selectedMesa.getCPedidas()!=null) {
                for(ComidasPedidas cp : cPedidas) {
                    comidas= comidas +"\n"+cp.getCantidad()+"  "+cp.getComida().getNombre();
                }
            }
            taComidas.setText(comidas);


            List<BebidasPedidas> bPedidas = new ArrayList<>(selectedMesa.getBPedidas());
            selectedMesa.setBPedidas(bPedidas);

            String bebidas= "BEBIDAS\n---------------------------";
            if(selectedMesa.getBPedidas()!=null) {
                for(BebidasPedidas bp : selectedMesa.getBPedidas()) {
                    bebidas= bebidas +"\n"+bp.getCantidad()+"  "+bp.getBebida().getNombre();
                }
            }
            taBebidas.setText(bebidas);
        }

    }

    public void setMainApp(App mainApp) {
        this.mainApp = mainApp;

    }


}
