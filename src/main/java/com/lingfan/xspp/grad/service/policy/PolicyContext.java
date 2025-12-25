package com.lingfan.xspp.grad.service.policy;

import com.lingfan.xspp.grad.config.GradRecommendProperties;
import com.lingfan.xspp.grad.entity.GradMentor;
import com.lingfan.xspp.grad.entity.GradMentorQuota;
import com.lingfan.xspp.grad.entity.GradMentorRequirement;
import com.lingfan.xspp.grad.entity.GradStudentProfile;
import com.lingfan.xspp.grad.entity.GradMajorDomainMap;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class PolicyContext {
    private final GradStudentProfile profile;
    private final GradMentor mentor;
    private final GradMentorRequirement requirement;
    private final List<Long> mentorDirections;
    private final Map<Long, Double> targetDirectionWeights; // student Top-N
    private final Map<Long, Long> directionParent; // dir->parent
    private final Set<String> mentorTags;
    private final GradMentorQuota mentorQuota;
    private final GradRecommendProperties props;
    private final boolean noPref; // whether Top-N is empty
    private final Map<Long, String> directionDomain; // dir->domainCode
    private final List<GradMajorDomainMap> majorDomainMaps; // major keyword mapping

    public PolicyContext(GradStudentProfile profile,
                         GradMentor mentor,
                         GradMentorRequirement requirement,
                         List<Long> mentorDirections,
                         Map<Long, Double> targetDirectionWeights,
                         Map<Long, Long> directionParent,
                         Set<String> mentorTags,
                         GradMentorQuota mentorQuota,
                         GradRecommendProperties props,
                         boolean noPref,
                         Map<Long, String> directionDomain,
                         List<GradMajorDomainMap> majorDomainMaps) {
        this.profile = profile;
        this.mentor = mentor;
        this.requirement = requirement;
        this.mentorDirections = mentorDirections;
        this.targetDirectionWeights = targetDirectionWeights;
        this.directionParent = directionParent;
        this.mentorTags = mentorTags;
        this.mentorQuota = mentorQuota;
        this.props = props;
        this.noPref = noPref;
        this.directionDomain = directionDomain;
        this.majorDomainMaps = majorDomainMaps;
    }

    public GradStudentProfile getProfile() { return profile; }
    public GradMentor getMentor() { return mentor; }
    public GradMentorRequirement getRequirement() { return requirement; }
    public List<Long> getMentorDirections() { return mentorDirections; }
    public Map<Long, Double> getTargetDirectionWeights() { return targetDirectionWeights; }
    public Map<Long, Long> getDirectionParent() { return directionParent; }
    public Set<String> getMentorTags() { return mentorTags; }
    public GradMentorQuota getMentorQuota() { return mentorQuota; }
    public GradRecommendProperties getProps() { return props; }
    public boolean isNoPref() { return noPref; }
    public Map<Long, String> getDirectionDomain() { return directionDomain; }
    public List<GradMajorDomainMap> getMajorDomainMaps() { return majorDomainMaps; }
}
