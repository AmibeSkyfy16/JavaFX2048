package ch.skyfy.game;

import ch.skyfy.game.ui.GameView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GameApplication extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.setScene(new Scene(new GameView()));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
