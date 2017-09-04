package ua.nure.dzhafarov.airport.Model;

/**
 * Created by Emil on 21.11.2016.
 */
public class Discount
{
    private String name;
    private Double value;
    private Long code;

    public Discount(String name, Double value, Long code)
    {
        if (name == null)
        {
            this.name = "Без скидки";
        }
        else
        {
            this.name = name;
        }

        this.value = value;
        if (code == 0)
        {
            this.code = 0L;
        }
        else
        {
            this.code = code;
        }
    }

    public String getName()
    {
        return name;
    }

    public Double getValue()
    {
        return value;
    }

    public Long getCode()
    {
        return code;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
