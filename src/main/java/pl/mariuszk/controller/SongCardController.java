package pl.mariuszk.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.apache.commons.io.FilenameUtils;
import org.kordamp.ikonli.javafx.FontIcon;
import pl.mariuszk.model.Song;

import javax.naming.OperationNotSupportedException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.io.FileUtils.checksumCRC32;

public class SongCardController {

    @FXML
    private Button btnSaveSongCard;
    @FXML
    private Button btnEditSongCard;
    @FXML
    private Button btnPlaySongCard;
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

    public void initialize() {
        emptyStars = new FontIcon[] {starEmpty1, starEmpty2, starEmpty3, starEmpty4, starEmpty5};
        filledStars = new FontIcon[] {starFilled1, starFilled2, starFilled3, starFilled4, starFilled5};
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
        for (int i = 0; i < rating; i++) {
            emptyStars[i].setVisible(false);
            filledStars[i].setVisible(true);
        }
    }

    @FXML
    void editSongCard(ActionEvent event) throws OperationNotSupportedException {
        throw new OperationNotSupportedException();
    }

    @FXML
    void playSong(ActionEvent event) throws OperationNotSupportedException {
        throw new OperationNotSupportedException();
    }

    @FXML
    void saveSongCard(ActionEvent event) throws OperationNotSupportedException {
        throw new OperationNotSupportedException();
    }
}
