package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private static Stage stage;
    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        changeScreen("menu.fxml");
        stage.show();
    }

    public static void changeScreen(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Main.class.getResource("/view/" + fxml)
            );
            Scene scene = new Scene(loader.load(), 600, 400);
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
