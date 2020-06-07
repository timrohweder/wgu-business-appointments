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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author tim
 */
public class CalendarViewController implements Initializable {

    private ObservableList<Appointment> allAppointments = observableArrayList();
    private ObservableList<Appointment> filteredAppointments = observableArrayList();

    private boolean monthMode = true;
    private LocalDate selectedDate = LocalDate.now();

    @FXML
    private TableView<Appointment> appointmentsTable;
    @FXML
    private TableColumn<Appointment, String> customerNameColumn;
    @FXML
    private TableColumn<Appointment, String> consultantNameColumn;
    @FXML
    private TableColumn<Appointment, String> titleColumn;
    @FXML
    private TableColumn<Appointment, String> descriptionColumn;
    @FXML
    private TableColumn<Appointment, String> locationColumn;
    @FXML
    private TableColumn<Appointment, String> contactColumn;
    @FXML
    private TableColumn<Appointment, String> typeColumn;
    @FXML
    private TableColumn<Appointment, LocalDateTime> startColumn;
    @FXML
    private TableColumn<Appointment, LocalDateTime> endColumn;
    @FXML
    private TableColumn<Appointment, LocalTime> dateColumn;
    @FXML
    private DatePicker datePicker;
    @FXML
    private RadioButton monthRadioButton;
    @FXML
    private RadioButton weekRadioButton;
    private ToggleGroup calendarModeToggleGroup;
    @FXML
    private Label dateRangeExplanationLabel;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        datePicker.setValue(LocalDate.now());
        renderDateRange();

        try {
            populateAppointmentsTable();
        } catch (SQLException ex) {
            Logger.getLogger(AppointmentsViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        consultantNameColumn.setCellValueFactory(new PropertyValueFactory<>("consultantName"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contact"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        startColumn.setCellValueFactory(new PropertyValueFactory<>("formattedStart"));
        endColumn.setCellValueFactory(new PropertyValueFactory<>("formattedEnd"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        appointmentsTable.setItems(filteredAppointments);

        calendarModeToggleGroup = new ToggleGroup();
        monthRadioButton.setToggleGroup(calendarModeToggleGroup);
        weekRadioButton.setToggleGroup(calendarModeToggleGroup);
        monthRadioButton.setSelected(true);
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

    public void handleRadioButtonChange() {
        if (this.calendarModeToggleGroup.getSelectedToggle().equals(monthRadioButton)) {
            monthMode = true;
        }
        if (this.calendarModeToggleGroup.getSelectedToggle().equals(weekRadioButton)) {
            monthMode = false;
        }
        renderDateRange();
        filterAppointmentsTable();
    }

    public void handleDatePickerChange() {
        selectedDate = datePicker.getValue();
        renderDateRange();
        filterAppointmentsTable();
    }

    public void renderDateRange() {
        if (monthMode) {
            String month = selectedDate.getMonth().toString().toLowerCase();
            String formattedMonth = month.substring(0, 1).toUpperCase() + month.substring(1);
            dateRangeExplanationLabel.setText("the month of " + formattedMonth + ", " + selectedDate.getYear());
        } else {
            int dateOffset = selectedDate.getDayOfWeek().getValue() - 1;
            selectedDate = selectedDate.minusDays(dateOffset);
            String month = selectedDate.getMonth().toString().toLowerCase();
            String formattedMonth = month.substring(0, 1).toUpperCase() + month.substring(1);
            String dayOfWeek = selectedDate.getDayOfWeek().toString().toLowerCase();
            String formattedDayOfWeek = dayOfWeek.substring(0, 1).toUpperCase() + dayOfWeek.substring(1);
            dateRangeExplanationLabel.setText("the week beginning " + formattedDayOfWeek + ", " + formattedMonth + " " + selectedDate.getDayOfMonth() + ", " + selectedDate.getYear());
        }
    }

    public void populateAppointmentsTable() throws SQLException {
        allAppointments.clear();
        Connection conn = Util.DatabaseHelper.getConnection();
        Statement statement = conn.createStatement();
        try {
            ResultSet rs = statement.executeQuery("SELECT appointment.*, customer.customerName, user.userName FROM appointment INNER JOIN customer ON appointment.customerId = customer.customerId INNER JOIN user ON appointment.userId = user.userId");
            while (rs.next()) {
                int appointmentId = rs.getInt("appointmentId");
                int customerId = rs.getInt("customerId");
                int userId = rs.getInt("userId");
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

                allAppointments.add(appointment);
                filterAppointmentsTable();
            }
            rs.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public void filterAppointmentsTable() {
        filteredAppointments.clear();
        if (monthMode) {
            // this lambda makes the program more efficient since it would take more time and code to create a custom function just for the purpose of
            // checking each appointment if it should pass through the filter. This way, the check can be done "in place"
            allAppointments.forEach(appointment -> {
                LocalDateTime date = appointment.getStart();
                if (date.getMonth() == selectedDate.getMonth() && date.getYear() == selectedDate.getYear()) {
                    filteredAppointments.add(appointment);
                }
            });
        } else {
            LocalDate endDate = selectedDate.plusDays(6);
            // this lambda makes the program more efficient since it would take more time and code to create a custom function just for the purpose of
            // checking each appointment if it should pass through the filter. This way, the check can be done "in place"
            allAppointments.forEach(appointment -> {
                LocalDate date = appointment.getStart().toLocalDate();
                if ((date.isAfter(selectedDate) && date.isBefore(endDate)) || (date.isEqual(selectedDate) || date.isEqual(endDate))) {
                    filteredAppointments.add(appointment);
                }
            });
        }
    }

    public void handleExitClick() {
        System.exit(0);
    }

}
