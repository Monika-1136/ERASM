package com.erasm.core.repository;

import com.erasm.core.entity.Allocation;
import com.erasm.core.enums.AllocationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Collection;
import java.util.List;

@Repository
public interface AllocationRepository extends JpaRepository<Allocation, Long> {
    List<Allocation> findByEmployeeEmployeeId(Long employeeId);
    List<Allocation> findByProjectProjectId(Long projectId);
    List<Allocation> findByEmployeeEmployeeIdAndStatusIn(Long employeeId, Collection<AllocationStatus> statuses);

    @Query("SELECT COALESCE(SUM(a.allocationPercentage), 0.0) FROM Allocation a WHERE a.employee.employeeId = :employeeId AND a.status IN :statuses")
    Double sumAllocationPercentageByEmployeeAndStatusIn(@Param("employeeId") Long employeeId, @Param("statuses") Collection<AllocationStatus> statuses);

    @Query("SELECT a.employee.employeeId, COALESCE(SUM(a.allocationPercentage), 0.0) FROM Allocation a WHERE a.status IN :statuses GROUP BY a.employee.employeeId")
    List<Object[]> sumAllocationPercentageGroupedByEmployee(@Param("statuses") Collection<AllocationStatus> statuses);

    @Query("SELECT a FROM Allocation a JOIN FETCH a.employee e JOIN FETCH e.user JOIN FETCH a.project")
    List<Allocation> findAllWithEmployeeAndProject();
}
