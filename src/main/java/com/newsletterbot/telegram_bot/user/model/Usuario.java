package com.newsletterbot.telegram_bot.user.model;

import com.newsletterbot.telegram_bot.topic.model.Assunto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name ="usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "telegram_id", nullable = false, unique = true)
    private Long telegramId;

    @Column(name = "chat_id", nullable = false, unique = true)
    private Long chatId;

    private String nome;
    private String username;
    private boolean ativo = true;

    @Column(nullable = false)
    private String frequencia = "DIARIA";

    @Column(name = "horario_envio")
    private LocalTime horarioEnvio = LocalTime.of(9,0);

    private String timezone = "America/Sao_Paulo";

    @Column(name = "criado_em",insertable = false, updatable = false)
    private OffsetDateTime criadoEm;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "usuario_assunto",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "assunto_id")
    )
    private Set<Assunto> assuntos = new HashSet<>();
}
