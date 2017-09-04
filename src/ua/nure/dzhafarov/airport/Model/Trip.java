package ua.nure.dzhafarov.airport.Model;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Emil on 14.11.2016.
 */
public class Trip
{
    private String number;
    private String departureFrom;
    private String arrivalIn;
    private String airline;
    private LocalTime duration;
    private String regExp = "[a-zA-Z\\s]+|[а-яёА-ЯЁ\\s]+";

    public Trip(String number, String departureFrom, String arrivalIn, String airline, String duration) throws TripException
    {
        setNumber(number);
        setDepartureFrom(departureFrom);
        setArrivalIn(arrivalIn);
        setAirline(airline);
        setDuration(duration);
    }

    private void setNumber(String number) throws TripException
    {
        number = number.toUpperCase();

        if (!number.matches("[0-9A-Z\\s]+"))
        {
            throw new TripException("Недопустимые символы в номере рейса");
        }
        else
        {
            this.number = number;
        }
    }

    private void setDepartureFrom(String departureFrom) throws TripException
    {
        if (!departureFrom.matches(regExp))
        {
            throw new TripException("Недопустимые символы в точке отправления рейса");
        }
        else
        {
            this.departureFrom = departureFrom;
        }
    }

    private void setArrivalIn(String arrivalIn) throws TripException
    {
        if (!arrivalIn.matches(regExp))
        {
            throw new TripException("Недопустимые символы в точке прибытия рейса");
        }
        else
        {
            this.arrivalIn = arrivalIn;
        }
    }

    private void setAirline(String airline) throws TripException
    {
        if (!airline.matches(regExp))
        {
            throw new TripException("Недопустимые символы в названии авиакомпании");
        }
        else
        {
            this.airline = airline;
        }
    }

    private void setDuration(String duration) throws TripException
    {
        boolean check = false;

        try
        {
            this.duration = LocalTime.of(Integer.parseInt(duration.split(":")[0]), Integer.parseInt(duration.split(":")[1]));

            if (this.duration.getHour() == 0 && this.duration.getMinute() == 0)
            {
                check = true;
                throw new Exception();
            }
        }
        catch (Exception ex)
        {
            if (check)
            {
                throw new TripException("Длительность полета не может\nсостовлять 00:00");
            }

            throw new TripException("Неверные данные длительности полета");
        }
    }

    public String getNumber()
    {
        return this.number;
    }

    public String getDepartureFrom()
    {
        return this.departureFrom;
    }

    public String getArrivalIn()
    {
        return this.arrivalIn;
    }

    public String getAirline()
    {
        return this.airline;
    }

    public LocalTime getDuration()
    {
        return this.duration;
    }

    public String getDurationForPresentation()
    {
        return this.duration.format(DateTimeFormatter.ofPattern("HH:mm"));
    }
    /**
     * Created by Emil on 14.11.2016.
     */
    public static class TripException extends Exception
    {
        public TripException(String str)
        {
            super(str);
        }
    }
}
