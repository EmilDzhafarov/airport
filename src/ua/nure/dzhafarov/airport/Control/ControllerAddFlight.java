package ua.nure.dzhafarov.airport.Control;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import org.controlsfx.control.textfield.TextFields;
import ua.nure.dzhafarov.airport.Model.Flight;
import ua.nure.dzhafarov.airport.Model.Trip;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;

/**
 * Created by Emil on 26.11.2016.
 */
public class ControllerAddFlight
{
    @FXML
    private TextField number;
    @FXML
    private TextField departureFrom;
    @FXML
    private TextField arrivalIn;
    @FXML
    private TextField airline;
    @FXML
    private Spinner<Integer> departureHour;
    @FXML
    private Spinner<Integer> departureMinute;
    @FXML
    private DatePicker departureDate;
    @FXML
    private Spinner<Integer> durationHour;
    @FXML
    private Spinner<Integer> durationMinute;
    @FXML
    private Text mistakeText;
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
    private void initialize()
    {
        try
        {
            if (ControllerForManager.checkAddFlight)
            {
                number.setText(ControllerForManager.getCurrentTrip().getNumber());
                departureFrom.setText(ControllerForManager.getCurrentTrip().getDepartureFrom());
                arrivalIn.setText(ControllerForManager.getCurrentTrip().getArrivalIn());
                airline.setText(ControllerForManager.getCurrentTrip().getAirline());
                arrivalIn.setDisable(true);
                airline.setDisable(true);
                departureFrom.setDisable(true);
                number.setDisable(true);

            }

            departureHour.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,23,0,1));
            departureMinute.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,59,0,1));
            durationHour.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,23,0,1));
            durationMinute.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,59,0,1));
            departureDate.setValue(LocalDate.now());
            initInfoForTextFields(departureFrom, "SELECT DISTINCT departure_from FROM trip "
                                                 + "UNION "
                                                 + "SELECT DISTINCT arrival_in FROM trip");
            initInfoForTextFields(arrivalIn, "SELECT DISTINCT departure_from FROM trip "
                                             + "UNION "
                                             + "SELECT DISTINCT arrival_in FROM trip");
            initInfoForTextFields(airline, "SELECT DISTINCT airline FROM trip");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @FXML
    private void onClickAddFlight()
    {
        try
        {
            Trip trip = new Trip(
                    number.getText(),
                    departureFrom.getText(),
                    arrivalIn.getText(),
                    airline.getText(),
                    durationHour.getValue() + ":" + durationMinute.getValue()

            );

            LocalDateTime dep = LocalDateTime.of(departureDate.getValue().getYear(),
                    departureDate.getValue().getMonthValue(),
                    departureDate.getValue().getDayOfMonth(),
                    departureHour.getValue(),
                    departureMinute.getValue());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            Flight flight = new Flight(trip, dep.format(formatter));

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

            String SQL = "INSERT INTO trip (number, departure_from, arrival_in, airline, duration) "
                         + "VALUES ('" + trip.getNumber() + "', '" + trip.getDepartureFrom() + "', '"
                         + trip.getArrivalIn() + "', '" + trip.getAirline() + "', '" +
                         trip.getDuration().format(DateTimeFormatter.ofPattern("HH:mm")) + "')";
            ControllerIndexForm.getDb().executeStatement(SQL);

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

            SQL = "INSERT INTO flight (trip_id, departure) "
                  + "VALUES((SELECT MAX(id) FROM trip), '" + flight.getDeparture() + "')";
            ControllerIndexForm.getDb().executeStatement(SQL);

            ControllerForManager.getSomeStage().close();
        }
        catch (Trip.TripException | Flight.FlightException ex)
        {
            mistakeText.setText(ex.getMessage());
        }
        catch (MySQLIntegrityConstraintViolationException ex)
        {
            mistakeText.setText("Номер уже используется");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
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

    private void insertClassComfort(String places, String cost, String type) throws Exception
    {
        Integer count = Integer.parseInt(places);
        Double price = Double.parseDouble(cost);

        String SQL = "INSERT INTO class (id_of_trip, type, count_of_places, ticket_cost) "
                     + "VALUES ((SELECT MAX(id) FROM trip), '" + type + "', " + count + "," + price + ")";

        ControllerIndexForm.getDb().executeStatement(SQL);
    }
}
