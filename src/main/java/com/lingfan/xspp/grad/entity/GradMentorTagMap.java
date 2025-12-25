package com.lingfan.xspp.grad.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "grad_mentor_tag_map")
@IdClass(GradMentorTagMap.PK.class)
public class GradMentorTagMap {
    @Id
    private Long mentorId;
    @Id
    private Long tagId;

    public Long getMentorId() { return mentorId; }
    public void setMentorId(Long mentorId) { this.mentorId = mentorId; }
    public Long getTagId() { return tagId; }
    public void setTagId(Long tagId) { this.tagId = tagId; }

    public static class PK implements Serializable {
        private Long mentorId;
        private Long tagId;
        public PK() {}
        public PK(Long mentorId, Long tagId) { this.mentorId = mentorId; this.tagId = tagId; }
        @Override public boolean equals(Object o) { if (this == o) return true; if (o == null || getClass() != o.getClass()) return false; PK pk = (PK) o; return Objects.equals(mentorId, pk.mentorId) && Objects.equals(tagId, pk.tagId); }
        @Override public int hashCode() { return Objects.hash(mentorId, tagId); }
    }
}
