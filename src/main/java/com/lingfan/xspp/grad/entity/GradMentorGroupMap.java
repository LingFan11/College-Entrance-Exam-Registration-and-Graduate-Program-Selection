package com.lingfan.xspp.grad.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * 导师与机构分组关联实体 (Requirements 4.4)
 * 实现导师与机构分组的多对多关系
 */
@Entity
@Table(name = "grad_mentor_group_map")
@IdClass(GradMentorGroupMap.MentorGroupId.class)
public class GradMentorGroupMap {

    @Id
    @Column(name = "mentor_id", nullable = false)
    private Long mentorId;

    @Id
    @Column(name = "group_id", nullable = false)
    private Long groupId;

    // Getters and Setters
    public Long getMentorId() { return mentorId; }
    public void setMentorId(Long mentorId) { this.mentorId = mentorId; }

    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }

    /**
     * 复合主键类
     */
    public static class MentorGroupId implements Serializable {
        private Long mentorId;
        private Long groupId;

        public MentorGroupId() {}

        public MentorGroupId(Long mentorId, Long groupId) {
            this.mentorId = mentorId;
            this.groupId = groupId;
        }

        public Long getMentorId() { return mentorId; }
        public void setMentorId(Long mentorId) { this.mentorId = mentorId; }

        public Long getGroupId() { return groupId; }
        public void setGroupId(Long groupId) { this.groupId = groupId; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MentorGroupId that = (MentorGroupId) o;
            return Objects.equals(mentorId, that.mentorId) && Objects.equals(groupId, that.groupId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(mentorId, groupId);
        }
    }
}
