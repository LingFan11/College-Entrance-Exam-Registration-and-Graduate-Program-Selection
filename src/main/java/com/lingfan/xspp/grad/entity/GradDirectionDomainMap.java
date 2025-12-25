package com.lingfan.xspp.grad.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "grad_direction_domain_map")
public class GradDirectionDomainMap {
    @Id
    @Column(name = "direction_id")
    private Long directionId;

    @Column(nullable = false, length = 32)
    private String domainCode; // e.g., CS, LIT

    public Long getDirectionId() { return directionId; }
    public void setDirectionId(Long directionId) { this.directionId = directionId; }
    public String getDomainCode() { return domainCode; }
    public void setDomainCode(String domainCode) { this.domainCode = domainCode; }
}
