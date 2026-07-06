package com.erasm.core.dto.response;

import com.erasm.core.enums.RoleName;

public class RoleResponse {

    private Long roleId;
    private RoleName roleName;

    public RoleResponse() {
    }

    public RoleResponse(Long roleId, RoleName roleName) {
        this.roleId = roleId;
        this.roleName = roleName;
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
}
