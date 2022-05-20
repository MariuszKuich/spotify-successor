package pl.mariuszk.util.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.mariuszk.model.SongsDirectory;

import java.io.File;
import java.io.IOException;
import java.net.URL;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class JsonFileWriter extends JsonFileHandler {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static void saveUsersFilePath(SongsDirectory songsDirectory) throws IOException {
        URL resource = loadDictionaryJsonFileAsResource();
        mapper.writeValue(new File(resource.getPath()), songsDirectory);
    }
}
