package pl.mariuszk.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;

public class MainViewController {

    @FXML
    private ProgressBar progressBar;
    @FXML
    private Button btnPrevious;
    @FXML
    private Slider volumeSlider;
    @FXML
    private Button btnNext;
    @FXML
    private Button btnStop;
    @FXML
    private Button btnStart;
    @FXML
    private Button btnReset;
    @FXML
    private Label lblSongName;

    private SongController songController;

    public void initialize() {
        songController = new SongController();
        updateCurrentSongTitle();
    }

    @FXML
    void nextMedia(ActionEvent event) {
        throw new UnsupportedOperationException();
    }

    @FXML
    void pauseMedia(ActionEvent event) {
        songController.pauseSong();
    }

    @FXML
    void playMedia(ActionEvent event) {
        songController.playSong();
    }

    @FXML
    void previousMedia(ActionEvent event) {
        throw new UnsupportedOperationException();
    }

    @FXML
    void resetMedia(ActionEvent event) {
        songController.resetSong();
    }

    private void updateCurrentSongTitle() {
        lblSongName.setText(songController.getCurrentSongName());
    }
}
