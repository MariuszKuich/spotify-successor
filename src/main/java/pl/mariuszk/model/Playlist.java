package pl.mariuszk.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class Playlist {

    private String name;
    private List<Integer> filesChecksums;

    @Override
    public String toString() {
        return this.name;
    }
}
