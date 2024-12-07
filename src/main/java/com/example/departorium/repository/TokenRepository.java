package com.example.departorium.repository;

import com.example.departorium.entity.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TokenRepository extends JpaRepository<TokenEntity, Long> {
    Boolean existsByRefresh(String refresh);
    @Transactional
    void deleteByRefresh(String refresh);

    @Query(value = "select * from user_token where token_refresh = :refresh", nativeQuery = true)
    TokenEntity findByRefresh(String refresh);
}
