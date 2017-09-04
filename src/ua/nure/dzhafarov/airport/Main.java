package ua.nure.dzhafarov.airport;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import ua.nure.dzhafarov.airport.Control.ControllerIndexForm;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("View/index.fxml"));
        primaryStage.setTitle("Авторизация");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @Override
    public void stop()
    {
        try
        {
            ControllerIndexForm.getDb().destroy();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Оповещение");
            alert.setHeaderText("Прекращение работы системы");
            alert.setContentText("Соединение с базой данных успешно закрыто\nВсе ресурсы приложения освобождены!");
            alert.showAndWait();
        }
        catch (Exception ex)
        {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
