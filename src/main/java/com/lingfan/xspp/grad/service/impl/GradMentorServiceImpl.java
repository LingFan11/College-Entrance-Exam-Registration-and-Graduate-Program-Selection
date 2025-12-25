package com.lingfan.xspp.grad.service.impl;

import com.lingfan.xspp.grad.dto.MentorDTO;
import com.lingfan.xspp.grad.entity.GradMentor;
import com.lingfan.xspp.grad.repository.GradMentorDirectionRepository;
import com.lingfan.xspp.grad.repository.GradMentorRepository;
import com.lingfan.xspp.grad.repository.GradMentorTagMapRepository;
import com.lingfan.xspp.grad.repository.GradMentorQuotaRepository;
import com.lingfan.xspp.grad.repository.GradMentorPublicationRepository;
import com.lingfan.xspp.common.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GradMentorServiceImpl implements com.lingfan.xspp.grad.service.GradMentorService {
    private final GradMentorRepository mentorRepo;
    private final GradMentorDirectionRepository mentorDirRepo;
    private final GradMentorTagMapRepository tagMapRepo;
    private final GradMentorQuotaRepository quotaRepo;
    private final GradMentorPublicationRepository pubRepo;

    public GradMentorServiceImpl(GradMentorRepository mentorRepo,
                                 GradMentorDirectionRepository mentorDirRepo,
                                 GradMentorTagMapRepository tagMapRepo,
                                 GradMentorQuotaRepository quotaRepo,
                                 GradMentorPublicationRepository pubRepo) {
        this.mentorRepo = mentorRepo;
        this.mentorDirRepo = mentorDirRepo;
        this.tagMapRepo = tagMapRepo;
        this.quotaRepo = quotaRepo;
        this.pubRepo = pubRepo;
    }

    @Override
    public List<MentorDTO> listMentors(Long universityId, Long directionId, String title, String keyword, int page, int size) {
        // DB-level filtering + paging via Specification
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1));
        final String kw = keyword == null ? null : keyword.toLowerCase(Locale.ROOT);
        final String t = title == null ? null : title.toLowerCase(Locale.ROOT);

        // pre-filter mentorIds by direction if provided
        final java.util.Set<Long> dirMentorIds;
        if (directionId != null) {
            dirMentorIds = mentorDirRepo.findByDirectionId(directionId).stream()
                    .map(md -> md.getMentorId()).collect(java.util.stream.Collectors.toSet());
            if (dirMentorIds.isEmpty()) {
                return new ArrayList<>();
            }
        } else {
            dirMentorIds = null;
        }

        Specification<GradMentor> spec = (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
            if (universityId != null) {
                predicates.add(cb.equal(root.get("universityId"), universityId));
            }
            if (t != null) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + t + "%"));
            }
            if (kw != null) {
                var nameLike = cb.like(cb.lower(root.get("name")), "%" + kw + "%");
                var homepageLike = cb.like(cb.lower(root.get("homepage")), "%" + kw + "%");
                var briefLike = cb.like(cb.lower(root.get("brief")), "%" + kw + "%");
                predicates.add(cb.or(nameLike, homepageLike, briefLike));
            }
            if (dirMentorIds != null) {
                predicates.add(root.get("id").in(dirMentorIds));
            }
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        Page<GradMentor> pageData = mentorRepo.findAll(spec, pageable);
        return pageData.getContent().stream()
                .map(m -> new MentorDTO(m.getId(), m.getName(), m.getTitle(), m.getEmail(), m.getHomepage(), m.getBrief()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<MentorDTO> getMentor(Long mentorId) {
        return mentorRepo.findById(mentorId)
                .map(m -> new MentorDTO(m.getId(), m.getName(), m.getTitle(), m.getEmail(), m.getHomepage(), m.getBrief()));
    }

    @Override
    public void deleteMentor(Long mentorId) {
        // Reference checks (no FKs): direction map, tag map, quota, publication
        long dirCnt = mentorDirRepo.countByMentorId(mentorId);
        long tagCnt = tagMapRepo.countByMentorId(mentorId);
        long quotaCnt = quotaRepo.countByMentorId(mentorId);
        long pubCnt = pubRepo.countByMentorId(mentorId);
        if (dirCnt > 0 || tagCnt > 0 || quotaCnt > 0 || pubCnt > 0) {
            throw new BusinessException("mentor_in_use", "导师存在引用数据，禁止删除，请先清理映射/标签/名额/论文");
        }
        if (!mentorRepo.existsById(mentorId)) {
            throw new BusinessException("mentor_not_found", "导师不存在");
        }
        mentorRepo.deleteById(mentorId);
    }
}
