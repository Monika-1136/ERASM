package com.erasm.core.entity;

import com.erasm.core.enums.RoleName;
import jakarta.persistence.*;

@Entity
@Table(name="roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, unique=true)
    private RoleName roleName;

    @Column
    private String description;

    public Role() {
    }

    public Role(Long roleId, RoleName roleName) {
        this.roleId = roleId;
        this.roleName = roleName;
    }

    public Role(Long roleId, RoleName roleName, String description) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.description = description;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public RoleName getRoleName() {
        return roleName;
    }

    public void setRoleName(RoleName roleName) {
        this.roleName = roleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
