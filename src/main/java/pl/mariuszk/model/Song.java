package pl.mariuszk.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Song {

    @EqualsAndHashCode.Include
    private long fileChecksumCRC32;
    private String title;
    private String artist;
    private String album;
    private String genre;
    private int rating;
}
