package com.lingfan.xspp.grad.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "grad_mentor_publication")
public class GradMentorPublication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long mentorId;

    @Column(nullable = false, length = 256)
    private String title;

    private String venue;
    private Integer year;
    private String link;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMentorId() { return mentorId; }
    public void setMentorId(Long mentorId) { this.mentorId = mentorId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }
}
