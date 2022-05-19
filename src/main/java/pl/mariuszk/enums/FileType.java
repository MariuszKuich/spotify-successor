package pl.mariuszk.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum FileType {
    MP3("mp3");

    @Getter
    private final String fileExtension;
}
