package com.newsletterbot.telegram_bot.newsletter.model;

import com.newsletterbot.telegram_bot.news.model.Noticia;
import com.newsletterbot.telegram_bot.user.model.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "newsletter")
public class Newsletter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "janela_inicio", nullable = false)
    private OffsetDateTime janelaInicio;

    @Column(name = "janela_fim", nullable = false)
    private OffsetDateTime janelaFim;

    private String status = "PENDENTE";

    @Column(name = "criada_em", insertable = false, updatable = false)
    private OffsetDateTime criadaEm;

    @Column(name = "enviada_em")
    private OffsetDateTime enviadaEm;

    private int tentativas;

    @Column(name="telegram_id")
    private Long telegramId;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "newsletter_itens",
            joinColumns = @JoinColumn(name="newsletter_id"),
            inverseJoinColumns = @JoinColumn(name = "noticia_id")
    )
    private Set<Noticia> noticias = new HashSet<>();
}
