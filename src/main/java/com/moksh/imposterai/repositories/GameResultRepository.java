package com.moksh.imposterai.repositories;

import com.moksh.imposterai.entities.GameResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameResultRepository extends JpaRepository<GameResultEntity,String> {

}
