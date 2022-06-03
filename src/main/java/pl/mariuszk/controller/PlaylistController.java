package pl.mariuszk.controller;

import lombok.Getter;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import pl.mariuszk.model.Playlist;
import pl.mariuszk.model.PlaylistItem;
import pl.mariuszk.util.FileLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.io.FileUtils.checksumCRC32;
import static pl.mariuszk.util.json.JsonFileReader.loadSavedPlaylists;
import static pl.mariuszk.util.json.JsonFileWriter.persistPlaylistsData;

public class PlaylistController {

    @Getter
    private final List<Playlist> playlists;

    public PlaylistController() throws FileNotFoundException {
        playlists = loadSavedPlaylists();
    }

    public boolean playlistNameBlankOrNotUnique(String name) {
        return StringUtils.isBlank(name) || playlists.stream()
                .map(Playlist::getName)
                .anyMatch(item -> item.equals(name.trim()));
    }

    public void saveNewPlaylist(String playlistName) throws IOException {
        Playlist newPlaylist = Playlist.builder().name(playlistName).items(new ArrayList<>()).build();
        playlists.add(newPlaylist);
        persistPlaylistsData(playlists);
    }

    public void removePlaylist(Playlist playlist) throws IOException {
        playlists.remove(playlist);
        persistPlaylistsData(playlists);
    }

    public void addSongToPlaylist(Playlist playlist, File song) throws IOException {
        PlaylistItem newItem = PlaylistItem.builder()
                .checksum(checksumCRC32(song))
                .lastFileName(FilenameUtils.removeExtension(song.getName()))
                .accessible(true)
                .build();

        playlist.getItems().add(newItem);
        persistPlaylistsData(playlists);
    }

    public void verifySongsAvailability(Playlist playlist, List<File> availableSongsFiles) {
        List<Long> filesChecksums = availableSongsFiles.stream()
                .map(FileLoader::checksumCRC32DefaultZero)
                .collect(Collectors.toList());

        playlist.getItems().forEach(item -> item.setAccessible(filesChecksums.contains(item.getChecksum())));
    }
}
