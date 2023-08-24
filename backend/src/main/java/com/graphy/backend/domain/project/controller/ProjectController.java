package com.graphy.backend.domain.project.controller;

import com.graphy.backend.domain.member.domain.Member;
import com.graphy.backend.domain.project.dto.request.CreateProjectRequest;
import com.graphy.backend.domain.project.dto.request.GetProjectPlanRequest;
import com.graphy.backend.domain.project.dto.request.GetProjectsRequest;
import com.graphy.backend.domain.project.dto.request.UpdateProjectRequest;
import com.graphy.backend.domain.project.dto.response.CreateProjectResponse;
import com.graphy.backend.domain.project.dto.response.GetProjectDetailResponse;
import com.graphy.backend.domain.project.dto.response.GetProjectResponse;
import com.graphy.backend.domain.project.dto.response.UpdateProjectResponse;
import com.graphy.backend.domain.project.service.ProjectService;
import com.graphy.backend.domain.auth.util.annotation.CurrentUser;
import com.graphy.backend.global.common.PageRequest;
import com.graphy.backend.global.error.ErrorCode;
import com.graphy.backend.global.error.exception.EmptyResultException;
import com.graphy.backend.global.result.ResultCode;
import com.graphy.backend.global.result.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Tag(name = "ProjectController", description = "프로젝트 관련 API")
@RestController
@RequestMapping("api/v1/projects")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectController {
    private final ProjectService projectService;

    @Operation(summary = "createProject", description = "프로젝트 생성")
    @PostMapping
    public ResponseEntity<ResultResponse> projectAdd(@Validated @RequestBody CreateProjectRequest dto, @CurrentUser Member loginUser) {
        CreateProjectResponse response = projectService.addProject(dto, loginUser);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.PROJECT_CREATE_SUCCESS, response));
    }

    @Operation(summary = "deleteProject", description = "프로젝트 삭제(soft delete)")
    @DeleteMapping("/{project_id}")
    public ResponseEntity<ResultResponse> projectRemove(@PathVariable Long project_id, @CurrentUser Member loginUser) {
        projectService.removeProject(project_id);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.PROJECT_DELETE_SUCCESS));
    }


    @Operation(summary = "updateProject", description = "프로젝트 수정(변경감지)")
    @PutMapping("/{projectId}")
    public ResponseEntity<ResultResponse> projectModify(@PathVariable Long projectId,
                                                        @RequestBody @Validated UpdateProjectRequest dto, @CurrentUser Member loginUser) {
        UpdateProjectResponse result = projectService.modifyProject(projectId, dto);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.PROJECT_UPDATE_SUCCESS, result));
    }


    @Operation(summary = "findProjects", description = "프로젝트 조회 \n\n" + "\t⚠️ sort 주의사항 ⚠️\n\n" +
            "\t\t1. sort는 공백(\"\"), id, createdAt, updatedAt, content, description, projectName 중 하나 입력\n\n" +
            "\t\t2. 오름차순이 기본입니다. 내림차순을 원하실 경우 {정렬기준},desc (ex. \"id,desc\")를 입력해주세요 (콤마 사이 띄어쓰기 X)\n\n" +
            "\t\t3. sort의 default(공백 입력) : createdAt(최신순), 내림차순")

    @GetMapping("/search")
    public ResponseEntity<ResultResponse> projectList(GetProjectsRequest dto, PageRequest pageRequest) {
        Pageable pageable = pageRequest.of();
        List<GetProjectResponse> result = projectService.findProjectList(dto, pageable);
        if (result.size() == 0) throw new EmptyResultException(ErrorCode.PROJECT_DELETED_OR_NOT_EXIST);

        return ResponseEntity.ok(ResultResponse.of(ResultCode.PROJECT_PAGING_GET_SUCCESS, result));
    }

    @Operation(summary = "findProject", description = "프로젝트 상세 조회")
    @GetMapping("/{projectId}")
    public ResponseEntity<ResultResponse> projectDetails(@PathVariable Long projectId) {
        GetProjectDetailResponse result = projectService.findProjectById(projectId);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.PROJECT_GET_SUCCESS, result));
    }

    @Operation(summary = "getProjectPlan", description = "프로젝트 고도화 계획 제안")
    @PostMapping("/plans")
    public ResponseEntity<ResultResponse> projectPlanDetails(final @RequestBody GetProjectPlanRequest getPlanRequest, @CurrentUser Member loginUser) throws ExecutionException, InterruptedException {
        String prompt = projectService.getPrompt(getPlanRequest);
        projectService.checkGptRequestToken(prompt);

        CompletableFuture<String> futureResult =
                projectService.getProjectPlanAsync(prompt).thenApply(result -> {
                    return result;
                });
        String response = futureResult.get();

        return ResponseEntity.ok(ResultResponse.of(ResultCode.PLAN_CREATE_SUCCESS, response));
    }
}