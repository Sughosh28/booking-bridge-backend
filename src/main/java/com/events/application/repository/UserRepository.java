package com.events.application.repository;

import com.events.application.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByUsername(String username);

    @Query("SELECT u.id FROM UserEntity u WHERE u.username = :username")
    Long findIdByUsername(@Param("username") String username);

    UserEntity findByEmail(String email);

    @Query("SELECT u.email FROM UserEntity u")
    List<String> findAllMails();
}

