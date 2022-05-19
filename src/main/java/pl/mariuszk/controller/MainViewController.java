package pl.mariuszk.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import pl.mariuszk.model.SongsDirectory;

import javax.naming.OperationNotSupportedException;
import java.io.FileNotFoundException;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import static pl.mariuszk.util.JsonFileReader.readSavedFilePath;

public class MainViewController {

    private static final Long PROGRESS_COMPLETE = 1L;
    private static final Long TIMER_DELAY_MS = 5L;
    private static final Long TIMER_PERIOD_MS = 1000L;

    @FXML
    private ProgressBar progressBar;
    @FXML
    private Slider volumeSlider;
    @FXML
    private Label lblSongName;
    @FXML
    private Label lblFilePath;

    private SongController songController;
    private Timer timer;

    public void initialize() {
        try {
            songController = initSongsController();
            updateCurrentSongTitle();
            addVolumeSliderListener();
        } catch (Exception e) {
            displayErrorPopup(e.getMessage());
            Platform.exit();
        }
    }

    private SongController initSongsController() throws FileNotFoundException {
        Optional<SongsDirectory> songsDirectory = readSavedFilePath();
        if (songsDirectory.isPresent()) {
            String savedFilePath = songsDirectory.get().getFilePath();
            updateFilePathLabel(savedFilePath);
            return new SongController(volumeSlider.getValue(), savedFilePath);
        }
        return new SongController(volumeSlider.getValue());
    }

    private void updateFilePathLabel(String text) {
        lblFilePath.setText(text);
    }

    private void updateCurrentSongTitle() {
        lblSongName.setText(songController.getCurrentSongName());
    }

    private void addVolumeSliderListener() {
        volumeSlider.valueProperty().addListener((arg0, arg1, arg2) -> songController.changeVolume(volumeSlider.getValue()));
    }

    private void displayErrorPopup(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR, errorMessage);
        alert.showAndWait();
    }

    @FXML
    void nextMedia(ActionEvent event) {
        songController.nextSong();
        scheduleProgressBarUpdating();
        updateCurrentSongTitle();
    }

    @FXML
    void previousMedia(ActionEvent event) {
        songController.previousSong();
        scheduleProgressBarUpdating();
        updateCurrentSongTitle();
    }

    @FXML
    void playMedia(ActionEvent event) {
        songController.playSong();
        scheduleProgressBarUpdating();
    }

    @FXML
    void pauseMedia(ActionEvent event) {
        songController.pauseSong();
        cancelProgressBarUpdating();
    }

    @FXML
    void resetMedia(ActionEvent event) {
        songController.resetSong();
        scheduleProgressBarUpdating();
    }

    @FXML
    void changeFilePath(ActionEvent event) throws OperationNotSupportedException {
        throw new OperationNotSupportedException();
    }

    @FXML
    void reloadFiles(ActionEvent event) throws OperationNotSupportedException {
        throw new OperationNotSupportedException();
    }

    private void scheduleProgressBarUpdating() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(getTimerTask(), TIMER_DELAY_MS, TIMER_PERIOD_MS);
    }

    private TimerTask getTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                double progress = songController.getSongsCurrentSecond() / songController.getSongsDuration();
                progressBar.setProgress(progress);
                if (progress == PROGRESS_COMPLETE) {
                    cancelProgressBarUpdating();
                }
            }
        };
    }

    private void cancelProgressBarUpdating() {
        if (timer == null) {
            return;
        }
        timer.cancel();
    }
}
