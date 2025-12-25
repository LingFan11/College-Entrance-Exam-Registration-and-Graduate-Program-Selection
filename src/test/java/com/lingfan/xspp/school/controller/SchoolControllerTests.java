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
class SchoolControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("学校检索：keyword=东南，返回分页内容")
    void listSchools() throws Exception {
        mockMvc.perform(get("/api/school/list")
                        .param("keyword", "东南")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"))
                .andExpect(jsonPath("$.data.content[0].id").value(1));
    }

    @Test
    @DisplayName("学校详情：id=1")
    void schoolDetail() throws Exception {
        mockMvc.perform(get("/api/school/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("院系列表：返回至少一个学院")
    void departments() throws Exception {
        mockMvc.perform(get("/api/school/1/departments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"))
                .andExpect(jsonPath("$.data[0].id").value(11));
    }

    @Test
    @DisplayName("强势学科：year=2024, topN=5, 分页")
    void disciplineStrength() throws Exception {
        mockMvc.perform(get("/api/school/1/disciplines/strength")
                        .param("year", "2024")
                        .param("topN", "5")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "grade")
                        .param("order", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"))
                .andExpect(jsonPath("$.data[0].year").value(2024));
    }

    @Test
    @DisplayName("专业列表：degree=本科，分页，带院系名称")
    void majors() throws Exception {
        mockMvc.perform(get("/api/school/1/majors")
                        .param("degree", "本科")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "name")
                        .param("order", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"))
                .andExpect(jsonPath("$.data[0].id").exists())
                .andExpect(jsonPath("$.data[0].departmentName").exists());
    }

    @Test
    @DisplayName("招生简章：degree=本科, year=2024 或回退最近年份，分页")
    void brochures() throws Exception {
        mockMvc.perform(get("/api/school/1/brochures")
                        .param("degree", "本科")
                        .param("year", "2024")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"))
                .andExpect(jsonPath("$.data[0].id").value(401));
    }
}
