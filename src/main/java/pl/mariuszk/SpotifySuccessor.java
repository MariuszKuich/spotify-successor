package pl.mariuszk;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SpotifySuccessor extends Application {

    private static final String MAIN_VIEW_PATH = "/pl/mariuszk/view/mainView.fxml";
    private static final String TITLE = "Spotify Successor";
    private static final int INITIAL_WIDTH = 640;
    private static final int INITIAL_HEIGHT = 480;

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(MAIN_VIEW_PATH));
        stage.setTitle(TITLE);
        stage.setScene(new Scene(root, INITIAL_WIDTH, INITIAL_HEIGHT));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}