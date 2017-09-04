package ua.nure.dzhafarov.airport.Model;

/**
 * Created by Emil on 21.11.2016.
 */
public class Employee
{
    private Person person;
    private String occupation;
    private Long identificationCode;
    private String login;

    private final String regExp = "[a-zA-Z0-9@\\s]+";
    public Employee(Person person, String occupation, Long identificationCode, String login) throws Person.PassengerException
    {
        this.person = person;
        setOccupation(occupation);
        this.identificationCode = identificationCode;
        setLogin(login);
    }

    public Person getPerson() {return person; }

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

    public String getOccupation()
    {
        return occupation;
    }

    public String getLogin()
    {
        return login;
    }

    public Long getIdentificationCode()
    {
        return identificationCode;
    }

    public void setLogin(String login) throws Person.PassengerException
    {
        if (!login.matches(regExp))
        {
            throw new Person.PassengerException("Логин содержит недопустимые символы");
        }
        else
        {
            this.login = login;
        }
    }

    private void setOccupation(String occupation) throws Person.PassengerException
    {
        if (!occupation.matches("[а-яА-Я\\s]+"))
        {
            throw new Person.PassengerException("Недопустимые символы в профессии");
        }
        else
        {
            this.occupation = occupation;
        }
    }
}
