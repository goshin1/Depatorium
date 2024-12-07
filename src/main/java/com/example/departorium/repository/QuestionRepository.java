package com.example.departorium.repository;


import com.example.departorium.entity.QuestionEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<QuestionEntity, Long> {
    @Transactional
    void deleteAllByTask_Id(Long task_id);
    List<QuestionEntity> findAllByTask_IdOrderByCreate(Long task_id);
}
