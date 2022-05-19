package pl.mariuszk.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import pl.mariuszk.model.SongsDirectory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JsonFileReader {

    private static final String SAVED_DIRECTORY_FILE_PATH = "/pl/mariuszk/data/directory.json";
    private static final ObjectMapper mapper = new ObjectMapper();

    public static Optional<SongsDirectory> readSavedFilePath() throws FileNotFoundException {
        URL resource = Optional.ofNullable(JsonFileReader.class.getResource(SAVED_DIRECTORY_FILE_PATH))
                .orElseThrow(() -> new FileNotFoundException("Could not find dictionary.json file"));

        try {
            SongsDirectory songsDirectory = mapper.readValue(resource, SongsDirectory.class);

            if (StringUtils.isBlank(songsDirectory.getFilePath())) {
                return Optional.empty();
            }

            return Optional.of(songsDirectory);
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}
