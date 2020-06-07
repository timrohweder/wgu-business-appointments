/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;

import Model.Appointment;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javafx.collections.FXCollections.observableArrayList;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class
 *
 * @author tim
 */
public class AppointmentsViewController implements Initializable {

    private ObservableList<Appointment> allAppointments = observableArrayList();

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
    private TableColumn<Appointment, LocalTime> startColumn;
    @FXML
    private TableColumn<Appointment, LocalTime> endColumn;
    @FXML
    private TableColumn<Appointment, LocalTime> dateColumn;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
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

        appointmentsTable.setItems(allAppointments);
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

    public void handleAddAppointmentClick(ActionEvent event) throws IOException {
        Parent addAppointmentScreenParent = FXMLLoader.load(getClass().getResource("AddAppointment.fxml"));
        Scene addAppointmentScreenScene = new Scene(addAppointmentScreenParent);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(addAppointmentScreenScene);
        window.show();
    }

    public void handleEditAppointmentClick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("EditAppointment.fxml"));
        Parent editAppointmentScreenParent = loader.load();
        Scene editAppointmentScreenScene = new Scene(editAppointmentScreenParent);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        EditAppointmentController controller = loader.getController();
        controller.setAppointmentToModify(appointmentsTable.getSelectionModel().getSelectedItems().get(0));
        window.setScene(editAppointmentScreenScene);
        window.show();
    }

    public void handleDeleteAppointmentClick() throws SQLException {
        Connection conn = Util.DatabaseHelper.getConnection();
        Statement statement = conn.createStatement();
        String sql = String.format("DELETE FROM appointment WHERE appointmentId = %d", appointmentsTable.getSelectionModel().getSelectedItems().get(0).getAppointmentId());
        statement.executeUpdate(sql);
        populateAppointmentsTable();
    }

    public void handleCustomerProfileClick(ActionEvent event) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("CustomerProfileView.fxml"));
        Parent customerProfileScreenParent = loader.load();
        Scene customerProfileScreenScene = new Scene(customerProfileScreenParent);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        CustomerProfileViewController controller = loader.getController();
        controller.setCustomer(appointmentsTable.getSelectionModel().getSelectedItems().get(0).getCustomerId());
        window.setScene(customerProfileScreenScene);
        window.show();
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
            }
            rs.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

}
