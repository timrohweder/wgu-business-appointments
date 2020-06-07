/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;

import Model.Customer;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author tim
 */
public class CustomerProfileViewController implements Initializable {

    private int customerId;
    @FXML
    private Label customerIdLabel;
    @FXML
    private Label customerNameLabel;
    @FXML
    private Label addressLabel;
    @FXML
    private Label address2Label;
    @FXML
    private Label cityLabel;
    @FXML
    private Label postalCodeLabel;
    @FXML
    private Label phoneLabel;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void setCustomer(int customerId) throws SQLException {
        this.customerId = customerId;
        Connection conn = Util.DatabaseHelper.getConnection();
        Statement statement = conn.createStatement();
        try {
            ResultSet rs = statement.executeQuery("SELECT customer.customerId, customer.customerName, address.address, address.address2, city.city, address.postalCode, address.phone FROM customer INNER JOIN address ON customer.addressId = address.addressId INNER JOIN city ON address.cityId = city.cityId");
            while (rs.next()) {
                String customerName = rs.getString("customerName");
                String address = rs.getString("address");
                String address2 = rs.getString("address2");
                String city = rs.getString("city");
                String postalCode = rs.getString("postalCode");
                String phone = rs.getString("phone");

                customerIdLabel.setText(Integer.toString(customerId));
                customerNameLabel.setText(customerName);
                addressLabel.setText(address);
                address2Label.setText(address2);
                cityLabel.setText(city);
                postalCodeLabel.setText(postalCode);
                phoneLabel.setText(phone);

            }
            rs.close();
        } catch (SQLException se) {
            se.printStackTrace();
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

    public void handleExitClick() {
        System.exit(0);
    }
}
