package com.newsletterbot.telegram_bot.user.repository;

import com.newsletterbot.telegram_bot.user.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioTopico extends JpaRepository<Usuario, Long> {
    boolean existsByTelegramId(Long id);

}
