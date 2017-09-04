package ua.nure.dzhafarov.airport.Control;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;
import ua.nure.dzhafarov.airport.ConnectionToDB;
import ua.nure.dzhafarov.airport.Model.Discount;
import ua.nure.dzhafarov.airport.Model.Employee;
import ua.nure.dzhafarov.airport.Model.Person;
import ua.nure.dzhafarov.airport.View.GetResources;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by Emil on 21.11.2016.
 */
public class ControllerForAdmin
{
    private ConnectionToDB connectionToDB = ControllerIndexForm.getDb();
    private ObservableList<Discount> discounts = FXCollections.observableArrayList();
    private ObservableList<Employee> employees = FXCollections.observableArrayList();
    private static Stage someStage = null;
    static Stage getSomeStage()
    {
        return someStage;
    }
    private static Discount currentDiscount = null;
    public static Discount getCurrentDiscount() {
        return currentDiscount;
    }

    @FXML
    private TextField firstName;
    @FXML
    private TextField lastName;
    @FXML
    private TextField middleName;
    @FXML
    private DatePicker birthday;
    @FXML
    private TextField nationality;
    @FXML
    private TextField passportNumber;
    @FXML
    private TextField identificationCode;
    @FXML
    private ChoiceBox<String> occupation;
    @FXML
    private TextField login;
    @FXML
    private TextField typeDiscount;
    @FXML
    private TextField valueDiscount;
    @FXML
    private TableView<Discount> tableViewDiscounts;
    @FXML
    private TableColumn<Discount, Long> codeDiscountColumn;
    @FXML
    private TableColumn<Discount, Double> valueDiscountColumn;
    @FXML
    private TableColumn<Discount, String> typeDiscountColumn;
    @FXML
    private TableView<Employee> tableViewEmployees;
    @FXML
    private TableColumn<Employee, String> firstNameColumn;
    @FXML
    private TableColumn<Employee, String> lastNameColumn;
    @FXML
    private TableColumn<Employee, String> middleNameColumn;
    @FXML
    private TableColumn<Employee, String> passportNumberColumn;
    @FXML
    private TableColumn<Employee, String> occupationColumn;
    @FXML
    private Label employeeInfo;
    @FXML
    private Label discountInfo;
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private void initialize()
    {
        codeDiscountColumn.setCellValueFactory(new PropertyValueFactory<Discount, Long>("Code"));
        valueDiscountColumn.setCellValueFactory(new PropertyValueFactory<Discount, Double>("Value"));
        typeDiscountColumn.setCellValueFactory(new PropertyValueFactory<Discount, String>("Name"));

        firstNameColumn.setCellValueFactory(new PropertyValueFactory<Employee, String>("FirstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<Employee, String>("LastName"));
        middleNameColumn.setCellValueFactory(new PropertyValueFactory<Employee, String>("MiddleName"));
        passportNumberColumn.setCellValueFactory(new PropertyValueFactory<Employee, String>("PassportNumber"));
        occupationColumn.setCellValueFactory(new PropertyValueFactory<Employee, String>("Occupation"));

        occupation.setItems(FXCollections.observableArrayList("Кассир","Менеджер"));
        occupation.setValue("Кассир");
        birthday.setValue(LocalDate.of(1980,1,1));
        initEmployees();
        initDiscounts();

        tableViewEmployees.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> source, Number oldWidth, Number newWidth)
            {
                TableHeaderRow header = (TableHeaderRow) tableViewEmployees.lookup("TableHeaderRow");
                header.reorderingProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        header.setReordering(false);
                    }
                });
            }
        });

        tableViewDiscounts.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> source, Number oldWidth, Number newWidth)
            {
                TableHeaderRow header = (TableHeaderRow) tableViewDiscounts.lookup("TableHeaderRow");
                header.reorderingProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        header.setReordering(false);
                    }
                });
            }
        });

        tableViewEmployees.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(javafx.scene.input.MouseEvent event) {
                if (event.isSecondaryButtonDown()) {
                    final ContextMenu menu = new ContextMenu();
                    MenuItem delete = new MenuItem("Уволить");
                    delete.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            try
                            {
                                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                alert.setTitle("Подтверждение");
                                alert.setHeaderText("Вы действительно хотите уволить сотрудника?");
                                alert.setContentText("Для подтверждения нажмите \"Уволить\"");
                                alert.getButtonTypes().clear();
                                alert.getButtonTypes().add(new ButtonType("Уволить"));
                                alert.getButtonTypes().add(new ButtonType("Отмена"));
                                alert.showAndWait();

                                if (alert.getResult().getText().equals("Уволить"))
                                {
                                    onClickDeleteEmployee(tableViewEmployees.getSelectionModel().getSelectedItem());
                                }
                                else
                                {
                                    alert.close();
                                }
                            }
                            catch (NullPointerException e) {
                                Alert ob = new Alert(Alert.AlertType.WARNING);
                                ob.setTitle("");
                                ob.setHeaderText("Предупреждение");
                                ob.setContentText("Выберите сотрудника для продолжения.");
                                ob.showAndWait();
                            }
                            catch (Exception r) {
                                r.printStackTrace();
                                Alert ob = new Alert(Alert.AlertType.ERROR);
                                ob.setTitle("");
                                ob.setHeaderText("Непредвиденная ошибка");
                                ob.setContentText("Попробуйте заново или перезапустите программу.");
                                ob.showAndWait();
                            }
                        }
                    });

                    menu.getItems().addAll(delete);
                    tableViewEmployees.setContextMenu(menu);
                }
            }
        });

        tableViewDiscounts.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(javafx.scene.input.MouseEvent event) {
                if (event.isSecondaryButtonDown()) {
                    final ContextMenu menu = new ContextMenu();
                    MenuItem delete = new MenuItem("Удалить");
                    MenuItem change = new MenuItem("Изменить");
                    delete.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            try
                            {
                                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                alert.setTitle("Подтверждение");
                                alert.setHeaderText("Вы действительно хотите удалить скидку?");
                                alert.setContentText("Нажмите \"Удалить\" чтобы удалить");
                                alert.getButtonTypes().clear();
                                alert.getButtonTypes().add(new ButtonType("Удалить"));
                                alert.getButtonTypes().add(new ButtonType("Отмена"));
                                alert.showAndWait();

                                if (alert.getResult().getText().equals("Удалить"))
                                {
                                    onClickDeleteDiscount(tableViewDiscounts.getSelectionModel().getSelectedItem());
                                }
                                else
                                {
                                    alert.close();
                                }
                            }
                            catch (NullPointerException e) {
                                Alert ob = new Alert(Alert.AlertType.WARNING);
                                ob.setTitle("");
                                ob.setHeaderText("Предупреждение");
                                ob.setContentText("Выберите скидку для продолжения.");
                                ob.showAndWait();
                            }
                            catch (Exception r)
                            {
                                r.printStackTrace();
                                Alert ob = new Alert(Alert.AlertType.ERROR);
                                ob.setTitle("");
                                ob.setHeaderText("Непредвиденная ошибка  ");
                                ob.setContentText("Попробуйте заново или перезапустите программу.");
                                ob.showAndWait();
                            }
                        }
                    });

                    change.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            try
                            {
                                onClickChangeDiscount(tableViewDiscounts.getSelectionModel().getSelectedItem());
                            }
                            catch (NullPointerException e) {
                                Alert ob = new Alert(Alert.AlertType.WARNING);
                                ob.setTitle("");
                                ob.setHeaderText("Предупреждение ");
                                ob.setContentText("Выберите скидку для продолжения.");
                                ob.showAndWait();
                            }
                            catch (Exception r)
                            {
                                r.printStackTrace();
                                Alert ob = new Alert(Alert.AlertType.ERROR);
                                ob.setTitle("");
                                ob.setHeaderText("Непредвиденная ошибка   ");
                                ob.setContentText("Попробуйте заново или перезапустите программу.");
                                ob.showAndWait();
                            }
                        }
                    });
                    menu.getItems().addAll(delete, change);
                    tableViewDiscounts.setContextMenu(menu);
                }
            }
        });

        try
        {
            initNationalities();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void initDiscounts()
    {
        try
        {
            discounts.clear();

            String SQL = "SELECT id, type, discount FROM discount";
            ResultSet resultSet = connectionToDB.retriveData(SQL);

            while (resultSet.next())
            {
                Discount discount = new Discount(
                       resultSet.getString("type"),
                       resultSet.getDouble("discount"),
                       resultSet.getLong("id")
                );
                discounts.add(discount);
            }

            tableViewDiscounts.setItems(discounts);

            if (discounts.size() == 0)
            {
                tableViewDiscounts.setVisible(false);
                discountInfo.setText("Не найдено ни одной скидки");
                discountInfo.setStyle("-fx-text-fill: crimson");
            }
            else
            {
                tableViewDiscounts.setVisible(true);
                discountInfo.setText("Активные скидки");
                discountInfo.setStyle("-fx-text-fill: darkblue");
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void initEmployees()
    {
        try
        {
            employees.clear();

            String SQL = "SELECT id, first_name, last_name, middle_name, "
                         + "birthday, nationality, passport_number, identification_code, "
                         + "login, occupation FROM employee";
            ResultSet resultSet = connectionToDB.retriveData(SQL);

            while (resultSet.next())
            {
                Person person = new Person(
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("middle_name"),
                        resultSet.getString("birthday"),
                        resultSet.getString("passport_number"),
                        resultSet.getString("nationality")
                );

                Employee employee = new Employee(
                        person,
                        resultSet.getString("occupation"),
                        resultSet.getLong("identification_code"),
                        resultSet.getString("login")
                );

                employees.add(employee);
            }

            tableViewEmployees.setItems(employees);

            if (employees.size() == 0)
            {
                tableViewEmployees.setVisible(false);
                employeeInfo.setText("Не найдено ни одного сотрудника");
                employeeInfo.setStyle("-fx-text-fill: crimson");
            }
            else
            {
                tableViewEmployees.setVisible(true);
                employeeInfo.setText("Сотрудники аэропорта");
                employeeInfo.setStyle("-fx-text-fill: darkblue");
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @FXML
    private void onClickAddEmployee()
    {
        try
        {
            Person person = new Person(
                    firstName.getText(),
                    lastName.getText(),
                    middleName.getText(),
                    birthday.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    passportNumber.getText(),
                    nationality.getText()
            );

            Long INN = Long.parseLong(identificationCode.getText());

            Employee employee = new Employee(
                    person,
                    occupation.getValue(),
                    INN,
                    login.getText()
            );

            String SQL;

            if (occupation.getValue().equals("Кассир"))
            {
                employee.setLogin("paymaster" + login.getText());

                SQL = "FLUSH PRIVILEGES;";
                connectionToDB.executeStatement(SQL);

                SQL = "CREATE USER paymaster" + login.getText() + "@localhost IDENTIFIED BY '123456';";
                connectionToDB.executeStatement(SQL);

                SQL = " GRANT INSERT,UPDATE,DELETE ON airport2.passenger " +
                      " TO 'paymaster" + login.getText() +"'@'localhost';";
                connectionToDB.executeStatement(SQL);

                SQL = " GRANT INSERT,UPDATE,DELETE ON airport2.orders " +
                      " TO 'paymaster" + login.getText() +"'@'localhost';";
                connectionToDB.executeStatement(SQL);

                SQL = "GRANT SELECT ON airport2.* TO 'paymaster" + login.getText() +"'@'localhost';";
                connectionToDB.executeStatement(SQL);
            }

            if (occupation.getValue().equals("Менеджер"))
            {
                employee.setLogin("manager" + login.getText());
                SQL = "FLUSH PRIVILEGES;";
                connectionToDB.executeStatement(SQL);

                SQL = "CREATE USER manager" + login.getText() + "@localhost IDENTIFIED BY '123456';";
                connectionToDB.executeStatement(SQL);

                SQL = " GRANT INSERT,UPDATE,DELETE ON airport2.trip" +
                      " TO 'manager" + login.getText() +"'@'localhost';";
                connectionToDB.executeStatement(SQL);

                SQL = " GRANT INSERT,UPDATE,DELETE ON airport2.flight" +
                      " TO 'manager" + login.getText() +"'@'localhost';";
                connectionToDB.executeStatement(SQL);

                SQL = " GRANT INSERT,UPDATE,DELETE ON airport2.class" +
                      " TO 'manager" + login.getText() +"'@'localhost';";
                connectionToDB.executeStatement(SQL);

                SQL = "GRANT SELECT ON airport2.* TO 'manager"+ login.getText() +"'@'localhost';";
                connectionToDB.executeStatement(SQL);
            }

            SQL = "INSERT INTO employee(first_name, last_name, middle_name, birthday, passport_number, "
                  + "nationality, identification_code, occupation, login) "
                  + "VALUES ('" + employee.getFirstName() + "', '" + employee.getLastName() + "','"
                  + employee.getMiddleName() + "','" + employee.getBirthdayForInsertDB() + "','"
                  + employee.getPassportNumber() + "','" + employee.getNationality() + "', "
                  + employee.getIdentificationCode() + ", '" + employee.getOccupation() + "', '"
                  + employee.getLogin() + "')";
            connectionToDB.executeStatement(SQL);
            initNationalities();
            flushFields();
            initEmployees();
        }
        catch (Person.PassengerException ex)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Произошла ошибка при добавлении пользователя");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
        catch (NumberFormatException ex)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Произошла ошибка при добавлении пользователя");
            alert.setContentText("ИНН должен содержать только цифры");
            alert.showAndWait();
        }
        catch (SQLException ex)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Произошла ошибка при добавлении пользователя ");
            alert.setContentText("Такой логин уже используется другим пользователем");
            alert.showAndWait();
        }
        catch (Exception er)
        {
            er.printStackTrace();
        }
    }

    @FXML
    private void onClickAddDiscount()
    {
        try
        {
            Double val = Double.parseDouble(valueDiscount.getText());
            Discount discount = new Discount(typeDiscount.getText(), val, 0L);

            String SQL = "INSERT INTO discount(type, discount) "
                         + "VALUES('" + discount.getName() + "', " + discount.getValue() + ")" ;
            connectionToDB.executeStatement(SQL);

            typeDiscount.setText("");
            valueDiscount.setText("");
            initDiscounts();
        }
        catch (NumberFormatException ex)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Произошла ошибка при добавлении скидки");
            alert.setContentText("Убедитесь в правильности ввода значения скидки");
            alert.showAndWait();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void flushFields()
    {
        firstName.setText("");
        lastName.setText("");
        middleName.setText("");
        birthday.setValue(LocalDate.of(1980,1,1));
        nationality.setText("");
        passportNumber.setText("");
        identificationCode.setText("");
        login.setText("");
    }

    private void onClickDeleteEmployee(Employee employee) throws Exception
    {
        String SQL = "DELETE FROM employee WHERE passport_number = '" + employee.getPassportNumber() + "'";
        connectionToDB.executeStatement(SQL);

        SQL = "DROP USER '" + employee.getLogin() + "'@'localhost'";
        connectionToDB.executeStatement(SQL);

        initEmployees();
    }

    private void onClickDeleteDiscount(Discount discount) throws Exception
    {
        String SQL = "DELETE FROM discount WHERE id = " + discount.getCode();
        connectionToDB.executeStatement(SQL);
        initDiscounts();
    }

    @FXML
    private void onClickChangePassword() throws Exception
    {
        someStage = new Stage();
        someStage.setScene(new Scene(FXMLLoader.load(GetResources.class.getResource("changePassword.fxml"))));
        someStage.setResizable(false);
        someStage.setTitle("Смена пароля администратора");
        someStage.initModality(Modality.WINDOW_MODAL);
        someStage.initOwner(anchorPane.getScene().getWindow());
        someStage.showAndWait();
    }

    @FXML
    private void onClickShowDiagramPaymaster()
    {
        try
        {
            ArrayList<String> passports = new ArrayList<>();
            ArrayList<Long> counts = new ArrayList<>();

            String SQL = "SELECT IFNULL(COUNT(orders.id), 0) AS order_count, passport_number "
                         + "FROM employee LEFT OUTER JOIN orders "
                         + "ON orders.id_of_employee = employee.id "
                         + "WHERE occupation = 'Кассир' "
                         + "GROUP BY passport_number "
                         + "ORDER BY IFNULL(COUNT(orders.id), 0)";

            ResultSet resultSet = connectionToDB.retriveData(SQL);

            while (resultSet.next())
            {
                passports.add(resultSet.getString("passport_number"));
                counts.add(resultSet.getLong("order_count"));
            }

            final CategoryAxis xAxis = new CategoryAxis();
            final NumberAxis yAxis = new NumberAxis();
            xAxis.setLabel("Кассиры");
            yAxis.setLabel("Количество обработанных заказов");
            final LineChart<String, Number> lineChart = new LineChart<String, Number>(xAxis, yAxis);
            XYChart.Series<String, Number> seria = new XYChart.Series<>();
            seria.setName("Выполненные заказы");
            Scene scene = new Scene(lineChart, 900, 550);

            for (int i = 0; i < counts.size(); i++)
            {
                SQL = "SELECT first_name, last_name FROM employee WHERE passport_number = '" + passports.get(i) + "'";
                ResultSet resSet = connectionToDB.retriveData(SQL);
                resSet.first();

                seria.getData().add(new XYChart.Data<>(resSet.getString("first_name") + " " + resSet.getString("last_name"),
                        counts.get(i)));
            }

            lineChart.getData().add(seria);

            lineChart.setLegendSide(Side.BOTTOM);
            someStage = new Stage();
            someStage.initModality(Modality.WINDOW_MODAL);
            someStage.initOwner(anchorPane.getScene().getWindow());
            someStage.setTitle("Диаграмма продуктивности кассиров");
            someStage.setResizable(false);
            someStage.setScene(scene);
            someStage.show();


        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void onClickChangeDiscount(Discount discount) throws Exception
    {
        currentDiscount = discount;

        someStage = new Stage();
        someStage.setScene(new Scene(FXMLLoader.load(GetResources.class.getResource("updateDiscount.fxml"))));
        someStage.setResizable(false);
        someStage.setTitle("Редактирование скидки");
        someStage.initModality(Modality.WINDOW_MODAL);
        someStage.initOwner(anchorPane.getScene().getWindow());
        someStage.showAndWait();

        initDiscounts();
    }

    private void initNationalities() throws  Exception
    {
        ResultSet resultSet = connectionToDB.retriveData("SELECT DISTINCT nationality FROM employee");
        LinkedList<String> list = new LinkedList<>();
        list.add("");

        while (resultSet.next())
        {
            list.add(resultSet.getString(1));
        }

        TextFields.bindAutoCompletion(nationality, list);
    }
}
