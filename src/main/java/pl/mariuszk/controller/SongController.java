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

    public SongController() {
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
}
