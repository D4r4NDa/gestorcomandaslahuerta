package es.damdi.gestorcomandaslahuerta.controllers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import es.damdi.gestorcomandaslahuerta.App;
import es.damdi.gestorcomandaslahuerta.models.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

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
    List<Map<String, Object>> hashMapList = new ArrayList<>();

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
        /**
         * Este hilo se encarga de comprobar constantemente si hay algún cambio en la base de datos, si lo hay actualiza los datos en local y la tabla
         * de mesas para representar la infromación actualizada.
         */
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


                                try {
                                    hashMapList= (List<Map<String, Object>>) data.get("cpedidas");
                                    List<ComidasPedidas> comidasPedidasList = new ArrayList<>();
                                    if(hashMapList != null) {
                                        for (Map<String, Object> hashMap : hashMapList) {
                                            Map<String, Object> mapComida= (Map<String, Object>) hashMap.get("comida");

                                            Comida comida = new Comida();
                                            comida.setNombre((String) mapComida.get("nombre"));
                                            comida.setPrecio((Double) mapComida.get("precio"));

                                            Long cantidad = (Long) hashMap.get("cantidad");

                                            ComidasPedidas comidasPedidas = new ComidasPedidas();
                                            comidasPedidas.setComida(comida);
                                            comidasPedidas.setCantidad(cantidad);

                                            comidasPedidasList.add(comidasPedidas);
                                        }
                                    }

                                    m.setCPedidas(comidasPedidasList);
                                }catch (Exception e) {
                                    e.printStackTrace();
                                }

                                try {
                                    hashMapList= (List<Map<String, Object>>) data.get("bpedidas");
                                    List<BebidasPedidas> bebidasPedidasList = new ArrayList<>();
                                    if(hashMapList != null) {
                                        for (Map<String, Object> hashMap : hashMapList) {
                                            Map<String, Object> mapBebida= (Map<String, Object>) hashMap.get("bebida");

                                            Bebida bebida = new Bebida();
                                            bebida.setNombre((String) mapBebida.get("nombre"));

                                            try {
                                                bebida.setPrecio((Double) mapBebida.get("precio"));
                                            }catch (Exception e) {
                                                bebida.setPrecio(Double.parseDouble(Long.toString((Long) mapBebida.get("precio"))));
                                            }

                                            Long cantidad = (Long) hashMap.get("cantidad");

                                            BebidasPedidas bebidasPedidas = new BebidasPedidas();
                                            bebidasPedidas.setBebida(bebida);
                                            bebidasPedidas.setCantidad(cantidad);

                                            bebidasPedidasList.add(bebidasPedidas);
                                        }
                                    }

                                    m.setBPedidas(bebidasPedidasList);
                                }catch (Exception e) {
                                    e.printStackTrace();
                                }


                                tempMesas.add(m);
                                System.out.println(m.getNumMesa());
                                selectMesa();
                            }catch(DatabaseException e) {
                                e.printStackTrace();
                            }
                        }

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

    /**
     * Este método se encarga de gestionar el acceso al perfil de administrador, primero muestra un cuadro donde donde introducir la clave,
     * y si es correcta cambia la ventana al panel de administrador.
     */
    @FXML
    private void irAdmin() {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle(null);
        dialog.setHeaderText(null);

        Image icon= new Image(mainApp.getClass().getResourceAsStream("icon.png"));
        Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(icon);

        Label messageLabel = new Label("Clave de administrador:");
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
                Stage dialogStage2 = (Stage) alert.getDialogPane().getScene().getWindow();
                dialogStage2.getIcons().add(icon);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Clave incorrecta. Prueba de nuevo");
                alert.showAndWait();
            } else if (result == null) {

            }
        });
    }


    /**
     * Este metodo se ejecuta al pulsar sobre una mesa en la tabla y se encarga de mostrar su inforamción en el panel de
     * la derecha.
     */
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


            String comidas= "PLATOS\n---------------------------";
            if(selectedMesa.getCPedidas()!=null) {
                for(ComidasPedidas cp : selectedMesa.getCPedidas()) {
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

    /**
     * Este método se encarga de formatear un número double que le llega por parametro
     * a un string con formato de moneda de euro 0000.00,00€ y dejando delante el número de espacios indicado.
     * Esto último es para la correcta alineación del texto cuando sea necesario.
     * @param number
     * @param numSpaces
     * @return
     */
    public static String formatEuro(double number, int numSpaces) {
        DecimalFormat euroFormat = new DecimalFormat("0.00\u20AC");

        DecimalFormatSymbols symbols = euroFormat.getDecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        euroFormat.setDecimalFormatSymbols(symbols);

        String formattedNumber = euroFormat.format(number);

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < numSpaces; i++) {
            stringBuilder.append(' ');
        }
        stringBuilder.append(formattedNumber);

        return stringBuilder.toString();
    }

    /**
     * Este método se encarga de calcular la factura total de una mesa que se le
     * pase por parámetro y devolverlo
     * @param m
     * @return
     */
    private double calcularTotal(MesaProperties m) {
        double total=0;

        for(ComidasPedidas cp : m.getCPedidas()) {
            total= total + (cp.getComida().getPrecio() * cp.getCantidad());
        }

        for(BebidasPedidas bp : m.getBPedidas()) {
            total= total + (bp.getBebida().getPrecio() * bp.getCantidad());
        }

        return total;

    }

    /**
     * Este método se encarga de mostrar un cuadro con la cuenta total de la mesa, la que normalmente
     * va impresa, y deja la mesa libre para poder ser ocupada de nuevo.
     */
    @FXML
    private void cobrarMesa() {
        if(mesaTable.getSelectionModel().getSelectedItem()!=null) {
            MesaProperties selectedMesa = mesaTable.getSelectionModel().getSelectedItem();
            try {
                if(selectedMesa.getCamarero()!=null) {
                    String comidas= "PLATOS\n---------------------------";
                    if(selectedMesa.getCPedidas()!=null) {
                        for(ComidasPedidas cp : selectedMesa.getCPedidas()) {
                            String precio= cp.getComida().getPrecio().toString();
                            if(precio.substring(0, precio.indexOf('.')).length()== 1) {
                                comidas= comidas +"\n"+formatEuro(cp.getComida().getPrecio(),2)+"    "+cp.getCantidad()+"  "+cp.getComida().getNombre();
                            }else {
                                comidas= comidas +"\n"+formatEuro(cp.getComida().getPrecio(),0)+"    "+cp.getCantidad()+"  "+cp.getComida().getNombre();
                            }

                        }
                    }


                    List<BebidasPedidas> bPedidas = new ArrayList<>(selectedMesa.getBPedidas());
                    selectedMesa.setBPedidas(bPedidas);

                    String bebidas= "BEBIDAS\n---------------------------";
                    if(selectedMesa.getBPedidas()!=null) {
                        for(BebidasPedidas bp : selectedMesa.getBPedidas()) {
                            String precio= bp.getBebida().getPrecio().toString();
                            if(precio.substring(0, precio.indexOf('.')).length()== 1) {
                                bebidas= bebidas +"\n"+formatEuro(bp.getBebida().getPrecio(),2)+"    "+bp.getCantidad()+"  "+bp.getBebida().getNombre();
                            }else {
                                bebidas= bebidas +"\n"+formatEuro(bp.getBebida().getPrecio(),0)+"    "+bp.getCantidad()+"  "+bp.getBebida().getNombre();
                            }
                        }
                    }


                    Dialog<String> dialog = new Dialog<>();
                    dialog.setTitle("FACTURA DE MESA " + selectedMesa.getNumMesa());
                    dialog.setHeaderText(null);

                    Image icon= new Image(mainApp.getClass().getResourceAsStream("icon.png"));
                    Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
                    dialogStage.getIcons().add(icon);

                    // Create controls
                    TextArea textArea = new TextArea();
                    textArea.setEditable(false);
                    textArea.setText("LA HUERTA\nHa sido atendido por: " + selectedMesa.getCamarero().getNombre() + "\n\n\n" + comidas + "\n\n" + bebidas + "\n\nTOTAL: " + formatEuro(calcularTotal(selectedMesa),0));
                    textArea.setPrefRowCount(10);
                    textArea.setPrefColumnCount(20);


                    // Set dialog content
                    GridPane grid = new GridPane();
                    grid.setPadding(new Insets(10));
                    grid.setHgap(10);
                    grid.setVgap(10);

                    grid.add(textArea, 0, 0);

                    dialog.getDialogPane().setContent(textArea);
                    dialog.getDialogPane().setPrefHeight(350);
                    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
                    dialog.setResultConverter(buttonType -> {
                        if (buttonType == ButtonType.OK) {
                            Mesa m= new Mesa();
                            m.setCamarero(null);
                            m.setEstado(0L);
                            m.setNumMesa(selectedMesa.getNumMesa());
                            m.setCPedidas(null);
                            m.setBPedidas(null);

                            db.getReference("mesas").child(m.getNumMesa().toString()).setValue(m, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError error, DatabaseReference ref) {
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    Image icon= new Image(mainApp.getClass().getResourceAsStream("icon.png"));
                                    Stage dialogStage = (Stage) alert.getDialogPane().getScene().getWindow();
                                    dialogStage.getIcons().add(icon);
                                    alert.setTitle("PAGO REALIZADO");
                                    alert.setHeaderText(null);
                                    alert.setContentText("El pago ha sido realizado y la mesa queda libre.");
                                    alert.showAndWait();
                                }
                            });
                            return null;
                        }

                        return null;
                    });

                    // Show the dialog
                    dialog.showAndWait();
                }else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    Image icon= new Image(mainApp.getClass().getResourceAsStream("icon.png"));
                    Stage dialogStage = (Stage) alert.getDialogPane().getScene().getWindow();
                    dialogStage.getIcons().add(icon);
                    alert.setTitle("Aviso");
                    alert.setHeaderText(null);
                    alert.setContentText("Esta mesa está vacia, no se puede cobrar.");
                    alert.showAndWait();
                }

            }catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            Image icon= new Image(mainApp.getClass().getResourceAsStream("icon.png"));
            Stage dialogStage = (Stage) alert.getDialogPane().getScene().getWindow();
            dialogStage.getIcons().add(icon);
            alert.setTitle("Aviso");
            alert.setHeaderText(null);
            alert.setContentText("Seleccione una mesa para realizar el cobro.");
            alert.showAndWait();
        }
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
