package com.pathz.tgbot.messageStatBot.repo;

import com.pathz.tgbot.messageStatBot.entity.Horo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface HoroRepository extends CrudRepository<Horo, Long> {
    Optional<Horo> getAllByDateAndSign(LocalDate localDate, String sign);
}
