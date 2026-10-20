package com.newsletterbot.telegram_bot.newsletter.repository;

import com.newsletterbot.telegram_bot.newsletter.model.Newsletter;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.OffsetDateTime;
import java.util.Optional;

public interface NewsletterRepository extends JpaRepository<Newsletter, Long> {
    Optional<Newsletter> findByStatus(String status);

    boolean existsByUsuario_IdAndJanelaInicioAndJanelaFim(
            Long usuarioId,
            OffsetDateTime inicio,
            OffsetDateTime fim
    );
}
