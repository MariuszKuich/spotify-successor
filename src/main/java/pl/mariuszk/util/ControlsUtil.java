package pl.mariuszk.util;

import javafx.scene.control.TextField;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class ControlsUtil {

    public static void addTextLimiter(TextField textField, int maxTextFieldLength) {
        textField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue.length() > maxTextFieldLength) {
                textField.setText(oldValue);
            }
        });
    }
}
