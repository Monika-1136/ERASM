package com.erasm.core.service.impl;

import com.erasm.core.dto.request.RoleRequest;
import com.erasm.core.dto.response.RoleResponse;
import com.erasm.core.entity.Role;
import com.erasm.core.exception.DuplicateResourceException;
import com.erasm.core.exception.ResourceNotFoundException;
import com.erasm.core.repository.RoleRepository;
import com.erasm.core.repository.UserRepository;
import com.erasm.core.service.AuditService;
import com.erasm.core.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private static final Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;

    public RoleServiceImpl(RoleRepository roleRepository, UserRepository userRepository, AuditService auditService) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.auditService = auditService;
    }

    @Override
    @Transactional
    public RoleResponse createRole(RoleRequest request) {
        logger.info("Creating new role with name: {}", request.getRoleName());
        if (roleRepository.findByRoleName(request.getRoleName()).isPresent()) {
            throw new DuplicateResourceException("Role already exists with name: " + request.getRoleName());
        }

        Role role = new Role();
        role.setRoleName(request.getRoleName());

        Role saved = roleRepository.save(role);
        auditService.logAction("CREATE_ROLE", "Role", saved.getRoleId(), "ADMIN", "Created role " + request.getRoleName());

        return new RoleResponse(saved.getRoleId(), saved.getRoleName());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {
        logger.debug("Fetching all roles");
        return roleRepository.findAll().stream()
                .map(role -> new RoleResponse(role.getRoleId(), role.getRoleName()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponse getRoleById(Long id) {
        logger.debug("Fetching role with ID: {}", id);
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + id));
        return new RoleResponse(role.getRoleId(), role.getRoleName());
    }

    @Override
    @Transactional
    public RoleResponse updateRole(Long id, RoleRequest request) {
        logger.info("Updating role with ID: {}", id);
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + id));

        if (roleRepository.findByRoleName(request.getRoleName()).isPresent() && 
            !role.getRoleName().equals(request.getRoleName())) {
            throw new DuplicateResourceException("Role already exists with name: " + request.getRoleName());
        }

        role.setRoleName(request.getRoleName());
        Role saved = roleRepository.save(role);
        auditService.logAction("UPDATE_ROLE", "Role", saved.getRoleId(), "ADMIN", "Updated role ID " + id + " to " + request.getRoleName());

        return new RoleResponse(saved.getRoleId(), saved.getRoleName());
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        logger.info("Deleting role with ID: {}", id);
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + id));

        if (userRepository.existsByRoleRoleId(id)) {
            throw new IllegalArgumentException("Cannot delete role as it is assigned to users.");
        }

        roleRepository.delete(role);
        auditService.logAction("DELETE_ROLE", "Role", id, "ADMIN", "Deleted role " + role.getRoleName());
    }
}
