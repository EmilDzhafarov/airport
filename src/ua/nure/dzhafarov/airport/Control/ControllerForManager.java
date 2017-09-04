package ua.nure.dzhafarov.airport.Control;


import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.controlsfx.control.textfield.TextFields;
import ua.nure.dzhafarov.airport.ConnectionToDB;
import ua.nure.dzhafarov.airport.Model.Flight;
import ua.nure.dzhafarov.airport.Model.Trip;
import ua.nure.dzhafarov.airport.View.GetResources;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by Emil on 14.11.2016.
 */
public class ControllerForManager
{
    private ConnectionToDB connectionToDB = null;
    private static Stage someStage = null;
    private static Trip currentTrip = null;
    private static Flight currentFlight = null;

    static Stage getSomeStage()
    {
        return someStage;
    }
    static Trip getCurrentTrip()
    {
        return currentTrip;
    }
    static Flight getCurrentFlight() {return currentFlight;}

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private TextField numberFlight;
    @FXML
    private TextField departureFrom;
    @FXML
    private TextField arrivalIn;
    @FXML
    private TextField airline;
    @FXML
    private Spinner<Integer> firstFlightHour;
    @FXML
    private Spinner<Integer> firstFlightMinute;
    @FXML
    private DatePicker firstFlightDate;
    @FXML
    private Spinner<Integer> lastFlightHour;
    @FXML
    private Spinner<Integer> lastFlightMinute;
    @FXML
    private DatePicker lastFlightDate;
    @FXML
    private Spinner<Integer> durationHour;
    @FXML
    private Spinner<Integer> durationMinute;
    @FXML
    private TextField valuePeriod;
    @FXML
    private ComboBox<String> period;
    @FXML
    private CheckBox economClass;
    @FXML
    private CheckBox businessClass;
    @FXML
    private CheckBox firstClass;
    @FXML
    private CheckBox anotherClass;
    @FXML
    private TextField businessClassPlaces;
    @FXML
    private TextField economClassPlaces;
    @FXML
    private TextField firstClassPlaces;
    @FXML
    private TextField anotherClassPlaces;
    @FXML
    private TextField economClassCost;
    @FXML
    private TextField businessClassCost;
    @FXML
    private TextField firstClassCost;
    @FXML
    private TextField anotherClassCost;
    @FXML
    private TextField anotherClassName;
    @FXML
    private ImageView imageViewArrow;

    private ObservableList<Trip> trips = FXCollections.observableArrayList();

    @FXML
    private TableView<Trip> tableViewTrips;
    @FXML
    private TableColumn<Trip, String> numberTripColumn;
    @FXML
    private TableColumn<Trip, String> departureFromTripColumn;
    @FXML
    private TableColumn<Trip, String> arrivalInTripColumn;
    @FXML
    private TableColumn<Trip, String> airlineTripColumn;
    @FXML
    private TableColumn<Trip, String> durationTripColumn;
    @FXML
    private TextField searchTrip;
    @FXML
    private Label infoLabelViewTrips;

    private ObservableList<Flight> flights = FXCollections.observableArrayList();

    @FXML
    private TableView<Flight> tableViewFlights;
    @FXML
    private TableColumn<Flight, String> numberFlightColumn;
    @FXML
    private TableColumn<Flight, String> departureFromFlightColumn;
    @FXML
    private TableColumn<Flight, String> arrivalInFlightColumn;
    @FXML
    private TableColumn<Flight, String> airlineFlightColumn;
    @FXML
    private TableColumn<Flight, String> departureFlightColumn;
    @FXML
    private TableColumn<Flight, String> arrivalFlightColumn;
    @FXML
    private TextField searchFlight;
    @FXML
    private Label infoLabelViewFlights;

    @FXML
    private CheckBox diagramAmountPassengerForYear;
    @FXML
    private CheckBox diagramAmountPassengerForMonth;
    @FXML
    private CheckBox diagramPopularFlightForYear;
    @FXML
    private CheckBox diagramPopularFlightForMonth;
    @FXML
    private CheckBox diagramPopularAirline;

    @FXML
    private ComboBox<Integer> yearForDiagramPassengerAmount;
    @FXML
    private ComboBox<Integer> yearForDiagramFlightPopular;
    @FXML
    private Button buttonForDiagramPassengers;
    @FXML
    private Button buttonForDiagramFlights;

    @FXML
    private void initialize()
    {
        try
        {
            connectionToDB = ControllerIndexForm.getDb();

            flushFields();

            numberTripColumn.setCellValueFactory(new PropertyValueFactory<Trip, String>("Number"));
            departureFromTripColumn.setCellValueFactory(new PropertyValueFactory<Trip, String>("DepartureFrom"));
            arrivalInTripColumn.setCellValueFactory(new PropertyValueFactory<Trip, String>("ArrivalIn"));
            airlineTripColumn.setCellValueFactory(new PropertyValueFactory<Trip, String>("Airline"));
            durationTripColumn.setCellValueFactory(new PropertyValueFactory<Trip, String>("DurationForPresentation"));

            numberFlightColumn.setCellValueFactory(new PropertyValueFactory<Flight, String>("NumberFlight"));
            departureFromFlightColumn.setCellValueFactory(new PropertyValueFactory<Flight, String>("DepartureFrom"));
            arrivalInFlightColumn.setCellValueFactory(new PropertyValueFactory<Flight, String>("ArrivalIn"));
            airlineFlightColumn.setCellValueFactory(new PropertyValueFactory<Flight, String>("Airline"));
            departureFlightColumn.setCellValueFactory(new PropertyValueFactory<Flight, String>("DepartureForPresentation"));
            arrivalFlightColumn.setCellValueFactory(new PropertyValueFactory<Flight, String>("ArrivalForPresentation"));
            departureFlightColumn.setComparator(new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    int hours1 = Integer.parseInt(o1.split(" ")[0].split(":")[0]);
                    int minutes1 = Integer.parseInt(o1.split(" ")[0].split(":")[1]);
                    int days1 = Integer.parseInt(o1.split(" ")[1].split("\\.")[0]);
                    int monthes1 = Integer.parseInt(o1.split(" ")[1].split("\\.")[1]);
                    int years1 = Integer.parseInt(o1.split(" ")[1].split("\\.")[2]);

                    LocalDateTime one = LocalDateTime.of(years1, monthes1, days1, hours1, minutes1);

                    int hours2 = Integer.parseInt(o2.split(" ")[0].split(":")[0]);
                    int minutes2 = Integer.parseInt(o2.split(" ")[0].split(":")[1]);
                    int days2 = Integer.parseInt(o2.split(" ")[1].split("\\.")[0]);
                    int monthes2 = Integer.parseInt(o2.split(" ")[1].split("\\.")[1]);
                    int years2 = Integer.parseInt(o2.split(" ")[1].split("\\.")[2]);

                    LocalDateTime two = LocalDateTime.of(years2, monthes2, days2, hours2, minutes2);

                    if (one.isAfter(two))
                    {
                        return 1;
                    }
                    else if (one.isBefore(two))
                    {
                        return -1;
                    }
                    else
                    {
                        return 0;
                    }
                }
            });
            initInfoForTextFields(departureFrom, "SELECT DISTINCT departure_from FROM trip "
                                                 + "UNION "
                                                 + "SELECT DISTINCT arrival_in FROM trip");
            initInfoForTextFields(arrivalIn, "SELECT DISTINCT departure_from FROM trip "
                                             + "UNION "
                                             + "SELECT DISTINCT arrival_in FROM trip");
            initInfoForTextFields(airline, "SELECT DISTINCT airline FROM trip");

            initInfoForTextFields(searchFlight, "SELECT DISTINCT departure_from FROM trip "
                                                + "UNION "
                                                + "SELECT DISTINCT arrival_in FROM trip");

            searchTrip.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue)
                {
                    ObservableList<Trip> list = FXCollections.observableArrayList();

                    try
                    {
                        String str = "SELECT DISTINCT departure_from, arrival_in, airline, number, duration"
                                     + " FROM trip JOIN flight ON trip.id = flight.trip_id "
                                     + "WHERE flight.departure > NOW() AND ("
                                     + "departure_from LIKE '" + searchTrip.getText() + "%' "
                                     + "OR arrival_in LIKE '" + searchTrip.getText() + "%' "
                                     + "OR number LIKE '" + searchTrip.getText() + "%' "
                                     + "OR airline LIKE '" + searchTrip.getText() + "%') "
                                     + "ORDER BY trip.number";

                        ResultSet resultSet = ControllerIndexForm.getDb().retriveData(str);

                        while (resultSet.next())
                        {
                            Trip trip = new Trip(
                                    resultSet.getString("number"),
                                    resultSet.getString("departure_from"),
                                    resultSet.getString("arrival_in"),
                                    resultSet.getString("airline"),
                                    resultSet.getString("duration")
                            );

                            list.add(trip);
                        }

                        tableViewTrips.setItems(list);

                        if (list.size() == 0)
                        {
                            infoLabelViewTrips.setText("Не найдено соответствующих запросу рейсов");
                            infoLabelViewTrips.setStyle("-fx-text-fill: crimson");
                            tableViewTrips.setVisible(false);
                        }
                        else
                        {
                            infoLabelViewTrips.setText("Выполняемые рейсы");
                            infoLabelViewTrips.setStyle("-fx-text-fill: darkblue");
                            tableViewTrips.setVisible(true);
                        }
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
            });
            searchFlight.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    ObservableList<Flight> list = FXCollections.observableArrayList();

                    try
                    {
                        String str, one, two;

                        if (searchFlight.getText().contains("-"))
                        {
                             one = searchFlight.getText().split("-")[0];
                             two = (searchFlight.getText().split("-").length <= 1) ? "" : searchFlight.getText().split("-")[1];

                            str = "SELECT departure_from, arrival_in, airline, number, duration, departure "
                                  + " FROM trip INNER JOIN flight ON trip.id = flight.trip_id "
                                  + "WHERE flight.departure > NOW() AND ("
                                  + "departure_from LIKE '" + one + "%' "
                                  + "AND arrival_in LIKE '" + two + "%') "
                                  + "ORDER BY departure";
                        }
                        else
                        {
                            str = "SELECT departure_from, arrival_in, airline, number, duration, departure "
                                        + " FROM trip INNER JOIN flight ON trip.id = flight.trip_id "
                                        + "WHERE flight.departure > NOW() AND ("
                                        + "departure_from LIKE '" + searchFlight.getText() + "%' "
                                        + "OR arrival_in LIKE '" + searchFlight.getText() + "%') "
                                        + "ORDER BY departure";
                        }

                        ResultSet resultSet = ControllerIndexForm.getDb().retriveData(str);

                        while (resultSet.next())
                        {
                            Trip trip = new Trip(
                                    resultSet.getString("number"),
                                    resultSet.getString("departure_from"),
                                    resultSet.getString("arrival_in"),
                                    resultSet.getString("airline"),
                                    resultSet.getString("duration")
                            );

                            Flight flight = new Flight(trip, resultSet.getString("departure"));
                            list.add(flight);

                        }

                        tableViewFlights.setItems(list);


                        if (list.size() == 0)
                        {
                            infoLabelViewFlights.setText("Не найдено соответствующих запросу полетов");
                            infoLabelViewFlights.setStyle("-fx-text-fill: crimson");
                            tableViewFlights.setVisible(false);
                        }
                        else
                        {
                            infoLabelViewFlights.setText("Выполняемые полеты");
                            infoLabelViewFlights.setStyle("-fx-text-fill: darkblue");
                            tableViewFlights.setVisible(true);
                        }
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
            });

            Tooltip tooltip = new Tooltip("Вычислить оптимальный период полетов");
            tooltip.setFont(Font.font("Regular", 16));

            Tooltip.install(imageViewArrow, tooltip);

            initTripData();
            initFlightData();
            period.setItems(FXCollections.observableArrayList("Минуты", "Часы", "Сутки", "Недели", "Месяцы"));
            tableViewTrips.widthProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> source, Number oldWidth, Number newWidth)
                {
                    TableHeaderRow header = (TableHeaderRow) tableViewTrips.lookup("TableHeaderRow");
                    header.reorderingProperty().addListener(new ChangeListener<Boolean>() {
                        @Override
                        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                            header.setReordering(false);
                        }
                    });
                }
            });

            tableViewTrips.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(javafx.scene.input.MouseEvent event) {
                    if (event.isSecondaryButtonDown())
                    {
                        final ContextMenu menu = new ContextMenu();
                        MenuItem add = new MenuItem("Добавить полет");
                        MenuItem change = new MenuItem("Изменить");
                        MenuItem delete = new MenuItem("Удалить");
                        MenuItem showInfo = new MenuItem("Детальная информация о рейсе");

                        add.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                try
                                {
                                    tableViewTrips.getSelectionModel().getSelectedItem().getNumber();
                                    addFlightForTrip(tableViewTrips.getSelectionModel().getSelectedItem());
                                }
                                catch (NullPointerException ex)
                                {
                                    Alert ob = new Alert(Alert.AlertType.WARNING);
                                    ob.setTitle("");
                                    ob.setHeaderText("Предупреждение ");
                                    ob.setContentText("Выберите  рейс  для продолжения.");
                                    ob.showAndWait();
                                }
                                catch (Exception ex)
                                {
                                    Alert ob = new Alert(Alert.AlertType.ERROR);
                                    ob.setTitle("");
                                    ob.setHeaderText("Непредвиденная Ошибка    ");
                                    ob.setContentText("Попробуйте заново или перезапустите программу.");
                                    ob.showAndWait();
                                }
                            }
                        });
                        change.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                try
                                {
                                    changeTrip(tableViewTrips.getSelectionModel().getSelectedItem());
                                }
                                catch (NullPointerException e) {
                                    Alert ob = new Alert(Alert.AlertType.WARNING);
                                    ob.setTitle("");
                                    ob.setHeaderText("Предупреждение ");
                                    ob.setContentText("Выберите рейс для продолжения.");
                                    ob.showAndWait();
                                }
                                catch (Exception r)
                                {
                                    r.printStackTrace();
                                    Alert ob = new Alert(Alert.AlertType.ERROR);
                                    ob.setTitle("");
                                    ob.setHeaderText("Непредвиденная Ошибка ");
                                    ob.setContentText("Попробуйте заново или перезапустите программу.");
                                    ob.showAndWait();
                                }
                            }
                        });

                        delete.setOnAction(new EventHandler<ActionEvent>()  {
                            @Override
                            public void handle(ActionEvent event){
                                try
                                {
                                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                    alert.setTitle("Подтверждение");
                                    alert.setHeaderText("Вы действительно хотите удалить данные о рейсе?");
                                    alert.setContentText("Для подтверждения нажмите \"Удалить\"");
                                    alert.getButtonTypes().clear();
                                    alert.getButtonTypes().add(new ButtonType("Удалить"));
                                    alert.getButtonTypes().add(new ButtonType("Отмена"));
                                    alert.showAndWait();

                                    if (alert.getResult().getText().equals("Удалить"))
                                    {
                                        deleteTrip(tableViewTrips.getSelectionModel().getSelectedItem());
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
                                    ob.setContentText("Выберите рейс для продолжения. ");
                                    ob.showAndWait();
                                }
                                catch (Exception r)
                                {
                                    Alert ob = new Alert(Alert.AlertType.ERROR);
                                    ob.setTitle("");
                                    ob.setHeaderText("Непредвиденная Ошибка  ");
                                    ob.setContentText("Попробуйте заново или перезапустите программу.");
                                    ob.showAndWait();
                                }

                            }
                        });

                        showInfo.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                try
                                {
                                    typingDocumentAboutTrip(tableViewTrips.getSelectionModel().getSelectedItem());
                                }
                                catch (NullPointerException e) {
                                    Alert ob = new Alert(Alert.AlertType.WARNING);
                                    ob.setTitle("");
                                    ob.setHeaderText("Предупреждение ");
                                    ob.setContentText("Выберите рейс для продолжения. ");
                                    ob.showAndWait();
                                }
                                catch (Exception r)
                                {
                                    Alert ob = new Alert(Alert.AlertType.ERROR);
                                    ob.setTitle("");
                                    ob.setHeaderText("Непредвиденная Ошибка   ");
                                    ob.setContentText("Попробуйте заново или перезапустите программу.");
                                    ob.showAndWait();
                                }
                            }
                        });

                        menu.getItems().addAll(add, new SeparatorMenuItem(), change, delete, showInfo);
                        tableViewTrips.setContextMenu(menu);
                    }
                }
            });

            tableViewFlights.widthProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> source, Number oldWidth, Number newWidth)
                {
                    TableHeaderRow header = (TableHeaderRow) tableViewFlights.lookup("TableHeaderRow");
                    header.reorderingProperty().addListener(new ChangeListener<Boolean>() {
                        @Override
                        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                            header.setReordering(false);
                        }
                    });
                }
            });

            tableViewFlights.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(javafx.scene.input.MouseEvent event) {
                    if (event.isSecondaryButtonDown())
                    {
                        final ContextMenu menu = new ContextMenu();
                        MenuItem add = new MenuItem("Добавить");
                        MenuItem change = new MenuItem("Изменить");
                        MenuItem delete = new MenuItem("Удалить");
                        MenuItem showInfo = new MenuItem("Детальная информация о полете");

                        add.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                try
                                {
                                    addFlight();
                                }
                                catch (Exception ec)
                                {
                                    ec.printStackTrace();                                }
                            }
                        });
                        change.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                try
                                {
                                    changeFlight(tableViewFlights.getSelectionModel().getSelectedItem());
                                }
                                catch (NullPointerException e) {
                                    Alert ob = new Alert(Alert.AlertType.WARNING);
                                    ob.setTitle("");
                                    ob.setHeaderText("Предупреждение ");
                                    ob.setContentText("Выберите полет для продолжения.");
                                    ob.showAndWait();
                                }
                                catch (Exception r)
                                {
                                    r.printStackTrace();
                                }
                            }
                        });

                        delete.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                try
                                {
                                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                    alert.setTitle("Подтверждение");
                                    alert.setHeaderText("Вы действительно хотите удалить данные о полете?");
                                    alert.setContentText("Для подтверждения нажмите \"Удалить\"");
                                    alert.getButtonTypes().clear();
                                    alert.getButtonTypes().add(new ButtonType("Удалить"));
                                    alert.getButtonTypes().add(new ButtonType("Отмена"));
                                    alert.showAndWait();

                                    if (alert.getResult().getText().equals("Удалить"))
                                    {
                                        deleteFlight(tableViewFlights.getSelectionModel().getSelectedItem());
                                    }
                                    else
                                    {
                                        alert.close();
                                    }
                                }
                                catch (NullPointerException e) {
                                    Alert ob = new Alert(Alert.AlertType.WARNING);
                                    ob.setTitle("");
                                    ob.setHeaderText("Предупреждение ");
                                    ob.setContentText("Выберите полет для продолжения. ");
                                    ob.showAndWait();
                                }
                                catch (Exception r)
                                {
                                    r.printStackTrace();
                                }
                            }
                        });

                        showInfo.setOnAction(x -> {
                            try
                            {
                                typingDocumentAboutFlight(tableViewFlights.getSelectionModel().getSelectedItem());
                            }
                            catch (NullPointerException er)
                            {
                                Alert ob = new Alert(Alert.AlertType.WARNING);
                                ob.setTitle("");
                                ob.setHeaderText("Предупреждение  ");
                                ob.setContentText("Выберите полет для продолжения. ");
                                ob.showAndWait();
                            }
                            catch (Exception ex)
                            {
                                ex.printStackTrace();
                            }
                        });

                        menu.getItems().addAll(add, new SeparatorMenuItem(), change, delete, showInfo);
                        tableViewFlights.setContextMenu(menu);
                    }
                }
            });
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @FXML
    private void onMouseEnteredArrow()
    {
        DropShadow innerShadow = new DropShadow();
        innerShadow.setColor(Color.valueOf("#244bc9"));
        innerShadow.setRadius(10);
        innerShadow.setHeight(30);
        innerShadow.setWidth(30);

        imageViewArrow.setEffect(innerShadow);
    }

    @FXML
    private void onMouseExitedArrow()
    {
        imageViewArrow.setEffect(null);
    }

    @FXML
    private void onClickShowDiagramAmountPassengerForMonth()
    {
    }

    @FXML
    private void onClickShowDiagramPopularFlightForMonth()
    {
        try
        {
            int kkk = Integer.parseInt(yearForDiagramFlightPopular.getValue() + "");

            ArrayList<String> monthes = new ArrayList<>();
            monthes.add("Январь");
            monthes.add("Февраль");
            monthes.add("Март");
            monthes.add("Апрель");
            monthes.add("Май");
            monthes.add("Июнь");
            monthes.add("Июль");
            monthes.add("Август");
            monthes.add("Сентябрь");
            monthes.add("Октябрь");
            monthes.add("Ноябрь");
            monthes.add("Декабрь");

            ArrayList<Integer> years = new ArrayList<>();
            LocalDate begin = LocalDate.now();

            for (int i = 0; i < 5; i++)
            {
                years.add(begin.getYear());
                begin = begin.minusYears(1);
            }

            final CategoryAxis xAxis = new CategoryAxis();
            final NumberAxis yAxis = new NumberAxis();
            xAxis.setLabel("Годы");
            yAxis.setLabel("Количество совершенных полетов");
            final LineChart<String, Number> lineChart = new LineChart<String, Number>(xAxis, yAxis);
            lineChart.setTitle("Детальный рейсооборот");

            Scene scene = new Scene(lineChart, 900, 550);
            int start;
            int end;

            if (kkk == 1)
            {
                start = 0;
                end = 3;
            }
            else if (kkk == 2)
            {
                start = 3;
                end = 6;
            }
            else if (kkk == 3)
            {
                start = 6;
                end = 9;
            }
            else
            {
                start = 9;
                end = 12;
            }

            for (int i = start; i < end; i++)
            {
                XYChart.Series<String, Number> ser = new XYChart.Series<>();
                ser.setName(monthes.get(i));

                for (int j = years.size() - 1; j >= 0; j--)
                {
                    String sql = "SELECT IFNULL(COUNT(flight.id),0) AS flight_count "
                                 + "FROM flight WHERE YEAR(departure) = " + years.get(j)
                                 + " AND MONTH(departure) = '" + (i + 1) + "' "
                                 + " AND departure < NOW()";
                    ResultSet set = connectionToDB.retriveData(sql);

                    while (set.next())
                    {
                        ser.getData().add(new XYChart.Data<>(years.get(j)+ "", set.getInt("flight_count")));
                    }
                }

                lineChart.getData().add(ser);
            }

            lineChart.setLegendSide(Side.BOTTOM);
            someStage = new Stage();
            someStage.initModality(Modality.WINDOW_MODAL);
            someStage.initOwner(anchorPane.getScene().getWindow());
            someStage.setTitle("Диаграмма рейсооборота за последние 5 лет");
            someStage.setResizable(false);
            someStage.setScene(scene);
            someStage.show();

            someStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    diagramPopularFlightForMonth.setSelected(false);
                }
            });

            showAdditionalDiagramElements(diagramPopularFlightForMonth, buttonForDiagramFlights, yearForDiagramFlightPopular);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Произошла ошибка при загрузке данных  ");
            alert.setContentText("Попробуйте снова повторить действие");
            alert.showAndWait();
        }
    }
    @FXML
    private void onClickShowDiagramAmountPassengerForYear()
    {
        try
        {
            ArrayList<Integer> years = new ArrayList<>();
            LocalDate begin = LocalDate.now();
            for (int i = 0; i < 5; i++)
            {
                years.add(begin.getYear());
                begin = begin.minusYears(1);
            }

            final CategoryAxis xAxis = new CategoryAxis();
            final NumberAxis yAxis = new NumberAxis();
            xAxis.setLabel("Годы");
            yAxis.setLabel("Количество пассажиров");
            final LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
            lineChart.setTitle("Пассажиропоток за последние 5 лет");

            XYChart.Series series = new XYChart.Series();
            series.setName("Пассажиропоток");


            for (int i = years.size() - 1; i >= 0; i--)
            {
                String SQL = "SELECT COALESCE(COUNT(orders.id), 0) AS pass_count "
                             + "FROM orders JOIN flight ON orders.flight_id = flight.id "
                             + "WHERE YEAR(flight.departure) = " + years.get(i)
                             + " AND flight.departure < NOW()";

                ResultSet resultSet = connectionToDB.retriveData(SQL);

                while (resultSet.next())
                {
                    series.getData().add(new XYChart.Data<>(years.get(i) + "", resultSet.getLong("pass_count")));
                }
            }


            Scene scene = new Scene(lineChart, 900, 550);

            lineChart.getData().add(series);
            lineChart.setLegendSide(Side.BOTTOM);
            someStage = new Stage();
            someStage.initModality(Modality.WINDOW_MODAL);
            someStage.initOwner(anchorPane.getScene().getWindow());
            someStage.setTitle("Диаграмма пассажиропотока ");
            someStage.setResizable(false);
            someStage.setScene(scene);
            someStage.show();

            someStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    diagramAmountPassengerForYear.setSelected(false);
                }
            });
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Произошла ошибка при загрузке данных");
            alert.setContentText("Попробуйте снова повторить действие");
            alert.showAndWait();
        }
    }

    @FXML
    private void onClickCheckBoxPassengers()
    {
        try
        {
            ArrayList<Integer> years = new ArrayList<>();
            LocalDate begin = LocalDate.now();

            for (int i = 0; i < 5; i++)
            {
                years.add(begin.getYear());
                begin = begin.minusYears(1);
            }

            final CategoryAxis xAxis = new CategoryAxis();
            final NumberAxis yAxis = new NumberAxis();
            xAxis.setLabel("Года");
            yAxis.setLabel("Количество обслуженых пассажиров");
            final BarChart<String ,Number> barChart = new BarChart<String, Number>(xAxis, yAxis);
            barChart.setTitle("Диаграмма популярности авиакомпаний");

            Scene scene = new Scene(barChart, 900, 550);

            HashMap<String, Integer> hashMap = new HashMap<>();

            String SQL = "SELECT IFNULL(COUNT(orders.id), 0) AS pass_count, trip.airline "
                         + "FROM trip JOIN flight JOIN orders "
                         + "ON trip.id = flight.trip_id "
                         + "AND orders.flight_id = flight.id "
                         + "WHERE YEAR(departure) BETWEEN (YEAR(NOW()) - 5) AND YEAR(NOW()) "
                         + "GROUP BY trip.airline "
                         + "ORDER BY IFNULL(COUNT(pass_id), 0) DESC LIMIT 5";

            ResultSet resultSet = connectionToDB.retriveData(SQL);

            while (resultSet.next())
            {
                hashMap.put(resultSet.getString("trip.airline"),
                        resultSet.getInt("pass_count"));
            }

            for (String name : hashMap.keySet())
            {
                XYChart.Series<String, Number> ser = new XYChart.Series<>();
                ser.setName(name);

                for (int i = years.size() - 1; i >= 0; i--)
                {
                    String sql = "SELECT IFNULL(COUNT(pass_id),0) AS pass_count "
                                 + "FROM trip JOIN flight JOIN orders "
                                 + "ON trip.id = flight.trip_id "
                                 + "AND orders.flight_id = flight.id "
                                 + "WHERE trip.airline = '" + name + "' "
                                 + "AND YEAR(departure) = " + years.get(i) + " "
                                 + "AND departure < NOW()";

                    ResultSet resSet = connectionToDB.retriveData(sql);

                    while (resSet.next())
                    {
                        ser.getData().add(new XYChart.Data<>(years.get(i) + "",
                                resSet.getLong("pass_count")));
                    }
                }
                barChart.getData().add(ser);
            }

            someStage = new Stage();
            someStage.initModality(Modality.WINDOW_MODAL);
            someStage.initOwner(anchorPane.getScene().getWindow());
            someStage.setTitle("Диаграмма популярности авиакомпаний");
            someStage.setResizable(false);
            someStage.setScene(scene);
            someStage.show();

            someStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    diagramAmountPassengerForMonth.setSelected(false);
                }
            });

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Произошла ошибка при загрузке данных ");
            alert.setContentText("Попробуйте снова повторить действие ");
            alert.showAndWait();
        }
    }

    @FXML
    private void onClickShowDiagramPopularFlightForYear()
    {
        if (diagramPopularFlightForYear.isSelected())
        {
            try
            {
                ArrayList<Integer> years = new ArrayList<>();
                LocalDate begin = LocalDate.now();
                for (int i = 0; i < 5; i++)
                {
                    years.add(begin.getYear());
                    begin = begin.minusYears(1);
                }

                final CategoryAxis xAxis = new CategoryAxis();
                final NumberAxis yAxis = new NumberAxis();
                xAxis.setLabel("Годы");
                yAxis.setLabel("Количество полетов");
                final LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
                lineChart.setTitle("Оборот полетов за последние 5 лет");

                XYChart.Series series = new XYChart.Series();
                series.setName("Полетооборот");

                for (int i = years.size() - 1; i >= 0; i--)
                {
                    String SQL = "SELECT COALESCE(COUNT(id), 0) AS flight_count "
                                 + "FROM flight WHERE departure < NOW() "
                                 + "AND YEAR(departure) = " + years.get(i);

                    ResultSet resultSet = connectionToDB.retriveData(SQL);

                    while (resultSet.next())
                    {
                        series.getData().add(new XYChart.Data<>(years.get(i) + "", resultSet.getLong("flight_count")));
                    }
                }

                Scene scene = new Scene(lineChart, 900, 550);

                lineChart.getData().add(series);
                lineChart.setLegendSide(Side.BOTTOM);
                someStage = new Stage();
                someStage.initModality(Modality.WINDOW_MODAL);
                someStage.initOwner(anchorPane.getScene().getWindow());
                someStage.setTitle("Диаграмма количества полетов");
                someStage.setResizable(false);
                someStage.setScene(scene);
                someStage.show();

                someStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent event) {
                        diagramPopularFlightForYear.setSelected(false);
                    }
                });
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка");
                alert.setHeaderText("Произошла ошибка при загрузке данных ");
                alert.setContentText("Попробуйте снова повторить действие");
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void onClickCheckBoxFlights()
    {
        showAdditionalDiagramElements(diagramPopularFlightForMonth,
                buttonForDiagramFlights,
                yearForDiagramFlightPopular);
    }

    @FXML
    private void onSort()
    {
        tableViewFlights.getItems().sort(new Flight());
    }
    @FXML
    private void onClickShowDiagramPopularAirline()
    {
        if (diagramPopularAirline.isSelected())
        {
            try
            {
                ArrayList<Integer> years = new ArrayList<>();
                LocalDate begin = LocalDate.now();
                for (int i = 0; i < 5; i++)
                {
                    years.add(begin.getYear());
                    begin = begin.minusYears(1);
                }

                Map<String, Integer> hashMap = new HashMap<>();

                String SQL = "SELECT IFNULL(COUNT(flight.id), 0) AS flight_count, trip.airline "
                             + "FROM trip JOIN flight ON trip.id = flight.trip_id "
                             + "WHERE YEAR(departure) BETWEEN (YEAR(NOW()) - 5) AND YEAR(NOW()) "
                             + "GROUP BY trip.airline "
                             + "ORDER BY IFNULL(COUNT(flight.id), 0) DESC LIMIT 5";

                ResultSet resultSet = connectionToDB.retriveData(SQL);

                while (resultSet.next())
                {
                    hashMap.put(resultSet.getString("trip.airline"),resultSet.getInt("flight_count"));
                }

                final CategoryAxis xAxis = new CategoryAxis();
                final NumberAxis yAxis = new NumberAxis();
                xAxis.setLabel("Года");
                yAxis.setLabel("Количество совершенных авиаперелетов");
                final BarChart<String ,Number> barChart = new BarChart<String, Number>(xAxis, yAxis);

                barChart.setTitle("Рейтинг авиакомпаний");

                Scene scene = new Scene(barChart, 1000, 550);

                for (String name : hashMap.keySet())
                {
                    XYChart.Series<String, Number> ser = new XYChart.Series<>();
                    ser.setName(name);

                    for (int i = years.size() - 1; i >= 0; i--)
                    {
                        String sql = "SELECT IFNULL(COUNT(flight.id),0) AS flight_count FROM trip JOIN flight "
                                     + "ON trip.id = flight.trip_id "
                                     + "WHERE trip.airline = '" + name + "' "
                                     + "AND YEAR(departure) = " + years.get(i) + "";

                        ResultSet resSet = connectionToDB.retriveData(sql);

                        while (resSet.next())
                        {
                            ser.getData().add(new XYChart.Data<>(years.get(i) + "", resSet.getInt("flight_count")));
                        }
                    }

                    barChart.getData().add(ser);
                }

                someStage = new Stage();
                someStage.initModality(Modality.WINDOW_MODAL);
                someStage.initOwner(anchorPane.getScene().getWindow());
                someStage.setTitle("Диаграмма рейтинга авиакомпаний");
                someStage.setResizable(false);
                someStage.setScene(scene);
                someStage.show();

                someStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent event) {
                        diagramPopularAirline.setSelected(false);
                    }
                });

            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    @FXML
    private void onClickEconomClass()
    {
        showAdditionalClassComfortElements(economClass, economClassPlaces, economClassCost);
    }

    @FXML
    private void onClickBusinessClass()
    {
        showAdditionalClassComfortElements(businessClass, businessClassPlaces, businessClassCost);
    }

    @FXML
    private void onClickFirstClass()
    {
        showAdditionalClassComfortElements(firstClass, firstClassPlaces, firstClassCost);
    }

    @FXML
    private void onClickAnotherClass()
    {
        showAdditionalClassComfortElements(anotherClass, anotherClassPlaces, anotherClassCost, anotherClassName);
    }

    @FXML
    private void onClickAddFlight()
    {
        try
        {
            String number = this.numberFlight.getText();
            String departureFrom = this.departureFrom.getText();
            String arrivalIn = this.arrivalIn.getText();
            String airline = this.airline.getText();
            String duration = this.durationHour.getValue() + ":" + this.durationMinute.getValue();

            LocalDateTime begin = LocalDateTime.of(this.firstFlightDate.getValue().getYear(),
                               this.firstFlightDate.getValue().getMonthValue(),
                               this.firstFlightDate.getValue().getDayOfMonth(),
                               this.firstFlightHour.getValue(),
                               this.firstFlightMinute.getValue());

            LocalDateTime end = LocalDateTime.of(this.lastFlightDate.getValue().getYear(),
                         this.lastFlightDate.getValue().getMonthValue(),
                         this.lastFlightDate.getValue().getDayOfMonth(),
                         this.lastFlightHour.getValue(),
                         this.lastFlightMinute.getValue());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            Trip trip = new Trip(number, departureFrom, arrivalIn, airline, duration);
            Flight flight = new Flight(trip, begin.format(formatter));

            if (begin.isAfter(end))
            {
                throw new Flight.FlightException("Дата и время конца полетов не должны \nпредшествовать дате и времени начала полетов");
            }
            if ((!economClassPlaces.getText().matches("[0-9]+") && economClass.isSelected()) ||
                    (!firstClassPlaces.getText().matches("[0-9]+") && firstClass.isSelected()) ||
                    (!businessClassPlaces.getText().matches("[0-9]+") && businessClass.isSelected()) ||
                     (!anotherClassPlaces.getText().matches("[0-9]+") && anotherClass.isSelected())) {

                throw new Flight.FlightException("Убедитесь в правильности ввода\nзначений посадочных мест");
            }
            if (!economClass.isSelected() && !businessClass.isSelected()
                     && !firstClass.isSelected() && !anotherClass.isSelected()) {

                throw new Flight.FlightException("Необходимо указать классы комфортности\n"
                                                 + "и количество посадочных мест");
            }
            if ((!firstClassCost.getText().matches("[0-9]+\\.[0-9]{2}|[0-9]+") && firstClass.isSelected()) ||
                 (!businessClassCost.getText().matches("[0-9]+\\.[0-9]{2}|[0-9]+") && businessClass.isSelected()) ||
                 (!economClassCost.getText().matches("[0-9]+\\.[0-9]{2}|[0-9]+") && economClass.isSelected()) ||
                     (!anotherClassCost.getText().matches("[0-9]+\\.[0-9]{2}|[0-9]+") && anotherClass.isSelected())) {

                throw new Flight.FlightException("Убедитесь в правильности ввода\nстоимости билета");
            }
            if (!anotherClass.getText().matches("[a-zA-Z\\s]+|[а-яА-Я\\s]+") && anotherClass.isSelected())
            {
                throw new Flight.FlightException("Укажите название нового класса комфортности");
            }
            if (!valuePeriod.getText().matches("[0-9]+"))
            {
                throw new Flight.FlightException("Убедитесь в правильности ввода\n"
                                                 + "значения периодичности полетов");
            }
            if (period.getValue().equals("Не выбрано"))
            {
                throw new Flight.FlightException("Выберите периодичность между полетами");
            }

            String SQL = "INSERT INTO trip (number, departure_from, arrival_in, airline, duration) "
                         + "VALUES('" + number  + "', '" + departureFrom + "', '" + arrivalIn + "', '"
                         + airline + "', '" + duration + "')";

            connectionToDB.executeStatement(SQL);

            if (economClass.isSelected())
            {
                insertClassComfort(economClassPlaces.getText(), economClassCost.getText(), "Эконом");
            }

            if (businessClass.isSelected())
            {
                insertClassComfort(businessClassPlaces.getText(), businessClassCost.getText(), "Бизнесс");
            }

            if (firstClass.isSelected())
            {
                insertClassComfort(firstClassPlaces.getText(), firstClassCost.getText(), "Первый");
            }

            if (anotherClass.isSelected())
            {
                insertClassComfort(anotherClassPlaces.getText(), anotherClassCost.getText(), anotherClassName.getText());
            }


            ResultSet resultSet = connectionToDB.retriveData("SELECT MAX(id) FROM trip");
            resultSet.first();
            int id = resultSet.getInt(1);

            switch (period.getValue())
            {
                case "Минуты":
                    while (begin.isBefore(end))
                    {
                        SQL = "INSERT INTO flight(trip_id, departure) VALUES (" + id + ",'" + begin.format(formatter) + "')";
                        connectionToDB.executeStatement(SQL);

                        begin = begin.plusMinutes(Long.parseLong(valuePeriod.getText()));
                    }
                    break;
                case "Часы":
                    while (begin.isBefore(end))
                    {
                        SQL = "INSERT INTO flight(trip_id, departure) VALUES (" + id + ",'" + begin.format(formatter) + "')";
                        connectionToDB.executeStatement(SQL);

                        begin = begin.plusHours(Long.parseLong(valuePeriod.getText()));
                    }
                    break;
                case "Сутки":
                    while (begin.isBefore(end))
                    {
                        SQL = "INSERT INTO flight(trip_id, departure) VALUES (" + id + ",'" + begin.format(formatter) + "')";
                        connectionToDB.executeStatement(SQL);

                        begin = begin.plusDays(Long.parseLong(valuePeriod.getText()));
                    }
                    break;
                case "Недели":
                    while (begin.isBefore(end))
                    {
                        SQL = "INSERT INTO flight(trip_id, departure) VALUES (" + id + ",'" + begin.format(formatter) + "')";
                        connectionToDB.executeStatement(SQL);

                        begin = begin.plusWeeks(Long.parseLong(valuePeriod.getText()));
                    }
                    break;
                case "Месяцы":
                    while (begin.isBefore(end))
                    {
                        SQL = "INSERT INTO flight(trip_id, departure) VALUES (" + id + ",'" + begin.format(formatter) + "')";
                        connectionToDB.executeStatement(SQL);

                        begin = begin.plusMonths(Long.parseLong(valuePeriod.getText()));
                    }
                    break;
            }

            flushFields();
            initFlightData();
            initTripData();
        }
        catch (Trip.TripException | Flight.FlightException ex)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Ошибка в введенных данных");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
        catch (MySQLIntegrityConstraintViolationException ex)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Ошибка в введенных данных");
            alert.setContentText("Номер рейса уже используется");
            alert.showAndWait();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @FXML
    private void onClickArrow()
    {
        try
        {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            LocalDateTime begin = LocalDateTime.of(firstFlightDate.getValue().getYear(),
                    firstFlightDate.getValue().getMonthValue(),
                    firstFlightDate.getValue().getDayOfMonth(),
                    firstFlightHour.getValue(),
                    firstFlightMinute.getValue());

            LocalDateTime end = LocalDateTime.of(lastFlightDate.getValue().getYear(),
                         lastFlightDate.getValue().getMonthValue(),
                         lastFlightDate.getValue().getDayOfMonth(),
                         lastFlightHour.getValue(),
                    lastFlightMinute.getValue());

            getFlightPeriodAndCount(begin.format(formatter), end.format(formatter), departureFrom.getText(), arrivalIn.getText());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @FXML
    private void onClickClearFields()
    {
        flushFields();
    }

    private void flushFields()
    {
        this.numberFlight.setText("");
        this.departureFrom.setText("");
        this.arrivalIn.setText("");
        this.period.setValue("Не выбрано");
        this.valuePeriod.setText("");
        this.airline.setText("");

        if (economClass.isSelected())
        {
            economClass.setSelected(false);
            showAdditionalClassComfortElements(economClass, economClassPlaces, economClassCost);
        }

        if (businessClass.isSelected())
        {
            businessClass.setSelected(false);
            showAdditionalClassComfortElements(businessClass, businessClassPlaces, businessClassCost);
        }

        if (firstClass.isSelected())
        {
            firstClass.setSelected(false);
            showAdditionalClassComfortElements(firstClass, firstClassPlaces, firstClassCost);
        }

        if (anotherClass.isSelected())
        {
            anotherClass.setSelected(false);
            showAdditionalClassComfortElements(anotherClass, anotherClassPlaces, anotherClassCost, anotherClassName);
        }


        firstFlightHour.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0, 1));
        firstFlightMinute.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0, 1));
        firstFlightDate.setValue(LocalDate.now());

        lastFlightHour.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0, 1));
        lastFlightMinute.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0, 1));
        lastFlightDate.setValue(LocalDate.now());

        durationHour.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0, 1));
        durationMinute.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0, 1));
        yearForDiagramFlightPopular.setEditable(false);
        yearForDiagramFlightPopular.setItems(FXCollections.observableArrayList(1,2,3,4));
        yearForDiagramFlightPopular.setPromptText("Выберите квартиль");
    }

    private void initTripData()
    {
        try
        {
            trips.clear();

            String SQL = "SELECT DISTINCT number, departure_from, arrival_in, airline, duration " +
                         "FROM trip JOIN flight " +
                         "ON flight.trip_id = trip.id " +
                         "WHERE departure > NOW() " +
                         "ORDER BY number ASC, departure_from ASC, arrival_in ASC";

            ResultSet resultSet = connectionToDB.retriveData(SQL);

            while (resultSet.next())
            {
                Trip trip = new Trip(
                        resultSet.getString("number"),
                        resultSet.getString("departure_from"),
                        resultSet.getString("arrival_in"),
                        resultSet.getString("airline"),
                        resultSet.getString("duration")
                );

                trips.add(trip);
            }

            tableViewTrips.setItems(trips);

            if (trips.size() > 0)
            {
                infoLabelViewTrips.setText("Выполняемые рейсы");
                infoLabelViewTrips.setStyle("-fx-text-fill: navy");
                tableViewTrips.setVisible(true);
            }
            else
            {
                infoLabelViewTrips.setText("Не найдено выполняемых рейсов");
                infoLabelViewTrips.setStyle("-fx-text-fill: crimson");
                tableViewTrips.setVisible(false);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void initFlightData()
    {
        try
        {
            flights.clear();

            String SQL = "SELECT number, departure_from, arrival_in, " +
                         "airline, duration, departure " +
                         "FROM trip JOIN flight " +
                         "ON flight.trip_id = trip.id " +
                         "WHERE departure > NOW() " +
                         "ORDER BY departure ASC, number ASC";

            ResultSet resultSet = connectionToDB.retriveData(SQL);

            while (resultSet.next())
            {
                Trip trip = new Trip(
                        resultSet.getString("number"),
                        resultSet.getString("departure_from"),
                        resultSet.getString("arrival_in"),
                        resultSet.getString("airline"),
                        resultSet.getString("duration")
                );

                Flight flight = new Flight(trip, resultSet.getString("departure"));

                flights.add(flight);
            }

            tableViewFlights.setItems(flights);

            if (flights.size() > 0)
            {
                infoLabelViewFlights.setText("Выполняемые полеты");
                infoLabelViewFlights.setStyle("-fx-text-fill: navy");
                tableViewFlights.setVisible(true);
            }
            else
            {
                infoLabelViewFlights.setText("Не найдено выполняющихся полетов");
                infoLabelViewFlights.setStyle("-fx-text-fill: crimson");
                tableViewFlights.setVisible(false);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void showAdditionalDiagramElements(CheckBox checkBox, Button button, ComboBox comboBox)
    {
        if (checkBox.isSelected())
        {
            button.setVisible(true);
            comboBox.setVisible(true);
        }
        else
        {
            button.setVisible(false);
            comboBox.setVisible(false);
        }
    }

    private void showAdditionalClassComfortElements(CheckBox checkBox, TextField ... arrTextField)
    {
        if (checkBox.isSelected())
        {
            for (TextField textField : arrTextField)
            {
                textField.setVisible(true);
            }
        }
        else
        {
            for (TextField textField : arrTextField)
            {
                textField.setVisible(false);
                textField.setText("");
            }
        }
    }

    @FXML
    private void onClickChangePassword() throws Exception
    {
        someStage = new Stage();
        someStage.setScene(new Scene(FXMLLoader.load(GetResources.class.getResource("changePassword.fxml"))));
        someStage.setResizable(false);
        someStage.setTitle("Смена пароля менеджера");
        someStage.initModality(Modality.WINDOW_MODAL);
        someStage.initOwner(anchorPane.getScene().getWindow());
        someStage.showAndWait();
    }

    private void insertClassComfort(String places, String cost, String type) throws Exception
    {
        Integer count = Integer.parseInt(places);
        Double price = Double.parseDouble(cost);

        String SQL = "INSERT INTO class (id_of_trip, type, count_of_places, ticket_cost) "
              + "VALUES ((SELECT MAX(id) FROM trip), '" + type + "', " + count + "," + price + ")";

        connectionToDB.executeStatement(SQL);
    }

    private void getFlightPeriodAndCount(String dateTimeBegin, String dateTimeEnd,
                                         String depFrom, String arrIn) throws Exception
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime begin = LocalDateTime.parse(dateTimeBegin, formatter).minusYears(1);
        LocalDateTime end = LocalDateTime.parse(dateTimeEnd, formatter).minusYears(1);

        String SQL = "SELECT DISTINCT number FROM flight JOIN trip "
                     + "ON flight.trip_id = trip.id "
                     + "WHERE flight.departure >= '" + begin.format(formatter) + "' "
                     + "AND flight.departure <=  '"  + end.format(formatter)  + "' "
                     + "AND trip.departure_from = '" + depFrom + "' "
                     + "AND trip.arrival_in = '" + arrIn + "'";

        ResultSet resultSet = connectionToDB.retriveData(SQL);

        LinkedList<String> flightNumbers = new LinkedList<>();

        while (resultSet.next())
        {
            flightNumbers.add(resultSet.getString("number"));
        }

        ArrayList<Integer> passengerCount = new ArrayList<>();
        ArrayList<Integer> placeCount = new ArrayList<>();
        ArrayList<Integer> flightCount = new ArrayList<>();
        ArrayList<Long> secondsCount = new ArrayList<>();

        for (String number : flightNumbers)
        {
            SQL = "SELECT SUM(count_of_places) AS all_places "
                  + "FROM trip JOIN class "
                  + "ON trip.id = class.id_of_trip "
                  + "WHERE number = '" + number + "' ";
            resultSet = connectionToDB.retriveData(SQL);
            resultSet.first();
            int countOfPlaces = resultSet.getInt("all_places");

            SQL = "SELECT IFNULL(COUNT(pass_id), 0) AS pass_count  "
                  + "FROM orders JOIN flight JOIN trip "
                  + "ON trip.id = flight.trip_id "
                  + "AND orders.flight_id = flight.id "
                  + "WHERE number = '" + number + "'";
            resultSet = connectionToDB.retriveData(SQL);
            resultSet.first();
            int countOfPassenger = resultSet.getInt("pass_count");

            SQL = "SELECT IFNULL(COUNT(flight.id), 0) AS flight_count "
                  + "FROM flight JOIN trip "
                  + "ON trip.id = flight.trip_id "
                  + "WHERE number = '" + number + "'";
            resultSet = connectionToDB.retriveData(SQL);
            resultSet.first();
            int countOfFlights = resultSet.getInt("flight_count");

            passengerCount.add(countOfPassenger);
            placeCount.add(countOfPlaces);
            flightCount.add(countOfFlights);

            SQL = "SELECT TIMESTAMPDIFF(MINUTE, '" + begin.format(formatter) +
                  "', '" + end.format(formatter) + "')";
            resultSet = connectionToDB.retriveData(SQL);
            resultSet.first();
            secondsCount.add(resultSet.getLong(1) / countOfFlights);
        }

        double avgCountPassengers = 0;
        double avgCountPlaces = 0;
        double avgCountFlights = 0;
        double avgCountMinutes = 0;

        for (int i = 0; i < passengerCount.size(); i++)
        {
            avgCountPassengers += passengerCount.get(i);
            avgCountFlights += flightCount.get(i);
            avgCountPlaces += placeCount.get(i);
            avgCountMinutes += secondsCount.get(i);
        }

        avgCountPassengers = avgCountPassengers / passengerCount.size();
        avgCountFlights = avgCountFlights / flightCount.size();
        avgCountPlaces = avgCountPlaces / placeCount.size();
        avgCountMinutes = avgCountMinutes / secondsCount.size();

        double result = avgCountPassengers / (avgCountFlights * avgCountPlaces);

        if (avgCountMinutes < 60)
        {
            period.getSelectionModel().selectFirst();
            valuePeriod.setText((int) (avgCountMinutes / result) + "");
        }
        else if (avgCountMinutes >= 60 && avgCountMinutes < 1440)
        {
            period.getSelectionModel().select(1);
            valuePeriod.setText((int) (avgCountMinutes / (60 * result)) + "");
        }
        else if (avgCountMinutes >= 1440 && avgCountMinutes < 10080)
        {
            period.getSelectionModel().select(2);
            valuePeriod.setText((int) (avgCountMinutes / (60 * 24 * result)) + "");
        }
        else if (avgCountMinutes >= 10080 && avgCountMinutes < 302400)
        {
            period.getSelectionModel().select(3);
            valuePeriod.setText((int) (avgCountMinutes / (60 * 24 * 30 * result)) + "");
        }
        else
        {
            period.getSelectionModel().select(4);
            valuePeriod.setText((int) (avgCountMinutes / (60 * 24 * 30 * 12 * result)) + "");
        }
    }

    private void deleteTrip(Trip trip) throws  Exception
    {
        String SQL = "DELETE FROM trip WHERE trip.number = '" + trip.getNumber() + "' ";
        connectionToDB.executeStatement(SQL);

        initTripData();
        initFlightData();
    }

    private void changeTrip(Trip trip) throws Exception
    {
        currentTrip = trip;

        someStage = new Stage();
        someStage.setScene(new Scene(FXMLLoader.load(GetResources.class.getResource("updateTrip.fxml"))));
        someStage.setResizable(false);
        someStage.setTitle("Редактирование рейса");
        someStage.initModality(Modality.WINDOW_MODAL);
        someStage.initOwner(anchorPane.getScene().getWindow());
        someStage.showAndWait();

        initTripData();
        initFlightData();
    }

    private void deleteFlight(Flight flight) throws Exception
    {
        String SQL = "SELECT COUNT(flight.id) FROM flight JOIN trip "
                     + "ON flight.trip_id = trip.id "
                     + "WHERE number = '" + flight.getTrip().getNumber() + "'";
        ResultSet resultSet = connectionToDB.retriveData(SQL);
        resultSet.first();

        int count = resultSet.getInt(1);
        System.out.println(count);
        if (count == 1)
        {
            SQL = "DELETE FROM trip WHERE number = '" + flight.getTrip().getNumber() + "'";
            connectionToDB.executeStatement(SQL);

            SQL = "DELETE FROM flight WHERE trip_id = "
                  + "(SELECT id FROM trip WHERE number = '" + flight.getNumberFlight() + "') "
                  + "AND departure = '" + flight.getDeparture() + "'";
            connectionToDB.executeStatement(SQL);
        }
        else
        {
            SQL = "DELETE FROM flight WHERE trip_id = "
                  + "(SELECT id FROM trip WHERE number = '" + flight.getNumberFlight() + "') "
                  + "AND departure = '" + flight.getDeparture() + "'";
            connectionToDB.executeStatement(SQL);
        }
        initFlightData();
        initTripData();
    }

    private void changeFlight(Flight flight) throws Exception
    {
        currentFlight = flight;
        currentTrip = flight.getTrip();

        someStage = new Stage();
        someStage.setScene(new Scene(FXMLLoader.load(GetResources.class.getResource("updateFlight.fxml"))));
        someStage.setResizable(false);
        someStage.setTitle("Редактирование полета");
        someStage.initModality(Modality.WINDOW_MODAL);
        someStage.initOwner(anchorPane.getScene().getWindow());
        someStage.showAndWait();

        initFlightData();
    }

    private void typingDocumentAboutTrip(Trip trip)
    {
        try
        {
            File file = new File("temp.pdf");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            PdfWriter writer = new PdfWriter(fileOutputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument, PageSize.A4);
            ImageData im = ImageDataFactory.create("C:\\Users\\Emil\\IdeaProjects\\airport3\\src\\sample\\View\\airReserve.png");
            com.itextpdf.layout.element.Image image1  = new Image(im);
            image1.setWidth(200);
            image1.setHeight(80);
            image1.setAutoScale(false);
            image1.setMarginLeft(-40);
            image1.setMarginBottom(-30);

            document.setTopMargin(10);
            document.setLeftMargin(50);
            document.setRightMargin(50);
            document.setBottomMargin(20);
            document.add(image1);

            PdfFont font = PdfFontFactory.createFont("C:\\Users\\Emil\\IdeaProjects\\airport3\\FreeSans.ttf",
                    "Cp1251", true);
            com.itextpdf.layout.element.Text text = new com.itextpdf.layout.element.Text("Рейсовая ведомость");
            text.setFontColor(com.itextpdf.kernel.color.Color.convertRgbToCmyk(new DeviceRgb(15,80, 180)));
            text.setFontSize(14);
            text.setItalic().setBold();

            Paragraph paragraph = new Paragraph(text);
            paragraph.setTextAlignment(TextAlignment.CENTER);
            paragraph.setFont(font);

            com.itextpdf.layout.element.Text text1 = new com.itextpdf.layout.element.Text("Данные о рейсе");
            text1.setFontColor(com.itextpdf.kernel.color.Color.BLACK);
            text1.setFontSize(12);

            com.itextpdf.layout.element.Text text2 = new com.itextpdf.layout.element.Text("Данные о полетах рейса ");
            text2.setFontColor(com.itextpdf.kernel.color.Color.BLACK);
            text2.setFontSize(12);

            Paragraph paragraph1 = new Paragraph(text1);
            paragraph1.setFont(font);

            Paragraph paragraph2 = new Paragraph(text2);
            paragraph2.setFont(font);


            Table table = new Table(5);
            table.setFont(font);
            table.setFontSize(10);
            table.setItalic();

            com.itextpdf.layout.element.Cell fname = new com.itextpdf.layout.element.Cell();
            fname.add("Номер");
            fname.setTextAlignment(TextAlignment.CENTER);
            fname.setBackgroundColor(com.itextpdf.kernel.color.Color.convertRgbToCmyk(new DeviceRgb(230,235,255)));
            fname.setFont(font);
            table.addCell(fname);

            com.itextpdf.layout.element.Cell lname = new com.itextpdf.layout.element.Cell();
            lname.add("Откуда");
            lname.setBackgroundColor(com.itextpdf.kernel.color.Color.convertRgbToCmyk(new DeviceRgb(230,235,255)));
            lname.setTextAlignment(TextAlignment.CENTER);
            lname.setFont(font);
            table.addCell(lname);

            com.itextpdf.layout.element.Cell mname = new com.itextpdf.layout.element.Cell();
            mname.add("Куда");
            mname.setTextAlignment(TextAlignment.CENTER);
            mname.setBackgroundColor(com.itextpdf.kernel.color.Color.convertRgbToCmyk(new DeviceRgb(230,235,255)));
            mname.setFont(font);
            table.addCell(mname);

            com.itextpdf.layout.element.Cell nat = new com.itextpdf.layout.element.Cell();
            nat.add("Авиакомпания");
            nat.setTextAlignment(TextAlignment.CENTER);
            nat.setBackgroundColor(com.itextpdf.kernel.color.Color.convertRgbToCmyk(new DeviceRgb(230,235,255)));
            nat.setFont(font);
            table.addCell(nat);

            com.itextpdf.layout.element.Cell pass = new com.itextpdf.layout.element.Cell();
            pass.add("Длительность");
            pass.setTextAlignment(TextAlignment.CENTER);
            pass.setBackgroundColor(com.itextpdf.kernel.color.Color.convertRgbToCmyk(new DeviceRgb(230,235,255)));
            pass.setFont(font);
            table.addCell(pass);

            table.startNewRow();

            table.addCell(trip.getNumber()).setTextAlignment(TextAlignment.CENTER);
            table.addCell(trip.getDepartureFrom()).setTextAlignment(TextAlignment.CENTER);
            table.addCell(trip.getArrivalIn()).setTextAlignment(TextAlignment.CENTER);
            table.addCell(trip.getAirline()).setTextAlignment(TextAlignment.CENTER);
            table.addCell(trip.getDurationForPresentation()).setTextAlignment(TextAlignment.CENTER);

            Table table1 = new Table(2);
            table1.setFontSize(11);
            table1.setItalic();
            table1.setFont(font);

            com.itextpdf.layout.element.Cell departure = new com.itextpdf.layout.element.Cell();
            departure.add("Отправление");
            departure.setTextAlignment(TextAlignment.CENTER);
            departure.setBackgroundColor(com.itextpdf.kernel.color.Color.convertRgbToCmyk(new DeviceRgb(230,235,255)));
            departure.setFont(font);
            table1.addCell(departure);

            com.itextpdf.layout.element.Cell arrival = new com.itextpdf.layout.element.Cell();
            arrival.add("Прибытие");
            arrival.setTextAlignment(TextAlignment.CENTER);
            arrival.setBackgroundColor(com.itextpdf.kernel.color.Color.convertRgbToCmyk(new DeviceRgb(230,235,255)));
            arrival.setFont(font);
            table1.addCell(arrival);

            String SQL = "SELECT DATE_FORMAT(departure, '%H:%i  %e.%c.%Y') AS departure, "
                         + "DATE_FORMAT(ADDTIME(departure, duration), '%H:%i  %e.%c.%Y') AS arrival "
                         + "FROM flight JOIN trip ON trip.id = flight.trip_id "
                         + "WHERE number = '" + trip.getNumber() + "'";

            ResultSet resultSet = connectionToDB.retriveData(SQL);

            while (resultSet.next())
            {
                table1.addCell(resultSet.getString("departure")).setTextAlignment(TextAlignment.CENTER);
                table1.addCell(resultSet.getString("arrival")).setTextAlignment(TextAlignment.CENTER);
                table1.startNewRow();
            }

            com.itextpdf.layout.element.Text result = new com.itextpdf.layout.element.Text("Дополнительно ");
            result.setFontSize(12);
            result.setFontColor(com.itextpdf.kernel.color.Color.BLACK);

            Paragraph paragraph3 = new Paragraph(result);
            paragraph3.setFont(font);

            String str = "Классы комфортности: ";
            com.itextpdf.layout.element.Text dop = new com.itextpdf.layout.element.Text(str);
            dop.setFontSize(10);
            Paragraph paragraph4 = new Paragraph(dop);
            paragraph4.setFont(font);
            paragraph4.setPaddingLeft(20);

            Table table2 = new Table(3);
            table2.setFontSize(11);
            table2.setItalic();
            table2.setFont(font);

            com.itextpdf.layout.element.Cell type = new com.itextpdf.layout.element.Cell();
            type.add("Название");
            type.setTextAlignment(TextAlignment.CENTER);
            type.setBackgroundColor(com.itextpdf.kernel.color.Color.convertRgbToCmyk(new DeviceRgb(230,235,255)));
            type.setFont(font);
            table2.addCell(type);

            com.itextpdf.layout.element.Cell places = new com.itextpdf.layout.element.Cell();
            places.add("Количество мест");
            places.setTextAlignment(TextAlignment.CENTER);
            places.setBackgroundColor(com.itextpdf.kernel.color.Color.convertRgbToCmyk(new DeviceRgb(230,235,255)));
            places.setFont(font);
            table2.addCell(places);

            com.itextpdf.layout.element.Cell cost = new com.itextpdf.layout.element.Cell();
            cost.add("Стоимость билета ($)");
            cost.setTextAlignment(TextAlignment.CENTER);
            cost.setBackgroundColor(com.itextpdf.kernel.color.Color.convertRgbToCmyk(new DeviceRgb(230,235,255)));
            cost.setFont(font);
            table2.addCell(cost);

            SQL = "SELECT type, count_of_places, ticket_cost "
                  + "FROM class JOIN trip ON class.id_of_trip = trip.id "
                  + "WHERE trip.number = '" + trip.getNumber() + "'";
            resultSet = connectionToDB.retriveData(SQL);


            while (resultSet.next())
            {
                table2.addCell(resultSet.getString("type")).setTextAlignment(TextAlignment.CENTER);
                table2.addCell(resultSet.getString("count_of_places")).setTextAlignment(TextAlignment.CENTER);
                table2.addCell(resultSet.getString("ticket_cost")).setTextAlignment(TextAlignment.CENTER);
                table2.startNewRow();
            }

            com.itextpdf.layout.element.Text date = new com.itextpdf.layout.element.Text(
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy")));
            date.setFontSize(12);

            Paragraph paragraph6 = new Paragraph(date);
            paragraph6.setFont(font);
            paragraph6.setTextAlignment(TextAlignment.RIGHT);

            document.add(paragraph);
            document.add(paragraph1);
            document.add(table);
            document.add(paragraph2);
            document.add(table1);
            document.add(paragraph3);
            document.add(paragraph4);
            document.add(table2);
            document.add(paragraph6);
            document.close();

            Desktop.getDesktop().open(file);
            file.deleteOnExit();
        }
        catch (Exception er)
        {
            er.printStackTrace();
            Alert ob  = new Alert(Alert.AlertType.ERROR);
            ob.setTitle("");
            ob.setHeaderText("Ошибка при открытии файла!");
            ob.setContentText("Попробуйте снова открыть файл. ");
            ob.showAndWait();
        }
    }

    private void typingDocumentAboutFlight(Flight flight)
    {
        try
        {
            File file = new File("temp.pdf");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            PdfWriter writer = new PdfWriter(fileOutputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument, PageSize.A4);
            ImageData im = ImageDataFactory.create("C:\\Users\\Emil\\IdeaProjects\\airport3\\src\\sample\\View\\airReserve.png");
            com.itextpdf.layout.element.Image image1  = new com.itextpdf.layout.element.Image(im);
            image1.setWidth(200);
            image1.setHeight(80);
            image1.setAutoScale(false);
            image1.setMarginLeft(-40);
            image1.setMarginBottom(-30);

            document.setTopMargin(10);
            document.setLeftMargin(50);
            document.setRightMargin(50);
            document.setBottomMargin(20);
            document.add(image1);

            PdfFont font = PdfFontFactory.createFont("FreeSans.ttf", "Cp1251", true);
            com.itextpdf.layout.element.Text text = new com.itextpdf.layout.element.Text("Ведомость о полете");
            text.setFontColor(com.itextpdf.kernel.color.Color.convertRgbToCmyk(new DeviceRgb(15,80, 180)));
            text.setFontSize(14);
            text.setItalic().setBold();

            Paragraph paragraph = new Paragraph(text);
            paragraph.setTextAlignment(TextAlignment.CENTER);
            paragraph.setFont(font);

            com.itextpdf.layout.element.Text text1 = new com.itextpdf.layout.element.Text("Данные о полете");
            text1.setFontColor(com.itextpdf.kernel.color.Color.BLACK);
            text1.setFontSize(12);

            com.itextpdf.layout.element.Text text2 = new com.itextpdf.layout.element.Text("Данные о пассажирах");
            text2.setFontColor(com.itextpdf.kernel.color.Color.BLACK);
            text2.setFontSize(12);

            Paragraph paragraph1 = new Paragraph(text1);
            paragraph1.setFont(font);

            Paragraph paragraph2 = new Paragraph(text2);
            paragraph2.setFont(font);


            Table table = new Table(6);
            table.setFont(font);
            table.setFontSize(10);
            table.setItalic();

            com.itextpdf.layout.element.Cell fname = new com.itextpdf.layout.element.Cell();
            fname.add("Номер");
            fname.setTextAlignment(TextAlignment.CENTER);
            fname.setBackgroundColor(com.itextpdf.kernel.color.Color.convertRgbToCmyk(new DeviceRgb(230,235,255)));
            fname.setFont(font);
            table.addCell(fname);

            com.itextpdf.layout.element.Cell lname = new com.itextpdf.layout.element.Cell();
            lname.add("Откуда");
            lname.setBackgroundColor(com.itextpdf.kernel.color.Color.convertRgbToCmyk(new DeviceRgb(230,235,255)));
            lname.setTextAlignment(TextAlignment.CENTER);
            lname.setFont(font);
            table.addCell(lname);

            com.itextpdf.layout.element.Cell mname = new com.itextpdf.layout.element.Cell();
            mname.add("Куда");
            mname.setTextAlignment(TextAlignment.CENTER);
            mname.setBackgroundColor(com.itextpdf.kernel.color.Color.convertRgbToCmyk(new DeviceRgb(230,235,255)));
            mname.setFont(font);
            table.addCell(mname);

            com.itextpdf.layout.element.Cell nat = new com.itextpdf.layout.element.Cell();
            nat.add("Авиакомпания");
            nat.setTextAlignment(TextAlignment.CENTER);
            nat.setBackgroundColor(com.itextpdf.kernel.color.Color.convertRgbToCmyk(new DeviceRgb(230,235,255)));
            nat.setFont(font);
            table.addCell(nat);

            com.itextpdf.layout.element.Cell pass = new com.itextpdf.layout.element.Cell();
            pass.add("Отправление");
            pass.setTextAlignment(TextAlignment.CENTER);
            pass.setBackgroundColor(com.itextpdf.kernel.color.Color.convertRgbToCmyk(new DeviceRgb(230,235,255)));
            pass.setFont(font);
            table.addCell(pass);

            com.itextpdf.layout.element.Cell arr = new com.itextpdf.layout.element.Cell();
            arr.add("Прибытие");
            arr.setTextAlignment(TextAlignment.CENTER);
            arr.setBackgroundColor(com.itextpdf.kernel.color.Color.convertRgbToCmyk(new DeviceRgb(230,235,255)));
            arr.setFont(font);
            table.addCell(arr);

            table.startNewRow();

            table.addCell(flight.getTrip().getNumber()).setTextAlignment(TextAlignment.CENTER);
            table.addCell(flight.getTrip().getDepartureFrom()).setTextAlignment(TextAlignment.CENTER);
            table.addCell(flight.getTrip().getArrivalIn()).setTextAlignment(TextAlignment.CENTER);
            table.addCell(flight.getTrip().getAirline()).setTextAlignment(TextAlignment.CENTER);
            table.addCell(flight.getDepartureForPresentation()).setTextAlignment(TextAlignment.CENTER);
            table.addCell(flight.getArrivalForPresentation()).setTextAlignment(TextAlignment.CENTER);

            Table table1 = new Table(4);
            table1.setFontSize(11);
            table1.setItalic();
            table1.setFont(font);

            com.itextpdf.layout.element.Cell firstName = new com.itextpdf.layout.element.Cell();
            firstName.add("Имя");
            firstName.setTextAlignment(TextAlignment.CENTER);
            firstName.setBackgroundColor(com.itextpdf.kernel.color.Color.convertRgbToCmyk(new DeviceRgb(230,235,255)));
            firstName.setFont(font);
            table1.addCell(firstName);

            com.itextpdf.layout.element.Cell lastName = new com.itextpdf.layout.element.Cell();
            lastName.add("Фамилия");
            lastName.setTextAlignment(TextAlignment.CENTER);
            lastName.setBackgroundColor(com.itextpdf.kernel.color.Color.convertRgbToCmyk(new DeviceRgb(230,235,255)));
            lastName.setFont(font);
            table1.addCell(lastName);

            com.itextpdf.layout.element.Cell middleName = new com.itextpdf.layout.element.Cell();
            middleName.add("Отчество");
            middleName.setTextAlignment(TextAlignment.CENTER);
            middleName.setBackgroundColor(com.itextpdf.kernel.color.Color.convertRgbToCmyk(new DeviceRgb(230,235,255)));
            middleName.setFont(font);
            table1.addCell(middleName);

            com.itextpdf.layout.element.Cell reserveDate = new com.itextpdf.layout.element.Cell();
            reserveDate.add("Забронировано");
            reserveDate.setTextAlignment(TextAlignment.CENTER);
            reserveDate.setBackgroundColor(com.itextpdf.kernel.color.Color.convertRgbToCmyk(new DeviceRgb(230,235,255)));
            reserveDate.setFont(font);
            table1.addCell(reserveDate);

            String SQL = "SELECT passenger.first_name, passenger.last_name, passenger.middle_name, orders.date_time_reserve "
                         + "FROM passenger INNER JOIN orders INNER JOIN flight INNER JOIN trip "
                         + "ON passenger.id = orders.pass_id "
                         + "AND flight.id = orders.flight_id "
                         + "AND trip.id = flight.trip_id "
                         + "WHERE trip.number = '" + flight.getTrip().getNumber() + "' "
                         + "AND flight.departure = '" + flight.getDeparture() + "'";

            ResultSet resultSet = connectionToDB.retriveData(SQL);

            while (resultSet.next())
            {
                table1.addCell(resultSet.getString("passenger.first_name")).setTextAlignment(TextAlignment.CENTER);
                table1.addCell(resultSet.getString("passenger.last_name")).setTextAlignment(TextAlignment.CENTER);
                table1.addCell(resultSet.getString("passenger.middle_name")).setTextAlignment(TextAlignment.CENTER);
                table1.addCell(resultSet.getString("orders.date_time_reserve")).setTextAlignment(TextAlignment.CENTER);

                table1.startNewRow();
            }

            com.itextpdf.layout.element.Text result = new com.itextpdf.layout.element.Text("Дополнительно ");
            result.setFontSize(12);
            result.setFontColor(com.itextpdf.kernel.color.Color.BLACK);

            Paragraph paragraph3 = new Paragraph(result);
            paragraph3.setFont(font);

            SQL = "SELECT SUM(count_of_places) FROM class JOIN trip "
                  + "ON trip.id = class.id_of_trip "
                  + "WHERE trip.number = '" + flight.getTrip().getNumber() + "'";
            resultSet = connectionToDB.retriveData(SQL);
            resultSet.first();
            int allPlaces = resultSet.getInt(1);

            SQL = "SELECT IFNULL(COUNT(pass_id), 0) FROM orders INNER JOIN trip INNER JOIN flight "
                  + "ON trip.id = flight.trip_id AND orders.flight_id = flight.id "
                  + "WHERE trip.number = '" + flight.getTrip().getNumber() + "' "
                  + "AND flight.departure = '" + flight.getDeparture() + "' ";
            resultSet = connectionToDB.retriveData(SQL);
            resultSet.first();
            int notFreePlaces = resultSet.getInt(1);

            String str = "Количество всех посадочных мест: " + allPlaces + "\n "
                         + "Количество свободных мест: " + (allPlaces - notFreePlaces);

            com.itextpdf.layout.element.Text dop = new com.itextpdf.layout.element.Text(str);
            dop.setFontSize(11);
            Paragraph paragraph4 = new Paragraph(dop);
            paragraph4.setFont(font);
            paragraph4.setPaddingLeft(20);


            com.itextpdf.layout.element.Text date = new com.itextpdf.layout.element.Text(
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy")));
            date.setFontSize(12);

            Paragraph paragraph6 = new Paragraph(date);
            paragraph6.setFont(font);
            paragraph6.setTextAlignment(TextAlignment.RIGHT);

            document.add(paragraph);
            document.add(paragraph1);
            document.add(table);
            document.add(paragraph2);
            document.add(table1);
            document.add(paragraph3);
            document.add(paragraph4);
            document.add(paragraph6);
            document.close();

            Desktop.getDesktop().open(file);
            file.deleteOnExit();
        }
        catch (Exception er)
        {
            er.printStackTrace();
            Alert ob  = new Alert(Alert.AlertType.ERROR);
            ob.setTitle("");
            ob.setHeaderText("Ошибка при открытии файла! ");
            ob.setContentText("Попробуйте снова открыть файл. ");
            ob.showAndWait();
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

    public  static boolean checkAddFlight = false;

    private void addFlight() throws Exception
    {
        someStage = new Stage();
        someStage.setScene(new Scene(FXMLLoader.load(GetResources.class.getResource("addFlight.fxml"))));
        someStage.setResizable(false);
        someStage.setTitle("Добавление полета");
        someStage.initModality(Modality.WINDOW_MODAL);
        someStage.initOwner(anchorPane.getScene().getWindow());
        someStage.showAndWait();

        initFlightData();
        initTripData();
    }

    private void addFlightForTrip(Trip trip) throws Exception
    {
        currentTrip = trip;

        someStage = new Stage();
        someStage.setScene(new Scene(FXMLLoader.load(GetResources.class.getResource("addFlightForTrip.fxml"))));
        someStage.setResizable(false);
        someStage.setTitle("Добавление полета указаного рейса");
        someStage.initModality(Modality.WINDOW_MODAL);
        someStage.initOwner(anchorPane.getScene().getWindow());
        someStage.showAndWait();

        initFlightData();
        initTripData();
    }
}
