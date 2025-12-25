package com.lingfan.xspp.grad.service;

import com.lingfan.xspp.grad.dto.MentorDTO;

import java.util.List;
import java.util.Optional;

public interface GradMentorService {
    List<MentorDTO> listMentors(Long universityId, Long directionId, String title, String keyword, int page, int size);
    Optional<MentorDTO> getMentor(Long mentorId);
    void deleteMentor(Long mentorId);
}
