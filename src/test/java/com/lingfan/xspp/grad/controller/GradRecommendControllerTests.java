package com.lingfan.xspp.grad.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lingfan.xspp.common.GlobalExceptionHandler;
import com.lingfan.xspp.grad.config.GradRecommendProperties;
import com.lingfan.xspp.grad.dto.RecommendRequest;
import com.lingfan.xspp.grad.dto.RecommendResultDTO;
import com.lingfan.xspp.grad.repository.GradSessionRepository;
import com.lingfan.xspp.grad.service.GradRecommendService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = GradRecommendController.class)
@Import(GlobalExceptionHandler.class)
class GradRecommendControllerTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GradRecommendService recommendService;

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
    void list_ok_query() throws Exception {
        Mockito.when(recommendService.recommend(10001L, 0, 20))
                .thenReturn(List.of(new RecommendResultDTO(301L, "张伟", 88.5, "fit", List.of())));

        mockMvc.perform(get("/api/grad/recommend")
                        .param("userId", "10001")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("fit")));
    }

    @Test
    void compute_ok_body() throws Exception {
        RecommendRequest req = new RecommendRequest();
        req.setUserId(10001L);
        req.setPage(0);
        req.setSize(10);

        Mockito.when(recommendService.recommend(10001L, 0, 10))
                .thenReturn(List.of(new RecommendResultDTO(301L, "张伟", 90.0, "fit", List.of())));

        mockMvc.perform(post("/api/grad/recommend/compute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("\"status\":\"ok\"")));
    }

    @Test
    void compute_missing_userId_validation_error() throws Exception {
        mockMvc.perform(post("/api/grad/recommend/compute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"page\":0,\"size\":20}"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("error")));
    }
}
