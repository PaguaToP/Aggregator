package ru.netris.aggregator.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Data
public class Camera {
    private int id;
    private String sourceDataUrl;
    private String tokenDataUrl;

    @JsonIgnore
    private SourceData sourceData;
    @JsonIgnore
    private TokenData tokenData;
}
