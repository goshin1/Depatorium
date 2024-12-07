package com.example.departorium.repository;

import com.example.departorium.entity.ChatEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<ChatEntity, Long> {
    List<ChatEntity> findAllByDepart_id(Long depart_id);


    void deleteAllByDepart_Id(Long depart_id);

    @Query(value = "select * from project_chat where depart_id = :roomId", nativeQuery = true)
    List<ChatEntity> findByRoomId(Long roomId);
}
