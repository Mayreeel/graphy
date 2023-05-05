package com.graphy.backend.domain.project.controller;

import com.graphy.backend.domain.comment.dto.GetCommentWithMaskingDto;
import com.graphy.backend.domain.comment.service.CommentService;
import com.graphy.backend.domain.project.service.ProjectService;
import com.graphy.backend.test.MockApiTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static com.graphy.backend.domain.project.dto.ProjectDto.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
@ExtendWith(RestDocumentationExtension.class)
public class ProjectControllerTest extends MockApiTest {


    @Autowired
    private WebApplicationContext context;
    @MockBean
    ProjectService projectService;

    @MockBean
    CommentService commentService;
    private static String baseUrl = "/api/v1/projects";

    @BeforeEach
    public void setup(RestDocumentationContextProvider provider) {
        this.mvc = buildMockMvc(context, provider);
    }


    @Test
    @DisplayName("프로젝트 조회 시 댓글도 조회된다")
    public void getProjectWithComments() throws Exception {

        //given
        GetCommentWithMaskingDto dto = new GetCommentWithMaskingDto() {
            @Override
            public String getContent() {
                return "testComment";
            }

            @Override
            public Long getCommentId() {
                return null;
            }

            @Override
            public Integer getChildCount() {
                return null;
            }
        };
        List<GetCommentWithMaskingDto> list = new ArrayList<>();
        list.add(dto);

        given(projectService.getProjectById(1L))
                .willReturn(GetProjectDetailResponse.builder()
                        .projectName("project")
                        .commentsList(list).build());



        //then
        ResultActions result = mvc.perform(get(baseUrl + "/{projectId}", 1L));
        result.andExpect(status().isOk());
    }
}
