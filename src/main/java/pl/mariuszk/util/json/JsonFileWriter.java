package pl.mariuszk.util.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.mariuszk.model.Song;
import pl.mariuszk.model.SongsDirectory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import static pl.mariuszk.util.json.JsonFileReader.loadSavedSongsData;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class JsonFileWriter extends JsonFileHandler {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static void saveUsersFilePath(SongsDirectory songsDirectory) throws IOException {
        URL resource = loadDictionaryJsonFileAsResource();
        mapper.writeValue(new File(resource.getPath()), songsDirectory);
    }

    public static void saveSongData(Song newSongData) throws IOException {
        URL resource = loadSongsDataJsonFileAsResource();

        List<Song> songsData = loadSavedSongsData();
        insertNewSongDataIntoExistingData(songsData, newSongData);

        mapper.writeValue(new File(resource.getPath()), songsData);
    }

    private static void insertNewSongDataIntoExistingData(List<Song> songsData, Song newSongData) {
        songsData.remove(newSongData);
        songsData.add(newSongData);
    }
}
