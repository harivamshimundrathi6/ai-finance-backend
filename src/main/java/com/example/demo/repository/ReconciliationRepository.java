package com.example.demo.repository;

import com.example.demo.entity.ReconciliationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReconciliationRepository extends JpaRepository<ReconciliationRecord, Long> {
    List<ReconciliationRecord> findByMerchantEmailOrderByCheckedAtDesc(String merchantEmail);
    List<ReconciliationRecord> findByMerchantEmail(String merchantEmail);
}