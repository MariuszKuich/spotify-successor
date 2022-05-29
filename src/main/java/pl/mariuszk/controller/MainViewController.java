package pl.mariuszk.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import pl.mariuszk.model.Song;
import pl.mariuszk.model.SongCardPaneController;
import pl.mariuszk.model.SongsDirectory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import static pl.mariuszk.enums.FileType.MP3;
import static pl.mariuszk.util.FileLoader.dictionaryContainsAnyFileWithExtension;
import static pl.mariuszk.util.FileLoader.loadSongCardPaneAndController;
import static pl.mariuszk.util.json.JsonFileReader.loadSavedSongsData;
import static pl.mariuszk.util.json.JsonFileReader.readSavedFilePath;
import static pl.mariuszk.util.json.JsonFileWriter.saveUsersFilePath;

public class MainViewController {

    private static final Long PROGRESS_COMPLETE = 1L;
    private static final Long TIMER_DELAY_MS = 5L;
    private static final Long TIMER_PERIOD_MS = 1000L;
    private static final String DIRECTORY_CHOOSER_LABEL = "Choose a directory with your music";
    private static final String NO_MP3_FILES_FOUND = "Selected directory doesn't contain any MP3 file";
    private static final int GRID_PANE_COLUMNS_COUNT = 3;
    private static final Insets GRID_CELL_MARGIN = new Insets(10);

    @FXML
    private ProgressBar progressBar;
    @FXML
    private Slider volumeSlider;
    @FXML
    private Label lblSongName;
    @FXML
    private Label lblFilePath;
    @FXML
    private Button btnReloadFiles;
    @FXML
    private Button btnChangeFilePath;
    @FXML
    private GridPane songCardsGridPane;
    @FXML
    private ScrollPane songCardsScrollPane;

    private SongController songController;
    private Timer timer;

    public void initialize() {
        try {
            songController = initSongsController();
            loadSongsCardsToGridPane();
            updateCurrentSongTitle();
            addVolumeSliderListener();
            setGridPaneWidthProperty();
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
        btnReloadFiles.setDisable(true);
        return new SongController(volumeSlider.getValue());
    }

    private void loadSongsCardsToGridPane() throws IOException {
        int colNum = 0;
        int rowNum = 0;
        List<Song> savedSongsData = loadSavedSongsData();
        for (File song : songController.getSongs()) {
            SongCardPaneController paneController = loadSongCardPaneAndController();
            paneController.getSongCardController().setCardFields(song, savedSongsData);

            if (colNum == GRID_PANE_COLUMNS_COUNT) {
                colNum = 0;
                rowNum++;
            }

            songCardsGridPane.add(paneController.getSongCardPane(), colNum++, rowNum);
            adjustGridPropertiesForPane(paneController.getSongCardPane());
        }
    }

    private void adjustGridPropertiesForPane(AnchorPane songCardPane) {
        GridPane.setMargin(songCardPane, GRID_CELL_MARGIN);
        GridPane.setHalignment(songCardPane, HPos.CENTER);
        GridPane.setValignment(songCardPane, VPos.CENTER);
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

    private void setGridPaneWidthProperty() {
        songCardsGridPane.prefWidthProperty().bind(songCardsScrollPane.widthProperty());
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
        btnChangeFilePath.setDisable(true);
        try {
            File usersDirectory = prepareAndRunDirectoryChooser();

            if (userClosedDialogWindow(usersDirectory)) {
                return;
            }

            if (dictionaryContainsAnyFileWithExtension(usersDirectory, MP3.getFileExtension())) {
                String songsAbsolutePath = usersDirectory.getAbsolutePath();
                saveUsersFilePath(new SongsDirectory(songsAbsolutePath));
                updateFilePathLabel(songsAbsolutePath);
                resetPlayback(songsAbsolutePath);
                btnReloadFiles.setDisable(false);
            } else {
                displayWarningPopup(NO_MP3_FILES_FOUND);
            }
        } catch (Exception e) {
            displayErrorPopup(e.getMessage());
        } finally {
            btnChangeFilePath.setDisable(false);
        }
    }

    private boolean userClosedDialogWindow(File usersDirectory) {
        return usersDirectory == null;
    }

    private File prepareAndRunDirectoryChooser() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(DIRECTORY_CHOOSER_LABEL);
        return directoryChooser.showDialog(new Stage());
    }

    private void displayWarningPopup(String warningMessage) {
        Alert alert = new Alert(Alert.AlertType.WARNING, warningMessage);
        alert.show();
    }

    @FXML
    void reloadFiles(ActionEvent event) {
        try {
            resetPlayback(lblFilePath.getText());
        } catch (Exception e) {
            displayErrorPopup(e.getMessage());
        }
    }

    private void resetPlayback(String songsFilePath) throws IOException {
        songController.pauseSong();
        cancelProgressBarUpdating();
        resetProgressBar();
        songController = new SongController(volumeSlider.getValue(), songsFilePath);
        loadSongsCardsToGridPane();
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
