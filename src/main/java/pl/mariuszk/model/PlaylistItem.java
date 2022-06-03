package pl.mariuszk.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlaylistItem {

    private long checksum;
    private String lastFileName;
    @JsonIgnore
    private boolean accessible;
}
