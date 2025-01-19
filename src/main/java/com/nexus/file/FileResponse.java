package com.nexus.file;

import com.nexus.project.BasicProjectResponse;
import com.nexus.user.UserInfoDTO;

import java.util.List;

public record FileResponse(
    Integer id,
    String name,
    String description,
    String type,
    String url,
    UserInfoDTO uploader,
    List<BasicProjectResponse> projects
) { }
