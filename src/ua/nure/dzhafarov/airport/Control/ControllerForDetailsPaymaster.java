package ua.nure.dzhafarov.airport.Control;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import ua.nure.dzhafarov.airport.ConnectionToDB;
import ua.nure.dzhafarov.airport.Model.ClassOfFlight;
import ua.nure.dzhafarov.airport.Model.Discount;
import ua.nure.dzhafarov.airport.Model.Passenger;
import ua.nure.dzhafarov.airport.Model.Person;

import java.sql.ResultSet;

/**
 * Created by Emil on 07.11.2016.
 */
public class ControllerForDetailsPaymaster
{
    private ObservableList<Passenger> listOfPassengers = FXCollections.observableArrayList();
    private ConnectionToDB connectionToDB = ControllerIndexForm.getDb();
    private ClassOfFlight selectedClassOfFlight = ControllerForPaymaster.getTempClassOfFlight();

    @FXML
    private TableView<Passenger> passengersTable;

    @FXML
    private TableColumn<Passenger, String> firstName;

    @FXML
    private TableColumn<Passenger, String> lastName;

    @FXML
    private TableColumn<Passenger, String> middleName;


    @FXML
    private TableColumn<Passenger, String> passportNumber;


    @FXML
    private TableColumn<Passenger, String> discountColumn;

    @FXML
    private Label infoLabel;

    @FXML
    private void initialize()
    {
        firstName.setCellValueFactory(new PropertyValueFactory<Passenger, String>("FirstName"));
        lastName.setCellValueFactory(new PropertyValueFactory<Passenger, String>("LastName"));
        middleName.setCellValueFactory(new PropertyValueFactory<Passenger, String>("MiddleName"));
        passportNumber.setCellValueFactory(new PropertyValueFactory<Passenger, String>("PassportNumber"));
        discountColumn.setCellValueFactory(new PropertyValueFactory<Passenger, String>("DiscountName"));

        passengersTable.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> source, Number oldWidth, Number newWidth)
            {
                TableHeaderRow header = (TableHeaderRow) passengersTable.lookup("TableHeaderRow");
                header.reorderingProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        header.setReordering(false);
                    }
                });
            }
        });

        initData();

        passengersTable.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(javafx.scene.input.MouseEvent event) {
                if (event.isSecondaryButtonDown()) {
                    final ContextMenu menu = new ContextMenu();
                    MenuItem delete = new MenuItem("Отменить бронирование");
                    delete.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            try
                            {
                                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                alert.setTitle("Подтверждение");
                                alert.setHeaderText("Вы действительно хотите отменить бронирование?");
                                alert.setContentText("Для подтверждения нажмите \"ОК\"");
                                alert.getButtonTypes().clear();
                                alert.getButtonTypes().add(new ButtonType("ОК"));
                                alert.getButtonTypes().add(new ButtonType("Отмена"));
                                alert.showAndWait();

                                if (alert.getResult().getText().equals("ОК"))
                                {
                                    deletePassenger(passengersTable.getSelectionModel().getSelectedItem());
                                }
                                else
                                {
                                    alert.close();
                                }
                            }
                            catch (NullPointerException e) {
                                Alert ob = new Alert(Alert.AlertType.WARNING);
                                ob.setTitle("");
                                ob.setHeaderText("Предупреждение");
                                ob.setContentText("Выберите пасажира для продолжения.");
                                ob.showAndWait();
                            }
                            catch (Exception r) {
                                r.printStackTrace();
                            }
                        }
                    });

                    menu.getItems().add(delete);
                    passengersTable.setContextMenu(menu);
                }
            }
        });
    }

    private void initData()
    {
        listOfPassengers.clear();

        try
        {
            String SQL = "SELECT first_name, last_name, middle_name, birthday, "
                         + "passport_number, nationality, discount.type, discount.discount, discount.id " +
                    "FROM passenger JOIN discount JOIN orders JOIN flight JOIN class JOIN trip  " +
                    "ON orders.pass_id = passenger.id " +
                         "AND IFNULL(passenger.discount, 12) = discount.id " +
                    "AND orders.class_of_comfort = class.id " +
                    "AND orders.flight_id = flight.id " +
                    "AND flight.trip_id = trip.id " +
                    "AND class.id_of_trip = trip.id " +
                    "WHERE trip.number = '" + selectedClassOfFlight.getNumber() + "' " +
                    "AND flight.departure = '" + selectedClassOfFlight.getDeparture() + "' " +
                    "AND class.type = '" + selectedClassOfFlight.getClassComfort() + "'";

            ResultSet resultSet = connectionToDB.retriveData(SQL);

            while (resultSet.next())
            {
                Person temp = new Person(
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("middle_name"),
                        resultSet.getString("birthday"),
                        resultSet.getString("passport_number"),
                        resultSet.getString("nationality")
                );

                Discount discount = new Discount(
                        resultSet.getString("discount.type"),
                        resultSet.getDouble("discount.discount"),
                        resultSet.getLong("discount.id")
                );

                Passenger passenger = new Passenger(temp, discount);
                listOfPassengers.add(passenger);
            }

            passengersTable.setItems(listOfPassengers);

            if (listOfPassengers.size() > 0)
            {
                passengersTable.setVisible(true);
                infoLabel.setText("Список пассажиров");
            }
            else
            {
                passengersTable.setVisible(false);
                infoLabel.setText("Список пассажиров пуст");

                ControllerForPaymaster.getDetailStage().close();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void deletePassenger(Passenger person) throws Exception
    {
        String SQL = "DELETE FROM orders WHERE pass_id = " +
                     "(SELECT id FROM passenger WHERE passport_number='" +
                     person.getPassportNumber() + "') " +
                     "AND class_of_comfort = " +
                     "(SELECT class.id FROM class JOIN trip ON class.id_of_trip = trip.id " +
                     "AND trip.number = '" + selectedClassOfFlight.getNumber() + "' " +
                     "AND class.type = '" + selectedClassOfFlight.getClassComfort() + "') " +
                     "AND flight_id = (SELECT flight.id FROM flight JOIN trip " +
                     "ON flight.trip_id = trip.id " +
                     "AND trip.number = '" + selectedClassOfFlight.getNumber() + "' " +
                     "AND flight.departure = '" + selectedClassOfFlight.getDeparture() + "')";

        connectionToDB.executeStatement(SQL);

        initData();
    }
}
