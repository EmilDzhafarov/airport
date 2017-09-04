package ua.nure.dzhafarov.airport.Model;

/**
 * Created by Emil on 21.11.2016.
 */
public class Passenger
{
    private Person person;
    private Discount discount;

    public Passenger(Person person, Discount discount)
    {
        this.person = person;
        this.discount = discount;
    }

    public String getFirstName()
    {
        return person.getFirstName();
    }

    public String getLastName()
    {
        return person.getLastName();
    }

    public String getMiddleName()
    {
        return person.getMiddleName();
    }

    public String getBirthdayForPresentation()
    {
        return person.getBirthdayForPresentation();
    }

    public String getBirthdayForInsertDB()
    {
       return person.getBirthdayForInsertDB();
    }

    public String getPassportNumber()
    {
        return person.getPassportNumber();
    }

    public String getNationality()
    {
        return person.getNationality();
    }

    public String getDiscountName()
    {
        return discount.getName();
    }

    public Discount getDiscount()
    {
        return discount;
    }

    public Person getPerson()
    {
        return person;
    }

}
