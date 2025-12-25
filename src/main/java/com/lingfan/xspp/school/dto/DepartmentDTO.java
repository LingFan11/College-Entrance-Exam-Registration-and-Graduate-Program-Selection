package com.lingfan.xspp.school.dto;

public class DepartmentDTO {
    private Long id;
    private String name;
    private String brief;

    public DepartmentDTO() {}
    public DepartmentDTO(Long id, String name, String brief) {
        this.id = id;
        this.name = name;
        this.brief = brief;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getBrief() { return brief; }
    public void setBrief(String brief) { this.brief = brief; }
}
