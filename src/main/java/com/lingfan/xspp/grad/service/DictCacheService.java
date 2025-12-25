package com.lingfan.xspp.grad.service;

import com.lingfan.xspp.grad.entity.GradResearchDirection;
import com.lingfan.xspp.grad.entity.GradDirectionDomainMap;
import com.lingfan.xspp.grad.entity.GradMajorDomainMap;
import com.lingfan.xspp.grad.repository.GradResearchDirectionRepository;
import com.lingfan.xspp.grad.repository.GradDirectionDomainMapRepository;
import com.lingfan.xspp.grad.repository.GradMajorDomainMapRepository;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.*;

@Service
public class DictCacheService {
    private final GradResearchDirectionRepository dirRepo;
    private final GradDirectionDomainMapRepository dirDomainRepo;
    private final GradMajorDomainMapRepository majorDomainRepo;

    private Map<Long, Long> directionParent = new HashMap<>();
    private Map<Long, String> directionDomain = new HashMap<>();
    private List<GradMajorDomainMap> majorDomainMaps = new ArrayList<>();

    public DictCacheService(GradResearchDirectionRepository dirRepo,
                            GradDirectionDomainMapRepository dirDomainRepo,
                            GradMajorDomainMapRepository majorDomainRepo) {
        this.dirRepo = dirRepo;
        this.dirDomainRepo = dirDomainRepo;
        this.majorDomainRepo = majorDomainRepo;
    }

    @PostConstruct
    public synchronized void load() {
        // directions
        directionParent = new HashMap<>();
        for (GradResearchDirection d : dirRepo.findAll()) {
            directionParent.put(d.getId(), d.getParentId());
        }
        // direction->domain
        directionDomain = new HashMap<>();
        for (GradDirectionDomainMap dm : dirDomainRepo.findAll()) {
            directionDomain.put(dm.getDirectionId(), dm.getDomainCode());
        }
        // major-domain maps
        majorDomainMaps = new ArrayList<>(majorDomainRepo.findAll());
    }

    public synchronized void refresh() { load(); }

    public Map<Long, Long> getDirectionParent() { return directionParent; }

    public Map<Long, String> getDirectionDomain() { return directionDomain; }

    public List<GradMajorDomainMap> getMajorDomainMaps() { return majorDomainMaps; }
}
