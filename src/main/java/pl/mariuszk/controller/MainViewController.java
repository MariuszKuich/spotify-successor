package pl.mariuszk.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import pl.mariuszk.model.Playlist;
import pl.mariuszk.model.PlaylistItem;
import pl.mariuszk.model.Song;
import pl.mariuszk.model.SongCardPaneController;
import pl.mariuszk.model.SongsDirectory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import static pl.mariuszk.enums.FileType.MP3;
import static pl.mariuszk.util.AlertUtil.INCORRECT_PLAYLIST_NAME;
import static pl.mariuszk.util.AlertUtil.NO_MP3_FILES_FOUND;
import static pl.mariuszk.util.AlertUtil.displayErrorPopup;
import static pl.mariuszk.util.AlertUtil.displayWarningPopup;
import static pl.mariuszk.util.ControlsUtil.addTextLimiter;
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
    private static final int GRID_PANE_COLUMNS_COUNT = 3;
    private static final Insets GRID_CELL_MARGIN = new Insets(10);
    private static final int PLAYLIST_NAME_MAX_LENGTH = 26;
    private static final String NOT_AVAILABLE_PREFIX = "[N/A] ";

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
    @FXML
    private Button btnAddPlaylist;
    @FXML
    private TextField inputPlaylistName;
    @FXML
    private Button btnPlaylistCancel;
    @FXML
    private ChoiceBox<Playlist> cbPlaylists;
    @FXML
    private Button btnPlaylistSave;
    @FXML
    private Button btnPlayAll;
    @FXML
    private Button btnPlayPlaylist;
    @FXML
    private Button btnDeletePlaylist;
    @FXML
    private ListView<String> lvPlaylistSongs;

    private SongController songController;
    private PlaylistController playlistController;
    private Timer timer;

    @FXML
    private void initialize() {
        try {
            songController = initSongsController();
            playlistController = new PlaylistController();
            loadSongsCardsToGridPane();
            updateCurrentSongTitle();
            updatePlaylistsChoiceBox();
            configureControlsProperties();
        } catch (Exception e) {
            displayErrorPopup(e.getMessage());
            Platform.exit();
        }
    }

    private SongController initSongsController() throws IOException {
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
        clearGridPane();

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

    private void clearGridPane() {
        songCardsGridPane.getChildren().clear();
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

    private void configureControlsProperties() {
        addVolumeSliderListener();
        addPlaylistChoiceBoxListener();
        setGridPaneWidthProperty();
        addTextLimiter(inputPlaylistName, PLAYLIST_NAME_MAX_LENGTH);
    }

    private void addVolumeSliderListener() {
        volumeSlider.valueProperty().addListener((arg0, arg1, arg2) -> songController.changeVolume(volumeSlider.getValue()));
    }

    private void addPlaylistChoiceBoxListener() {
        cbPlaylists.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue)  -> {
            if (newValue == null) {
                clearPlaylistSongsListView();
                return;
            }

            btnDeletePlaylist.setDisable(false);
            updatePlaylistSongsListView();
        });
    }

    private void setGridPaneWidthProperty() {
        songCardsGridPane.prefWidthProperty().bind(songCardsScrollPane.widthProperty());
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
        updatePlaylistSongsIfPlaylistPicked();
    }

    private void updatePlaylistSongsIfPlaylistPicked() {
        if (cbPlaylists.getSelectionModel().isEmpty()) {
            return;
        }
        updatePlaylistSongsListView();
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

    void playGivenMedia(File songFile) {
        songController.playGivenSong(songFile);
        scheduleProgressBarUpdating();
        updateCurrentSongTitle();
    }

    @FXML
    void addPlaylist(ActionEvent event) {
        toggleVisibilityOfControlsForAddingNewPlaylist(true);
    }

    private void toggleVisibilityOfControlsForAddingNewPlaylist(boolean visible) {
        btnAddPlaylist.setVisible(!visible);
        inputPlaylistName.setVisible(visible);
        btnPlaylistCancel.setVisible(visible);
        btnPlaylistSave.setVisible(visible);
    }

    @FXML
    void savePlaylist(ActionEvent event) {
        String playlistName = inputPlaylistName.getText();

        if (playlistController.playlistNameBlankOrNotUnique(playlistName)) {
            displayWarningPopup(INCORRECT_PLAYLIST_NAME);
            return;
        }

        try {
            playlistController.saveNewPlaylist(playlistName);
        } catch (IOException e) {
            displayErrorPopup(e.getMessage());
        }
        updatePlaylistsChoiceBox();
        finishPlaylistAdding();
    }

    private void updatePlaylistsChoiceBox() {
        cbPlaylists.getItems().clear();
        cbPlaylists.getItems().addAll(playlistController.getPlaylists());
    }

    private void finishPlaylistAdding() {
        inputPlaylistName.clear();
        toggleVisibilityOfControlsForAddingNewPlaylist(false);
    }

    @FXML
    void cancelPlaylistAdding(ActionEvent event) {
        finishPlaylistAdding();
    }

    @FXML
    void deleteSelectedPlaylist(ActionEvent event) {
        try {
            playlistController.removePlaylist(getSelectedPlaylist());
        } catch (IOException e) {
            displayErrorPopup(e.getMessage());
        }
        updatePlaylistsChoiceBox();
        btnDeletePlaylist.setDisable(true);
    }

    private Playlist getSelectedPlaylist() {
        return cbPlaylists.getSelectionModel().getSelectedItem();
    }

    @FXML
    void playAllFromFolder(ActionEvent event) {

    }

    @FXML
    void playFromPlaylist(ActionEvent event) {

    }

    void addSongToPlaylist(File song) {
        if (cbPlaylists.getSelectionModel().isEmpty()) {
            return;
        }

        try {
            playlistController.addSongToPlaylist(getSelectedPlaylist(), song);
            updatePlaylistSongsListView();
        } catch (IOException e) {
            displayErrorPopup(e.getMessage());
        }
    }

    private void updatePlaylistSongsListView() {
        playlistController.verifySongsAvailability(getSelectedPlaylist(), songController.getSongs());
        clearPlaylistSongsListView();

        for (PlaylistItem item : getSelectedPlaylist().getItems()) {
            String label = item.isAccessible() ? item.getLastFileName() : NOT_AVAILABLE_PREFIX.concat(item.getLastFileName());
            lvPlaylistSongs.getItems().add(label);
        }
    }

    private void clearPlaylistSongsListView() {
        lvPlaylistSongs.getItems().clear();
    }
}
