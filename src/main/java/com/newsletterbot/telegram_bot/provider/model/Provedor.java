package com.newsletterbot.telegram_bot.provider.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "provedores")
public class Provedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codigo;

    private int prioridade;
    private boolean ativo = true;

    @Column(name= "limite_diario")
    private int limiteDiario;

    @Column(name ="reserva_diaria")
    private int reservaDiaria;
}
