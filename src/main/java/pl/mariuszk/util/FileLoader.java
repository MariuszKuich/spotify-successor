package pl.mariuszk.util;

import javafx.fxml.FXMLLoader;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import pl.mariuszk.model.SongCardPaneController;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class FileLoader {

    private static final String SONG_CARD_VIEW_FILE_PATH = "/pl/mariuszk/view/songCardView.fxml";

    public static List<File> loadFiles(String filePath, String extension) throws FileNotFoundException {
        String resourcePath;
        if (Paths.get(filePath).isAbsolute()) {
            resourcePath = filePath;
        }
        else {
            resourcePath = getAbsolutePathOfResource(filePath);
        }
        File dictionary = new File(resourcePath);
        File[] files = dictionary.listFiles(((dir, name) -> name.endsWith("." + extension)));
        if (files == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(files);
    }

    private static String getAbsolutePathOfResource(String filePath) throws FileNotFoundException {
        return Optional.ofNullable(FileLoader.class.getResource(filePath))
                .map(URL::getPath)
                .orElseThrow(() -> new FileNotFoundException("Could not find or access directory " + filePath));
    }

    public static boolean dictionaryContainsAnyFileWithExtension(File directory, String extension) {
        File[] files = directory.listFiles((dir, name) -> name.endsWith("." + extension));
        return ArrayUtils.isNotEmpty(files);
    }

    public static SongCardPaneController loadSongCardPaneAndController() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(FileLoader.class.getResource(SONG_CARD_VIEW_FILE_PATH));

        return SongCardPaneController.builder()
                .songCardPane(fxmlLoader.load())
                .songCardController(fxmlLoader.getController())
                .build();
    }
}
