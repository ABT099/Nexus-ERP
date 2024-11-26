package com.nexus.common;

@FunctionalInterface
public interface MultiMapper<Source> {
    <DTO> DTO mapTo(Source source, Class<DTO> targetType);
}
