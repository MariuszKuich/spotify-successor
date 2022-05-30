package pl.mariuszk.util;

import javafx.scene.control.Alert;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class AlertUtil {

    public static void displayWarningPopup(String warningMessage) {
        Alert alert = new Alert(Alert.AlertType.WARNING, warningMessage);
        alert.show();
    }

    public static void displayErrorPopup(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR, errorMessage);
        alert.showAndWait();
    }
}
