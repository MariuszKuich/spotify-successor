package pl.mariuszk.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlaylistItem {

    private long checksum;
    private String lastFileName;
    private boolean accessible;
}
