package com.newsletterbot.telegram_bot.provider.repository;

import com.newsletterbot.telegram_bot.provider.model.Provedor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProvedorRepository extends JpaRepository<Provedor, Long> {

    Optional<Provedor> findByAtivoTrueOrderByPrioridadeAsc();
    Optional<Provedor> findByCodigo(String codigo);
}
