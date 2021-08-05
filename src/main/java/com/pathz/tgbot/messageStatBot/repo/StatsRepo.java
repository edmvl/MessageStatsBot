package com.pathz.tgbot.messageStatBot.repo;

import com.pathz.tgbot.messageStatBot.entity.Stats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatsRepo extends JpaRepository<Stats, Long> {

    Stats findByMessage(String message);

    boolean existsByMessage(String message);;

    Stats findTopByOrderByCountDesc();

    List<Stats> findTop25ByOrderByCountDesc();

    void deleteByMessage(String message);
}
