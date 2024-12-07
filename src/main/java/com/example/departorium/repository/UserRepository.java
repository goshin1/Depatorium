package com.example.departorium.repository;


import com.example.departorium.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Boolean existsByUsername(String username);
    Boolean existsByNickname(String nickname);
//    UserEntity findByUsername(String username);

    @Query(value = "select * from user_main where user_username = :username", nativeQuery = true)
    Optional<UserEntity> findByUsername(String username);

    @Query(value = "select * from user_main where user_username = :username and user_password = :password", nativeQuery = true)
    Optional<UserEntity> findByUsernameAndPassword(String username, String password);
}
