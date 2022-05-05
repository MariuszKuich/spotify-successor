package pl.mariuszk.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;

import java.util.Timer;
import java.util.TimerTask;

public class MainViewController {

    private static final Long PROGRESS_COMPLETE = 1L;
    private static final Long TIMER_DELAY_MS = 0L;
    private static final Long TIMER_PERIOD_MS = 1000L;

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
    private Timer timer;

    public void initialize() {
        songController = new SongController(volumeSlider.getValue());
        updateCurrentSongTitle();
        addVolumeSliderListener();
    }

    private void updateCurrentSongTitle() {
        lblSongName.setText(songController.getCurrentSongName());
    }

    private void addVolumeSliderListener() {
        volumeSlider.valueProperty().addListener((arg0, arg1, arg2) -> songController.changeVolume(volumeSlider.getValue()));
    }

    @FXML
    void nextMedia(ActionEvent event) {
        scheduleProgressBarUpdating();
        songController.nextSong();
        updateCurrentSongTitle();
    }

    @FXML
    void previousMedia(ActionEvent event) {
        scheduleProgressBarUpdating();
        songController.previousSong();
        updateCurrentSongTitle();
    }

    @FXML
    void playMedia(ActionEvent event) {
        scheduleProgressBarUpdating();
        songController.playSong();
    }

    @FXML
    void pauseMedia(ActionEvent event) {
        cancelProgressBarUpdating();
        songController.pauseSong();
    }

    @FXML
    void resetMedia(ActionEvent event) {
        songController.resetSong();
        scheduleProgressBarUpdating();
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
