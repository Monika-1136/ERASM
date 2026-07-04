package com.erasm.core.repository;

import com.erasm.core.entity.EmployeeSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeSkillRepository extends JpaRepository<EmployeeSkill, Long> {
    List<EmployeeSkill> findByEmployeeEmployeeId(Long employeeId);
    List<EmployeeSkill> findBySkillSkillId(Long skillId);
    Optional<EmployeeSkill> findByEmployeeEmployeeIdAndSkillSkillId(Long employeeId, Long skillId);

    @Query("SELECT es FROM EmployeeSkill es JOIN FETCH es.skill JOIN FETCH es.employee e JOIN FETCH e.user")
    List<EmployeeSkill> findAllWithSkillAndEmployee();
}
