/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;

import Exceptions.InvalidCustomerDeleteException;
import Model.Customer;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javafx.collections.FXCollections.observableArrayList;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author tim
 */
public class CustomersViewController implements Initializable {

    private ObservableList<Customer> allCustomers = observableArrayList();

    @FXML
    private TableView<Customer> customersTable;
    @FXML
    private TableColumn<Customer, String> nameColumn;
    @FXML
    private TableColumn<Customer, String> addressColumn;
    @FXML
    private TableColumn<Customer, String> address2Column;
    @FXML
    private TableColumn<Customer, String> cityColumn;
    @FXML
    private TableColumn<Customer, String> postalCodeColumn;
    @FXML
    private TableColumn<Customer, String> phoneColumn;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            populateCustomersTable();
        } catch (SQLException ex) {
            Logger.getLogger(AppointmentsViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        address2Column.setCellValueFactory(new PropertyValueFactory<>("address2"));
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
        postalCodeColumn.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));

        customersTable.setItems(allCustomers);
    }

    public void handleAppointmentsClick(ActionEvent event) throws IOException {
        Parent mainScreenParent = FXMLLoader.load(getClass().getResource("AppointmentsView.fxml"));
        Scene mainScreenScene = new Scene(mainScreenParent);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(mainScreenScene);
        window.show();
    }

    public void handleCustomersClick(ActionEvent event) throws IOException {
        Parent mainScreenParent = FXMLLoader.load(getClass().getResource("CustomersView.fxml"));
        Scene mainScreenScene = new Scene(mainScreenParent);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(mainScreenScene);
        window.show();
    }

    public void handleCalendarClick(ActionEvent event) throws IOException {
        Parent mainScreenParent = FXMLLoader.load(getClass().getResource("CalendarView.fxml"));
        Scene mainScreenScene = new Scene(mainScreenParent);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(mainScreenScene);
        window.show();
    }

    public void handleReportsClick(ActionEvent event) throws IOException {
        Parent mainScreenParent = FXMLLoader.load(getClass().getResource("ReportsView.fxml"));
        Scene mainScreenScene = new Scene(mainScreenParent);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(mainScreenScene);
        window.show();
    }

    public void handleAddCustomerClick(ActionEvent event) throws IOException {
        Parent addCustomerScreenParent = FXMLLoader.load(getClass().getResource("AddCustomer.fxml"));
        Scene addCustomerScreenScene = new Scene(addCustomerScreenParent);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(addCustomerScreenScene);
        window.show();
    }

    public void handleEditCustomerClick(ActionEvent event) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("EditCustomer.fxml"));
        Parent editCustomerScreenParent = loader.load();
        Scene editCustomerScreenScene = new Scene(editCustomerScreenParent);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        EditCustomerController controller = loader.getController();
        controller.setCustomerToModify(customersTable.getSelectionModel().getSelectedItems().get(0));
        window.setScene(editCustomerScreenScene);
        window.show();
    }

    public void handleDeleteCustomerClick() throws SQLException {
        Connection conn = Util.DatabaseHelper.getConnection();
        Statement statement = conn.createStatement();
        String sql = String.format("DELETE FROM customer WHERE customerId = %d", customersTable.getSelectionModel().getSelectedItems().get(0).getCustomerId());
        try {
            ResultSet rs = statement.executeQuery("SELECT customerId FROM appointment");
            while(rs.next()) {
                if (rs.getInt("customerId") == customersTable.getSelectionModel().getSelectedItems().get(0).getCustomerId()) {
                    throw new InvalidCustomerDeleteException("This customer cannot be deleted while they have appointments assigned to them.");
                }
            }
            statement.executeUpdate(sql);
        } catch (InvalidCustomerDeleteException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Cannot delete customer");
            alert.setHeaderText("Appointments found for this customer.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        populateCustomersTable();
    }

    public void handleExitClick() {
        System.exit(0);
    }

    public void populateCustomersTable() throws SQLException {
        allCustomers.clear();
        Connection conn = Util.DatabaseHelper.getConnection();
        Statement statement = conn.createStatement();
        try {
            ResultSet rs = statement.executeQuery("SELECT customer.*, address.address, address.address2, city.city, address.postalCode, address.phone FROM customer INNER JOIN address ON customer.addressId = address.addressId INNER JOIN city ON address.cityId = city.cityId");
            while (rs.next()) {
                int customerId = rs.getInt("customerId");
                String customerName = rs.getString("customerName");
                int addressId = rs.getInt("addressId");
                String address = rs.getString("address");
                String address2 = rs.getString("address2");
                String city = rs.getString("city");
                String postalCode = rs.getString("postalCode");
                String phone = rs.getString("phone");

                Customer customer = new Customer(
                        customerId,
                        customerName,
                        addressId,
                        address,
                        address2,
                        city,
                        postalCode,
                        phone
                );

                allCustomers.add(customer);
            }
            rs.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

}
