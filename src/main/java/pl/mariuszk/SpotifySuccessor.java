package pl.mariuszk;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pl.mariuszk.controller.MainViewController;

import java.io.IOException;

public class SpotifySuccessor extends Application {

    private static final String MAIN_VIEW_PATH = "/pl/mariuszk/view/mainView.fxml";
    private static final String TITLE = "Spotify Successor";
    private static final int INITIAL_WIDTH = 1014;
    private static final int INITIAL_HEIGHT = 562;
    private static final int MIN_WIDTH = 1034;
    private static final int MIN_HEIGHT = 602;

    public static MainViewController mainControllerHandle;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(MAIN_VIEW_PATH));
        Parent root = fxmlLoader.load();
        mainControllerHandle = fxmlLoader.getController();
        stage.setTitle(TITLE);
        stage.setScene(new Scene(root, INITIAL_WIDTH, INITIAL_HEIGHT));
        stage.setMinHeight(MIN_HEIGHT);
        stage.setMinWidth(MIN_WIDTH);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}