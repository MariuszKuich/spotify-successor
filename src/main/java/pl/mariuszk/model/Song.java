package pl.mariuszk.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Song {

    private long fileChecksumCRC32;
    private String title;
    private String artist;
    private String album;
    private String genre;
    private int rating;
}
