package com.example.departorium.repository;


import com.example.departorium.entity.ScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<ScheduleEntity, Long> {
    List<ScheduleEntity> findAllByUser_Id(Long user_id);
    List<ScheduleEntity> findAllByProject_Id(Long project_id);
    List<ScheduleEntity> findAllByUser_IdAndProject_Id(Long user_id, Long project_id);


    void deleteAllByProject_Id(Long id);
}
