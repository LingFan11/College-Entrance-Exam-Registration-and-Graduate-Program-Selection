package com.lingfan.xspp.grad.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "grad_major_domain_map")
public class GradMajorDomainMap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 32)
    private String domainCode; // e.g., CS, LIT

    @Column(nullable = false, length = 64)
    private String keyword; // e.g., 计算机、汉语言

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDomainCode() { return domainCode; }
    public void setDomainCode(String domainCode) { this.domainCode = domainCode; }
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
}
