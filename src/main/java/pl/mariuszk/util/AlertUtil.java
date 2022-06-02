package pl.mariuszk.util;

import javafx.scene.control.Alert;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class AlertUtil {

    public static final String NO_MP3_FILES_FOUND = "Selected directory doesn't contain any MP3 file";
    public static final String INCORRECT_PLAYLIST_NAME = "Playlist name not specified or it already exists";

    public static void displayWarningPopup(String warningMessage) {
        Alert alert = new Alert(Alert.AlertType.WARNING, warningMessage);
        alert.show();
    }

    public static void displayErrorPopup(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR, errorMessage);
        alert.showAndWait();
    }
}
