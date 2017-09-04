package ua.nure.dzhafarov.airport.Control;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import ua.nure.dzhafarov.airport.Model.Flight;
import ua.nure.dzhafarov.airport.Model.Trip;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Emil on 26.11.2016.
 */
public class ControllerAddFlightForTrip
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
    private Trip currentTrip = ControllerForManager.getCurrentTrip();

    @FXML
    private void initialize()
    {
        number.setText(currentTrip.getNumber());
        departureFrom.setText(currentTrip.getDepartureFrom());
        arrivalIn.setText(currentTrip.getArrivalIn());
        airline.setText(currentTrip.getAirline());

        departureHour.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,23,0,1));
        departureMinute.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,59,0,1));
        durationHour.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,23,0,1));
        durationMinute.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,59,0,1));
        departureDate.setValue(LocalDate.now());

        int hour = currentTrip.getDuration().getHour();
        int minute = currentTrip.getDuration().getMinute();

        while (hour != 0)
        {
            durationHour.increment();
            hour--;
        }

        while (minute != 0)
        {
            durationMinute.increment();
            minute--;
        }
    }

    @FXML
    private void onClickAddFlight()
    {
        try
        {
            LocalDateTime dep = LocalDateTime.of(departureDate.getValue().getYear(),
                    departureDate.getValue().getMonthValue(),
                    departureDate.getValue().getDayOfMonth(),
                    departureHour.getValue(),
                    departureMinute.getValue());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            Flight flight = new Flight(currentTrip, dep.format(formatter));

            String SQL = "INSERT INTO flight (trip_id, departure) "
                  + "VALUES((SELECT id FROM trip WHERE number = '" + currentTrip.getNumber() + "'), "
                         + "'" + flight.getDeparture() + "')";
            ControllerIndexForm.getDb().executeStatement(SQL);

            ControllerForManager.getSomeStage().close();
        }
        catch (Flight.FlightException ex)
        {
            mistakeText.setText(ex.getMessage());
        }
        catch (MySQLIntegrityConstraintViolationException ex)
        {
            mistakeText.setText("В указаные дату и время отправления этот рейс уже имеет полет");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
