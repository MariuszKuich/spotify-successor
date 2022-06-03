package pl.mariuszk.controller;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import pl.mariuszk.model.Playlist;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

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
        Playlist newPlaylist = Playlist.builder().name(playlistName).filesChecksums(Collections.emptyList()).build();
        playlists.add(newPlaylist);
        persistPlaylistsData(playlists);
    }

    public void removePlaylist(Playlist playlist) throws IOException {
        playlists.remove(playlist);
        persistPlaylistsData(playlists);
    }
}
