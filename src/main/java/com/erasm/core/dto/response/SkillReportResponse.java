package com.erasm.core.dto.response;

import java.util.ArrayList;
import java.util.List;

public class SkillReportResponse {

    private Long skillId;
    private String skillName;
    private String category;
    private Integer count;
    private List<EmployeeSkillResponse> employeeSkills = new ArrayList<>();

    public SkillReportResponse() {
    }

    public SkillReportResponse(Long skillId, String skillName, String category, Integer count, List<EmployeeSkillResponse> employeeSkills) {
        this.skillId = skillId;
        this.skillName = skillName;
        this.category = category;
        this.count = count;
        this.employeeSkills = employeeSkills;
    }

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<EmployeeSkillResponse> getEmployeeSkills() {
        return employeeSkills;
    }

    public void setEmployeeSkills(List<EmployeeSkillResponse> employeeSkills) {
        this.employeeSkills = employeeSkills;
    }
}
