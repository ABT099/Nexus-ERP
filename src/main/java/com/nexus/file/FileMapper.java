package com.nexus.file;

import com.nexus.project.BasicProjectResponse;
import com.nexus.project.ProjectMapper;
import com.nexus.user.UserMapper;
import org.springframework.stereotype.Component;

@Component
public class FileMapper {

    private final UserMapper userMapper;

    public FileMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public BasicFileResponse toBasicFileResponse(File file) {
        return new BasicFileResponse(
                file.getId(),
                file.getName(),
                file.getType()
        );
    }

    public FileResponse toFileResponse(File file) {
        return new FileResponse(
                file.getId(),
                file.getName(),
                file.getDescription(),
                file.getType(),
                file.getUrl(),
                userMapper.toUserInfo(file.getCreatedBy()),
                file.getProjects().stream().map(
                        project -> new BasicProjectResponse(project.getId(), project.getName())
                ).toList()
        );
    }
}
