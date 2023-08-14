package com.graphy.backend.domain.comment.controller;

import com.graphy.backend.domain.comment.domain.Comment;
import com.graphy.backend.domain.comment.dto.request.CreateCommentRequest;
import com.graphy.backend.domain.comment.dto.request.UpdateCommentRequest;
import com.graphy.backend.domain.comment.dto.response.GetReplyListResponse;
import com.graphy.backend.domain.comment.service.CommentService;
import com.graphy.backend.global.auth.jwt.TokenProvider;
import com.graphy.backend.global.auth.redis.repository.RefreshTokenRepository;
import com.graphy.backend.test.MockApiTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
@ExtendWith(RestDocumentationExtension.class)
class CommentControllerTest extends MockApiTest {

    @Autowired
    private WebApplicationContext context;
    @MockBean
    private CommentService commentService;
    @MockBean
    private TokenProvider tokenProvider;

    @MockBean
    private RefreshTokenRepository refreshTokenRepository;


    @BeforeEach
    public void setup(RestDocumentationContextProvider provider) {
        this.mvc = buildMockMvc(context, provider);
    }

    @Test
    @DisplayName("댓글 생성 API 테스트")
    void createCommentTest() throws Exception {
        // given
        CreateCommentRequest dto = new CreateCommentRequest("test", 1L, null);

        // when
        String body = objectMapper.writeValueAsString(dto);
        when(commentService.addComment(dto)).thenReturn(any());
        mvc.perform(post("/api/v1/comments")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(document("create-comment", preprocessResponse(prettyPrint())));
    }

    @Test
    @DisplayName("댓글 수정 API 테스트")
    void updateCommentTest() throws Exception {
        // given
        Long commentId = 1L;

        String updatedContent = "수정된 내용";

        UpdateCommentRequest commentRequest = new UpdateCommentRequest(updatedContent);

        given(commentService.modifyComment(commentId, commentRequest)).willReturn(commentId);

        // when
        String body = objectMapper.writeValueAsString(commentRequest);
        ResultActions resultActions = mvc.perform(put("/api/v1/comments/{commentId}", 1L).content(body).contentType(MediaType.APPLICATION_JSON));


        // then
        resultActions.andExpect((status().isOk()));
    }

    @Test
    @DisplayName("댓글 삭제 API 테스트")
    void deleteCommentTest() throws Exception {
        // given
        Comment comment = Comment.builder().id(1L).content("TEST").build();


        // when
        ResultActions resultActions = mvc.perform(delete("/api/v1/comments/{commentId}", 1L)
                .contentType(MediaType.APPLICATION_JSON));


        // then
        resultActions.andExpect((status().isOk()))
                .andDo(print())
                .andDo(document("comment-delete",
                        preprocessResponse(prettyPrint()))
                );
    }
}