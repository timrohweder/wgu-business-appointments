/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class CountReportController implements Initializable {

    @FXML
    Label customersLabel;
    @FXML
    Label consultantsLabel;
    @FXML
    Label appointmentsLabel;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            retrieveCounts();
        } catch (SQLException ex) {
            Logger.getLogger(CountReportController.class.getName()).log(Level.SEVERE, null, ex);
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

    public void retrieveCounts() throws SQLException {
        Connection conn = Util.DatabaseHelper.getConnection();
        Statement statement = conn.createStatement();
        try {
            ResultSet rs = statement.executeQuery("SELECT COUNT(customerId) AS count from customer");
            while (rs.next()) {
                int count = rs.getInt("count");
                customersLabel.setText(Integer.toString(count));
            }
            rs.close();
            rs = statement.executeQuery("SELECT COUNT(userId) AS count from user");
            while (rs.next()) {
                int count = rs.getInt("count");
                consultantsLabel.setText(Integer.toString(count));
            }
            rs.close();
            rs = statement.executeQuery("SELECT COUNT(appointmentId) AS count from appointment");
            while (rs.next()) {
                int count = rs.getInt("count");
                appointmentsLabel.setText(Integer.toString(count));
            }
            rs.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

}
