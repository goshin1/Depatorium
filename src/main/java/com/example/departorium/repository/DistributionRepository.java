package com.example.departorium.repository;


import com.example.departorium.entity.DistributionEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DistributionRepository extends JpaRepository<DistributionEntity, Long> {
    @Transactional
    void deleteAllByTask_Id(Long task_id);
    List<DistributionEntity> findAllByTask_Id(Long task_id);
    Boolean existsByMember_IdAndTask_Id(Long member_id, Long task_id);

    List<DistributionEntity> findAllByMember_id(Long memberId);
}
