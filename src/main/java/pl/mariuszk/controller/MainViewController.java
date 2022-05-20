package pl.mariuszk.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import pl.mariuszk.model.SongsDirectory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import static pl.mariuszk.enums.FileType.MP3;
import static pl.mariuszk.util.FileLoader.dictionaryContainsAnyFileWithExtension;
import static pl.mariuszk.util.json.JsonFileReader.readSavedFilePath;
import static pl.mariuszk.util.json.JsonFileWriter.saveUsersFilePath;

public class MainViewController {

    private static final Long PROGRESS_COMPLETE = 1L;
    private static final Long TIMER_DELAY_MS = 5L;
    private static final Long TIMER_PERIOD_MS = 1000L;
    private static final String DIRECTORY_CHOOSER_LABEL = "Choose a directory with your music";
    private static final String NO_MP3_FILES_FOUND = "Selected directory doesn't contain any MP3 file";

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
    void changeFilePath(ActionEvent event) {
        try {
            File usersDirectory = prepareAndRunDirectoryChooser();
            if (dictionaryContainsAnyFileWithExtension(usersDirectory, MP3.getFileExtension())) {
                String songsAbsolutePath = usersDirectory.getAbsolutePath();
                saveUsersFilePath(new SongsDirectory(songsAbsolutePath));
                updateFilePathLabel(songsAbsolutePath);
                resetPlayback(songsAbsolutePath);
            }
            else {
                displayWarningPopup(NO_MP3_FILES_FOUND);
            }
        } catch (Exception e) {
            displayErrorPopup(e.getMessage());
        }
    }

    private void displayWarningPopup(String warningMessage) {
        Alert alert = new Alert(Alert.AlertType.WARNING, warningMessage);
        alert.show();
    }

    private File prepareAndRunDirectoryChooser() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(DIRECTORY_CHOOSER_LABEL);
        return directoryChooser.showDialog(new Stage());
    }

    @FXML
    void reloadFiles(ActionEvent event) {
        try {
            resetPlayback(lblFilePath.getText());
        } catch (Exception e) {
            displayErrorPopup(e.getMessage());
        }
    }

    private void resetPlayback(String songsFilePath) throws FileNotFoundException {
        songController.pauseSong();
        cancelProgressBarUpdating();
        resetProgressBar();
        songController = new SongController(volumeSlider.getValue(), songsFilePath);
        updateCurrentSongTitle();
    }

    private void resetProgressBar() {
        progressBar.setProgress(0.0);
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
