package com.lingfan.xspp.grad.dto;

public class MentorDTO {
    private Long id;
    private String name;
    private String title;
    private String email;
    private String homepage;
    private String brief;

    public MentorDTO() {}
    public MentorDTO(Long id, String name, String title, String email, String homepage, String brief) {
        this.id = id; this.name = name; this.title = title; this.email = email; this.homepage = homepage; this.brief = brief;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getHomepage() { return homepage; }
    public void setHomepage(String homepage) { this.homepage = homepage; }
    public String getBrief() { return brief; }
    public void setBrief(String brief) { this.brief = brief; }
}
