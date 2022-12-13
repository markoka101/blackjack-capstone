package com.example.demo.repository;

import com.example.demo.entity.Game;
import org.springframework.data.repository.CrudRepository;

public interface GameRepository extends CrudRepository<Game, Long>{
}
