package com.lingfan.xspp.grad.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lingfan.xspp.common.GlobalExceptionHandler;
import com.lingfan.xspp.grad.config.GradRecommendProperties;
import com.lingfan.xspp.grad.dto.ProfileSaveRequest;
import com.lingfan.xspp.grad.entity.GradStudentProfile;
import com.lingfan.xspp.grad.repository.GradSessionRepository;
import com.lingfan.xspp.grad.service.GradProfileService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = GradProfileController.class)
@Import(GlobalExceptionHandler.class)
class GradProfileControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GradProfileService gradProfileService;

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
    void saveProfile_ok() throws Exception {
        ProfileSaveRequest req = new ProfileSaveRequest();
        req.setUserId(10001L);
        req.setEnglishType("CET6");
        req.setEnglishScore(550);
        GradStudentProfile saved = new GradStudentProfile();
        saved.setId(1L);
        saved.setUserId(10001L);
        Mockito.when(gradProfileService.saveOrUpdate(Mockito.any())).thenReturn(saved);

        mockMvc.perform(post("/api/grad/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("\"status\":\"ok\"")));
    }

    @Test
    void getProfile_notFound_unfit() throws Exception {
        Mockito.when(gradProfileService.getByUserId(999999L)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/grad/profile").param("userId", "999999"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("unfit")));
    }
}
