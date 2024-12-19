package com.nexus.utils;

@FunctionalInterface
public interface MultiMapper<Source> {
    <DTO> DTO mapTo(Source source, Class<DTO> targetType);
}
