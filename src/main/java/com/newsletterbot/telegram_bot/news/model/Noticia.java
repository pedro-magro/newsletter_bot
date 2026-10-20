package com.newsletterbot.telegram_bot.news.model;

import com.newsletterbot.telegram_bot.provider.model.Provedor;
import com.newsletterbot.telegram_bot.topic.model.Assunto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name="noticia")
public class Noticia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String url;

    @Column(name ="url_hash", nullable = false, unique = true)
    private String urlHash;

    @Column(name="fonte_nome")
    private String fonteNome;

    @Column(name = "publicada_em")
    private OffsetDateTime publicadaEm;

    @Column(name = "coletada_em", insertable = false, updatable = false)
    private OffsetDateTime coletadaEm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provedor_id")
    private Provedor provedor;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "noticia_assunto",
            joinColumns = @JoinColumn(name = "noticia_id"),
            inverseJoinColumns = @JoinColumn(name = "assunto_id")
    )
    private Set<Assunto> assuntos = new HashSet<>();
}
