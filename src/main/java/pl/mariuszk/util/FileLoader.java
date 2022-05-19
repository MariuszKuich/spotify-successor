package pl.mariuszk.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class FileLoader {

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
}
