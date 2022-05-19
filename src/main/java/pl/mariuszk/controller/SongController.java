package pl.mariuszk.controller;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import static pl.mariuszk.enums.FileType.MP3;
import static pl.mariuszk.util.FileLoader.loadFiles;

public class SongController {

    private static final String DEFAULT_SONGS_FILE_PATH = "/pl/mariuszk/songs";
    private MediaPlayer mediaPlayer;
    private final List<File> songs;
    private int currentSongIndex = 0;
    private double volumePercent;

    public SongController(double initialVolumePercent) throws FileNotFoundException {
        this(initialVolumePercent, DEFAULT_SONGS_FILE_PATH);
    }

    public SongController(double initialVolumePercent, String songsFilePath) throws FileNotFoundException {
        this.volumePercent = initialVolumePercent;
        songs = loadFiles(songsFilePath, MP3.getFileExtension());
        loadMedia();
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
