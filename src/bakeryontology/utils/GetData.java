package bakeryontology.utils;

import bakeryontology.entities.Baking;
import bakeryontology.entities.Person;
import bakeryontology.entities.Transaction;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * @author Adrian Wilk
 * @since 15.05.2017
 */

public class GetData {
    
     public static void getTransactionColumns(TableView<Transaction> transactionTable) {
         
        TableColumn<Transaction, Integer> idColumn = new TableColumn<>("Id");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<Transaction, Integer> costColumn = new TableColumn<>("Cost");
        costColumn.setCellValueFactory(new PropertyValueFactory<>("cost"));

        TableColumn<Transaction, String> movieColumn = new TableColumn<>("Baking");
        movieColumn.setCellValueFactory(new PropertyValueFactory<>("baking"));

        TableColumn<Transaction, String> customerColumn = new TableColumn<>("Client");
        customerColumn.setCellValueFactory(new PropertyValueFactory<>("client"));

        TableColumn<Transaction, String> employeeColumn = new TableColumn<>("Employee");
        employeeColumn.setCellValueFactory(new PropertyValueFactory<>("employee"));

        transactionTable.getColumns().add(idColumn);
        transactionTable.getColumns().add(costColumn);
        transactionTable.getColumns().add(movieColumn);
        transactionTable.getColumns().add(customerColumn);
        transactionTable.getColumns().add(employeeColumn);
         
    }
     
    public static void getBakingColumns(TableView<Baking> bakingTable) {
        TableColumn<Baking, Integer> idColumn = new TableColumn<>("Id");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Baking, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<Baking, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Baking, Integer> validityColumn = new TableColumn<>("Validity days");
        validityColumn.setCellValueFactory(new PropertyValueFactory<>("validity"));

        bakingTable.getColumns().add(idColumn);
        bakingTable.getColumns().add(typeColumn);
        bakingTable.getColumns().add(nameColumn);
        bakingTable.getColumns().add(validityColumn);
    }
    
    public static void getPersonColumns(TableView<Person> personTable) {
        TableColumn<Person, Integer> idColumn = new TableColumn<>("Id");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Person, String> nameColumn = new TableColumn<>("Full name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));

        TableColumn<Person, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

        personTable.getColumns().add(idColumn);
        personTable.getColumns().add(nameColumn);
        personTable.getColumns().add(typeColumn);
    }
    
}
