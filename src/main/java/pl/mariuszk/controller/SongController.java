package pl.mariuszk.controller;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SongController {

    private static final String SONGS_PATH = "/pl/mariuszk/songs";

    private MediaPlayer mediaPlayer;
    private final List<File> songs;
    private int currentSongIndex = 0;
    private double volumePercent;

    public SongController(double initialVolumePercent) {
        this.volumePercent = initialVolumePercent;
        songs = loadSongs();
        loadMedia();
    }

    private List<File> loadSongs() {
        File songsDictionary = new File(getClass().getResource(SONGS_PATH).getPath());
        File[] songsFiles = songsDictionary.listFiles();
        if (songsFiles == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(songsFiles);
    }

    private void loadMedia() {
        Media media = new Media(songs.get(currentSongIndex).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        updateVolume();
    }

    public void playSong() {
        mediaPlayer.play();
    }

    public void pauseSong() {
        mediaPlayer.pause();
    }

    public void resetSong() {
        mediaPlayer.seek(Duration.ZERO);
    }

    public String getCurrentSongName() {
        String fullFilename = songs.get(currentSongIndex).getName();
        return FilenameUtils.removeExtension(fullFilename);
    }

    public void nextSong() {
        mediaPlayer.dispose();
        incrementCurrentSongIndex();
        loadMedia();
        playSong();
    }

    public void previousSong() {
        mediaPlayer.dispose();
        decrementCurrentSongIndex();
        loadMedia();
        playSong();
    }

    private void incrementCurrentSongIndex() {
        currentSongIndex++;
        if (currentSongIndex == songs.size()) {
            currentSongIndex = 0;
        }
    }

    private void decrementCurrentSongIndex() {
        currentSongIndex--;
        if (currentSongIndex < 0) {
            currentSongIndex = songs.size() - 1;
        }
    }

    public void changeVolume(double volumePercent) {
        this.volumePercent = volumePercent;
        updateVolume();
    }

    private void updateVolume() {
        if (mediaPlayer == null) {
            return;
        }
        mediaPlayer.setVolume(volumePercent * 0.01);
    }

    public double getSongsCurrentSecond() {
        return mediaPlayer.getCurrentTime().toSeconds();
    }

    public double getSongsDuration() {
        return mediaPlayer.getTotalDuration().toSeconds();
    }
}
