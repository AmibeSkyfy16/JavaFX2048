package ch.skyfy.game.ui.utils;

import ch.skyfy.game.GameApplication;
import javafx.fxml.FXMLLoader;

import java.io.IOException;

public class FXMLUtils {
    public static <T, R> void loadFXML(Class<T> tClass, String fxmlPath, R view){
        try {
            new FXMLLoader(tClass.getResource(fxmlPath)){{
                setController(view);
                setRoot(view);
            }}.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
