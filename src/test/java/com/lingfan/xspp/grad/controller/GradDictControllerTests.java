package com.lingfan.xspp.grad.controller;

import com.lingfan.xspp.grad.config.GradRecommendProperties;
import com.lingfan.xspp.grad.entity.GradResearchDirection;
import com.lingfan.xspp.grad.repository.GradResearchDirectionRepository;
import com.lingfan.xspp.grad.repository.GradSessionRepository;
import com.lingfan.xspp.grad.service.DictCacheService;
import com.lingfan.xspp.grad.service.GradInstitutionGroupService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = GradDictController.class)
class GradDictControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GradResearchDirectionRepository dirRepo;

    @MockBean
    private DictCacheService dictCacheService;

    @MockBean
    private GradInstitutionGroupService institutionGroupService;

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
    void list_all_ok() throws Exception {
        GradResearchDirection d1 = new GradResearchDirection();
        d1.setId(101L); d1.setName("计算机科学"); d1.setParentId(null);
        GradResearchDirection d2 = new GradResearchDirection();
        d2.setId(102L); d2.setName("人工智能"); d2.setParentId(101L);
        Mockito.when(dirRepo.findAll()).thenReturn(List.of(d1, d2));

        mockMvc.perform(get("/api/grad/dicts/directions").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("计算机科学")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("人工智能")));
    }

    @Test
    void list_children_by_parent_ok() throws Exception {
        GradResearchDirection d1 = new GradResearchDirection();
        d1.setId(102L); d1.setName("人工智能"); d1.setParentId(101L);
        Mockito.when(dirRepo.findAll()).thenReturn(List.of(d1));

        mockMvc.perform(get("/api/grad/dicts/directions").param("parentId", "101"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("人工智能")));
    }
}
