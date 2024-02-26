package ru.netris.aggregator.models;

import lombok.*;

@Data
public class TokenData {
    private String value;
    private long ttl;
}
