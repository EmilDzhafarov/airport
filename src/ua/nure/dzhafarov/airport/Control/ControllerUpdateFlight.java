package ua.nure.dzhafarov.airport.Control;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import ua.nure.dzhafarov.airport.ConnectionToDB;
import ua.nure.dzhafarov.airport.Model.Flight;
import ua.nure.dzhafarov.airport.Model.Trip;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Emil on 20.11.2016.
 */
public class ControllerUpdateFlight
{
    private Flight currentFlight = ControllerForManager.getCurrentFlight();
    private Trip currentTrip = ControllerForManager.getCurrentTrip();
    private ConnectionToDB connectionToDB = ControllerIndexForm.getDb();

    @FXML
    private TextField numberFlight;
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
    private Text mistakeText;

    @FXML
    private void initialize()
    {
        departureHour.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,23,0,1));
        departureMinute.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,59,0,1));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dep = LocalDateTime.parse(currentFlight.getDeparture(), formatter);


        numberFlight.setText(currentFlight.getNumberFlight());
        departureFrom.setText(currentFlight.getDepartureFrom());
        arrivalIn.setText(currentFlight.getArrivalIn());
        airline.setText(currentFlight.getAirline());
        mistakeText.setText("");


        int dh = dep.getHour();
        while (dh != 0)
        {
            departureHour.increment();
            dh--;
        }

        int dm = dep.getMinute();
        while (dm != 0)
        {
            departureMinute.increment();
            dm--;
        }

        departureDate.setValue(dep.toLocalDate());
    }

    @FXML
    private void onClickUpdate()
    {
        try
        {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime departure = LocalDateTime.of(
                            departureDate.getValue().getYear(),
                            departureDate.getValue().getMonthValue(),
                            departureDate.getValue().getDayOfMonth(),
                            departureHour.getValue(),
                            departureMinute.getValue()
            );

            Flight flight = new Flight(currentTrip, departure.format(formatter));

            String SQL = "UPDATE flight SET departure = '" + flight.getDeparture() + "' WHERE trip_id = "
                  + "(SELECT id FROM trip WHERE number = '" + flight.getNumberFlight() + "') "
                  + "AND departure = '" + currentFlight.getDeparture() + "'";
            connectionToDB.executeStatement(SQL);

            mistakeText.setText("");
            ControllerForManager.getSomeStage().close();
        }
        catch (Flight.FlightException ex)
        {
            mistakeText.setText(ex.getMessage());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
