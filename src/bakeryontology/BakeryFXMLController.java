package bakeryontology;

import bakeryontology.entities.Baking;
import bakeryontology.entities.Person;
import bakeryontology.entities.Transaction;
import bakeryontology.utils.Connection;
import bakeryontology.utils.GetData;
import java.awt.Desktop;
import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

/**
 * @author Adrian Wilk
 * @since 15.05.2017
 */


public class BakeryFXMLController implements Initializable {
    
    
    //Top menu 
    @FXML
    private MenuItem menuOpen;
    @FXML
    private MenuItem menuSave;
    @FXML
    private MenuItem menuExit;
    @FXML
    private MenuItem menuAbout;
    @FXML
    private MenuItem menuDocumentation;
    
    //Tab panes
    @FXML
    private TabPane tabPane;
    
    //Person tab
    @FXML
    private ComboBox<String> personType;
    @FXML
    private TextField personID;
    @FXML
    private TextField personFullName;
    @FXML
    private Button personAdd;
    @FXML
    private Button personUpdate;
    @FXML
    private TableView<Person> personTable;
    @FXML
    private MenuItem personDelete;
    
    //Baking tab
    @FXML
    private ComboBox<String> bakingType;
    @FXML
    private TextField bakingID;
    @FXML
    private TextField bakingName;
    @FXML
    private Button bakingAdd;
    @FXML
    private TextField bakingValidity;
    @FXML
    private Button bakingUpdate;
    @FXML
    private TableView<Baking> bakingTable;
    @FXML
    private MenuItem bakingDelete;
    
    //Transaction tab
    @FXML
    private ComboBox<String> transactionEmployee;
    @FXML
    private TextField transactionID;
    @FXML
    private TextField transactionCost;
    @FXML
    private Button transactionAdd;
    @FXML
    private Button transactionUpdate;
    @FXML
    private ComboBox<String> transactionBaking;
    @FXML
    private ComboBox<String> transactionClient;
    @FXML
    private TableView<Transaction> transactionTable;
    @FXML
    private MenuItem transactionDelete;
    
    
    //Connection with ontology
    public static Connection connection;
    
    //Default value false, hecking the action
    public static boolean isChanged = false;

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        /**
         * Basic window setting,
         * excludes access to the window before opening the ontology
         */
        tabPane.setDisable(true);
        menuSave.setDisable(true);
        
        //Excludes acces to the update
        transactionUpdate.setDisable(true);
        bakingUpdate.setDisable(true);
        personUpdate.setDisable(true);

        
        connection = new Connection();
        connection.openConnection(Connection.mainFile);
        
        //Fills combo boxes Person
        String pType[] = {"Employee", "Client"};
        personType.getItems().addAll(pType);
        
        //Fills combo boxes Baking
        String bType[] = {"Bread", "Roll"};
        bakingType.getItems().addAll(bType);
        
        //Download columns to the tabels
        GetData.getTransactionColumns(transactionTable);
        GetData.getBakingColumns(bakingTable);
        GetData.getPersonColumns(personTable);
        
        //Adds content to the tables loaded from the ontology
        updateTransactionTable();
        updateBakingTable();
        updatePersonTable();
        
        //**** TOP MENU SERVICE ****
        menuOpen.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("OWL(*.owl)", "*.owl"));
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            File selectedFile = fileChooser.showOpenDialog(null);
            if (selectedFile != null) {
                if (connection.openConnection(selectedFile)) {

                    //unblocks window
                    tabPane.setDisable(false);
                    menuSave.setDisable(false);

                    // Updates data
                    updateTransactionTable();
                    updateBakingTable();
                    updatePersonTable();

                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("ERROR");
                    alert.setHeaderText(null);
                    alert.setContentText("Error during parse file!");
                    alert.show();
                }
            }
        });
        
        //Saves ontology
        menuSave.setOnAction(e ->{connection.saveConnection(null);});
        
        /**
         * Exits the program, deletes temporary file before exiting
         * If there are unsaved changes, the window will pop out
         **/
        menuExit.setOnAction(e -> {           
            if(isChanged==true){   
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("CONFIRMATION");
                alert.setHeaderText("No saved files.");
                alert.setContentText("Do you want to save?");
                Optional<ButtonType> result = alert.showAndWait();

                if (result.get() == ButtonType.OK){
                   connection.saveConnection(null);
                }else{
                    Optional<File> optional = Optional.ofNullable(Connection.temporaryData);
                    optional.ifPresent(file -> Connection.temporaryData.delete());
                    System.exit(0);
                }
            }else{
                Optional<File> optional = Optional.ofNullable(Connection.temporaryData);
                optional.ifPresent(file -> Connection.temporaryData.delete());
                System.exit(0);
            }
        });
        
        //Displays "About" window
        menuAbout.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Bakery Ontology");
            alert.setHeaderText(null);
            alert.setContentText("Author: Adrian Wilk \nDate of completion: 12.06.2017");
            alert.show();
        });
        
        //Opens documentation file
        menuDocumentation.setOnAction(e->{ 
            try{
                if(Desktop.isDesktopSupported()){
                String path="./utils/dokumentacja.pdf";
                File myFile = new File(path);
                Desktop.getDesktop().open(myFile);
                }
            }catch(Exception ex){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setHeaderText(null);
                alert.setContentText("Read file error!");
                alert.show();  
            }     
        });
        
        //**** SELECT ****

        //Clicking on the record in the tables will fill the fields
        transactionTable.setOnMouseClicked(e -> {
            try{
                Transaction model = connection.transactionList.get(transactionTable.getSelectionModel().getFocusedIndex());
                transactionID.setText(String.valueOf(model.getId()));
                transactionCost.setText(String.valueOf(model.getCost()));
                transactionBaking.setValue(transactionBaking.getItems().stream()
                        .filter(baking -> baking.equals(model.getBaking())).findAny().get());
                transactionClient.setValue(transactionClient.getItems().stream()
                        .filter(client -> client.equals(model.getClient())).findAny().get());
                transactionEmployee.setValue(transactionEmployee.getItems().stream()
                        .filter(employee -> employee.equals(model.getEmployee())).findAny().get());
                transactionUpdate.setDisable(false);
            }catch(Exception ex){
                System.out.println(ex);
            }
        });

        bakingTable.setOnMouseClicked(e -> {
            try{
                Baking model = connection.bakingList.get(bakingTable.getSelectionModel().getFocusedIndex());
                bakingID.setText(String.valueOf(model.getId()));
                bakingValidity.setText(String.valueOf(model.getValidity()));
                bakingName.setText(model.getName());
                bakingType.setValue(bakingType.getItems().stream()
                        .filter(baking -> baking.equals(model.getType())).findAny().get());
                bakingUpdate.setDisable(false);
            }catch(Exception ex){
                System.out.println(ex);
            }
        });
        
        personTable.setOnMouseClicked(e -> {
            try{
                Person model = connection.personList.get(personTable.getSelectionModel().getFocusedIndex());
                personID.setText(String.valueOf(model.getId()));
                personFullName.setText(model.getFullName());
                personType.setValue(personType.getItems().stream()
                        .filter(person -> person.equals(model.getType())).findAny().get());
                personUpdate.setDisable(false);
            }catch(Exception ex){
                System.out.println(ex);
            }
        });
        
        //**** INSERT ****

        /**
         *Clicking on the add button will create a new clean model
         *Fills it with data from fields and send it to the connection for
         *addition to the ontology, checking correctness of the individual
         **/
        transactionAdd.setOnAction(e -> {
            try{
                isChanged=true;
                Transaction model = new Transaction();
                model.setId(transactionID.getText());
                model.setCost(Double.parseDouble(transactionCost.getText()));
                model.setBaking(transactionBaking.getValue());
                model.setClient(transactionClient.getValue());
                model.setEmployee(transactionEmployee.getValue());
                System.out.println(model.toString());
                if (checkIfTransactionUnique(model)) {
                    connection.addTransaction(model);
                    updateTransactionTable();
                }
            }catch(Exception ex){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("ERROR");
                alert.setHeaderText(null);
                alert.setContentText("Wrong data format.\n You can not leave empty fields.");
                alert.show(); 
            }
        });
        
        bakingAdd.setOnAction(e -> {
            try{
                isChanged=true;
                Baking model = new Baking();
                model.setId(Integer.parseInt(bakingID.getText()));
                model.setValidity(Integer.parseInt(bakingValidity.getText()));
                model.setName(bakingName.getText());
                model.setType(bakingType.getValue());
                
                if (checkIfBakingUnique(model)){
                    connection.addBaking(model);
                    updateBakingTable();
                }
            }catch(Exception ex){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("ERROR");
                alert.setHeaderText(null);
                alert.setContentText("Wrong data format.\n You can not leave empty fields.");
                alert.show();   
            }
        });

        personAdd.setOnAction(e -> {
            try{
                isChanged=true;
                Person model = new Person();
                model.setId(Integer.parseInt(personID.getText()));
                System.out.println(model.getId());
                model.setFullName(personFullName.getText());
                model.setType(personType.getValue());
                
                if (checkIfPersonUnique(model)) {
                    connection.addPerson(model);
                    updatePersonTable();
                }
            }catch(Exception ex){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("ERROR");
                alert.setHeaderText(null);
                alert.setContentText("Wrong data format.\n You can not leave empty fields.");
                alert.show();
            }
        });

        //**** DELETE ****

        /**
         *Delete is based on selecting the row from the table its ID from the list,
         *use this id to select an object and from the object its name,
         *by name can already remove the object from the ontology
         **/
        transactionDelete.setOnAction(e -> {
            if(transactionTable.getItems().size()>1){
                isChanged=true;
                connection.deleteEntity(connection.transactionList.get(transactionTable.
                        getSelectionModel().getFocusedIndex()).getId());
                System.out.println(transactionTable.getItems().size());
                updateTransactionTable();
            }else{
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("ERROR");
                alert.setHeaderText(null);
                alert.setContentText("Can not delete last record!");
                alert.show();        
            }
        });
        
        bakingDelete.setOnAction(e -> {
            if(bakingTable.getItems().size()>1){
                isChanged=true;
                connection.deleteEntity(connection.bakingList.get(bakingTable.getSelectionModel().
                        getFocusedIndex()).getName());
                updateBakingTable();
            }else{
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("ERROR");
                alert.setHeaderText(null);
                alert.setContentText("Can not delete last record!");
                alert.show();        
            }
        });

        personDelete.setOnAction(e -> {
            try{
                if(personTable.getItems().size()>1){
                    isChanged=true;
                    connection.deleteEntity(connection.personList.get(personTable.getSelectionModel().
                            getFocusedIndex()).getFullName());
                    updatePersonTable();
                }else{
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("ERROR");
                    alert.setHeaderText(null);
                    alert.setContentText("Can not delete last record!");
                    alert.show();        
                }
            }catch(Exception ex){
                System.out.println(ex);
            }
        });
        
        //**** UPDATE ****
        
        //Update is a combination of adding a new record and deleting an old one
        transactionUpdate.setOnAction(e -> {
            try{
                isChanged=true;
                Transaction model = new Transaction();
                model.setId(transactionID.getText());
                model.setCost(Double.parseDouble(transactionCost.getText()));
                model.setBaking(transactionBaking.getValue());
                model.setClient(transactionClient.getValue());
                model.setEmployee(transactionEmployee.getValue());
                connection.deleteEntity(connection.transactionList.get(transactionTable.
                        getSelectionModel().getFocusedIndex()).getId());
                connection.addTransaction(model);
                updateTransactionTable();
                transactionUpdate.setDisable(true);
            }catch(Exception ex){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("ERROR");
                alert.setHeaderText(null);
                alert.setContentText("Wrong data format.\n You can not leave empty fields.");
                alert.show();   
            }
        });
        
         bakingUpdate.setOnAction(e -> {
            try{
                isChanged=true;
                Baking model = new Baking();
                model.setId(Integer.parseInt(bakingID.getText()));
                model.setValidity(Integer.parseInt(bakingValidity.getText()));
                model.setName(bakingName.getText());
                model.setType(bakingType.getValue());
                connection.deleteEntity(connection.bakingList.get(bakingTable.getSelectionModel().
                        getFocusedIndex()).getName());
                connection.addBaking(model);
                updateBakingTable();
                bakingUpdate.setDisable(true);
            }catch(Exception ex){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("ERROR");
                alert.setHeaderText(null);
                alert.setContentText("Wrong data format.\n You can not leave empty fields.");
                alert.show();
            }
        });
         
        personUpdate.setOnAction(e -> {
            try{
                isChanged=true;
                Person model = new Person();
                model.setId(Integer.parseInt(personID.getText()));
                System.out.println(model.getId());
                model.setFullName(personFullName.getText());
                model.setType(personType.getValue());
                connection.deleteEntity(connection.personList.get(personTable.getSelectionModel().
                        getFocusedIndex()).getFullName());
                connection.addPerson(model);
                updatePersonTable();
                personUpdate.setDisable(true);
            }catch(Exception ex){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("ERROR");
                alert.setHeaderText(null);
                alert.setContentText("Wrong data format.\n You can not leave empty fields.");
                alert.show();
            }
        });
    }    
    
    private void updateTransactionTable() {       
        try{
            connection.transactionList.clear(); //clear list
            connection.updateTransactionsForTable(); //load data from ontology
            transactionTable.setItems(connection.transactionList); // add list to table
        }catch(Exception ex){
            System.out.println(ex);
        }
    }
    
    private void updateBakingTable() { 
        try{
            connection.bakingList.clear();
            connection.updateBakingForTable();
            bakingTable.setItems(connection.bakingList);
            transactionBaking.getItems().setAll(connection.getBakingList()); // add upgraded data to the comboBox
            transactionBaking.setValue(transactionBaking.getItems().get(0)); // set the combobox to 1 value
        }catch(Exception ex){
            
            System.out.println(ex);
        }
    }
    
    private void updatePersonTable() {
        try{
            connection.personList.clear();
            connection.updatePersonsForTable();
            personTable.setItems(connection.personList);

            transactionClient.getItems().setAll(connection.getClientList());
            transactionClient.setValue(transactionClient.getItems().get(0));

            transactionEmployee.getItems().setAll(connection.getEmployeeList());
            transactionEmployee.setValue(transactionEmployee.getItems().get(0));
        }catch(Exception ex){
            System.out.println(ex);
        }
    }
    
    //Checks validation of person data
    private boolean checkIfPersonUnique(Person p){
        for (Person person : connection.personList) {
            if (person.getFullName().equals(p.getFullName())) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("WRONG NAME");
                alert.setHeaderText(null);
                alert.setContentText("Individual with this NAME already exist");
                alert.show();
                return false;
            }
            if (person.getId() == p.getId()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("WRONG ID");
                alert.setHeaderText(null);
                alert.setContentText("Individual with this ID already exist!");
                alert.show();
                return false;
            }
            if (p.getId()<=0) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("WRONG ID");
                alert.setHeaderText(null);
                alert.setContentText("ID can not be less than and can not be 0!");
                alert.show();
                return false;
            }
            if (p.getFullName().matches(".*\\s.*") || p.getFullName().matches("[0-9].*")) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("WRONG NAME");
                alert.setHeaderText(null);
                alert.setContentText("Name can not equal spaces and"
                        + " can not start with numbers!");
                alert.show();
                return false;
            }
        }
        return true;
    }
    
    //Checks validation of person data
    private boolean checkIfBakingUnique(Baking b) {
        for (Baking baking : connection.bakingList) {
            if (baking.getName().equals(b.getName())) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("WRONG NAME");
                alert.setHeaderText(null);
                alert.setContentText("Individual with this NAME already exist!");
                alert.show();
                return false;
            }
            if (baking.getId() == b.getId()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("WRONG ID");
                alert.setHeaderText(null);
                alert.setContentText("Individual with this ID already exist!");
                alert.show();
                return false;
            }
            if (b.getId() <=0) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("WRONG ID");
                alert.setHeaderText(null);
                alert.setContentText("ID can not be less than and can not be 0!");
                alert.show();
                return false;
            }
            
            if (b.getName().matches(".*\\s.*") || b.getName().matches("[0-9].*")) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("WRONG NAME");
                alert.setHeaderText(null);
                alert.setContentText("Name can not equal spaces and"
                        + " can not start with numbers!");
                alert.show();
                return false;
            }
        }
        return true;
    }

    //Checks validation of person data
    private boolean checkIfTransactionUnique(Transaction t) {
        for (Transaction transaction : connection.transactionList) {
            if(transaction.getId().equals(t.getId())){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("WRONG ID");
                alert.setHeaderText(null);
                alert.setContentText("Individual with this ID already exist");
                alert.show();
                return false;
            }
            if (t.getCost() <=0){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("WRONG PRICE");
                alert.setHeaderText(null);
                alert.setContentText("Price can not be less than and can not be 0!");
                alert.show();
                return false;
            }
            if (t.getId().matches(".*\\s.*") || t.getId().matches("[0-9].*")) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("WRONG ID");
                alert.setHeaderText(null);
                alert.setContentText("Transaction ID can not equal spaces and"
                        + " can not start with numbers!");
                alert.show();
                return false;
            }
        }
        return true;
    }
}
