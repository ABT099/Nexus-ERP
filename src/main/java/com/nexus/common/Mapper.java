package com.nexus.common;

@FunctionalInterface
public interface Mapper<Source, DTO> {
    DTO map(Source source);
}