package com.lingfan.xspp.grad.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "grad_mentor_direction")
@IdClass(GradMentorDirection.PK.class)
public class GradMentorDirection {
    @Id
    private Long mentorId;
    @Id
    private Long directionId;

    public Long getMentorId() { return mentorId; }
    public void setMentorId(Long mentorId) { this.mentorId = mentorId; }
    public Long getDirectionId() { return directionId; }
    public void setDirectionId(Long directionId) { this.directionId = directionId; }

    public static class PK implements Serializable {
        private Long mentorId;
        private Long directionId;
        public PK() {}
        public PK(Long mentorId, Long directionId) { this.mentorId = mentorId; this.directionId = directionId; }
        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PK pk = (PK) o;
            return Objects.equals(mentorId, pk.mentorId) && Objects.equals(directionId, pk.directionId);
        }
        @Override public int hashCode() { return Objects.hash(mentorId, directionId); }
    }
}
