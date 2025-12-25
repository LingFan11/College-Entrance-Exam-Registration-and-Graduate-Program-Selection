package com.lingfan.xspp.grad.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "grad_domain")
public class GradDomain {
    @Id
    @Column(length = 32)
    private String code; // e.g., CS, LIT

    @Column(nullable = false, length = 64)
    private String name; // e.g., 计算机、文学

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
