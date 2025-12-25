package com.lingfan.xspp.school.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SchoolDictsControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("字典 levels：返回 985/211/双一流/普通本科（有序）")
    void levels() throws Exception {
        mockMvc.perform(get("/api/school/dicts/levels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"))
                .andExpect(jsonPath("$.data[0]").value("985"));
    }

    @Test
    @DisplayName("学科字典：parentId=0 顶层")
    void disciplinesTop() throws Exception {
        mockMvc.perform(get("/api/school/dicts/disciplines").param("parentId", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"))
                .andExpect(jsonPath("$.data[0].id").exists());
    }

    @Test
    @DisplayName("标签：type=school 全量")
    void tags() throws Exception {
        mockMvc.perform(get("/api/school/tags").param("type", "school"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"))
                .andExpect(jsonPath("$.data[0]").exists());
    }
}
