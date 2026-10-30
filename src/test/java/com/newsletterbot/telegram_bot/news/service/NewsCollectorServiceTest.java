package com.newsletterbot.telegram_bot.news.service;

import com.newsletterbot.telegram_bot.news.dto.NewsArticle;
import com.newsletterbot.telegram_bot.news.provider.NewsProvider;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class NewsCollectorServiceTest {

    @Test
    void deveColetarNoticiasDoProvedorSelecionado(){
        NewsProvider provider = mock(NewsProvider.class);

        NewsPersistenceService persistenceService = mock(NewsPersistenceService.class);

        NewsArticle artigo = new NewsArticle(
                "Nova vulnerabilidade PHP descoberta",
                "CVE344324",
                "https://teste.exemplo.cve",
                "Fonte fictícia",
                null
        );

        when(provider.getCodigo()).thenReturn("GNEWS");
        when(provider.buscarNoticias("cybersecurity")).thenReturn(List.of(artigo));

        when(persistenceService.salvar(eq("cibersegurança"), eq("GNEWS"), anyList())).thenReturn(1);

        NewsCollectorService service = new NewsCollectorService(List.of(provider), persistenceService);

        int resultado = service.coletar(
                "cibersegurança",
                "cibersecurity",
                "GNEWS"
        );

        assertEquals(1,resultado);
        verify(persistenceService, times(1)).salvar("cibersegurança", "GNEWS", List.of(artigo));
    }
}
