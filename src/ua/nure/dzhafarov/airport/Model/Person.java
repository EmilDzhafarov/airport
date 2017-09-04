package ua.nure.dzhafarov.airport.Model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Created by Emil on 12.10.2016.
 */
public class Person
{
    private String firstName;
    private String lastName;
    private String middleName;
    private LocalDate birthday;
    private String passportNumber;
    private String nationality;

    private final String regExp = "[a-zA-Z\\s]+|[а-яА-Я\\s]+";

    public Person(String F, String L, String M, String birth, String passNum, String nationality)
            throws PassengerException {

        setFirstName(F);
        setLastName(L);
        setMiddleName(M);
        setBirthday(birth);
        setPassportNumber(passNum);
        setNationality(nationality);
    }


    private void setFirstName(String string) throws PassengerException
    {
        if (!string.matches(regExp))
        {
            throw new PassengerException("Имя должно состоять из букв русского или английского алфавита, а также пропусков!");
        }
        else
        {
            firstName = removeWhiteSpacesAndUpperFirstLater(string);
        }
    }

    private void setLastName(String string) throws PassengerException
    {
        if (!string.matches(regExp))
        {
            throw new PassengerException("Фамилия должна состоять из букв русского или английского алфавита, а также пропусков!");
        }
        else
        {
            lastName = removeWhiteSpacesAndUpperFirstLater(string);
        }
    }

    private void setMiddleName(String string) throws PassengerException
    {
        if (!string.matches(regExp))
        {
            throw new PassengerException("Отчество должно состоять из букв русского или английского алфавита, а также пропусков!");
        }
        else
        {
            middleName = removeWhiteSpacesAndUpperFirstLater(string);
        }
    }

    private void setBirthday(String string) throws PassengerException
    {
        boolean check = false;

        try
        {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.parse(string, formatter);
            if (date.isAfter(LocalDate.now()))
            {
                check = true;
                throw new PassengerException("Дата рождения не может быть больше текущей !");
            }
            birthday = date;
        }
        catch (Exception ex)
        {
            if (check)
            {
                throw ex;
            }
            else
            {
                throw new PassengerException("Проверьте правильость ввода даты рождения!");
            }
        }
    }

    private void setPassportNumber(String string) throws PassengerException
    {
        if (!string.matches("[0-9a-zA-Z\\s]+|[0-9а-яА-Я\\s]+"))
        {
            throw new PassengerException("Номер пасспорта может содержать только буквы русского или " +
                    "английского алфавита, а также пропуски и цифры!");
        }
        else
        {
            passportNumber = removeWhiteSpacesAndUpperFirstLater(string);
        }
    }

    private void setNationality(String string) throws PassengerException
    {
        if (!string.matches(regExp))
        {
            throw new PassengerException("Недопустимые символы в указаном гражданстве!");
        }
        else
        {
            nationality = removeWhiteSpacesAndUpperFirstLater(string);
        }
    }

    public String getFirstName(){return firstName;}

    public String getLastName(){return lastName;}

    public String getMiddleName() {return middleName;}

    public String getBirthdayForInsertDB()
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return birthday.format(formatter);
    }

    public String getBirthdayForPresentation()
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return birthday.format(formatter);
    }

    public String getPassportNumber(){return passportNumber;}

    public String getNationality() {return nationality;}

    @Override
    public boolean equals(Object ob)
    {
        if (!ob.getClass().getName().equals(getClass().getName()))
        {
            return false;
        }

        Person curr = (Person) ob;

        if (curr.getFirstName().equals(getFirstName()) &&
                curr.getLastName().equals(getLastName()) &&
                curr.getMiddleName().equals(getMiddleName()) &&
                curr.getBirthdayForPresentation().equals(getBirthdayForPresentation()) &&
                curr.getNationality().equals(getNationality())) {

            return true;
        }

        return false;
    }

    private String removeWhiteSpacesAndUpperFirstLater(String string)
    {
        String result = string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();

        String res = "";

        for (int i = 0; i < result.length() - 1; i++)
        {
            if (!(result.charAt(i) == ' ' && result.charAt(i + 1) == ' '))
            {
                res += result.charAt(i);
            }
        }

        if (result.charAt(result.length() - 1) != ' ')
        {
            res += result.charAt(result.length() - 1);
        }

        return res;
    }

    /**
     * Created by Emil on 12.10.2016.
     */
    public static class PassengerException extends Exception
    {
        public PassengerException(String s)
        {
            super(s);
        }
    }
}
