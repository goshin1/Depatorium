package com.example.departorium.repository;

import com.example.departorium.entity.DepartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartRepository extends JpaRepository<DepartEntity, Long> {
    void deleteByProject_Id(Long id);
    List<DepartEntity> findAllByProject_Id(Long project_id);
}
