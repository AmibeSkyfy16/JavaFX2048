package ch.skyfy.game;

import ch.skyfy.game.test.UniformGridTestOne;
import ch.skyfy.game.test.UniformGridTestTwo;
import ch.skyfy.game.ui.GameView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GameApplication extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setWidth(800);
        primaryStage.setHeight(480);
        primaryStage.setScene(new Scene(new GameView()));

        // some test for my custom layout (UniformGrid)
//        primaryStage.setScene(new Scene(new UniformGridTestOne()));
//        primaryStage.setScene(new Scene(new UniformGridTestTwo()));

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
