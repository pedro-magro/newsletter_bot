package com.newsletterbot.telegram_bot.user.repository;

import com.newsletterbot.telegram_bot.user.model.Usuario;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    boolean existsByTelegramId(Long id);

    @EntityGraph(attributePaths = "assuntos")
    @Query("""
            SELECT u
            FROM USUARIO u
            WHERE u.telegramId = :telegramId
    """)
    Optional<Usuario> findWithAssuntosByTelegramId(@Param("telegramId")Long telegramId);
    Optional<Usuario> findByTelegramId(Long telegramId);

}
