package com.lingfan.xspp.school.service;

import com.lingfan.xspp.school.dto.BrochureDTO;
import com.lingfan.xspp.school.dto.DepartmentDTO;
import com.lingfan.xspp.school.dto.DisciplineStrengthDTO;
import com.lingfan.xspp.school.dto.MajorDTO;
import com.lingfan.xspp.school.dto.SchoolBriefDTO;
import com.lingfan.xspp.school.dto.SchoolDetailDTO;
import com.lingfan.xspp.school.entity.SchAdmissionBrochure;
import com.lingfan.xspp.school.entity.SchDepartment;
import com.lingfan.xspp.school.entity.SchDiscipline;
import com.lingfan.xspp.school.entity.SchMajor;
import com.lingfan.xspp.school.entity.SchUnivDisciplineStrength;
import com.lingfan.xspp.school.entity.SchUnivMajor;
import com.lingfan.xspp.school.entity.SchUniversity;
import com.lingfan.xspp.school.repo.SchAdmissionBrochureRepository;
import com.lingfan.xspp.school.repo.SchDepartmentRepository;
import com.lingfan.xspp.school.repo.SchDisciplineRepository;
import com.lingfan.xspp.school.repo.SchMajorRepository;
import com.lingfan.xspp.school.repo.SchUnivDisciplineStrengthRepository;
import com.lingfan.xspp.school.repo.SchUnivMajorRepository;
import com.lingfan.xspp.school.repo.SchUniversityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SchoolService {

    private final SchUniversityRepository universityRepository;
    private final SchDepartmentRepository departmentRepository;
    private final SchUnivDisciplineStrengthRepository strengthRepository;
    private final SchDisciplineRepository disciplineRepository;
    private final SchUnivMajorRepository univMajorRepository;
    private final SchMajorRepository majorRepository;
    private final SchAdmissionBrochureRepository brochureRepository;
    private final com.lingfan.xspp.school.repo.SchTagRepository tagRepository;

    public SchoolService(SchUniversityRepository universityRepository,
                         SchDepartmentRepository departmentRepository,
                         SchUnivDisciplineStrengthRepository strengthRepository,
                         SchDisciplineRepository disciplineRepository,
                         SchUnivMajorRepository univMajorRepository,
                         SchMajorRepository majorRepository,
                         SchAdmissionBrochureRepository brochureRepository,
                         com.lingfan.xspp.school.repo.SchTagRepository tagRepository) {
        this.universityRepository = universityRepository;
        this.departmentRepository = departmentRepository;
        this.strengthRepository = strengthRepository;
        this.disciplineRepository = disciplineRepository;
        this.univMajorRepository = univMajorRepository;
        this.majorRepository = majorRepository;
        this.brochureRepository = brochureRepository;
        this.tagRepository = tagRepository;
    }

    public Page<SchoolBriefDTO> search(String keyword, String level, String province, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<SchUniversity> spec = (root, query, cb) -> {
            var predicate = cb.conjunction();
            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.trim() + "%";
                predicate = cb.and(predicate, cb.or(
                        cb.like(root.get("name"), like),
                        cb.like(root.get("alias"), like)
                ));
            }
            if (level != null && !level.isBlank()) {
                predicate = cb.and(predicate, cb.like(root.get("level"), "%" + level.trim() + "%"));
            }
            if (province != null && !province.isBlank()) {
                predicate = cb.and(predicate, cb.equal(root.get("province"), province.trim()));
            }
            return predicate;
        };
        return universityRepository.findAll(spec, pageable)
                .map(u -> new SchoolBriefDTO(
                        u.getId(), u.getName(), u.getLevel(), u.getProvince(), u.getCity(), u.getWebsite()
                ));
    }

    public Optional<SchoolDetailDTO> getById(Long id) {
        return universityRepository.findById(id).map(u -> {
            SchoolDetailDTO dto = new SchoolDetailDTO();
            dto.setId(u.getId());
            dto.setName(u.getName());
            dto.setAlias(u.getAlias());
            dto.setLevel(u.getLevel());
            dto.setProvince(u.getProvince());
            dto.setCity(u.getCity());
            dto.setWebsite(u.getWebsite());
            dto.setBrief(u.getBrief());
            dto.setFoundedYear(u.getFoundedYear());
            return dto;
        });
    }

    public List<DepartmentDTO> listDepartments(Long universityId) {
        return departmentRepository.findByUniversityId(universityId).stream()
                .map(d -> new DepartmentDTO(d.getId(), d.getName(), d.getBrief()))
                .collect(Collectors.toList());
    }

    public List<DisciplineStrengthDTO> listDisciplineStrength(Long universityId,
                                                              Integer year,
                                                              Integer topN,
                                                              Integer page,
                                                              Integer size,
                                                              String sortBy,
                                                              String order) {
        List<SchUnivDisciplineStrength> list = (year != null)
                ? strengthRepository.findByUniversityIdAndYear(universityId, year)
                : strengthRepository.findByUniversityId(universityId);

        if (list.isEmpty()) return List.of();

        // If year is null, pick latest year by grouping
        if (year == null) {
            int latest = list.stream().map(SchUnivDisciplineStrength::getYear).max(Integer::compareTo).orElse(0);
            list = list.stream().filter(it -> it.getYear() == latest).collect(Collectors.toList());
        }

        // Load discipline names
        Map<Long, String> discNames = disciplineRepository.findByIdIn(
                        list.stream().map(SchUnivDisciplineStrength::getDisciplineId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(SchDiscipline::getId, SchDiscipline::getName));

        List<DisciplineStrengthDTO> dtoList = new ArrayList<>();
        for (SchUnivDisciplineStrength s : list) {
            DisciplineStrengthDTO dto = new DisciplineStrengthDTO();
            dto.setDisciplineId(s.getDisciplineId());
            dto.setDisciplineName(discNames.getOrDefault(s.getDisciplineId(), ""));
            dto.setRankGrade(s.getRankGrade());
            dto.setRankValue(s.getRankValue());
            dto.setSource(s.getSource());
            dto.setYear(s.getYear());
            dtoList.add(dto);
        }

        // Sorting
        Comparator<DisciplineStrengthDTO> cmp;
        if ("rankValue".equalsIgnoreCase(sortBy)) {
            cmp = Comparator.comparing((DisciplineStrengthDTO d) -> d.getRankValue() == null ? Integer.MAX_VALUE : d.getRankValue());
        } else { // default by grade
            cmp = Comparator
                    .comparing((DisciplineStrengthDTO d) -> gradeOrder(d.getRankGrade()))
                    .thenComparing(d -> d.getRankValue() == null ? Integer.MAX_VALUE : d.getRankValue());
        }
        if ("desc".equalsIgnoreCase(order)) {
            cmp = cmp.reversed();
        }
        dtoList.sort(cmp);

        if (topN != null && topN > 0 && dtoList.size() > topN) {
            dtoList = new ArrayList<>(dtoList.subList(0, topN));
        }
        // pagination slice
        if (page != null && size != null && page >= 0 && size > 0) {
            int from = page * size;
            int to = Math.min(from + size, dtoList.size());
            if (from >= dtoList.size()) return List.of();
            return dtoList.subList(from, to);
        }
        return dtoList;
    }

    private int gradeOrder(String grade) {
        if (grade == null) return 999;
        return switch (grade) {
            case "A+" -> 1;
            case "A" -> 2;
            case "A-" -> 3;
            case "B+" -> 4;
            case "B" -> 5;
            case "B-" -> 6;
            default -> 100;
        };
    }

    public List<MajorDTO> listMajors(Long universityId,
                                     String degree,
                                     Long disciplineId,
                                     Integer page,
                                     Integer size,
                                     String sortBy,
                                     String order) {
        List<SchUnivMajor> ums = univMajorRepository.findByUniversityId(universityId);
        if (ums.isEmpty()) return List.of();
        List<Long> majorIds = ums.stream().map(SchUnivMajor::getMajorId).toList();
        List<SchMajor> majors = majorRepository.findByIdIn(majorIds);
        // Filter by degree/discipline
        majors = majors.stream().filter(m -> (degree == null || degree.isBlank() || degree.equals(m.getDegree()))
                && (disciplineId == null || disciplineId.equals(m.getDisciplineId()))).toList();
        if (majors.isEmpty()) return List.of();
        // Map majorId -> departmentId
        Map<Long, Long> majorToDeptId = ums.stream()
                .collect(Collectors.toMap(SchUnivMajor::getMajorId, SchUnivMajor::getDepartmentId, (a, b) -> a));
        // Load department names
        List<Long> deptIds = majorToDeptId.values().stream().filter(java.util.Objects::nonNull).distinct().toList();
        Map<Long, String> deptIdToName = deptIds.isEmpty() ? Map.of() :
                departmentRepository.findAllById(deptIds).stream()
                        .collect(Collectors.toMap(SchDepartment::getId, SchDepartment::getName));

        // map to DTO
        List<MajorDTO> all = majors.stream().map(m -> {
            MajorDTO dto = new MajorDTO();
            dto.setId(m.getId());
            dto.setName(m.getName());
            dto.setDegree(m.getDegree());
            dto.setDisciplineId(m.getDisciplineId());
            dto.setCode(m.getCode());
            Long deptId = majorToDeptId.get(m.getId());
            dto.setDepartmentName(deptId == null ? null : deptIdToName.getOrDefault(deptId, String.valueOf(deptId)));
            // find feature tag from mapping
            ums.stream().filter(um -> um.getMajorId().equals(m.getId())).findFirst()
                    .ifPresent(um -> dto.setFeatureTag(um.getFeatureTag()));
            return dto;
        }).collect(Collectors.toList());

        // sorting
        Comparator<MajorDTO> mcmp;
        if ("code".equalsIgnoreCase(sortBy)) {
            mcmp = Comparator.comparing(m -> m.getCode() == null ? "" : m.getCode(), String.CASE_INSENSITIVE_ORDER);
        } else { // default by name asc
            mcmp = Comparator.comparing(m -> m.getName() == null ? "" : m.getName(), String.CASE_INSENSITIVE_ORDER);
        }
        if ("desc".equalsIgnoreCase(order)) {
            mcmp = mcmp.reversed();
        }
        all.sort(mcmp);

        if (page != null && size != null && page >= 0 && size > 0) {
            int from = page * size;
            int to = Math.min(from + size, all.size());
            if (from >= all.size()) return List.of();
            return all.subList(from, to);
        }
        return all;
    }

    public List<BrochureDTO> listBrochures(Long universityId, String degree, Integer year, Integer page, Integer size) {
        List<SchAdmissionBrochure> list;
        if (degree != null && !degree.isBlank() && year != null) {
            list = brochureRepository.findByUniversityIdAndDegreeAndYear(universityId, degree, year);
        } else if (degree != null && !degree.isBlank()) {
            list = brochureRepository.findByUniversityIdAndDegree(universityId, degree);
        } else {
            list = brochureRepository.findByUniversityId(universityId);
        }

        // year fallback to latest when not specified but multiple years present
        if ((year == null || year <= 0) && !list.isEmpty()) {
            int latest = list.stream().map(SchAdmissionBrochure::getYear).max(Integer::compareTo).orElse(0);
            list = list.stream().filter(b -> b.getYear() != null && b.getYear() == latest).collect(Collectors.toList());
        }

        List<BrochureDTO> all = list.stream().map(b -> {
            BrochureDTO dto = new BrochureDTO();
            dto.setId(b.getId());
            dto.setDegree(b.getDegree());
            dto.setYear(b.getYear());
            dto.setTitle(b.getTitle());
            dto.setLink(b.getLink());
            return dto;
        }).collect(Collectors.toList());

        if (page != null && size != null && page >= 0 && size > 0) {
            int from = page * size;
            int to = Math.min(from + size, all.size());
            if (from >= all.size()) return List.of();
            return all.subList(from, to);
        }
        return all;
    }

    // =====================
    // Dicts & Tags
    // =====================

    public List<String> listLevels() {
        // 固定字典，顺序按常见权重
        Set<String> levels = new LinkedHashSet<>();
        levels.add("985");
        levels.add("211");
        levels.add("双一流");
        levels.add("普通本科");
        return new java.util.ArrayList<>(levels);
    }

    public List<com.lingfan.xspp.school.dto.DisciplineDTO> listDisciplines(Long parentId) {
        List<SchDiscipline> list;
        if (parentId == null) {
            list = disciplineRepository.findAll();
        } else if (parentId == 0) {
            list = disciplineRepository.findByParentId(null);
        } else {
            list = disciplineRepository.findByParentId(parentId);
        }
        return list.stream()
                .map(d -> new com.lingfan.xspp.school.dto.DisciplineDTO(d.getId(), d.getName(), d.getParentId()))
                .collect(java.util.stream.Collectors.toList());
    }

    public List<String> listTags(String type, String keyword) {
        List<com.lingfan.xspp.school.entity.SchTag> list;
        if (keyword != null && !keyword.isBlank()) {
            list = tagRepository.findByTypeAndNameContainingIgnoreCase(type, keyword.trim());
        } else {
            list = tagRepository.findByType(type);
        }
        return list.stream().map(com.lingfan.xspp.school.entity.SchTag::getName).collect(java.util.stream.Collectors.toList());
    }
}
