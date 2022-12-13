package com.example.demo.repository;

import com.example.demo.entity.Player;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PlayerRepository extends CrudRepository<Player, Long> {
    Optional<Player> findByUserId(Long userId);
}
