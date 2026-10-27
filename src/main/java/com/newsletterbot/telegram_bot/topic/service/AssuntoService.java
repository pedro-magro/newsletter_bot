package com.newsletterbot.telegram_bot.topic.service;

import com.newsletterbot.telegram_bot.telegram.dto.TelegramTopic;
import com.newsletterbot.telegram_bot.topic.repository.AssuntoRepository;
import com.newsletterbot.telegram_bot.user.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssuntoService {

    private final AssuntoRepository assuntoRepository;

    public AssuntoService(AssuntoRepository assuntoRepository) {
        this.assuntoRepository = assuntoRepository;
    }

    @Transactional
    public List<TelegramTopic> listarAtivos(){
        return assuntoRepository.findByAtivoTrueOrderByAsc()
                .stream()
                .map(a -> new TelegramTopic(
                        a.getNome(),
                        a.getSlug()
                )).toList();
    }


}
