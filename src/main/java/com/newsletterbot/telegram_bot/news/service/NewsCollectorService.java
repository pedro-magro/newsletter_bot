package com.newsletterbot.telegram_bot.news.service;

import com.newsletterbot.telegram_bot.news.provider.NewsProvider;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsCollectorService {

    private final List<NewsProvider> providers;
    private final NewsPersistenceService persistencia;

    public NewsCollectorService(List<NewsProvider> providers, NewsPersistenceService persistencia) {
        this.providers = providers;
        this.persistencia = persistencia;
    }

    public int coletar(String slug, String consulta, String codigoProvedor){
        NewsProvider provider = providers.stream()
                .filter(p-> p.getCodigo().equals(codigoProvedor))
                .findFirst()
                .orElseThrow(()-> new IllegalArgumentException("Provedor não reconhecido"));

        var artigos = provider.buscarNoticias(consulta);

        return persistencia.salvar(
                slug,
                provider.getCodigo(),
                artigos
        );
    }


}
