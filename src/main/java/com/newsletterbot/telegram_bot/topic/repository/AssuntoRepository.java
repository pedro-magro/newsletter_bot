package com.newsletterbot.telegram_bot.topic.repository;

import com.newsletterbot.telegram_bot.topic.model.Assunto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface AssuntoRepository extends JpaRepository<Assunto, Long> {

    Optional<Assunto> findBySlug(String slug);

    Optional<Assunto> findBySlugAtivoTrue(String slug);

    List<Assunto> findByAtivoTrueOrderByAsc();
}
