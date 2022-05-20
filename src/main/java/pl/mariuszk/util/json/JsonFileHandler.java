package pl.mariuszk.util.json;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Optional;

abstract class JsonFileHandler {
    private static final String SAVED_DIRECTORY_FILE_PATH = "/pl/mariuszk/data/directory.json";

    protected static URL loadDictionaryJsonFileAsResource() throws FileNotFoundException {
        return Optional.ofNullable(JsonFileReader.class.getResource(SAVED_DIRECTORY_FILE_PATH))
                .orElseThrow(() -> new FileNotFoundException("Could not find dictionary.json file"));
    }
}
