package com.erasm.core.repository;

import com.erasm.core.entity.RequestSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestSkillRepository extends JpaRepository<RequestSkill, Long> {
}
