package pl.mariuszk.util.json;

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
public final class JsonFileReader extends JsonFileHandler {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static Optional<SongsDirectory> readSavedFilePath() throws FileNotFoundException {
        URL resource = loadDictionaryJsonFileAsResource();

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