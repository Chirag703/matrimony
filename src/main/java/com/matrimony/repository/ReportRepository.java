package com.matrimony.repository;

import com.matrimony.entity.Report;
import com.matrimony.entity.Report.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findByStatus(Status status);

    List<Report> findByReportedUserId(Long reportedUserId);

    long countByStatus(Status status);
}
