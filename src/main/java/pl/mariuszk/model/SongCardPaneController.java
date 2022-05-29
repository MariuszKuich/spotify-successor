package pl.mariuszk.model;

import javafx.scene.layout.AnchorPane;
import lombok.Builder;
import lombok.Data;
import pl.mariuszk.controller.SongCardController;

@Data
@Builder
public class SongCardPaneController {

    private AnchorPane songCardPane;
    private SongCardController songCardController;
}
