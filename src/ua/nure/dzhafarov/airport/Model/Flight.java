package ua.nure.dzhafarov.airport.Model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

/**
 * Created by Emil on 14.11.2016.
 */
public class Flight implements Comparator<Flight>
{
    private Trip trip;
    private LocalDateTime departure;
    private LocalDateTime arrival;

    public Flight(Trip trip, String departure) throws FlightException
    {
        setTrip(trip);
        setDeparture(departure);
        setArrival();
    }

    private void setTrip(Trip trip)
    {
        this.trip = trip;
    }

    private void setDeparture(String departure) throws FlightException
    {
        boolean check = false;

        try
        {
            String date = departure.split(" ")[0];
            String time = departure.split(" ")[1];

            int year = Integer.parseInt(date.split("-")[0]);
            int month = Integer.parseInt(date.split("-")[1]);
            int day = Integer.parseInt(date.split("-")[2]);
            int hour = Integer.parseInt(time.split(":")[0]);
            int minute = Integer.parseInt(time.split(":")[1]);

            this.departure = LocalDateTime.of(year, month, day, hour, minute);


        }
        catch (Exception ex)
        {
            if (check)
            {
                throw new FlightException("Дата и время отправления не должны быть меньше текущих");
            }
            else
            {
                throw new FlightException("Неверные данные о дате и времени отправления");
            }
        }
    }

    private void setArrival() throws FlightException
    {
        try
        {
            LocalDateTime dep = departure;
            this.arrival = dep.plusHours(trip.getDuration().getHour()).plusMinutes(trip.getDuration().getMinute());
        }
        catch (Exception ex)
        {
            throw new FlightException("Неверные данные о дате и времени прибытия");
        }
    }

    public String getNumberFlight()
    {
        return trip.getNumber();
    }

    public String getDepartureFrom()
    {
        return trip.getDepartureFrom();
    }

    public String getArrivalIn()
    {
        return trip.getArrivalIn();
    }

    public String getAirline()
    {
        return trip.getAirline();
    }

    public String getDeparture()
    {
        return this.departure.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public String getArrival()
    {
        return this.arrival.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public String getDepartureForPresentation()
    {
        return this.departure.format(DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy"));
    }

    public String getArrivalForPresentation()
    {
        return this.arrival.format(DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy"));
    }
    public Trip getTrip()
    {
        return trip;
    }
    /**
     * Created by Emil on 14.11.2016.
     */
    public static class FlightException extends Exception
    {
        public FlightException(String str)
        {
            super(str);
        }
    }

    public Flight(){}
    @Override
    public int compare(Flight fl1, Flight fl2)
    {
        if (fl1.departure.isAfter(fl2.departure))
        {
            return 1;
        }
        else if (fl1.departure.isBefore(fl2.departure))
        {
            return -1;
        }
        else
        {
            return 0;
        }
    }
}
