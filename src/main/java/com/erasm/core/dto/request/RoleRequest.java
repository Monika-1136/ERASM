package com.erasm.core.dto.request;

import com.erasm.core.enums.RoleName;
import jakarta.validation.constraints.NotNull;

public class RoleRequest {

    @NotNull(message = "Role name is required")
    private RoleName roleName;

    public RoleRequest() {
    }

    public RoleRequest(RoleName roleName) {
        this.roleName = roleName;
    }

    public RoleName getRoleName() {
        return roleName;
    }

    public void setRoleName(RoleName roleName) {
        this.roleName = roleName;
    }
}
