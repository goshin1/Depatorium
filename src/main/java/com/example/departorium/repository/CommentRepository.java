package com.example.departorium.repository;


import com.example.departorium.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    void deleteAllBySchedule_Id(Long schedule_id);
    List<CommentEntity> findAllByProject_IdAndSchedule_IdOrderByCreate(Long project_id, Long schedule_id);

    void deleteAllByProject_Id(Long id);
}
