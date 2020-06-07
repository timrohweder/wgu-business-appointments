/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;

import Exceptions.InvalidLoginException;
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
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author tim
 */
public class LoginViewController implements Initializable {

    private ObservableList<Appointment> impendingAppointments = observableArrayList();
    ResourceBundle languageRb = ResourceBundle.getBundle("language_files/rb");

    @FXML
    private TextField userNameTextField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private Label errorLabel;
    @FXML
    private Label titleLabel;
    @FXML
    private Label userLabel;
    @FXML
    private Label passLabel;
    @FXML
    private Button loginButton;
    @FXML
    private Button exitButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        titleLabel.setText(languageRb.getString("scheduleSoftwareLogin"));
        userLabel.setText(languageRb.getString("username"));
        passLabel.setText(languageRb.getString("password"));
        loginButton.setText(languageRb.getString("login"));
        exitButton.setText(languageRb.getString("exit"));
    }

    public void handleLoginClick(ActionEvent event) throws SQLException, IOException {
        errorLabel.setText("");
        Connection conn = Util.DatabaseHelper.getConnection();
        Statement statement = conn.createStatement();
        ResultSet rs = statement.executeQuery(String.format("SELECT password FROM user WHERE userName = '%s'", userNameTextField.getText()));
        try {
            if (!rs.isBeforeFirst()) {
                throw new InvalidLoginException("");
            }
            while (rs.next()) {
                if (rs.getString("password").equals(passwordTextField.getText())) {
                    checkForImpendingAppointments();
                    logLogin(userNameTextField.getText());
                    goToAppointmentsPage(event);
                } else {
                    throw new InvalidLoginException("");
                }
            }
        } catch (InvalidLoginException e) {
            errorLabel.setText(languageRb.getString("loginError"));
        } finally {
            rs.close();
            conn.close();
        }
    }

    public void goToAppointmentsPage(ActionEvent event) throws IOException {
        Parent addAppointmentScreenParent = FXMLLoader.load(getClass().getResource("AppointmentsView.fxml"));
        Scene addAppointmentScreenScene = new Scene(addAppointmentScreenParent);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(addAppointmentScreenScene);
        window.show();
    }

    public void checkForImpendingAppointments() throws SQLException {
        Connection conn = Util.DatabaseHelper.getConnection();
        Statement statement = conn.createStatement();
        StringBuilder appointmentAnnouncements = new StringBuilder();
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
                LocalDateTime start = newLocalStart.toLocalDateTime();
                LocalDateTime end = newLocalEnd.toLocalDateTime();

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
                        start,
                        end,
                        customerName,
                        consultantName
                );

                if ((start.equals(LocalDateTime.now()) || start.isAfter(LocalDateTime.now())) && (start.equals(LocalDateTime.now()) || start.isBefore(LocalDateTime.now().plusMinutes(15)))) {
                    impendingAppointments.add(appointment);
                }

            }
            rs.close();
            if (impendingAppointments.size() > 0) {
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                impendingAppointments.forEach(appointment -> {
                    appointmentAnnouncements.append(String.format("%s: %s-%s\nName: %s, Location: %s\n\n", appointment.getStart().toLocalDate().format(dateFormatter), appointment.getStart().format(timeFormatter), appointment.getEnd().format(timeFormatter), appointment.getCustomerName(), appointment.getLocation()));
                });
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Upcoming appointments");
                alert.setHeaderText("Here are your upcoming appointments in the next 15 minutes: ");
                alert.setContentText(appointmentAnnouncements.toString());
                alert.showAndWait();
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public void logLogin(String userName) throws IOException {
        Logger logger = Logger.getLogger("LoginLog");
        FileHandler fileHandler = new FileHandler("loginLog.txt", true);
        logger.addHandler(fileHandler);
        logger.info("Login at " + LocalDateTime.now() + " by " + userName);
    }

    public void handleExitClick() {
        System.exit(0);
    }
}
