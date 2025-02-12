package com.moksh.imposterai.repositories;

import com.moksh.imposterai.entities.MatchEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MatchRepository extends JpaRepository<MatchEntity,String> {

    @Query("SELECT m FROM matches m WHERE m.playerOne.sessionId = :playerId OR m.playerTwo.sessionId = :playerId")
    Optional<MatchEntity> findByPlayerId(@Param("playerId")String playerId);

    @Query("DELETE FROM matches m WHERE m.playerOne.sessionId = :playerId OR m.playerTwo.sessionId = :playerId")
    @Modifying
    @Transactional
    void deleteByPlayerId(@Param("playerId") String playerId);
}
