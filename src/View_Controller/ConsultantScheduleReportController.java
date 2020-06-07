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
import java.time.format.DateTimeFormatter;
import java.util.Collections;
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
public class ConsultantScheduleReportController implements Initializable {

    private ObservableList<Appointment> appointments = observableArrayList();
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    ComboBox consultantComboBox;
    @FXML
    Label resultLabel;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            populateConsultantComboBox();
        } catch (SQLException ex) {
            Logger.getLogger(ConsultantScheduleReportController.class.getName()).log(Level.SEVERE, null, ex);
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

    public void handleGetReportClick() throws SQLException {
        int userId = extractId(consultantComboBox.getValue().toString());
        StringBuilder string = new StringBuilder();
        populateAppointments(userId);
        Collections.sort(appointments);
        appointments.forEach(appointment -> {
            string.append(String.format("%s: %s-%s\nName: %s, Location: %s\n\n", appointment.getStart().toLocalDate().format(dateFormatter), appointment.getStart().format(timeFormatter), appointment.getEnd().format(timeFormatter), appointment.getCustomerName(), appointment.getLocation()));
        });
        if(string.length() == 0) {
            string.append("No appointments found for this consultant.");
        }
        resultLabel.setText(string.toString());
    }

    public void populateConsultantComboBox() throws SQLException {
        Connection conn = Util.DatabaseHelper.getConnection();
        Statement statement = conn.createStatement();
        ObservableList<String> consultantsList = observableArrayList();
        try {
            ResultSet rsConsultants = statement.executeQuery("SELECT userId, userName FROM user");
            while (rsConsultants.next()) {
                int consultantId = rsConsultants.getInt("userId");
                String consultantName = rsConsultants.getString("userName");
                String consultantNameAndId = consultantName + " (" + consultantId + ")";
                consultantsList.add(consultantNameAndId);
            }
            consultantsList.forEach(consultant -> consultantComboBox.getItems().add(consultant));
            rsConsultants.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public void populateAppointments(int userId) throws SQLException {
        appointments.clear();
        Connection conn = Util.DatabaseHelper.getConnection();
        Statement statement = conn.createStatement();
        try {
            ResultSet rs = statement.executeQuery("SELECT appointment.*, customer.customerName, user.userName FROM appointment INNER JOIN customer ON appointment.customerId = customer.customerId INNER JOIN user ON appointment.userId = user.userId WHERE appointment.userId = " + userId);
            while (rs.next()) {
                int appointmentId = rs.getInt("appointmentId");
                int customerId = rs.getInt("customerId");
                String title = rs.getString("title");
                String description = rs.getString("description");
                String location = rs.getString("location");
                String contact = rs.getString("contact");
                String type = rs.getString("type");
                String url = rs.getString("url");
                ZoneId newzid = ZoneId.systemDefault();
                Timestamp startTs = rs.getTimestamp("start");
                ZonedDateTime newZdtStart = startTs.toLocalDateTime().atZone(ZoneId.of("UTC"));
                ZonedDateTime newLocalStart = newZdtStart.withZoneSameInstant(newzid);
                Timestamp endTs = rs.getTimestamp("end");
                ZonedDateTime newZdtEnd = endTs.toLocalDateTime().atZone(ZoneId.of("UTC"));
                ZonedDateTime newLocalEnd = newZdtEnd.withZoneSameInstant(newzid);
                String customerName = rs.getString("customerName");
                String consultantName = rs.getString("userName");

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

                appointments.add(appointment);
            }
            rs.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public Integer extractId(String string) {
        int openingParensIndex = string.indexOf('(');
        int closingParensIndex = string.indexOf(')');
        Integer id = Integer.parseInt(string.substring(openingParensIndex + 1, closingParensIndex));
        return id;
    }

    public void handleExitClick() {
        System.exit(0);
    }

}
