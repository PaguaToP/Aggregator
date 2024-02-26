package ru.netris.aggregator.dto;

import lombok.Data;

@Data
public class CameraDTO {
    private int id;
    private String urlType;
    private String videoUrl;
    private String value;
    private long ttl;
}
