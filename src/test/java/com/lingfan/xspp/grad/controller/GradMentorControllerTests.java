package com.lingfan.xspp.grad.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lingfan.xspp.common.BusinessException;
import com.lingfan.xspp.common.GlobalExceptionHandler;
import com.lingfan.xspp.grad.config.GradRecommendProperties;
import com.lingfan.xspp.grad.dto.MentorDTO;
import com.lingfan.xspp.grad.dto.MentorSearchRequest;
import com.lingfan.xspp.grad.repository.GradSessionRepository;
import com.lingfan.xspp.grad.service.GradMentorService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = GradMentorController.class)
@Import(GlobalExceptionHandler.class)
class GradMentorControllerTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GradMentorService mentorService;

    @MockBean
    private GradRecommendProperties gradRecommendProperties;

    @MockBean
    private GradSessionRepository gradSessionRepository;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        Mockito.when(gradRecommendProperties.getRateLimitPerIpPerMinute()).thenReturn(100);
        Mockito.when(gradRecommendProperties.getRateLimitPerSessionPerMinute()).thenReturn(100);
    }

    @Test
    void list_ok() throws Exception {
        Mockito.when(mentorService.listMentors(1L, null, null, "AI", 0, 10))
                .thenReturn(List.of(new MentorDTO(301L, "张伟", "教授", "zw@seu.edu.cn", null, null)));

        mockMvc.perform(get("/api/grad/mentors")
                        .param("universityId", "1")
                        .param("keyword", "AI")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("\"status\":\"ok\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("张伟")));
    }

    @Test
    void search_body_ok() throws Exception {
        MentorSearchRequest req = new MentorSearchRequest();
        req.setUniversityId(1L);
        req.setDirectionId(102L);
        req.setKeyword("AI");
        req.setPage(0);
        req.setSize(10);

        Mockito.when(mentorService.listMentors(1L, 102L, null, "AI", 0, 10))
                .thenReturn(List.of(new MentorDTO(301L, "张伟", "教授", "zw@seu.edu.cn", null, null)));

        mockMvc.perform(post("/api/grad/mentors/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("张伟")));
    }

    @Test
    void delete_in_use_business_error() throws Exception {
        Mockito.doThrow(new BusinessException("mentor_in_use", "导师存在引用数据，禁止删除"))
                .when(mentorService).deleteMentor(301L);

        mockMvc.perform(delete("/api/grad/mentors/301"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("mentor_in_use")));
    }

    @Test
    void paging_invalid_error() throws Exception {
        mockMvc.perform(get("/api/grad/mentors")
                        .param("universityId", "1")
                        .param("page", "-1")
                        .param("size", "0"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("error")));
    }
}
