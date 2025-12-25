package com.lingfan.xspp.gaokao.service;

import com.lingfan.xspp.gaokao.domain.entity.Major;
import com.lingfan.xspp.gaokao.domain.entity.University;
import com.lingfan.xspp.gaokao.domain.entity.UniversityMajorPlan;
import com.lingfan.xspp.gaokao.domain.repo.MajorRepository;
import com.lingfan.xspp.gaokao.domain.repo.UniversityMajorPlanRepository;
import com.lingfan.xspp.gaokao.domain.repo.UniversityRepository;
import com.lingfan.xspp.gaokao.dto.DictionaryResponse;
import com.lingfan.xspp.gaokao.service.impl.DictionaryServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

public class DictionaryServiceImplTest {

    private UniversityRepository universityRepository;
    private MajorRepository majorRepository;
    private UniversityMajorPlanRepository planRepository;

    private DictionaryService service;

    @BeforeEach
    void setUp() {
        universityRepository = Mockito.mock(UniversityRepository.class);
        majorRepository = Mockito.mock(MajorRepository.class);
        planRepository = Mockito.mock(UniversityMajorPlanRepository.class);
        service = new DictionaryServiceImpl(universityRepository, majorRepository, planRepository);
    }

    @Test
    void getDictionaries_shouldContainSeedValues() {
        University u = new University();
        u.setId(1L); u.setName("东南大学"); u.setCity("南京"); u.setTier("双一流");
        Major m = new Major(); m.setId(1L); m.setName("计算机科学与技术"); m.setCategory("工学");
        UniversityMajorPlan p1 = new UniversityMajorPlan();
        p1.setProvince("江苏"); p1.setSubjectType("physics"); p1.setBatch("本科批");

        Mockito.when(universityRepository.findAll()).thenReturn(List.of(u));
        Mockito.when(majorRepository.findAll()).thenReturn(List.of(m));
        Mockito.when(planRepository.findAll()).thenReturn(List.of(p1));

        DictionaryResponse resp = service.getDictionaries();
        Assertions.assertTrue(resp.getProvinces().contains("江苏"));
        Assertions.assertTrue(resp.getSubjectTypes().contains("physics"));
        Assertions.assertTrue(resp.getBatches().contains("本科批"));
        Assertions.assertTrue(resp.getTiers().contains("双一流"));
        Assertions.assertTrue(resp.getCities().contains("南京"));
        Assertions.assertTrue(resp.getMajorCategories().contains("工学"));
    }
}
