package com.lingfan.xspp.school.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "sch_univ_major")
@IdClass(SchUnivMajor.UnivMajorId.class)
public class SchUnivMajor {
    @Id
    @Column(name = "university_id")
    private Long universityId;

    @Id
    @Column(name = "major_id")
    private Long majorId;

    @Column(name = "department_id")
    private Long departmentId;

    @Column(name = "feature_tag")
    private String featureTag;

    private String note;

    public Long getUniversityId() { return universityId; }
    public void setUniversityId(Long universityId) { this.universityId = universityId; }
    public Long getMajorId() { return majorId; }
    public void setMajorId(Long majorId) { this.majorId = majorId; }
    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
    public String getFeatureTag() { return featureTag; }
    public void setFeatureTag(String featureTag) { this.featureTag = featureTag; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public static class UnivMajorId implements Serializable {
        private Long universityId;
        private Long majorId;
        public UnivMajorId() {}
        public UnivMajorId(Long universityId, Long majorId) {
            this.universityId = universityId;
            this.majorId = majorId;
        }
        public Long getUniversityId() { return universityId; }
        public void setUniversityId(Long universityId) { this.universityId = universityId; }
        public Long getMajorId() { return majorId; }
        public void setMajorId(Long majorId) { this.majorId = majorId; }
        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UnivMajorId that = (UnivMajorId) o;
            return java.util.Objects.equals(universityId, that.universityId)
                    && java.util.Objects.equals(majorId, that.majorId);
        }
        @Override public int hashCode() { return java.util.Objects.hash(universityId, majorId); }
    }
}
