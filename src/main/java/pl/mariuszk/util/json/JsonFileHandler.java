package pl.mariuszk.util.json;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Optional;

abstract class JsonFileHandler {
    private static final String SAVED_DIRECTORY_FILE_PATH = "/pl/mariuszk/data/directory.json";
    private static final String SAVED_SONGS_DATA_FILE_PATH = "/pl/mariuszk/data/songsData.json";
    private static final String SAVED_PLAYLISTS_FILE_PATH = "/pl/mariuszk/data/playlists.json";

    protected static URL loadDictionaryJsonFileAsResource() throws FileNotFoundException {
        return Optional.ofNullable(JsonFileHandler.class.getResource(SAVED_DIRECTORY_FILE_PATH))
                .orElseThrow(() -> new FileNotFoundException("Could not find dictionary.json file"));
    }

    protected static URL loadSongsDataJsonFileAsResource() throws FileNotFoundException {
        return Optional.ofNullable(JsonFileHandler.class.getResource(SAVED_SONGS_DATA_FILE_PATH))
                .orElseThrow(() -> new FileNotFoundException("Could not find songsData.json file"));
    }

    protected static URL loadPlaylistsJsonFileAsAResource() throws FileNotFoundException {
        return Optional.ofNullable(JsonFileHandler.class.getResource(SAVED_PLAYLISTS_FILE_PATH))
                .orElseThrow(() -> new FileNotFoundException("Could not find playlists.json file"));
    }
}
