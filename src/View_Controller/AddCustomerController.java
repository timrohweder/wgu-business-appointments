/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;

import Exceptions.InvalidCustomerDataException;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author tim
 */
public class AddCustomerController implements Initializable {

    @FXML
    private TextField nameTextField;
    @FXML
    private TextField addressTextField;
    @FXML
    private TextField address2TextField;
    @FXML
    private ComboBox cityComboBox;
    @FXML
    private TextField postalCodeTextField;
    @FXML
    private TextField phoneTextField;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            populateCityComboBox();
        } catch (SQLException ex) {
            Logger.getLogger(AddCustomerController.class.getName()).log(Level.SEVERE, null, ex);
        }
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

    public void returnToCustomerScreen(ActionEvent event) throws IOException {
        Parent customersViewScreenParent = FXMLLoader.load(getClass().getResource("CustomersView.fxml"));
        Scene customersViewScreenScene = new Scene(customersViewScreenParent);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(customersViewScreenScene);
        window.show();
    }

    public void populateCityComboBox() throws SQLException {
        Connection conn = Util.DatabaseHelper.getConnection();
        Statement statement = conn.createStatement();
        ObservableList<String> cityList = observableArrayList();
        try {
            ResultSet rsCities = statement.executeQuery("SELECT cityId, city FROM city");
            while (rsCities.next()) {
                int cityId = rsCities.getInt("cityId");
                String cityName = rsCities.getString("city");
                String cityNameAndId = cityName + " (" + cityId + ")";
                cityList.add(cityNameAndId);
            }

            cityList.forEach(customer -> cityComboBox.getItems().add(customer));
            rsCities.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public int generateAddressId() throws SQLException {
        Connection conn = Util.DatabaseHelper.getConnection();
        Statement statement = conn.createStatement();
        List<Integer> listOfAddressIds = new ArrayList<>();
        try {
            ResultSet rsAddressIds = statement.executeQuery("SELECT addressId FROM address");
            while (rsAddressIds.next()) {
                int addressId = rsAddressIds.getInt("addressId");
                listOfAddressIds.add(addressId);
            }
            rsAddressIds.close();
            if (listOfAddressIds.size() > 0) {
                return Collections.max(listOfAddressIds) + 1;
            } else {
                return 1;
            }

        } catch (SQLException se) {
            se.printStackTrace();
            return -1;
        }
    }

    public int generateCustomerId() throws SQLException {
        Connection conn = Util.DatabaseHelper.getConnection();
        Statement statement = conn.createStatement();
        List<Integer> listOfCustomerIds = new ArrayList<>();
        try {
            ResultSet rsCustomerIds = statement.executeQuery("SELECT customerId FROM customer");
            while (rsCustomerIds.next()) {
                int customerId = rsCustomerIds.getInt("customerId");
                listOfCustomerIds.add(customerId);
            }
            rsCustomerIds.close();
            if (listOfCustomerIds.size() > 0) {
                return Collections.max(listOfCustomerIds) + 1;
            } else {
                return 1;
            }

        } catch (SQLException se) {
            se.printStackTrace();
            return -1;
        }
    }

    // various regex patterns found through Stack Overflow
    public void handleSave(ActionEvent event) throws SQLException, IOException {

        try {
            String missingFieldMessage = "All fields are required.";
            if (checkIfTextFieldEmpty(nameTextField)) {
                throw new InvalidCustomerDataException(missingFieldMessage);
            }
            if (checkIfTextFieldEmpty(addressTextField)) {
                throw new InvalidCustomerDataException(missingFieldMessage);
            }
            if (checkIfTextFieldEmpty(address2TextField)) {
                throw new InvalidCustomerDataException(missingFieldMessage);
            }
            if (checkIfTextFieldEmpty(postalCodeTextField)) {
                throw new InvalidCustomerDataException(missingFieldMessage);
            }
            if (checkIfTextFieldEmpty(phoneTextField)) {
                throw new InvalidCustomerDataException(missingFieldMessage);
            }
            if (!nameTextField.getText().matches("^[a-zA-z ]+$")) {
                throw new InvalidCustomerDataException("Only letters and spaces are allowed in the name field.");
            }
            if (!addressTextField.getText().matches("^[a-zA-Z0-9 ]*$")) {
                throw new InvalidCustomerDataException("Only letters, numbers, and spaces are allowed in the address fields.");
            }
            if (!address2TextField.getText().matches("^[a-zA-Z0-9 ]*$")) {
                throw new InvalidCustomerDataException("Only letters, numbers, and spaces are allowed in the address fields.");
            }
            if (!postalCodeTextField.getText().matches("[0-9]+")) {
                throw new InvalidCustomerDataException("Only numbers are allowed in the postal code field.");
            }

            if (!phoneTextField.getText().matches("[0-9]+")) {
                throw new InvalidCustomerDataException("Only numbers are allowed in the phone field.");
            }
            if (cityComboBox.getValue() == null || cityComboBox.getValue().toString().equals("")) {
                throw new InvalidCustomerDataException(missingFieldMessage);
            }

            int customerId = generateCustomerId();
            int addressId = generateAddressId();
            String customerName = nameTextField.getText();
            String address = addressTextField.getText();
            String address2 = address2TextField.getText();
            int cityId = extractId(cityComboBox.getValue().toString());
            String postalCode = postalCodeTextField.getText();
            String phone = phoneTextField.getText();

            Connection conn = Util.DatabaseHelper.getConnection();
            Statement statement = conn.createStatement();

            String sql = String.format("INSERT INTO address VALUES (%d, '%s', '%s', %d, '%s', '%s', '2019-01-01 00:00:00', 'not needed', '2019-01-01 00:00:00', 'not needed')", addressId, address, address2, cityId, postalCode, phone);
            statement.executeUpdate(sql);

            sql = String.format("INSERT INTO customer VALUES (%d, '%s', %d, 1, '2019-01-01 00:00:00', 'not needed', '2019-01-01 00:00:00', 'not needed')", customerId, customerName, addressId);
            statement.executeUpdate(sql);

            returnToCustomerScreen(event);
        } catch (InvalidCustomerDataException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid customer data");
            alert.setHeaderText("Please fill out all fields.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }

    }

    public Boolean checkIfTextFieldEmpty(TextField field) throws InvalidCustomerDataException {
        return field.getText() == null || field.getText().trim().isEmpty();
    }

    public Integer extractId(String string) {
        int openingParensIndex = string.indexOf('(');
        int closingParensIndex = string.indexOf(')');
        Integer id = Integer.parseInt(string.substring(openingParensIndex + 1, closingParensIndex));
        return id;
    }

}
