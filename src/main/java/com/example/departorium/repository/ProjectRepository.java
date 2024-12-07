package com.example.departorium.repository;

import com.example.departorium.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {
    Boolean existsByTitle(String title);
    Boolean existsByInvitation(String invitation);
    @Query(value = "select * from project_main where project_invitation = :invitation", nativeQuery = true)
    ProjectEntity findByInvitation(String invitation);
}
