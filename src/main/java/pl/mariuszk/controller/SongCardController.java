package pl.mariuszk.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.apache.commons.io.FilenameUtils;
import org.kordamp.ikonli.javafx.FontIcon;
import pl.mariuszk.model.Song;

import javax.naming.OperationNotSupportedException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.io.FileUtils.checksumCRC32;

public class SongCardController {

    private static final int MAX_TEXT_FIELD_LENGTH = 35;
    private static final String STAR_CSS_CLASS = "star-generic";
    private static final int STARS_COUNT = 5;

    @FXML
    private Button btnSaveSongCard;
    @FXML
    private Button btnEditSongCard;
    @FXML
    private Button btnPlaySongCard;
    @FXML
    private Button btnDiscardChanges;
    @FXML
    private Label lblSongTitle;
    @FXML
    private TextField inputTitle;
    @FXML
    private TextField inputArtist;
    @FXML
    private TextField inputAlbum;
    @FXML
    private TextField inputGenre;
    @FXML
    private FontIcon starEmpty1;
    @FXML
    private FontIcon starEmpty2;
    @FXML
    private FontIcon starEmpty3;
    @FXML
    private FontIcon starEmpty4;
    @FXML
    private FontIcon starEmpty5;
    @FXML
    private FontIcon starFilled1;
    @FXML
    private FontIcon starFilled2;
    @FXML
    private FontIcon starFilled3;
    @FXML
    private FontIcon starFilled4;
    @FXML
    private FontIcon starFilled5;

    private FontIcon[] emptyStars;
    private FontIcon[] filledStars;
    private Song cardValuesSnapshot;

    public void initialize() {
        emptyStars = new FontIcon[] {starEmpty1, starEmpty2, starEmpty3, starEmpty4, starEmpty5};
        filledStars = new FontIcon[] {starFilled1, starFilled2, starFilled3, starFilled4, starFilled5};
        configureTextFieldsMaxLength();
        configureEmptyStarsClickEvents();
    }

    private void configureTextFieldsMaxLength() {
        addTextLimiter(inputTitle);
        addTextLimiter(inputArtist);
        addTextLimiter(inputAlbum);
        addTextLimiter(inputGenre);
    }

    private void addTextLimiter(TextField textField) {
        textField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue.length() > MAX_TEXT_FIELD_LENGTH) {
                textField.setText(oldValue);
            }
        });
    }

    private void configureEmptyStarsClickEvents() {
        starEmpty1.setOnMouseClicked((event) -> {
            showStars(starFilled1);
            hideStars(starFilled2, starFilled3, starFilled4, starFilled5);
        });
        starEmpty2.setOnMouseClicked((event) -> {
            showStars(starFilled1, starFilled2);
            hideStars(starFilled3, starFilled4, starFilled5);
        });
        starEmpty3.setOnMouseClicked((event) -> {
            showStars(starFilled1, starFilled2, starFilled3);
            hideStars(starFilled4, starFilled5);
        });
        starEmpty4.setOnMouseClicked((event) -> {
            showStars(starFilled1, starFilled2, starFilled3, starFilled4);
            hideStars(starFilled5);
        });
        starEmpty5.setOnMouseClicked((event) ->
                showStars(starFilled1, starFilled2, starFilled3, starFilled4, starFilled5));
    }

    private void showStars(FontIcon... stars) {
        for (FontIcon star : stars) {
            star.setVisible(true);
        }
    }

    private void hideStars(FontIcon... stars) {
        for (FontIcon star : stars) {
            star.setVisible(false);
        }
    }

    public void setCardFields(File song, List<Song> savedSongsData) throws IOException {
        lblSongTitle.setText(FilenameUtils.removeExtension(song.getName()));
        btnSaveSongCard.setDisable(true);

        Optional<Song> savedSongData = getSongDataIfPresent(song, savedSongsData);
        savedSongData.ifPresent(this::setFieldsBasedOnSavedData);
    }

    private Optional<Song> getSongDataIfPresent(File song, List<Song> savedSongsData) throws IOException {
        long songChecksum = checksumCRC32(song);
        return savedSongsData.stream()
                .filter(songData -> songData.getFileChecksumCRC32() == songChecksum)
                .findFirst();
    }

    private void setFieldsBasedOnSavedData(Song songData) {
        inputTitle.setText(songData.getTitle());
        inputArtist.setText(songData.getArtist());
        inputAlbum.setText(songData.getAlbum());
        inputGenre.setText(songData.getGenre());
        setStars(songData.getRating());
    }

    private void setStars(int rating) {
        for (int i = 0; i < STARS_COUNT; i++) {
            if (i < rating) {
                filledStars[i].setVisible(true);
                continue;
            }
            filledStars[i].setVisible(false);
        }
    }

    @FXML
    void editSongCard(ActionEvent event) {
        takeFieldsSnapshot();
        toggleButtonsIntoEditMode(true);
        toggleTextFieldsEditability(true);
        toggleEmptyStarsDisabledProp(false);
    }

    private void takeFieldsSnapshot() {
        cardValuesSnapshot = Song.builder()
                .title(inputTitle.getText())
                .artist(inputArtist.getText())
                .album(inputAlbum.getText())
                .genre(inputGenre.getText())
                .rating(getCurrentlySetRating())
                .build();
    }

    private int getCurrentlySetRating() {
        return (int) Arrays.stream(filledStars)
                .filter(Node::isVisible)
                .count();
    }

    private void toggleButtonsIntoEditMode(boolean editMode) {
        btnSaveSongCard.setDisable(!editMode);
        btnEditSongCard.setDisable(editMode);
        btnDiscardChanges.setVisible(editMode);
    }

    private void toggleTextFieldsEditability(boolean editable) {
        inputTitle.setEditable(editable);
        inputArtist.setEditable(editable);
        inputAlbum.setEditable(editable);
        inputGenre.setEditable(editable);
    }

    private void toggleEmptyStarsDisabledProp(boolean disabled) {
        for (FontIcon emptyStar : emptyStars) {
            emptyStar.setDisable(disabled);
            if (disabled) {
                emptyStar.getStyleClass().clear();
            } else {
                emptyStar.getStyleClass().add(STAR_CSS_CLASS);
            }
        }
    }

    @FXML
    void playSong(ActionEvent event) throws OperationNotSupportedException {
        throw new OperationNotSupportedException();
    }

    @FXML
    void saveSongCard(ActionEvent event) throws OperationNotSupportedException {
        throw new OperationNotSupportedException();
    }

    @FXML
    void discardChanges(ActionEvent event) {
        toggleButtonsIntoEditMode(false);
        toggleTextFieldsEditability(false);
        toggleEmptyStarsDisabledProp(true);
        setFieldsBasedOnSavedData(cardValuesSnapshot);
    }
}
