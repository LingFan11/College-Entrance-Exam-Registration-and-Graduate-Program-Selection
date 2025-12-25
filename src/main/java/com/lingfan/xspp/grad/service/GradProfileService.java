package com.lingfan.xspp.grad.service;

import com.lingfan.xspp.grad.entity.GradStudentProfile;

import java.util.Optional;

public interface GradProfileService {
    GradStudentProfile saveOrUpdate(GradStudentProfile profile);
    Optional<GradStudentProfile> getByUserId(Long userId);
}
