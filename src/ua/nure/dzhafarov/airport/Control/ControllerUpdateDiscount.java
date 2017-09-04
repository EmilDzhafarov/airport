package ua.nure.dzhafarov.airport.Control;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import ua.nure.dzhafarov.airport.Model.Discount;

/**
 * Created by Emil on 26.11.2016.
 */
public class ControllerUpdateDiscount
{
    private Discount currentDiscount = ControllerForAdmin.getCurrentDiscount();

    @FXML
    private TextField code;
    @FXML
    private TextField type;
    @FXML
    private TextField value;
    @FXML
    private Text mistakeText;

    @FXML
    private void initialize()
    {
        code.setText(currentDiscount.getCode() + "");
        type.setText(currentDiscount.getName());
        value.setText(currentDiscount.getValue() + "");
    }

    @FXML
    private void onClickUpdate()
    {
        try
        {
            Double value = Double.parseDouble(this.value.getText());
            String name = this.type.getText();

            Discount discount = new Discount(name, value, this.currentDiscount.getCode());

            String SQL = "UPDATE discount SET type = '" + discount.getName() + "',"
                         + "discount = " + discount.getValue() + " WHERE id = " + discount.getCode();
            ControllerIndexForm.getDb().executeStatement(SQL);

            ControllerForAdmin.getSomeStage().close();
        }
        catch (NumberFormatException ex)
        {
            mistakeText.setText("Убедитесь в правильности ввода значения скидки");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
