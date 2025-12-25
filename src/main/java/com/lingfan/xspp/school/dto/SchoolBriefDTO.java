package com.lingfan.xspp.school.dto;

public class SchoolBriefDTO {
    private Long id;
    private String name;
    private String level;
    private String province;
    private String city;
    private String website;

    public SchoolBriefDTO() {}

    public SchoolBriefDTO(Long id, String name, String level, String province, String city, String website) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.province = province;
        this.city = city;
        this.website = website;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
}
