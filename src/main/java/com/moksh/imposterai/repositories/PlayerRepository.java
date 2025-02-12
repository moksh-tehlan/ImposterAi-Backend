package com.moksh.imposterai.repositories;

import com.moksh.imposterai.dtos.enums.MatchStatus;
import com.moksh.imposterai.entities.PlayerEntity;
import com.moksh.imposterai.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlayerRepository extends JpaRepository<PlayerEntity, String> {

    @Query("SELECT p FROM players p WHERE p.user.id = :userId")
    Optional<PlayerEntity> findByUserId(@Param("userId") String userId);

    @Query("SELECT p FROM players p WHERE p.matchStatus = :matchStatus")
    List<PlayerEntity> getWaitingPlayer(@Param("matchStatus")MatchStatus matchStatus);

    String user(UserEntity user);
}
