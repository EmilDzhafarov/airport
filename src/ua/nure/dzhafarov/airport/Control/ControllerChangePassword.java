package ua.nure.dzhafarov.airport.Control;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.effect.InnerShadow;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import ua.nure.dzhafarov.airport.ConnectionToDB;

import java.util.regex.Pattern;

/**
 * Created by Emil on 22.10.2016.
 */
public class ControllerChangePassword
{
    private ConnectionToDB connectionToDB = null;

    @FXML
    private PasswordField oldPassword;

    @FXML
    private PasswordField newPassword;

    @FXML
    private PasswordField confirmPassword;

    @FXML
    private Text errorText;

    @FXML
    private void initialize() throws Exception
    {
        connectionToDB = ControllerIndexForm.getDb();
    }

    @FXML
    private void onClickDone()
    {
        InnerShadow effect = new InnerShadow();
        effect.setColor(Color.RED);
        effect.setWidth(15);
        effect.setHeight(15);
        effect.setRadius(5);

        if (!oldPassword.getText().equals(ControllerIndexForm.getPasswordEmploee()))
        {
            oldPassword.setEffect(effect);
            errorText.setText("Неверный текущий пароль");
        }
        else if (newPassword.getText().length() < 8)
        {
            oldPassword.setEffect(null);
            newPassword.setEffect(effect);
            errorText.setText("Слишком короткий пароль");
        }
        else if (!checkPassword(newPassword.getText()))
        {
            oldPassword.setEffect(null);
            newPassword.setEffect(effect);
            errorText.setText("Пароль может содержать:\n1) буквы английского алфавита;\n2) цифры и служебные символы - (!@#$%^&*()_+/).");
        }
        else if (!newPassword.getText().equals(confirmPassword.getText()))
        {
            oldPassword.setEffect(null);
            newPassword.setEffect(null);
            confirmPassword.setEffect(effect);

            errorText.setText("Пароли не совпадают");
        }
        else
        {
            oldPassword.setEffect(null);
            newPassword.setEffect(null);
            confirmPassword.setEffect(null);
            errorText.setText("");

            try
            {
                String SQL = "SET PASSWORD FOR '" + ControllerIndexForm.getLoginEmploee() + "'@'localhost' = PASSWORD('" +
                             confirmPassword.getText() + "');";
                connectionToDB.executeStatement(SQL);

                Alert ob = new Alert(Alert.AlertType.INFORMATION);
                ob.setTitle("");
                ob.setHeaderText("Пароль успешно сменен!");
                ob.setContentText("Для входа в Ваш аккаунт необходимо \nзапустить программу снова.");
                ob.showAndWait();

                System.exit(0);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                errorText.setText("Произошла ошибка при смене пароля.\nПопробуйте снова повторить действие.");
            }
        }
    }

    private boolean checkPassword(String password)
    {
        String regExp = "[0-9a-zA-Z!@#$%^&*()_+|/\\s]+";

        Pattern pattern = Pattern.compile(regExp);

        if (!pattern.matcher(password).matches())
        {
            return false;
        }

        return true;
    }

}
