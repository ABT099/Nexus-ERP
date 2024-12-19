package com.nexus.utils;

@FunctionalInterface
public interface Mapper<Source, DTO> {
    DTO map(Source source);
}