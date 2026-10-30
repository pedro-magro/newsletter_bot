package com.newsletterbot.telegram_bot.news.service;

import com.newsletterbot.telegram_bot.news.dto.NewsArticle;
import com.newsletterbot.telegram_bot.news.model.Noticia;
import com.newsletterbot.telegram_bot.news.repository.NoticiaRepository;
import com.newsletterbot.telegram_bot.provider.model.Provedor;
import com.newsletterbot.telegram_bot.provider.repository.ProvedorRepository;
import com.newsletterbot.telegram_bot.topic.model.Assunto;
import com.newsletterbot.telegram_bot.topic.repository.AssuntoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.Provider;
import java.util.HexFormat;
import java.util.List;

@Service
public class NewsPersistenceService {

    private final NoticiaRepository noticiaRepository;
    private final AssuntoRepository assuntoRepository;
    private final ProvedorRepository provedorRepository;

    public NewsPersistenceService(NoticiaRepository noticiaRepository, AssuntoRepository assuntos, ProvedorRepository provedorRepository) {
        this.noticiaRepository = noticiaRepository;
        this.assuntoRepository = assuntos;
        this.provedorRepository = provedorRepository;
    }

    
    @Transactional
    int salvar(String slug, String codigoProvedor, List<NewsArticle> noticias){
        Assunto assunto = assuntoRepository.findBySlugAtivoTrue(slug)
                .orElseThrow(()-> new IllegalArgumentException("Assunto não encontrado"));

        Provedor provedor = provedorRepository.findByCodigo(codigoProvedor)
                .orElseThrow(()-> new IllegalArgumentException("Provedor não encontrado"));

        int inseridas = 0;

        for (NewsArticle news : noticias){
            String url = news.url().trim();

            URI uri = URI.create(url);

            if(!"https".equalsIgnoreCase(uri.getScheme()) && "http".equalsIgnoreCase(uri.getScheme())){
                continue;
            }

            if(uri.getHost() == null){
                continue;
            }

            String urlNormalizada = url.split("#", 2)[0];

            String hash = gerarHash(urlNormalizada);

            Noticia noticia = noticiaRepository.findByUrlHash(hash)
                    .orElse(null);

            if(noticia == null){
                noticia = new Noticia();
                noticia.setTitulo(news.titulo());
                noticia.setDescricao(news.descricao());
                noticia.setUrl(urlNormalizada);
                noticia.setUrlHash(hash);
                noticia.setFonteNome(news.fonte());
                noticia.setPublicadaEm(news.publicadaEm());
                noticia.setProvedor(provedor);

                noticia.getAssuntos().add(assunto);
                noticiaRepository.save(noticia);
                inseridas++;
            }
            else{
                boolean associado = noticia.getAssuntos()
                        .stream()
                        .anyMatch(a-> a.getId() == assunto.getId());
                if(!associado){
                    noticia.getAssuntos().add(assunto);
                }
            }
        }

        return inseridas;
    }

    private String gerarHash(String valor){
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(valor.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(bytes);
        }
        catch(Exception e){
            throw new IllegalStateException("falha ao calcular hash", e);
        }
    }

}
