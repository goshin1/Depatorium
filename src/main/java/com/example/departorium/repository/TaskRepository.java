package com.example.departorium.repository;


import com.example.departorium.entity.TaskEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    @Transactional
    void deleteAllByDepart_Id(Long depart_id);
    List<TaskEntity> findAllByDepart_Id(Long depart_id);
    List<TaskEntity> findAllByDepart_IdAndMember_Id(Long depart_id, Long member_id);

    @Modifying
    @Query(value = "update task_main set task_submit = :now, task_work = :work where task_id = :taskId", nativeQuery = true)
    void updateSubmit(LocalDateTime now, String work, Long taskId);



    @Query(value = "select * from task_main where depart_id = :departId", nativeQuery = true)
    List<TaskEntity> findAllByDepartId(Long departId);
}
