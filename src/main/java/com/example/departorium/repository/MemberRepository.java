package com.example.departorium.repository;

import com.example.departorium.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    void deleteByProject_Id(Long id);
    Boolean existsByUser_IdAndProject_Id(Long user_id, Long project_id);
    MemberEntity findByUser_IdAndProject_Id(Long user_id, Long project_id);
    List<MemberEntity> findAllByUser_Id(Long user_id);
    List<MemberEntity> findAllByProject_Id(Long project_id);
    List<MemberEntity> findAllByProject_IdOrderByStatus(Long project_id);
}
