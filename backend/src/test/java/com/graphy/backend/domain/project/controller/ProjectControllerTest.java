package com.graphy.backend.domain.project.controller;

import com.graphy.backend.domain.project.domain.Project;
import com.graphy.backend.domain.project.domain.ProjectTags;
import com.graphy.backend.domain.project.dto.ProjectDto;
import com.graphy.backend.domain.project.repository.ProjectRepository;
import com.graphy.backend.domain.project.service.ProjectService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.graphy.backend.domain.project.dto.ProjectDto.CreateProjectRequest;
import static com.graphy.backend.domain.project.dto.ProjectDto.UpdateProjectRequest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ProjectControllerTest {
    @Autowired private ProjectRepository projectRepository;
    @Autowired private ProjectService projectService;

    @Test
    @DisplayName("프로젝트 soft delete 테스트")
    public void 삭제된_프로젝트는_조회X() throws Exception {
        //given
        Project project1 = Project.builder().projectName("projectTest1").content("content1").build();
        Project project2 = Project.builder().projectName("projectTest2").content("content2").build();
        Project project3 = Project.builder().projectName("projectTest3").content("content3").build();

        Project savedProject1 = projectRepository.save(project1);
        Project savedProject2 = projectRepository.save(project2);
        Project savedProject3 = projectRepository.save(project3);

        //when
        projectService.deleteProject(savedProject1.getId()); // savedProject1의 is_deleted를 true로 변경

        //then
        Optional<Project> result1 = projectRepository.findById(savedProject1.getId());
        Optional<Project> result2 = projectRepository.findById(savedProject2.getId());
        Optional<Project> result3 = projectRepository.findById(savedProject3.getId());

        assertThat(result1).isEmpty(); // is_deleted == true인 데이터는 조회가 안 되므로 True
        assertThat(result2).isNotEmpty(); // is_deleted == false인 데이터는 조회되므로 에러가 발생
        assertThat(result3).isNotEmpty(); // 위와 같은 이유로 True
    }

    @Test
    @DisplayName("프로젝트 수정 테스트")
    @Transactional
    public void 프로젝트를_수정하면_수정내용이_적용된다() throws Exception {
        //given
        Project project = Project.builder().projectName("TestProject")
                .description("Test")
                .content("Content").projectTags(new ProjectTags()).build();
        Project savedProject = projectRepository.save(project);

        UpdateProjectRequest dto = UpdateProjectRequest.builder()
                .projectName("updateName")
                .content("updateContent")
                .description("updateDesc").techTags(new ArrayList<>()).build();


        //when
        projectService.updateProject(savedProject.getId(), dto);
        Project findProject = projectRepository.findById(savedProject.getId()).get();


        /**
         * then
         * 저장한 Project 인스턴스인 savedProject와
         * 수정한 Project 인스턴스인 findProject를 비교
         */
        assertThat(findProject.getProjectName()).isEqualTo(savedProject.getProjectName());
        assertThat(findProject.getContent()).isEqualTo(savedProject.getContent());
        assertThat(findProject.getDescription()).isEqualTo(savedProject.getDescription());
    }

    @Test
    @DisplayName("프로젝트 생성 테스트")
    @Transactional
    void 프로젝트_생성후_조회() throws Exception {

        //given
        List<String> techTags = new ArrayList<String>(List.of("Spring boot", "React", "TypeScript"));

        CreateProjectRequest createRequest = CreateProjectRequest.builder().projectName("테스트 프로젝트")
                .content("내용").description("한 줄 소개").techTags(techTags).build();

         Pageable pageable = PageRequest.of(0,1);

        //when
        projectService.createProject(createRequest);
        ProjectDto.GetProjectResponse result = projectService.getProjectByName("테스트 프로젝트", pageable).get(0);

        //then
        assertThat(result.getProjectName()).isEqualTo(createRequest.getProjectName());
    }

    @Test
    @DisplayName("프로젝트 내용 검색 테스트")
    @Transactional
    void 프로젝트_생성후_내용검색() throws Exception {

        //given
        List<String> techTags = new ArrayList<String>(List.of("Spring boot", "React", "TypeScript"));

        CreateProjectRequest createRequest = CreateProjectRequest.builder().projectName("내용 검색 제목")
                .content("고양이").description("한 줄 소개").techTags(techTags).build();

        Pageable pageable = PageRequest.of(0,1);

        //when
        projectService.createProject(createRequest);
        ProjectDto.GetProjectResponse result = projectService.getProjectByContent("고양이", pageable).get(0);

        //then
        assertThat(result.getProjectName()).isEqualTo(createRequest.getProjectName());
    }
}