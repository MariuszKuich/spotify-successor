package pl.mariuszk.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Playlist {

    private String name;
    private List<Integer> filesChecksums;
}
