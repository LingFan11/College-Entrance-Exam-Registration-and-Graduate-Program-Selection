package com.lingfan.xspp.grad.service.impl;

import com.lingfan.xspp.grad.config.GradRecommendProperties;
import com.lingfan.xspp.grad.dto.RecommendResultDTO;
import com.lingfan.xspp.grad.entity.GradMentor;
import com.lingfan.xspp.grad.entity.GradMentorDirection;
import com.lingfan.xspp.grad.entity.GradMentorRequirement;
import com.lingfan.xspp.grad.entity.GradMentorQuota;
import com.lingfan.xspp.grad.entity.GradMentorTag;
import com.lingfan.xspp.grad.entity.GradMentorTagMap;
import com.lingfan.xspp.grad.entity.GradStudentProfile;
import com.lingfan.xspp.grad.entity.GradResearchDirection;
import com.lingfan.xspp.grad.repository.GradMentorDirectionRepository;
import com.lingfan.xspp.grad.repository.GradMentorRepository;
import com.lingfan.xspp.grad.repository.GradMentorRequirementRepository;
import com.lingfan.xspp.grad.repository.GradMentorQuotaRepository;
import com.lingfan.xspp.grad.repository.GradMentorTagMapRepository;
import com.lingfan.xspp.grad.repository.GradMentorTagRepository;
import com.lingfan.xspp.grad.repository.GradStudentProfileRepository;
import com.lingfan.xspp.grad.repository.GradResearchDirectionRepository;
import com.lingfan.xspp.grad.repository.GradMajorDomainMapRepository;
import com.lingfan.xspp.grad.repository.GradDirectionDomainMapRepository;
import com.lingfan.xspp.grad.repository.GradMentorGroupMapRepository;
import com.lingfan.xspp.grad.entity.GradMentorGroupMap;
import com.lingfan.xspp.grad.service.DictCacheService;
import com.lingfan.xspp.grad.service.GradRecommendService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import com.lingfan.xspp.common.BusinessException;
import com.lingfan.xspp.grad.service.policy.*;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GradRecommendServiceImpl implements GradRecommendService {
    private final GradStudentProfileRepository profileRepo;
    private final GradMentorRepository mentorRepo;
    private final GradMentorRequirementRepository requirementRepo;
    private final GradMentorDirectionRepository mentorDirectionRepo;
    private final GradMentorQuotaRepository quotaRepo;
    private final GradMentorTagRepository tagRepo;
    private final GradMentorTagMapRepository tagMapRepo;
    private final GradResearchDirectionRepository dirRepo;
    private final GradMajorDomainMapRepository majorDomainRepo;
    private final GradDirectionDomainMapRepository dirDomainRepo;
    private final GradMentorGroupMapRepository mentorGroupMapRepo;
    private final DictCacheService dictCache;
    private final GradRecommendProperties props;
    private final ObjectMapper mapper;

    public GradRecommendServiceImpl(GradStudentProfileRepository profileRepo,
                                    GradMentorRepository mentorRepo,
                                    GradMentorRequirementRepository requirementRepo,
                                    GradMentorDirectionRepository mentorDirectionRepo,
                                    GradMentorQuotaRepository quotaRepo,
                                    GradMentorTagRepository tagRepo,
                                    GradMentorTagMapRepository tagMapRepo,
                                    GradResearchDirectionRepository dirRepo,
                                    GradMajorDomainMapRepository majorDomainRepo,
                                    GradDirectionDomainMapRepository dirDomainRepo,
                                    GradMentorGroupMapRepository mentorGroupMapRepo,
                                    DictCacheService dictCache,
                                    GradRecommendProperties props,
                                    ObjectMapper mapper) {
        this.profileRepo = profileRepo;
        this.mentorRepo = mentorRepo;
        this.requirementRepo = requirementRepo;
        this.mentorDirectionRepo = mentorDirectionRepo;
        this.quotaRepo = quotaRepo;
        this.tagRepo = tagRepo;
        this.tagMapRepo = tagMapRepo;
        this.dirRepo = dirRepo;
        this.majorDomainRepo = majorDomainRepo;
        this.dirDomainRepo = dirDomainRepo;
        this.mentorGroupMapRepo = mentorGroupMapRepo;
        this.dictCache = dictCache;
        this.props = props;
        this.mapper = mapper;
    }

    @Override
    public List<RecommendResultDTO> recommend(Long userId, int page, int size) {
        GradStudentProfile profile = profileRepo.findByUserId(userId)
                .orElse(null);
        if (profile == null) {
            return Collections.emptyList();
        }
        // For V1: only mentors from SEU (universityId=1)
        List<GradMentor> mentors = mentorRepo.findByUniversityId(1L);
        // Load mentor requirements into a map
        Map<Long, GradMentorRequirement> reqMap = requirementRepo.findByMentorIdIn(
                mentors.stream().map(GradMentor::getId).collect(Collectors.toList())
        ).stream().collect(Collectors.toMap(GradMentorRequirement::getMentorId, r -> r));
        // Load mentor directions
        Map<Long, List<Long>> mentorDirs = new HashMap<>();
        for (GradMentor m : mentors) {
            List<Long> dirs = mentorDirectionRepo.findByMentorId(m.getId())
                    .stream().map(GradMentorDirection::getDirectionId).collect(Collectors.toList());
            mentorDirs.put(m.getId(), dirs);
        }

        // Load direction parent map for proximity scoring (from cache)
        Map<Long, Long> directionParent = new HashMap<>(dictCache.getDirectionParent());

        // Parse student's Top-N directions safely via Jackson
        String topnJson = profile.getTargetDirectionsTopn();
        Map<Long, Double> topnWeights = new LinkedHashMap<>();
        if (topnJson != null && !topnJson.isBlank()) {
            try {
                List<Map<String, Object>> arr = mapper.readValue(topnJson, new TypeReference<List<Map<String, Object>>>(){});
                for (Map<String, Object> item : arr) {
                    Object idObj = item.get("directionId");
                    Object wObj = item.get("weight");
                    if (idObj != null) {
                        Long did = idObj instanceof Number ? ((Number) idObj).longValue() : Long.parseLong(String.valueOf(idObj));
                        double w = 1.0;
                        if (wObj != null) {
                            w = wObj instanceof Number ? ((Number) wObj).doubleValue() : Double.parseDouble(String.valueOf(wObj));
                        }
                        topnWeights.put(did, w);
                    }
                }
            } catch (Exception ignored) {
                // fallback will apply below
            }
        }
        // cap by Top-N from props
        if (topnWeights.size() > props.getTopN()) {
            topnWeights = topnWeights.entrySet().stream().limit(props.getTopN())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a,b)->a, LinkedHashMap::new));
        }
        if (topnWeights.isEmpty()) {
            // fallback: use preferredDirections array ids if present
            String pref = profile.getPreferredDirections();
            if (pref != null) {
                // naive: extract numbers
                String[] nums = pref.replaceAll("[^0-9,]", "").split(",");
                int c = 0;
                for (String n : nums) {
                    if (n.isEmpty()) continue;
                    try {
                        topnWeights.put(Long.parseLong(n), 1.0);
                        c++;
                        if (c >= props.getTopN()) break;
                    } catch (Exception ignored) {}
                }
            }
        }
        boolean noPref = topnWeights.isEmpty();
        // normalize weights when present
        double sumW = topnWeights.values().stream().mapToDouble(Double::doubleValue).sum();
        if (sumW > 0) {
            final double norm = sumW;
            topnWeights.replaceAll((k, v) -> v / norm);
        }

        // insufficient profile short-circuit
        int missing = 0;
        if (profile.getCurrentMajor() == null || profile.getCurrentMajor().isBlank()) missing++;
        if (profile.getGpa() == null) missing++;
        boolean englishMissing = (profile.getEnglishType() == null || profile.getEnglishType().isBlank() || profile.getEnglishScore() == null);
        if (englishMissing) missing++;
        if (noPref) missing++;
        if (missing >= props.getInsufficientProfileMissingFieldsThreshold()) {
            throw new BusinessException("insufficient_profile", "请补充专业/GPA/英语/目标方向等关键信息后再计算");
        }

        List<RecommendResultDTO> results = new ArrayList<>();
        int currentYear = java.time.Year.now().getValue();
        // Load quotas in batch
        Map<Long, GradMentorQuota> quotaMap = new HashMap<>();
        var qList = quotaRepo.findByMentorIdInAndYear(mentors.stream().map(GradMentor::getId).toList(), currentYear);
        for (var q : qList) quotaMap.put(q.getMentorId(), q);

        // Load mentor tags (optional preference bonus)
        Map<Long, Set<String>> mentorTags = new HashMap<>();
        var allMaps = tagMapRepo.findByMentorIdIn(mentors.stream().map(GradMentor::getId).toList());
        Set<Long> tagIds = allMaps.stream().map(GradMentorTagMap::getTagId).collect(Collectors.toSet());
        Map<Long, String> tagNameById = tagRepo.findAllById(tagIds).stream().collect(Collectors.toMap(GradMentorTag::getId, GradMentorTag::getName));
        for (var m : mentors) mentorTags.put(m.getId(), new HashSet<>());
        for (var m : allMaps) {
            var set = mentorTags.get(m.getMentorId());
            if (set != null) {
                String name = tagNameById.get(m.getTagId());
                if (name != null) set.add(name);
            }
        }

        // Load mentor group mappings for InstitutionGroupPolicy (Requirements 4.4)
        Map<Long, Set<Long>> mentorGroups = new HashMap<>();
        for (var mentor : mentors) mentorGroups.put(mentor.getId(), new HashSet<>());
        List<GradMentorGroupMap> allGroupMaps = mentorGroupMapRepo.findAll();
        for (var gm : allGroupMaps) {
            var set = mentorGroups.get(gm.getMentorId());
            if (set != null) {
                set.add(gm.getGroupId());
            }
        }

        for (GradMentor m : mentors) {
            GradMentorRequirement req = reqMap.get(m.getId());
            List<Long> dirs = mentorDirs.getOrDefault(m.getId(), Collections.emptyList());

            // build context
            var ctx = new PolicyContext(
                    profile,
                    m,
                    req,
                    dirs,
                    topnWeights,
                    directionParent,
                    mentorTags.getOrDefault(m.getId(), Collections.emptySet()),
                    quotaMap.get(m.getId()),
                    props,
                    noPref,
                    dictCache.getDirectionDomain(),
                    dictCache.getMajorDomainMaps()
            );

            // pipeline (keep existing order)
            ScoreAccumulator acc = new ScoreAccumulator(0.0);
            new ThresholdPolicy().apply(ctx, acc);
            // 科目门槛检查 (Requirements 3.1, 3.2, 3.3, 3.4)
            new SubjectThresholdPolicy().apply(ctx, acc);
            // 机构分组过滤 (Requirements 4.4)
            new InstitutionGroupPolicy(mentorGroups.getOrDefault(m.getId(), Set.of())).apply(ctx, acc);
            new DirectionProximityPolicy().apply(ctx, acc);
            new TagBonusPolicy().apply(ctx, acc);
            new QuotaPenaltyPolicy().apply(ctx, acc);
            new DomainPenaltyPolicy().apply(ctx, acc);
            // if unfit then cap score at 30
            if ("unfit".equals(acc.getStatus())) {
                acc.capScore(30.0);
            }
            new MissingInfoPenaltyPolicy().apply(ctx, acc);

            results.add(new RecommendResultDTO(m.getId(), m.getName(), Math.max(0.0, acc.getScore()), acc.getStatus(), new ArrayList<>(acc.getReasons())));
        }
        // optional min score gate
        if (props.isUseMinScoreGate()) {
            double gate = props.getMinScore();
            results = results.stream().filter(r -> r.getScore() != null && r.getScore() >= gate).collect(Collectors.toList());
        }
        if (results.isEmpty()) {
            throw new BusinessException("no_match", "未找到匹配导师，建议完善画像或放宽条件");
        }
        // sort by score desc
        results.sort(Comparator.comparing(RecommendResultDTO::getScore).reversed());
        // paging
        int from = Math.max(page * size, 0);
        int to = Math.min(from + size, results.size());
        if (from >= to) return Collections.emptyList();
        return results.subList(from, to);
    }
}
