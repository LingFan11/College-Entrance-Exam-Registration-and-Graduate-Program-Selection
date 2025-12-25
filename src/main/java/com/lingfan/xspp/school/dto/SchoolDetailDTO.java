package com.lingfan.xspp.school.dto;

public class SchoolDetailDTO {
    private Long id;
    private String name;
    private String alias;
    private String level;
    private String province;
    private String city;
    private String website;
    private String brief;
    private Integer foundedYear;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
    public String getBrief() { return brief; }
    public void setBrief(String brief) { this.brief = brief; }
    public Integer getFoundedYear() { return foundedYear; }
    public void setFoundedYear(Integer foundedYear) { this.foundedYear = foundedYear; }
}
