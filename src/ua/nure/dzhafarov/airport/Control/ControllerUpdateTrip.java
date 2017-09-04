package ua.nure.dzhafarov.airport.Control;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import ua.nure.dzhafarov.airport.ConnectionToDB;
import ua.nure.dzhafarov.airport.Model.Flight;
import ua.nure.dzhafarov.airport.Model.Trip;

import java.sql.ResultSet;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Created by Emil on 20.11.2016.
 */
public class ControllerUpdateTrip
{
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

    private ArrayList<String> arrayList = new ArrayList<>();

    @FXML
    private void initialize() throws Exception
    {
        durationHour.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,30,0,1));
        durationMinute.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,59,0,1));

        numberFlight.setText(currentTrip.getNumber());
        departureFrom.setText(currentTrip.getDepartureFrom());
        arrivalIn.setText(currentTrip.getArrivalIn());
        airline.setText(currentTrip.getAirline());
        mistakeText.setText("");

        int h = currentTrip.getDuration().getHour();
        while (h != 0)
        {
            durationHour.increment();
            h--;
        }

        int m = currentTrip.getDuration().getMinute();
        while (m != 0)
        {
            durationMinute.increment();
            m--;
        }

        String SQL = "SELECT type, ticket_cost, count_of_places FROM class "
                     + "WHERE id_of_trip = (SELECT id FROM trip WHERE number = '" + numberFlight.getText() + "')";
        ResultSet resultSet = connectionToDB.retriveData(SQL);

        while (resultSet.next())
        {
            arrayList.add(resultSet.getString("type"));
            System.out.println(resultSet.getString("type"));
            if (resultSet.getString("type").equalsIgnoreCase("Эконом"))
            {
                economClass.setSelected(true);
                economClassCost.setText(resultSet.getString("ticket_cost"));
                economClassCost.setVisible(true);
                economClassPlaces.setText(resultSet.getString("count_of_places"));
                economClassPlaces.setVisible(true);
            }
            else if (resultSet.getString("type").equalsIgnoreCase("Бизнесс"))
            {
                businessClass.setSelected(true);
                businessClassCost.setText(resultSet.getString("ticket_cost"));
                businessClassCost.setVisible(true);
                businessClassPlaces.setText(resultSet.getString("count_of_places"));
                businessClassPlaces.setVisible(true);
            }
            else if (resultSet.getString("type").equalsIgnoreCase("Первый"))
            {
                firstClass.setSelected(true);
                firstClassCost.setText(resultSet.getString("ticket_cost"));
                firstClassCost.setVisible(true);
                firstClassPlaces.setText(resultSet.getString("count_of_places"));
                firstClassPlaces.setVisible(true);
            }
            else
            {
                anotherClass.setSelected(true);
                anotherClassCost.setText(resultSet.getString("ticket_cost"));
                anotherClassCost.setVisible(true);
                anotherClassPlaces.setText(resultSet.getString("count_of_places"));
                anotherClassPlaces.setVisible(true);
                anotherClassName.setText(resultSet.getString("type"));
                anotherClassName.setVisible(true);
                anotherClassName.setDisable(true);
            }
        }
    }

    @FXML
    private void onClickUpdate()
    {
        try
        {
            LocalTime duration = LocalTime.of(durationHour.getValue(), durationMinute.getValue());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

            Trip trip = new Trip(numberFlight.getText(),
                    departureFrom.getText(),
                    arrivalIn.getText(),
                    airline.getText(),
                    duration.format(formatter));

            String SQL = "UPDATE trip SET departure_from = '" + trip.getDepartureFrom() + "', "
                         + "arrival_in = '" + trip.getArrivalIn() + "', "
                         + "airline = '" + trip.getAirline() + "', "
                         + "duration = '" + trip.getDuration().format(formatter) + "' "
                         + "WHERE number = '" + trip.getNumber() + "'";
            connectionToDB.executeStatement(SQL);

            if (firstClass.isSelected())
            {
                if (arrayList.contains("Первый"))
                {
                    SQL = "UPDATE class SET ticket_cost = " + Double.parseDouble(firstClassCost.getText()) +
                          ", count_of_places = " + Integer.parseInt(firstClassPlaces.getText()) +
                          " WHERE id_of_trip = (SELECT id FROM trip WHERE number = '" + numberFlight.getText() + "') "
                          + "AND type = 'Первый'";
                }
                else
                {
                    SQL = "INSERT INTO class (id_of_trip, type, count_of_places, ticket_cost)  "
                          + "VALUES ((SELECT id FROM trip WHERE number = '" + numberFlight.getText() + "'), "
                          + "'Первый', " + Integer.parseInt(firstClassPlaces.getText()) + "," +
                          Double.parseDouble(firstClassCost.getText()) + ")";
                }
                connectionToDB.executeStatement(SQL);
            }
            else
            {
                if (arrayList.contains("Первый"))
                {
                    SQL = "DELETE FROM class WHERE id_of_trip = (SELECT id FROM trip WHERE number ='" + numberFlight.getText() +"')"
                          + "AND type = 'Первый'";
                    connectionToDB.executeStatement(SQL);
                }
            }

            if (businessClass.isSelected())
            {
                if (arrayList.contains("Бизнесс"))
                {
                    SQL = "UPDATE class SET ticket_cost = " + Double.parseDouble(businessClassCost.getText()) +
                          ", count_of_places = " + Integer.parseInt(businessClassPlaces.getText()) +
                          " WHERE id_of_trip = (SELECT id FROM trip WHERE number = '" + numberFlight.getText() + "') "
                          + "AND type = 'Бизнесс'";
                }
                else
                {
                    SQL = "INSERT INTO class (id_of_trip, type, count_of_places, ticket_cost)  "
                          + "VALUES ((SELECT id FROM trip WHERE number = '" + numberFlight.getText() + "'), "
                          + "'Бизнесс', " + Integer.parseInt(businessClassPlaces.getText()) + "," +
                          Double.parseDouble(businessClassCost.getText()) + ")";
                }
                connectionToDB.executeStatement(SQL);
            }
            else
            {
                if (arrayList.contains("Бизнесс"))
                {
                    SQL = "DELETE FROM class WHERE id_of_trip = (SELECT id FROM trip WHERE number ='" + numberFlight.getText() +"')"
                          + "AND type = 'Бизнесс'";
                    connectionToDB.executeStatement(SQL);
                }
            }

            if (economClass.isSelected())
            {
                if (arrayList.contains("Эконом"))
                {
                    SQL = "UPDATE class SET ticket_cost = " + Double.parseDouble(economClassCost.getText()) +
                          ", count_of_places = " + Integer.parseInt(economClassPlaces.getText()) +
                          " WHERE id_of_trip = (SELECT id FROM trip WHERE number = '" + numberFlight.getText() + "') "
                          + "AND type = 'Эконом'";
                }
                else
                {
                    SQL = "INSERT INTO class (id_of_trip, type, count_of_places, ticket_cost)  "
                          + "VALUES ((SELECT id FROM trip WHERE number = '" + numberFlight.getText() + "'), "
                          + "'Эконом', " + Integer.parseInt(economClassPlaces.getText()) + "," +
                          Double.parseDouble(economClassCost.getText()) + ")";
                }
                connectionToDB.executeStatement(SQL);
            }
            else
            {
                if (arrayList.contains("Эконом"))
                {
                    SQL = "DELETE FROM class WHERE id_of_trip = (SELECT id FROM trip WHERE number ='" + numberFlight.getText() +"')"
                          + "AND type = 'Эконом'";
                    connectionToDB.executeStatement(SQL);
                }
            }

            if (anotherClass.isSelected())
            {
                if (arrayList.contains(anotherClassName.getText()))
                {
                    anotherClassName.setDisable(true);
                    SQL = "UPDATE class SET ticket_cost = " + Double.parseDouble(anotherClassCost.getText()) +
                          ",count_of_places = " + Integer.parseInt(anotherClassPlaces.getText()) +
                          " WHERE id_of_trip = (SELECT id FROM trip WHERE number = '" + numberFlight.getText() + "') "
                          + "AND type = '" + anotherClassName.getText() + "'";
                }
                else
                {
                    anotherClassName.setDisable(false);
                    if (anotherClassName.getText().trim().isEmpty())
                    {
                        throw new Flight.FlightException("Введите название класса");
                    }

                    SQL = "INSERT INTO class (id_of_trip, type, count_of_places, ticket_cost)  "
                          + "VALUES ((SELECT id FROM trip WHERE number = '" + numberFlight.getText() + "'), "
                          + "'" + anotherClassName.getText() + "'," +
                          Integer.parseInt(anotherClassPlaces.getText()) + "," +
                          Double.parseDouble(anotherClassCost.getText()) + ")";
                }


                connectionToDB.executeStatement(SQL);
            }
            else
            {
                if (getAnotherName() != null)
                {
                    SQL = "DELETE FROM class WHERE id_of_trip = (SELECT id FROM trip WHERE number ='" + numberFlight.getText() +"') "
                          + "AND type = '" + getAnotherName() + "'";
                    connectionToDB.executeStatement(SQL);
                }
            }

            if (!economClass.isSelected() && !businessClass.isSelected()
                && !firstClass.isSelected() && !anotherClass.isSelected()) {

                throw new Flight.FlightException("Необходимо указать классы комфортности\n"
                                                 + "и количество посадочных мест");
            }

            mistakeText.setText("");
            ControllerForManager.getSomeStage().close();
        }
        catch (Trip.TripException | Flight.FlightException ex)
        {
            ex.printStackTrace();
            mistakeText.setText(ex.getMessage());
        }
        catch (NumberFormatException rc)
        {
            mistakeText.setText("Убедитесь в правильности введенных значений");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
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

    private String getAnotherName()
    {
        for (int i = 0; i < arrayList.size(); i++)
        {
            if (!arrayList.get(i).equals("Эконом") &&
                    !arrayList.get(i).equals("Первый") &&
                    !arrayList.get(i).equals("Бизнесс"))
            {
                return arrayList.get(i);
            }
        }

        return null;
    }
}
