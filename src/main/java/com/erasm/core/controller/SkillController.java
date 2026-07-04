package com.erasm.core.controller;

import com.erasm.core.dto.request.SkillRequest;
import com.erasm.core.dto.response.ApiResponse;
import com.erasm.core.dto.response.SkillResponse;
import com.erasm.core.service.SkillService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/skills", "/api/skills"})
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SkillResponse>> addSkill(@Valid @RequestBody SkillRequest request) {
        SkillResponse response = skillService.addSkill(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Skill added successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SkillResponse>> getSkillById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Skill fetched successfully", skillService.getSkillById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SkillResponse>>> getAllSkills() {
        return ResponseEntity.ok(ApiResponse.success("Skills fetched successfully", skillService.getAllSkills()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SkillResponse>> updateSkill(@PathVariable Long id, @Valid @RequestBody SkillRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Skill updated successfully", skillService.updateSkill(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteSkill(@PathVariable Long id) {
        skillService.deleteSkill(id);
        return ResponseEntity.ok(ApiResponse.success("Skill deleted successfully"));
    }
}
