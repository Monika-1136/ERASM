package com.erasm.core.service;

import com.erasm.core.dto.request.RoleRequest;
import com.erasm.core.dto.response.RoleResponse;
import java.util.List;

public interface RoleService {

    RoleResponse createRole(RoleRequest request);

    List<RoleResponse> getAllRoles();

    RoleResponse getRoleById(Long id);

    RoleResponse updateRole(Long id, RoleRequest request);

    void deleteRole(Long id);
}
