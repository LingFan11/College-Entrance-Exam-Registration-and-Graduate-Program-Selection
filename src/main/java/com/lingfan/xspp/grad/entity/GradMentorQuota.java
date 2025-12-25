package com.lingfan.xspp.grad.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "grad_mentor_quota")
public class GradMentorQuota {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long mentorId;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Integer totalSlots;

    @Column(nullable = false)
    private Integer filledSlots;

    private String notes;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMentorId() { return mentorId; }
    public void setMentorId(Long mentorId) { this.mentorId = mentorId; }
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    public Integer getTotalSlots() { return totalSlots; }
    public void setTotalSlots(Integer totalSlots) { this.totalSlots = totalSlots; }
    public Integer getFilledSlots() { return filledSlots; }
    public void setFilledSlots(Integer filledSlots) { this.filledSlots = filledSlots; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
