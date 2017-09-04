package ua.nure.dzhafarov.airport.Model;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class ClassOfFlight
{
    private Flight flight;
    private double ticketCost;
    private String classComfort;
    private Integer freePlaces;


    public ClassOfFlight(Flight flight, String cost, String classComfort, Integer freePlaces) throws ClassOfFlightException
    {
        setFlight(flight);
        setTicketCost(cost);
        setClassComfort(classComfort);
        setFreePlaces(freePlaces);
    }

    private void setFlight(Flight flight)
    {
        this.flight = flight;
    }
    private void setFreePlaces(int k) throws ClassOfFlightException
    {
        if (k < 0)
        {
            throw new ClassOfFlightException("Количество свободных мест не может быть отрицательным");
        }
        else
        {
            this.freePlaces = k;
        }
    }

    private void setClassComfort(String cl) throws ClassOfFlightException
    {
        if (!cl.matches("[a-zA-Zа-яА-Я-\\s]+"))
        {
            throw new ClassOfFlightException("Недопустимые символы в определении класса комфортности");
        }
        else
        {
            this.classComfort = cl;
        }
    }

    private void setTicketCost(String cost) throws ClassOfFlightException
    {
        try
        {
            Double tickCost = Double.parseDouble(cost);
            DecimalFormat formatter = new DecimalFormat("#0.00");
            DecimalFormatSymbols ss = new DecimalFormatSymbols();
            ss.setDecimalSeparator('.');
            formatter.setDecimalFormatSymbols(ss);

            ticketCost = Double.parseDouble(formatter.format(tickCost));
        }
        catch (Exception err)
        {
            throw new ClassOfFlightException("Неверные данные о стоимости полета !");
        }
    }



    public String getNumber()
    {
        return flight.getNumberFlight();
    }

    public String getDepartureFrom()
    {
        return flight.getDepartureFrom();
    }

    public String getArrivalIn()
    {
        return flight.getArrivalIn();
    }

    public String getAirline()
    {
        return flight.getAirline();
    }

    public String getDeparture()
    {
        return flight.getDeparture();
    }

    public String getClassComfort()
    {
        return this.classComfort;
    }

    public Integer getFreePlaces()
    {
        return this.freePlaces;
    }

    public String getDepartureForPresentation()
    {
        return flight.getDepartureForPresentation();
    }

    public String getArrivalForPresentation()
    {
        return flight.getArrivalForPresentation();
    }

    public Double getTicketCost()
    {
        return ticketCost;
    }

    /**
     * Created by Emil on 11.10.2016.
     */
    public static class ClassOfFlightException extends Exception
    {
        public ClassOfFlightException(String string)
        {
            super(string);
        }
    }
}
