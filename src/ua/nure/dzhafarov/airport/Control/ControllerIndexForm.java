package ua.nure.dzhafarov.airport.Control;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import ua.nure.dzhafarov.airport.ConnectionToDB;
import ua.nure.dzhafarov.airport.View.GetResources;

import java.sql.*;

/**
 * Created by Emil on 07.11.2016.
 */
public class ControllerIndexForm
{
    private static String loginEmploee = null;
    private static String passwordEmploee = null;

    private static ConnectionToDB db = null;
    @FXML
    private TextField login;
    @FXML
    private PasswordField password;
    @FXML
    private RadioButton admin;
    @FXML
    private RadioButton manager;
    @FXML
    private RadioButton paymaster;
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private void initialize()
    {
        login.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke)
            {
                if (ke.getCode().equals(KeyCode.ENTER))
                {
                    onClickEntrance();
                }
            }
        });
        password.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke)
            {
                if (ke.getCode().equals(KeyCode.ENTER))
                {
                    onClickEntrance();
                }
            }
        });
    }
    @FXML
    private void onClickEntrance()
    {
        loginEmploee = login.getText().trim();
        passwordEmploee = password.getText().trim();

        try
        {
            Stage stage = new Stage();

            if (admin.isSelected())
            {
                String url = "jdbc:mysql://localhost:3306/?useSSL=false";
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                Connection connection = DriverManager.getConnection(url, loginEmploee, passwordEmploee);
                Statement statement = connection.createStatement();
                String SQL = "CREATE DATABASE IF NOT EXISTS airport2";
                statement.execute(SQL);

                db = new ConnectionToDB(loginEmploee, passwordEmploee);

                SQL = "CREATE TABLE IF NOT EXISTS trip ("
                             + "  id int(11) NOT NULL AUTO_INCREMENT,"
                             + "  departure_from varchar(45) NOT NULL,"
                             + "  arrival_in varchar(45) NOT NULL,"
                             + "  duration time NOT NULL,"
                             + "  airline varchar(45) NOT NULL,"
                             + "  number varchar(45) NOT NULL,"
                             + "  PRIMARY KEY (id),"
                             + "  UNIQUE KEY number_UNIQUE (number)"
                             + ")";
                db.executeStatement(SQL);

                SQL = "CREATE TABLE IF NOT EXISTS flight ("
                      + "  id int(11) NOT NULL AUTO_INCREMENT,"
                      + "  departure datetime NOT NULL,"
                      + "  trip_id int(11) NOT NULL,"
                      + "  PRIMARY KEY (trip_id, departure),"
                      + "  UNIQUE KEY id_UNIQUE (id),"
                      + "  CONSTRAINT trip_id FOREIGN KEY (trip_id) "
                      + "  REFERENCES trip (id) ON DELETE CASCADE ON UPDATE CASCADE"
                      + ")";
                db.executeStatement(SQL);

                SQL = "CREATE TABLE IF NOT EXISTS class ("
                      + "  id_of_trip int(11) NOT NULL,"
                      + "  type varchar(30) NOT NULL,"
                      + "  count_of_places int(11) NOT NULL,"
                      + "  ticket_cost double NOT NULL,"
                      + "  id int(11) NOT NULL AUTO_INCREMENT,"
                      + "  PRIMARY KEY (id_of_trip,type),"
                      + "  UNIQUE KEY id_UNIQUE (id),"
                      + "  CONSTRAINT id_of_trip FOREIGN KEY (id_of_trip)"
                      + "  REFERENCES trip (id) ON DELETE CASCADE ON UPDATE CASCADE"
                      + ")";

                db.executeStatement(SQL);

                SQL = "CREATE TABLE IF NOT EXISTS discount ("
                        + "  id int(11) NOT NULL AUTO_INCREMENT,"
                        + "  discount double NOT NULL,"
                        + "  type varchar(45) NOT NULL,"
                        + "  PRIMARY KEY (id)"
                        + ")";
                db.executeStatement(SQL);

                SQL = "CREATE TABLE IF NOT EXISTS passenger ("
                        + "  id int(11) NOT NULL AUTO_INCREMENT,"
                        + "  first_name varchar(45) NOT NULL,"
                        + "  last_name varchar(45) NOT NULL,"
                        + "  middle_name varchar(45) NOT NULL,"
                        + "  birthday date NOT NULL,"
                        + "  passport_number varchar(45) NOT NULL,"
                        + "  nationality varchar(45) NOT NULL,"
                        + "  discount int(11),"
                        + "  PRIMARY KEY (id),"
                        + "  UNIQUE KEY passport_number_UNIQUE (passport_number),"
                        + "  KEY discount_idx (discount),"
                        + "  CONSTRAINT discount FOREIGN KEY (discount)"
                        + "  REFERENCES discount (id) ON DELETE SET NULL ON UPDATE CASCADE"
                        + ")";
                db.executeStatement(SQL);


                SQL = "CREATE TABLE IF NOT EXISTS employee ("
                      + "  id int(11) NOT NULL AUTO_INCREMENT,"
                      + "  passport_number varchar(45) NOT NULL,"
                      + "  birthday date NOT NULL,"
                      + "  first_name varchar(45) NOT NULL,"
                      + "  last_name varchar(45) NOT NULL,"
                      + "  middle_name varchar(45) NOT NULL,"
                      + "  nationality varchar(45) NOT NULL,"
                      + "  identification_code bigint(25) NOT NULL,"
                      + "  login varchar(45) DEFAULT NULL,"
                      + "  occupation varchar(45) NOT NULL,"
                      + "  PRIMARY KEY (id),"
                      + "  UNIQUE KEY passport_number_UNIQUE (passport_number),"
                      + "  UNIQUE KEY identification_code_UNIQUE (identification_code),"
                      + "  UNIQUE KEY login_UNIQUE (login)"
                      + ")";
                db.executeStatement(SQL);

                SQL = "CREATE TABLE IF NOT EXISTS orders ("
                        + "  id int(11) NOT NULL AUTO_INCREMENT,"
                        + "  class_of_comfort int(11) NOT NULL,"
                        + "  flight_id int(11) NOT NULL,"
                        + "  passenger_id int(11) NOT NULL,"
                        + "  date_time_reserve datetime NOT NULL,"
                        + "  id_of_employee int(11) DEFAULT NULL,"
                        + "  PRIMARY KEY (flight_id,passenger_id),"
                        + "  UNIQUE KEY id_UNIQUE (id),"
                        + "  KEY passenger_id_idx (passenger_id),"
                        + "  KEY class_of_comfort_idx (class_of_comfort),"
                        + "  KEY id_of_employee_idx (id_of_employee),"
                        + "  CONSTRAINT class_of_comfort FOREIGN KEY (class_of_comfort)"
                        + "  REFERENCES class (id) ON DELETE CASCADE ON UPDATE CASCADE,"
                        + "  CONSTRAINT flight_id FOREIGN KEY (flight_id) "
                        + "  REFERENCES flight (id) ON DELETE CASCADE ON UPDATE CASCADE,"
                        + "  CONSTRAINT id_of_employee FOREIGN KEY (id_of_employee) "
                        + "  REFERENCES employee (id) ON DELETE SET NULL ON UPDATE CASCADE,"
                        + "  CONSTRAINT pass_id FOREIGN KEY (passenger_id) "
                        + "  REFERENCES passenger (id) ON DELETE CASCADE ON UPDATE CASCADE"
                        + ")";
                db.executeStatement(SQL);


                stage.setScene(new Scene(FXMLLoader.load(GetResources.class.getResource("forAdmin.fxml"))));
                stage.setTitle("Панель администратора");
                stage.setResizable(false);
            }
            else if (manager.isSelected())
            {
                loginEmploee = "manager" + loginEmploee;
                db = new ConnectionToDB(loginEmploee, passwordEmploee);
                stage.setScene(new Scene(FXMLLoader.load(GetResources.class.getResource("forManager.fxml"))));
                stage.setMinHeight(600);
                stage.setMinWidth(905);
                stage.setTitle("Панель менеджера");
                stage.setResizable(false);
            }
            else if (paymaster.isSelected())
            {
                loginEmploee = "paymaster" + loginEmploee;
                db = new ConnectionToDB(loginEmploee, passwordEmploee);
                stage.setScene(new Scene(FXMLLoader.load(GetResources.class.getResource("forPayMaster.fxml"))));
                stage.setMinHeight(685);

                stage.setTitle("Панель кассира");
                stage.setMinWidth(1150);
            }
            else
            {
                System.exit(0);
            }

            ((Stage) anchorPane.getScene().getWindow()).close();
            stage.showAndWait();
        }
        catch (SQLException err)
        {
            err.printStackTrace();
            Alert ob = new Alert(Alert.AlertType.ERROR);
            ob.setTitle("Ошибка");
            ob.setHeaderText("Неверный логин/пароль");
            ob.setContentText("Проверьте правильность логина и пароля");
            ob.showAndWait();

            System.exit(-1);
        }
        catch (Exception exx)
        {
            exx.printStackTrace();
        }
        finally
        {
            try
            {
                db.destroy();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    public static String getLoginEmploee(){
        return loginEmploee;
    }
    public static String getPasswordEmploee(){
        return passwordEmploee;
    }
    public static ConnectionToDB getDb()
    {
        return db;
    }
}
