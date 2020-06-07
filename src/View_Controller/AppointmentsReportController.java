/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;

import Model.Appointment;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author tim
 */
public class AppointmentsReportController implements Initializable {

    private ObservableList<Appointment> allAppointments = observableArrayList();

    @FXML
    ComboBox monthComboBox;
    @FXML
    ComboBox yearComboBox;
    @FXML
    Label resultsLabel;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            populateAppointments();
        } catch (SQLException ex) {
            Logger.getLogger(AppointmentsReportController.class.getName()).log(Level.SEVERE, null, ex);
        }
        monthComboBox.getItems().addAll("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");
        yearComboBox.getItems().addAll("2020", "2021", "2022", "2023", "2024", "2025", "2026", "2027", "2028", "2029");
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

    public void handleGetReportClick() {
        String month = monthComboBox.getValue().toString();
        String year = yearComboBox.getValue().toString();
        final Map<String, Integer> hashMap = new HashMap();
        final StringBuilder output = new StringBuilder();

        allAppointments.forEach(appointment -> {
            LocalDateTime date = appointment.getStart();
            if (date.getMonth().toString().equalsIgnoreCase(month) && date.getYear() == Integer.parseInt(year)) {
                hashMap.putIfAbsent(appointment.getType(), 0);
                hashMap.put(appointment.getType(), hashMap.get(appointment.getType()) + 1);
            }
        });
        
        hashMap.forEach((k,v) -> output.append(String.format("%s: %d\n", k, v)));
        if (output.length() == 0) {
            output.append("No appointments found for this month.");
        }
        resultsLabel.setText(output.toString());
    }

    public void populateAppointments() throws SQLException {
        allAppointments.clear();
        Connection conn = Util.DatabaseHelper.getConnection();
        Statement statement = conn.createStatement();
        try {
            ResultSet rs = statement.executeQuery("SELECT start, type, end FROM appointment");
            while (rs.next()) {
                int appointmentId = 0;
                int customerId = 0;
                int userId = 0;
                String title = "not needed";
                String description = "not needed";
                String location = "not needed";
                String contact = "not needed";
                String type = rs.getString("type");
                String url = "not needed";
                ZoneId newzid = ZoneId.systemDefault();
                Timestamp startTs = rs.getTimestamp("start");
                ZonedDateTime newZdtStart = startTs.toLocalDateTime().atZone(ZoneId.of("UTC"));
                ZonedDateTime newLocalStart = newZdtStart.withZoneSameInstant(newzid);
                Timestamp endTs = rs.getTimestamp("end");
                ZonedDateTime newZdtEnd = endTs.toLocalDateTime().atZone(ZoneId.of("UTC"));
                ZonedDateTime newLocalEnd = newZdtEnd.withZoneSameInstant(newzid);
                String customerName = "not needed";;
                String consultantName = "not needed";;

                Appointment appointment = new Appointment(
                        appointmentId,
                        customerId,
                        userId,
                        title,
                        description,
                        location,
                        contact,
                        type,
                        url,
                        newLocalStart.toLocalDateTime(),
                        newLocalEnd.toLocalDateTime(),
                        customerName,
                        consultantName
                );

                allAppointments.add(appointment);
            }
            rs.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

}
