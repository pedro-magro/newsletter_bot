package com.newsletterbot.telegram_bot.user.service;

import com.newsletterbot.telegram_bot.topic.model.Assunto;
import com.newsletterbot.telegram_bot.topic.repository.AssuntoRepository;
import com.newsletterbot.telegram_bot.user.model.Usuario;
import com.newsletterbot.telegram_bot.user.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final AssuntoRepository assuntoRepository;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            AssuntoRepository assuntoRepository
    ){
        this.usuarioRepository = usuarioRepository;
        this.assuntoRepository = assuntoRepository;
    }

    @Transactional
    public Usuario registrar(Long telegramId, Long chatId, String nome){
        return usuarioRepository.findByTelegramId(telegramId).
                orElseGet(()->{
                    Usuario usuario = new Usuario();
                    usuario.setTelegramId(telegramId);
                    usuario.setChatId(chatId);
                    usuario.setNome(nome);
                    usuario.setAtivo(true);

                    return usuarioRepository.save(usuario);
                });
    }

    @Transactional
    public void inscrever(Long telegramId, String slug) {
        Usuario usuario = usuarioRepository
                .findByTelegramId(telegramId)
                .orElseThrow(()-> new IllegalArgumentException("Usuário não encontrado"));

        Assunto assunto = assuntoRepository
                .findBySlug(slug)
                .orElseThrow(()->new IllegalArgumentException("Assunto inválido"));

        boolean jaInscrito = usuario.getAssuntos()
                .stream()
                .anyMatch(a -> a.getId() == assunto.getId());

        if(!jaInscrito){
            usuario.getAssuntos().add(assunto);
        }
    }

    @Transactional
    public void cancelarInscricao(Long telegramId, String Slug){
        Usuario usuario = usuarioRepository
                .findByTelegramId(telegramId)
                .orElseThrow(()-> new IllegalArgumentException("usuário não encontrado"));

        usuario.getAssuntos()
                .removeIf(assunto -> assunto.getSlug().equals(Slug));
    }

    @Transactional
    public List<String> consultarPreferencias(Long telegramId){
        Usuario usuario = usuarioRepository
                .findByTelegramId(telegramId)
                .orElseThrow(()->new IllegalArgumentException("usuario não encontrado"));

        return usuario.getAssuntos()
                .stream()
                .map(Assunto::getNome)
                .toList();
    }
}
