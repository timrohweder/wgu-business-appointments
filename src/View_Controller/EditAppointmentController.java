/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;

import Exceptions.InvalidAppointmentHoursException;
import Model.Appointment;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author tim
 */
public class EditAppointmentController implements Initializable {

    private Appointment appointmentToModify;

    @FXML
    private ComboBox customerComboBox;
    @FXML
    private ComboBox consultantComboBox;
    @FXML
    private TextField titleTextField;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private TextField locationTextField;
    @FXML
    private TextField contactTextField;
    @FXML
    private TextField typeTextField;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private ComboBox startTimeHourComboBox;
    @FXML
    private ComboBox startTimeMinuteComboBox;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private ComboBox endTimeHourComboBox;
    @FXML
    private ComboBox endTimeMinuteComboBox;

    public void setAppointmentToModify(Appointment appointment) {
        appointmentToModify = appointment;
        try {
            // TODO
            populateCustomerAndConsultantComboBoxes();
        } catch (SQLException ex) {
            Logger.getLogger(AddAppointmentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            generateAppointmentId();
        } catch (SQLException ex) {
            Logger.getLogger(AddAppointmentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        populateTimeComboBoxes();
        titleTextField.setText(appointment.getTitle());
        descriptionTextField.setText(appointment.getDescription());
        locationTextField.setText(appointment.getLocation());
        contactTextField.setText(appointment.getContact());
        typeTextField.setText(appointment.getType());
        LocalDateTime startTime = appointment.getStart();
        startDatePicker.setValue(startTime.toLocalDate());
        startTimeHourComboBox.getSelectionModel().select(Integer.toString(startTime.getHour()));
        startTimeMinuteComboBox.getSelectionModel().select(Integer.toString(startTime.getMinute()));
        LocalDateTime endTime = appointment.getEnd();
        endDatePicker.setValue(endTime.toLocalDate());
        endTimeHourComboBox.getSelectionModel().select(Integer.toString(endTime.getHour()));
        endTimeMinuteComboBox.getSelectionModel().select(Integer.toString(endTime.getMinute()));
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

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

    public void populateCustomerAndConsultantComboBoxes() throws SQLException {
        Connection conn = Util.DatabaseHelper.getConnection();
        Statement statement = conn.createStatement();
        ObservableList<String> customersList = observableArrayList();
        ObservableList<String> consultantsList = observableArrayList();
        int customerComboBoxSelectionIndex = 0;
        int consultantComboBoxSelectionIndex = 0;
        try {
            ResultSet rsCustomers = statement.executeQuery("SELECT customerId, customerName FROM customer");
            while (rsCustomers.next()) {
                int customerId = rsCustomers.getInt("customerId");
                String customerName = rsCustomers.getString("customerName");
                String customerNameAndId = customerName + " (" + customerId + ")";
                if (customerId == appointmentToModify.getCustomerId()) {
                    customerComboBoxSelectionIndex = customersList.size();
                }
                customersList.add(customerNameAndId);
            }

            customersList.forEach(customer -> {
                customerComboBox.getItems().add(customer);
            });
            customerComboBox.getSelectionModel().select(customerComboBoxSelectionIndex);
            rsCustomers.close();
            ResultSet rsConsultants = statement.executeQuery("SELECT userId, userName FROM user");

            while (rsConsultants.next()) {
                int consultantId = rsConsultants.getInt("userId");
                String consultantName = rsConsultants.getString("userName");
                String consultantNameAndId = consultantName + " (" + consultantId + ")";
                if (consultantId == appointmentToModify.getUserId()) {
                    consultantComboBoxSelectionIndex = consultantsList.size();
                }
                consultantsList.add(consultantNameAndId);
            }

            consultantsList.forEach(consultant -> consultantComboBox.getItems().add(consultant));
            consultantComboBox.getSelectionModel().select(consultantComboBoxSelectionIndex);
            rsConsultants.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public void populateTimeComboBoxes() {
        startTimeHourComboBox.getItems().addAll("00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11",
                "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23");
        startTimeMinuteComboBox.getItems().addAll("00", "15", "30", "45");
        endTimeHourComboBox.getItems().addAll("00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11",
                "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23");
        endTimeMinuteComboBox.getItems().addAll("00", "15", "30", "45");
    }

    public int generateAppointmentId() throws SQLException {
        Connection conn = Util.DatabaseHelper.getConnection();
        Statement statement = conn.createStatement();
        List<Integer> listOfAppointmentIds = new ArrayList<>();
        try {
            ResultSet rsAppointmentIds = statement.executeQuery("SELECT appointmentId FROM appointment");
            while (rsAppointmentIds.next()) {
                int appointmentId = rsAppointmentIds.getInt("appointmentId");
                listOfAppointmentIds.add(appointmentId);
            }
            rsAppointmentIds.close();
            if (listOfAppointmentIds.size() > 0) {
                return Collections.max(listOfAppointmentIds) + 1;
            } else {
                return 1;
            }
        } catch (SQLException se) {
            se.printStackTrace();
            return -1;
        }
    }

    public void handleSave(ActionEvent event) throws SQLException, IOException {

        try {
            int customerId = extractId(customerComboBox.getValue().toString());
            int userId = extractId(consultantComboBox.getValue().toString());
            String title = titleTextField.getText();
            String description = descriptionTextField.getText();
            String location = locationTextField.getText();
            String contact = contactTextField.getText();
            String type = typeTextField.getText();
            String url = "not needed";
            LocalDate startLocalDate = startDatePicker.getValue();
            int startTimeHour = Integer.parseInt(startTimeHourComboBox.getValue().toString());
            if (startTimeHour < 8 || startTimeHour >= 18) {
                throw new InvalidAppointmentHoursException("Appointments must be between 8am and 6pm.");
            }
            int startTimeMinute = Integer.parseInt(startTimeMinuteComboBox.getValue().toString());
            LocalDate endLocalDate = endDatePicker.getValue();
            int endTimeHour = Integer.parseInt(endTimeHourComboBox.getValue().toString());
            int endTimeMinute = Integer.parseInt(endTimeMinuteComboBox.getValue().toString());
            if (endTimeHour > 18 || (endTimeHour == 18 && endTimeMinute > 0)) {
                throw new InvalidAppointmentHoursException("Appointments must be between 8am and 6pm.");
            }
            String createDate = "2019-01-01 00:00:00";
            String createdBy = "not needed";
            String lastUpdate = "2019-01-01 00:00:00";
            String lastUpdateBy = "not needed";

            LocalTime startLocalTime = LocalTime.of(startTimeHour, startTimeMinute);
            LocalTime endLocalTime = LocalTime.of(endTimeHour, endTimeMinute);

            ZoneId zid = ZoneId.systemDefault();

            LocalDateTime startTimeLocalDate = LocalDateTime.of(startLocalDate, startLocalTime);
            LocalDateTime endTimeLocalDate = LocalDateTime.of(endLocalDate, endLocalTime);

            if (endTimeLocalDate.isBefore(startTimeLocalDate)) {
                throw new InvalidAppointmentHoursException("The end time cannot be before the start time.");
            }

            ZonedDateTime zdtStart = startTimeLocalDate.atZone(zid);
            ZonedDateTime zdtEnd = endTimeLocalDate.atZone(zid);

            ZonedDateTime utcStart = zdtStart.withZoneSameInstant(ZoneId.of("UTC"));
            ZonedDateTime utcEnd = zdtEnd.withZoneSameInstant(ZoneId.of("UTC"));

            Timestamp startTs = Timestamp.valueOf(utcStart.toLocalDateTime());
            Timestamp endTs = Timestamp.valueOf(utcEnd.toLocalDateTime());

            Connection conn = Util.DatabaseHelper.getConnection();
            Statement statement = conn.createStatement();

            ResultSet rs = statement.executeQuery("SELECT start, appointmentId, end FROM appointment");
            while (rs.next()) {
                Timestamp startTsToCheck = rs.getTimestamp("start");
                ZonedDateTime newZdtStart = startTsToCheck.toLocalDateTime().atZone(ZoneId.of("UTC"));
                Timestamp endTsToCheck = rs.getTimestamp("end");
                ZonedDateTime newZdtEnd = endTsToCheck.toLocalDateTime().atZone(ZoneId.of("UTC"));

                if ((utcStart.isBefore(newZdtEnd) && newZdtStart.isBefore(utcEnd)) && rs.getInt("appointmentId") != appointmentToModify.getAppointmentId()) {
                    throw new InvalidAppointmentHoursException("This appointment overlaps with another appointment. Please change the time.");
                }
            }

            String sql = String.format("UPDATE appointment "
                    + "SET "
                    + "customerId = %d, "
                    + "userId = %d,"
                    + "title = '%s',"
                    + "description = '%s',"
                    + "location = '%s',"
                    + "contact = '%s',"
                    + "type = '%s',"
                    + "url = '%s',"
                    + "start = '%s',"
                    + "end = '%s',"
                    + "createDate = '%s',"
                    + "createdBy = '%s',"
                    + "lastUpdate = '%s',"
                    + "lastUpdateBy = '%s'"
                    + "WHERE appointmentId = %d", customerId, userId, title, description, location, contact, type, url, startTs, endTs, createDate, createdBy, lastUpdate, lastUpdateBy, appointmentToModify.getAppointmentId());
            statement.executeUpdate(sql);
            returnToAppointmentScreen(event);

        } catch (InvalidAppointmentHoursException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid appointment time");
            alert.setHeaderText("Please choose another appointment time");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }

    }

    public Integer extractId(String string) {
        int openingParensIndex = string.indexOf('(');
        int closingParensIndex = string.indexOf(')');
        Integer id = Integer.parseInt(string.substring(openingParensIndex + 1, closingParensIndex));
        return id;
    }

    public void returnToAppointmentScreen(ActionEvent event) throws IOException {
        Parent appointmentsViewScreenParent = FXMLLoader.load(getClass().getResource("AppointmentsView.fxml"));
        Scene appointmentsViewScreenScene = new Scene(appointmentsViewScreenParent);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(appointmentsViewScreenScene);
        window.show();
    }
}
