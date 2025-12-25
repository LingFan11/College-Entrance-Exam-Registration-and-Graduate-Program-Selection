package com.lingfan.xspp.grad.controller;

import com.lingfan.xspp.common.ApiResponse;
import com.lingfan.xspp.grad.dto.MentorDTO;
import com.lingfan.xspp.grad.dto.MentorSearchRequest;
import com.lingfan.xspp.grad.service.GradMentorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/grad/mentors")
@Validated
public class GradMentorController {
    private final GradMentorService mentorService;

    public GradMentorController(GradMentorService mentorService) {
        this.mentorService = mentorService;
    }

    @GetMapping
    public ApiResponse<List<MentorDTO>> list(
            @RequestParam(value = "universityId", required = false) Long universityId,
            @RequestParam(value = "directionId", required = false) Long directionId,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "0") @Min(0) int page,
            @RequestParam(value = "size", defaultValue = "20") @Min(1) int size
    ) {
        String traceId = UUID.randomUUID().toString();
        List<MentorDTO> list = mentorService.listMentors(universityId, directionId, title, keyword, page, size);
        return ApiResponse.ok(list, traceId);
    }

    @PostMapping(path = "/search", consumes = "application/json")
    public ApiResponse<List<MentorDTO>> search(@Valid @RequestBody MentorSearchRequest req) {
        String traceId = UUID.randomUUID().toString();
        List<MentorDTO> list = mentorService.listMentors(req.getUniversityId(), req.getDirectionId(), req.getTitle(), req.getKeyword(), req.getPage(), req.getSize());
        return ApiResponse.ok(list, traceId);
    }

    @GetMapping("/{id}")
    public ApiResponse<MentorDTO> get(@PathVariable("id") Long id) {
        String traceId = UUID.randomUUID().toString();
        Optional<MentorDTO> opt = mentorService.getMentor(id);
        return opt.map(dto -> ApiResponse.ok(dto, traceId))
                .orElseGet(() -> ApiResponse.unfit("mentor_not_found", traceId));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable("id") Long id) {
        String traceId = UUID.randomUUID().toString();
        mentorService.deleteMentor(id);
        return ApiResponse.ok(null, traceId);
    }
}
