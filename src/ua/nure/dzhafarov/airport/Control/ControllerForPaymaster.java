package ua.nure.dzhafarov.airport.Control;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;
import ua.nure.dzhafarov.airport.ConnectionToDB;
import ua.nure.dzhafarov.airport.Model.*;
import ua.nure.dzhafarov.airport.View.GetResources;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;



public class ControllerForPaymaster
{
    private ConnectionToDB connectionToDB = ControllerIndexForm.getDb();
    private ObservableList<ClassOfFlight> listOfClassOfFlights = FXCollections.observableArrayList();
    private static ClassOfFlight tempClassOfFlight = null;
    private static Stage detailStage = null;
    private static Passenger currentPassenger = null;

    static Passenger getCurrentPassenger()
    {
        return currentPassenger;
    }

    static ClassOfFlight getTempClassOfFlight()
    {
        return tempClassOfFlight;
    }

    static Stage getDetailStage()
    {
        return detailStage;
    }

    @FXML
    private TextField firstName;
    @FXML
    private TextField lastName;
    @FXML
    private TextField middleName;
    @FXML
    private DatePicker birthday;
    @FXML
    private TextField nationality;
    @FXML
    private TextField departureFrom;
    @FXML
    private TextField arrivalIn;
    @FXML
    private DatePicker flightDate;
    @FXML
    private ChoiceBox<String> comfortClass;
    @FXML
    private Label infoLabel;
    @FXML
    private TableView<ClassOfFlight> flightsTable;
    @FXML
    private TableColumn<ClassOfFlight, String> flightNumberColumn;
    @FXML
    private TableColumn<ClassOfFlight, String> departureFromColumn;
    @FXML
    private TableColumn<ClassOfFlight, String> arrivalInColumn;
    @FXML
    private TableColumn<ClassOfFlight, String> departureColumn;
    @FXML
    private TableColumn<ClassOfFlight, String> arrivalColumn;
    @FXML
    private TableColumn<ClassOfFlight, String> airlineColumn;
    @FXML
    private TableColumn<ClassOfFlight, Double> ticketCostColumn;
    @FXML
    private TableColumn<ClassOfFlight, String> classColumn;
    @FXML
    private TableColumn<ClassOfFlight, Integer> freePlacesColumn;
    @FXML
    private CheckBox checkBoxForCheckPassNum;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Text mistakeText;
    @FXML
    private ComboBox<Discount> discount;
    @FXML
    private TextField passNum;

    @FXML
    private void initialize()
    {
        try
        {
            flightNumberColumn.setCellValueFactory(new PropertyValueFactory<ClassOfFlight, String>("Number"));
            departureFromColumn.setCellValueFactory(new PropertyValueFactory<ClassOfFlight, String>("DepartureFrom"));
            arrivalInColumn.setCellValueFactory(new PropertyValueFactory<ClassOfFlight, String>("ArrivalIn"));
            airlineColumn.setCellValueFactory(new PropertyValueFactory<ClassOfFlight, String>("Airline"));
            departureColumn.setCellValueFactory(new PropertyValueFactory<ClassOfFlight, String>("DepartureForPresentation"));
            arrivalColumn.setCellValueFactory(new PropertyValueFactory<ClassOfFlight, String>("ArrivalForPresentation"));
            ticketCostColumn.setCellValueFactory(new PropertyValueFactory<ClassOfFlight, Double>("TicketCost"));
            freePlacesColumn.setCellValueFactory(new PropertyValueFactory<ClassOfFlight, Integer>("FreePlaces"));
            classColumn.setCellValueFactory(new PropertyValueFactory<ClassOfFlight, String>("ClassComfort"));
            flightsTable.setVisible(false);
            infoLabel.setVisible(false);
            birthday.setValue(LocalDate.of(1980, 1, 1));
            initInfoForTextFields(passNum, "SELECT passport_number FROM passenger");
            initInfoForTextFields(departureFrom, "SELECT DISTINCT departure_from FROM trip");
            initInfoForTextFields(arrivalIn, "SELECT DISTINCT arrival_in FROM trip");
            initInfoForTextFields(nationality, "SELECT DISTINCT nationality FROM passenger");
            initClassComforts();
            initDiscounts();

            flightsTable.widthProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> source, Number oldWidth, Number newWidth)
                {
                    TableHeaderRow header = (TableHeaderRow) flightsTable.lookup("TableHeaderRow");
                    header.reorderingProperty().addListener(new ChangeListener<Boolean>() {
                        @Override
                        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                            header.setReordering(false);
                        }
                    });
                }
            });
            flightsTable.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(javafx.scene.input.MouseEvent event) {
                    if (event.isSecondaryButtonDown()) {
                        final ContextMenu menu = new ContextMenu();
                        MenuItem details = new MenuItem("Список пассажиров");
                        details.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                try
                                {
                                    onClickDetails("detailsForPaymaster.fxml", "Список пассажиров");
                                }
                                catch (NullPointerException e) {
                                    Alert ob = new Alert(Alert.AlertType.WARNING);
                                    ob.setTitle("");
                                    ob.setHeaderText("Предупреждение");
                                    ob.setContentText("Выберите рейс для продолжения.");
                                    ob.showAndWait();
                                }
                                catch (Exception r) {
                                    Alert ob = new Alert(Alert.AlertType.ERROR);
                                    ob.setTitle("");
                                    ob.setHeaderText("Непредвиденная Ошибка");
                                    ob.setContentText("Попробуйте заново или перезапустите программу.");
                                    ob.showAndWait();
                                }
                            }
                        });

                        menu.getItems().addAll(details);
                        flightsTable.setContextMenu(menu);
                    }
                }
            });
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void onClickDetails(String pathScene, String title) throws Exception
    {
        tempClassOfFlight = this.flightsTable.getSelectionModel().getSelectedItem();

        if (tempClassOfFlight == null)
        {
            throw new NullPointerException();
        }

        detailStage = new Stage();
        detailStage.initModality(Modality.WINDOW_MODAL);
        detailStage.initOwner(anchorPane.getScene().getWindow());
        detailStage.setScene(new Scene(FXMLLoader.load(GetResources.class.getResource(pathScene))));
        detailStage.setTitle(title);
        detailStage.setResizable(false);
        detailStage.setMinHeight(400);
        detailStage.setMinWidth(820);
        detailStage.showAndWait();

        onClickSearchFlight();
        initInfoForTextFields(passNum, "SELECT passport_number FROM passenger");
        initInfoForTextFields(departureFrom, "SELECT DISTINCT departure_from FROM trip");
        initInfoForTextFields(arrivalIn, "SELECT DISTINCT arrival_in FROM trip");
        initInfoForTextFields(nationality, "SELECT DISTINCT nationality FROM passenger");
    }

    @FXML
    private void onClickReservePassenger()
    {
        try
        {
            String firstName = this.firstName.getText().trim();
            String lastName = this.lastName.getText().trim();
            String middleName = this.middleName.getText().trim();
            String birthday = this.birthday.getValue().toString();
            String passportNumber = this.passNum.getText().trim();
            String nationality = this.nationality.getText().trim();

            Person person = new Person(firstName, lastName, middleName,
                    birthday, passportNumber, nationality);

            currentPassenger = new Passenger(person, discount.getValue());


            if (flightsTable.getSelectionModel().getSelectedItem().getFreePlaces() == 0)
            {
                throw new Person.PassengerException("Недостаточно свободных мест");
            }

            tempClassOfFlight = flightsTable.getSelectionModel().getSelectedItem();

            if (this.firstName.isDisabled())
            {
                check = true;
            }

            onClickDetails("confirmationReserve.fxml", "Подтверждение бронирования");
            flushFields();
        }
        catch (Person.PassengerException ex)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Проверьте правльность введенных данных");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
        catch (NullPointerException ex)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Ошибка в при выборе полета");
            alert.setContentText("Выберите необходимый полет");
            alert.showAndWait();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @FXML
    private void onClickSearchFlight()
    {
        listOfClassOfFlights.clear();

        String departureFrom = this.departureFrom.getText().trim();
        String arrivalIn = this.arrivalIn.getText().trim();

        if (departureFrom.isEmpty() || arrivalIn.isEmpty())
        {
            String mistake;

            if (departureFrom.isEmpty())
                mistake = "Введите точку отправления";
            else
                mistake = "Введите точку прибытия";

            infoLabel.setText(mistake);
            infoLabel.setStyle("-fx-text-fill: crimson");
            infoLabel.setVisible(true);
            flightsTable.setVisible(false);
        }
        else
        {
            try
            {
                String SQL;

                if (!flightDate.getEditor().getText().isEmpty() && !comfortClass.getValue().isEmpty())
                {
                    String comfortClass = this.comfortClass.getValue();
                    String flightDate = this.flightDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    SQL = "SELECT trip.number, trip.departure_from, trip.arrival_in, " +
                          "flight.departure,  trip.duration, " +
                          " class.ticket_cost AS cost, trip.airline," +
                          " class.type, class.count_of_places -" +
                          " (SELECT IFNULL(COUNT(pass_id),0) FROM orders" +
                          " WHERE class_of_comfort=class.id AND flight_id = flight.id) AS free_places" +
                          " FROM  flight JOIN trip JOIN class" +
                          " ON flight.trip_id = trip.id " +
                          " AND class.id_of_trip = trip.id " +
                          " AND trip.departure_from LIKE '" + departureFrom + "%'" +
                          " AND trip.arrival_in LIKE '" + arrivalIn + "%'" +
                          " AND flight.departure > NOW()" +
                          " AND DATE(flight.departure)= '" + flightDate + "'" +
                          " AND class.type='" + comfortClass + "'" +
                          " ORDER BY flight.departure ASC, class.type ASC LIMIT 100";
                }
                else if (flightDate.getEditor().getText().isEmpty() && !comfortClass.getValue().isEmpty())
                {
                    String comfortClass = this.comfortClass.getValue();
                    SQL = "SELECT trip.number, trip.departure_from, trip.arrival_in, flight.departure, " +
                            " trip.duration, " +
                            " class.ticket_cost AS cost, trip.airline," +
                            " class.type, class.count_of_places -(SELECT COUNT(pass_id) FROM orders" +
                            " WHERE class_of_comfort=class.id AND flight_id = flight.id) AS free_places" +
                            " FROM  flight JOIN trip JOIN class" +
                            " ON flight.trip_id = trip.id " +
                            " AND class.id_of_trip = trip.id " +
                            " AND trip.departure_from LIKE '" + departureFrom + "%'" +
                            " AND trip.arrival_in LIKE '" + arrivalIn + "%'" +
                            " AND flight.departure > NOW() "  +
                            " AND class.type='" + comfortClass + "'" +
                            " ORDER BY flight.departure ASC, class.type ASC LIMIT 100";
                }
                else if (comfortClass.getValue().isEmpty() && !flightDate.getEditor().getText().isEmpty())
                {
                    String flightDate = this.flightDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                    SQL = "SELECT trip.number, trip.departure_from, trip.arrival_in, flight.departure, " +
                            " trip.duration, " +
                            " class.ticket_cost AS cost, trip.airline," +
                            " class.type, class.count_of_places -(SELECT COUNT(pass_id) FROM orders" +
                            " WHERE class_of_comfort=class.id AND flight_id = flight.id) AS free_places" +
                            " FROM  flight JOIN trip JOIN class" +
                            " ON flight.trip_id = trip.id " +
                            " AND class.id_of_trip = trip.id " +
                            " AND trip.departure_from LIKE '" + departureFrom + "%'" +
                            " AND trip.arrival_in LIKE '" + arrivalIn + "%'" +
                            " AND flight.departure > NOW() "
                          + " AND  DATE(flight.departure)='" + flightDate + "'" +
                            " ORDER BY flight.departure ASC, class.type ASC LIMIT 100";
                }
                else
                {
                    SQL = "SELECT trip.number, trip.departure_from, trip.arrival_in, flight.departure, " +
                            " trip.duration, " +
                            " class.ticket_cost AS cost, trip.airline," +
                            " class.type, class.count_of_places -(SELECT COUNT(pass_id) FROM orders" +
                            " WHERE class_of_comfort = class.id AND flight_id = flight.id) AS free_places" +
                            " FROM  flight JOIN trip JOIN class" +
                            " ON flight.trip_id = trip.id " +
                            " AND class.id_of_trip = trip.id " +
                            " AND trip.departure_from LIKE '" + departureFrom + "%'" +
                            " AND trip.arrival_in LIKE '" + arrivalIn + "%'" +
                            " AND flight.departure > NOW() " +
                            " ORDER BY flight.departure ASC, class.type ASC LIMIT 100";
                }

                ResultSet resultSet = connectionToDB.retriveData(SQL);

                while (resultSet.next())
                {
                    Trip trip = new Trip(resultSet.getString("trip.number"),
                            resultSet.getString("trip.departure_from"),
                            resultSet.getString("trip.arrival_in"),
                            resultSet.getString("trip.airline"),
                            resultSet.getString("trip.duration"));


                    Flight flight = new Flight(trip, resultSet.getString("flight.departure"));
                    ClassOfFlight temp = new ClassOfFlight(
                            flight,
                            resultSet.getString("cost"),
                            resultSet.getString("class.type"),
                            resultSet.getInt("free_places"));

                    listOfClassOfFlights.add(temp);
                }

                if (listOfClassOfFlights.size() > 0)
                {
                    infoLabel.setVisible(true);
                    infoLabel.setText("Найденные варианты");
                    infoLabel.setStyle("-fx-text-fill: #000000");
                    flightsTable.setVisible(true);
                    flightsTable.setItems(listOfClassOfFlights);
                }
                else
                {
                    infoLabel.setText("Не найдено подходящих вариантов");
                    infoLabel.setVisible(true);
                    infoLabel.setStyle("-fx-text-fill: crimson");
                    flightsTable.setVisible(false);
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    private void flushFields()
    {
        this.firstName.setText("");
        this.lastName.setText("");
        this.middleName.setText("");
        this.passNum.setText("");
        this.nationality.setText("");
        this.birthday.setValue(LocalDate.of(1980, 1, 1));
        checkBoxForCheckPassNum.setSelected(false);
        this.firstName.setDisable(false);
        this.lastName.setDisable(false);
        this.middleName.setDisable(false);
        this.discount.setDisable(false);
        this.nationality.setDisable(false);
        this.birthday.setDisable(false);
        this.discount.getSelectionModel().selectFirst();
    }

    @FXML
    private void onClickChangePassword() throws Exception
    {
        detailStage = new Stage();
        detailStage.setScene(new Scene(FXMLLoader.load(GetResources.class.getResource("changePassword.fxml"))));
        detailStage.setResizable(false);
        detailStage.setTitle("Смена пароля кассира");
        detailStage.initModality(Modality.WINDOW_MODAL);
        detailStage.initOwner(anchorPane.getScene().getWindow());
        detailStage.showAndWait();
    }

    @FXML
    private void onClickEnterPassword()
    {
        if (checkBoxForCheckPassNum.isSelected())
        {
            try
            {
                String SQL = "SELECT first_name, last_name, middle_name, birthday, passport_number, nationality,"
                             + "discount.type, discount.discount, discount.id "
                             + "FROM passenger LEFT OUTER JOIN discount ON passenger.discount = discount.id "
                             + "WHERE passport_number = '" + passNum.getText() + "' ";

                ResultSet resultSet = connectionToDB.retriveData(SQL);

                if (resultSet.first())
                {
                    firstName.setText(resultSet.getString(1));
                    lastName.setText(resultSet.getString(2));
                    middleName.setText(resultSet.getString(3));
                    birthday.setValue(LocalDate.parse(resultSet.getString(4), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    nationality.setText(resultSet.getString(6));
                    Discount disc = new Discount(resultSet.getString(7), resultSet.getDouble(8), resultSet.getLong(9));
                    discount.setValue(disc);
                    firstName.setDisable(true);
                    lastName.setDisable(true);
                    middleName.setDisable(true);
                    birthday.setDisable(true);
                    nationality.setDisable(true);
                    discount.setDisable(true);
                }
                else
                {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText("Пассажир не найден!");
                    alert.setContentText("В базе данных не найден соответствующий\nномер паспорта. Заполните вручную поля");
                    alert.showAndWait();
                    checkBoxForCheckPassNum.setSelected(false);
                }
            }
            catch (Exception exx)
            {
                exx.printStackTrace();
            }
        }
        else
        {
            firstName.setText("");
            lastName.setText("");
            middleName.setText("");
            birthday.setValue(LocalDate.of(1980,1,1));
            nationality.setText("");
            discount.getSelectionModel().selectFirst();
            firstName.setDisable(false);
            lastName.setDisable(false);
            middleName.setDisable(false);
            birthday.setDisable(false);
            nationality.setDisable(false);
            discount.setDisable(false);
        }
    }

    private void initInfoForTextFields(TextField textField, String SQL) throws Exception
    {
        ResultSet res = ControllerIndexForm.getDb().retriveData(SQL);
        LinkedList<String> list = new LinkedList<>();

        while (res.next())
        {
            list.add(res.getString(1));
        }
        TextFields.bindAutoCompletion(textField, list);
    }

    private void initClassComforts() throws Exception
    {
        ResultSet resultSet = connectionToDB.retriveData("SELECT DISTINCT type FROM class");
        ObservableList<String> comf = FXCollections.observableArrayList();
        comf.add("");

        while (resultSet.next())
        {
            comf.add(resultSet.getString("type"));
        }

        comfortClass.setItems(comf);
        comfortClass.setValue(comf.get(0));
    }

    private void initDiscounts() throws Exception
    {
        ResultSet resultSet = connectionToDB.retriveData("SELECT id, type, discount FROM discount");
        ObservableList<Discount> disc = FXCollections.observableArrayList();

        while (resultSet.next())
        {
            Discount discount = new Discount(
                    resultSet.getString("type"),
                    resultSet.getDouble("discount"),
                    resultSet.getLong("id")
            );
            disc.add(discount);
        }

        discount.setItems(disc);
        discount.getSelectionModel().selectFirst();
    }

    public static boolean check = false;
}
