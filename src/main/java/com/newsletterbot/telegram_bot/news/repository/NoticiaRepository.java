package com.newsletterbot.telegram_bot.news.repository;

import com.newsletterbot.telegram_bot.news.model.Noticia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface NoticiaRepository extends JpaRepository<Noticia, Long> {

    boolean existsByUrlHash(String urlHash);

    @Query("""
        SELECT DISTINCT n FROM Noticia n
        JOIN n.assuntos a
        WHERE a.slug IN :slugs
            AND n.publicadaEm >= :inicio
            AND n.publicadaEm < :fim
        ORDER BY n.publicadaEm DESC
        
    """)
    List<Noticia> buscarParaNesletter(
            @Param("slugs") Collection<String> slugs,
            @Param("inicio")OffsetDateTime inicio,
            @Param("fim") OffsetDateTime fim
            );
}
