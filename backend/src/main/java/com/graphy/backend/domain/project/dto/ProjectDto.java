package com.graphy.backend.domain.project.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class ProjectDto {

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class UpdateProjectRequest {
        private String projectName;
        private String content;
        private String description;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class UpdateProjectResponse {
        private Long projectId;
        private String projectName;
        private String content;
        private String description;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class GetProjectResponse {
        private Long id;
        private String projectName;
        private String description;
        private LocalDateTime createdAt;
        private List<String> techTags;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class GetProjectDetailResponse {
        private Long id;
        private String projectName;
        private String content;
        private String description;
        private LocalDateTime createdAt;
        private List<String> techTags;
    }
}
