package ua.nure.dzhafarov.airport.Control;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.*;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.property.TextAlignment;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import ua.nure.dzhafarov.airport.ConnectionToDB;
import ua.nure.dzhafarov.airport.Model.ClassOfFlight;
import ua.nure.dzhafarov.airport.Model.Passenger;
import ua.nure.dzhafarov.airport.Model.Person;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Emil on 09.11.2016.
 */
public class ControllerForConfirmReserve
{
    private ClassOfFlight selectedClassOfFlight = ControllerForPaymaster.getTempClassOfFlight();
    private Passenger currentPassenger = ControllerForPaymaster.getCurrentPassenger();
    private ConnectionToDB connectionToDB = ControllerIndexForm.getDb();

    @FXML
    private TextField firstName;
    @FXML
    private TextField lastName;
    @FXML
    private TextField middleName;
    @FXML
    private TextField passportNumber;
    @FXML
    private TextField nationality;
    @FXML
    private DatePicker birthday;

    @FXML
    private TextField flightNumber;
    @FXML
    private TextField airline;
    @FXML
    private TextField departure;
    @FXML
    private TextField comfortClass;
    @FXML
    private TextField ticketCost;
    @FXML
    private TextField typeDiscount;
    @FXML
    private TextField discount;
    @FXML
    private TextField resultCost;
    @FXML
    private Text mistakeLabel;
    @FXML
    private Button button;
    @FXML
    private AnchorPane pane;

    @FXML
    private void initialize()
    {
        try
        {
            initData(selectedClassOfFlight, currentPassenger);
            for (int i = 0; i < pane.getChildren().size(); i++)
            {
                pane.getChildren().get(i).setFocusTraversable(false);
            }

            button.requestFocus();

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(0);
        }
    }

    private void initData(ClassOfFlight selectedClassOfFlight, Passenger currentPassenger) throws Exception
    {
        if (ControllerForPaymaster.check)
        {
            firstName.setDisable(true);
            lastName.setDisable(true);
            middleName.setDisable(true);
            birthday.setDisable(true);
            nationality.setDisable(true);
            passportNumber.setDisable(true);
        }
        else
        {
            firstName.setDisable(false);
            lastName.setDisable(false);
            middleName.setDisable(false);
            birthday.setDisable(false);
            nationality.setDisable(false);
            passportNumber.setDisable(false);
        }

        firstName.setText(currentPassenger.getFirstName());
        lastName.setText(currentPassenger.getLastName());
        middleName.setText(currentPassenger.getMiddleName());
        birthday.setValue(LocalDate.parse(currentPassenger.getBirthdayForInsertDB(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        nationality.setText(currentPassenger.getNationality());
        passportNumber.setText(currentPassenger.getPassportNumber());

        flightNumber.setText(selectedClassOfFlight.getNumber());
        airline.setText(selectedClassOfFlight.getAirline());
        departure.setText(selectedClassOfFlight.getDepartureForPresentation());
        comfortClass.setText(selectedClassOfFlight.getClassComfort());
        ticketCost.setText(selectedClassOfFlight.getTicketCost() + " $");

        typeDiscount.setText(currentPassenger.getDiscount().getName());
        discount.setText(currentPassenger.getDiscount().getValue() * 100 + " %");
        Double resCost = selectedClassOfFlight.getTicketCost() * (1 - currentPassenger.getDiscount().getValue());
        DecimalFormat formatter = new DecimalFormat("#0.00");
        DecimalFormatSymbols ss = new DecimalFormatSymbols();
        ss.setDecimalSeparator('.');
        formatter.setDecimalFormatSymbols(ss);
        resultCost.setText(Double.parseDouble(formatter.format(resCost))+ " $");
    }

    @FXML
    private void onClickReserve()
    {
        try
        {
            Person person = new Person(
                    firstName.getText(),
                    lastName.getText(),
                    middleName.getText(),
                    birthday.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    passportNumber.getText(),
                    nationality.getText());

            currentPassenger = new Passenger(person, currentPassenger.getDiscount());

            String SQL = "SELECT id, first_name, last_name, middle_name, " +
                         "birthday, passport_number, nationality " +
                         "FROM passenger WHERE passport_number = '" + currentPassenger.getPassportNumber() + "' " +
                         "UNION " +
                         "SELECT id, first_name, last_name, middle_name, birthday, passport_number, nationality " +
                         "FROM employee WHERE passport_number = '" + currentPassenger.getPassportNumber() + "' ";

            ResultSet resultSet = connectionToDB.retriveData(SQL);

            boolean checkOnExist = false;
            long id = -1;

            while (resultSet.next())
            {
                Person per = new Person(
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("middle_name"),
                        resultSet.getString("birthday"),
                        resultSet.getString("passport_number"),
                        resultSet.getString("nationality")
                );

                if (!per.equals(currentPassenger.getPerson()))
                {
                    throw new Person.PassengerException("Номер паспорта уже был использован " +
                                                        "с другими персональными данными");
                }

                checkOnExist = true;
                id = resultSet.getLong("id");
            }

            if (checkOnExist)
            {
                SQL = "INSERT INTO orders(class_of_comfort, flight_id, pass_id, date_time_reserve, id_of_employee) " +
                      "VALUES ((SELECT class.id FROM class JOIN trip ON class.id_of_trip = trip.id " +
                      "AND trip.number = '" + selectedClassOfFlight.getNumber() + "' " +
                      "AND class.type = '" + selectedClassOfFlight.getClassComfort() + "'), " +
                      "(SELECT flight.id FROM flight JOIN trip ON flight.trip_id = trip.id " +
                      "AND trip.number = '" + selectedClassOfFlight.getNumber() + "' " +
                      "AND flight.departure = '" + selectedClassOfFlight.getDeparture() + "'), " + id +
                      ", NOW(), (SELECT id FROM employee WHERE login = '" + ControllerIndexForm.getLoginEmploee() + "'))";
                connectionToDB.executeStatement(SQL);
            }
            else
            {
                SQL = "INSERT INTO passenger(first_name, last_name, middle_name, " +
                      "birthday, passport_number, nationality, discount) " +
                      "VALUES('" + currentPassenger.getFirstName() + "','" + currentPassenger.getLastName() + "', '" +
                      currentPassenger.getMiddleName() + "', '" + currentPassenger.getBirthdayForInsertDB() + "', '" +
                      currentPassenger.getPassportNumber() + "', '" + currentPassenger.getNationality() + "'," +
                      (currentPassenger.getDiscount().getName().equals("Без скидки") ? null : currentPassenger.getDiscount().getCode()) + ")";
                connectionToDB.executeStatement(SQL);

                SQL = "INSERT INTO orders(class_of_comfort, flight_id, pass_id, date_time_reserve, id_of_employee) " +
                      "VALUES ((SELECT class.id FROM class JOIN trip ON class.id_of_trip = trip.id " +
                      "AND trip.number = '" + selectedClassOfFlight.getNumber() + "' " +
                      "AND class.type = '" + selectedClassOfFlight.getClassComfort() + "'), " +
                      "(SELECT flight.id FROM flight JOIN trip ON flight.trip_id = trip.id " +
                      "AND trip.number = '" + selectedClassOfFlight.getNumber() + "' " +
                      "AND flight.departure = '" + selectedClassOfFlight.getDeparture() + "'), " +
                      "(SELECT MAX(id) FROM passenger), NOW(), " +
                      "(SELECT id FROM employee WHERE login = '" + ControllerIndexForm.getLoginEmploee() + "'))";
                connectionToDB.executeStatement(SQL);
            }

            typingDocument();
            ControllerForPaymaster.getDetailStage().close();
        }
        catch (Person.PassengerException  ex)
        {
            mistakeLabel.setText(ex.getMessage());
        }
        catch (MySQLIntegrityConstraintViolationException ex)
        {
            mistakeLabel.setText("Пассажир ранее уже бронировал билет на этот рейс");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void typingDocument()
    {
        try
        {
            File file = new File("temp.pdf");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            PdfWriter writer = new PdfWriter(fileOutputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument, PageSize.A4);
            ImageData im = ImageDataFactory.create("C:\\Users\\Emil\\IdeaProjects\\airport3\\src\\sample\\View\\airReserve.png");
            Image image1  = new Image(im);
            image1.setWidth(200);
            image1.setHeight(80);
            image1.setAutoScale(false);
            image1.setMarginLeft(-40);
            image1.setMarginBottom(-30);

            document.setTopMargin(10);
            document.setLeftMargin(50);
            document.setRightMargin(50);
            document.setBottomMargin(20);
            document.add(image1);

            PdfFont font = PdfFontFactory.createFont("C:\\Users\\Emil\\IdeaProjects\\airport3\\FreeSans.ttf",
                    "Cp1251", true);
            com.itextpdf.layout.element.Text text = new com.itextpdf.layout.element.Text("Регистрационный талон");
            text.setFontColor(com.itextpdf.kernel.color.Color.convertRgbToCmyk(new DeviceRgb(15,80, 180)));
            text.setFontSize(14);
            text.setItalic().setBold();

            Paragraph paragraph = new Paragraph(text);
            paragraph.setTextAlignment(TextAlignment.CENTER);
            paragraph.setFont(font);

            com.itextpdf.layout.element.Text text1 = new com.itextpdf.layout.element.Text("Персональные данные пассажира");
            text1.setFontColor(com.itextpdf.kernel.color.Color.BLACK);
            text1.setFontSize(12);

            com.itextpdf.layout.element.Text text2 = new com.itextpdf.layout.element.Text("Данные о перелете ");
            text2.setFontColor(com.itextpdf.kernel.color.Color.BLACK);
            text2.setFontSize(12);

            Paragraph paragraph1 = new Paragraph(text1);
            paragraph1.setFont(font);

            Paragraph paragraph2 = new Paragraph(text2);
            paragraph2.setFont(font);

            Table table = new Table(5);
            table.setFont(font);
            table.setFontSize(10);
            table.setItalic();

            Cell fname = new Cell();
            fname.add("Имя");
            fname.setTextAlignment(TextAlignment.CENTER);
            fname.setBackgroundColor(com.itextpdf.kernel.color.Color.convertRgbToCmyk(new DeviceRgb(230,235,255)));
            fname.setFont(font);
            table.addCell(fname);

            Cell lname = new Cell();
            lname.add("Фамилия");
            lname.setBackgroundColor(com.itextpdf.kernel.color.Color.convertRgbToCmyk(new DeviceRgb(230,235,255)));
            lname.setTextAlignment(TextAlignment.CENTER);
            lname.setFont(font);
            table.addCell(lname);

            Cell mname = new Cell();
            mname.add("Отчество");
            mname.setTextAlignment(TextAlignment.CENTER);
            mname.setBackgroundColor(com.itextpdf.kernel.color.Color.convertRgbToCmyk(new DeviceRgb(230,235,255)));
            mname.setFont(font);
            table.addCell(mname);

            Cell nat = new Cell();
            nat.add("Гражданство");
            nat.setTextAlignment(TextAlignment.CENTER);
            nat.setBackgroundColor(com.itextpdf.kernel.color.Color.convertRgbToCmyk(new DeviceRgb(230,235,255)));
            nat.setFont(font);
            table.addCell(nat);

            Cell pass = new Cell();
            pass.add("Номер паспорта");
            pass.setTextAlignment(TextAlignment.CENTER);
            pass.setBackgroundColor(com.itextpdf.kernel.color.Color.convertRgbToCmyk(new DeviceRgb(230,235,255)));
            pass.setFont(font);
            table.addCell(pass);

            table.startNewRow();

            table.addCell(currentPassenger.getFirstName()).setTextAlignment(TextAlignment.CENTER);
            table.addCell(currentPassenger.getLastName()).setTextAlignment(TextAlignment.CENTER);
            table.addCell(currentPassenger.getMiddleName()).setTextAlignment(TextAlignment.CENTER);
            table.addCell(currentPassenger.getNationality()).setTextAlignment(TextAlignment.CENTER);
            table.addCell(currentPassenger.getPassportNumber()).setTextAlignment(TextAlignment.CENTER);

            Table table1 = new Table(2);
            table1.setFontSize(10);
            table1.setItalic();
            table1.setFont(font);

            table1.addCell("Номер рейса");
            table1.addCell(selectedClassOfFlight.getNumber()).setTextAlignment(TextAlignment.CENTER);
            table1.startNewRow();

            table1.addCell("Авиакомпания-перевозчик");
            table1.addCell(selectedClassOfFlight.getAirline()).setTextAlignment(TextAlignment.CENTER);
            table1.startNewRow();

            table1.addCell("Уровень удобства полета");
            table1.addCell(selectedClassOfFlight.getClassComfort()).setTextAlignment(TextAlignment.CENTER);
            table1.startNewRow();

            table1.addCell("Пункт отправления");
            table1.addCell(selectedClassOfFlight.getDepartureFrom()).setTextAlignment(TextAlignment.CENTER);
            table1.startNewRow();

            table1.addCell("Пункт прибытия");
            table1.addCell(selectedClassOfFlight.getArrivalIn()).setTextAlignment(TextAlignment.CENTER);
            table1.startNewRow();

            table1.addCell("Дата вылета");
            table1.addCell(selectedClassOfFlight.getDepartureForPresentation().split(" ")[1]).setTextAlignment(TextAlignment.CENTER);
            table1.startNewRow();

            table1.addCell("Время вылета");
            table1.addCell(selectedClassOfFlight.getDepartureForPresentation().split(" ")[0]).setTextAlignment(TextAlignment.CENTER);
            table1.startNewRow();

            table1.addCell("Дата прибытия");
            table1.addCell(selectedClassOfFlight.getArrivalForPresentation().split(" ")[1]).setTextAlignment(TextAlignment.CENTER);
            table1.startNewRow();

            table1.addCell("Время прибытия");
            table1.addCell(selectedClassOfFlight.getArrivalForPresentation().split(" ")[0]).setTextAlignment(TextAlignment.CENTER);
            table1.startNewRow();

            table1.addCell("Стоимость перелета");
            table1.addCell(selectedClassOfFlight.getTicketCost() + " $").setTextAlignment(TextAlignment.CENTER);

            for (int i = 0; i < table1.getNumberOfRows(); i++)
            {
                for (int j = 0; j < table1.getNumberOfColumns(); j++)
                {
                    if (i % 2 == 0)
                    {
                        table1.getCell(i, j).setBackgroundColor(
                                com.itextpdf.kernel.color.Color.convertRgbToCmyk(new DeviceRgb(230,235,255)));
                    }
                    else
                    {
                        table1.getCell(i, j).setBackgroundColor(
                                com.itextpdf.kernel.color.Color.WHITE);
                    }
                }
            }

            com.itextpdf.layout.element.Text result = new com.itextpdf.layout.element.Text("Дополнительно: ");
            result.setFontSize(12);
            result.setFontColor(com.itextpdf.kernel.color.Color.BLACK);

            Paragraph paragraph3 = new Paragraph(result);
            paragraph3.setFont(font);

            String str = "Привилегия: " + typeDiscount.getText() + "\n" +
                    "Скидка: " + discount.getText() + "\n";
            com.itextpdf.layout.element.Text dop = new com.itextpdf.layout.element.Text(str);
            dop.setFontSize(10);

            Paragraph paragraph4 = new Paragraph(dop);
            paragraph4.setFont(font);
            paragraph4.setPaddingLeft(20);

            com.itextpdf.layout.element.Text cost = new com.itextpdf.layout.element.Text("Итоговая стоимость: "
                    + resultCost.getText());
            cost.setFontSize(14);
            cost.setFontColor(com.itextpdf.kernel.color.Color.convertRgbToCmyk(new DeviceRgb(15,80, 180)));
            cost.setBold().setItalic().setUnderline();

            Paragraph paragraph5 = new Paragraph(cost);
            paragraph5.setFont(font);

            com.itextpdf.layout.element.Text date = new com.itextpdf.layout.element.Text(
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy")));
            date.setFontSize(12);

            Paragraph paragraph6 = new Paragraph(date);
            paragraph6.setFont(font);
            paragraph6.setTextAlignment(TextAlignment.RIGHT);
            paragraph6.setPaddingTop(-42);

            document.add(paragraph);
            document.add(paragraph1);
            document.add(table);
            document.add(paragraph2);
            document.add(table1);
            document.add(paragraph3);
            document.add(paragraph4);
            document.add(paragraph5);
            document.add(paragraph6);
            document.close();

            Desktop.getDesktop().open(file);
            file.deleteOnExit();
        }
        catch (Exception er)
        {
            Alert ob  = new Alert(Alert.AlertType.ERROR);
            ob.setTitle("");
            ob.setHeaderText("Ошибка при открытии файла!");
            ob.setContentText("Попробуйте снова открыть файл.");
            ob.showAndWait();
        }
    }
}
