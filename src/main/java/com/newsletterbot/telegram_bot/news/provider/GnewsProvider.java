package com.newsletterbot.telegram_bot.news.provider;

import com.newsletterbot.telegram_bot.news.dto.NewsArticle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class GnewsProvider implements  NewsProvider {

    private final RestClient client;
    private final String idioma;
    private final int limite;

    public GnewsProvider(
            RestClient.Builder builder,
            @Value("${news.providers.gnews.api-key}") String apiKey,
            @Value("${news.providers.gnews.base-url}") String baseUrl,
            @Value("${news.providers.gnews.language}") String idioma,
            @Value("${news.providers.gnews.max-results}") int limite

    ){
        this.client = builder
                .baseUrl(baseUrl)
                .defaultHeader("X-Api-Key", apiKey)
                .build();

        this.idioma = idioma;
        this.limite = limite;
    }

    @Override
    public String getCodigo() {
        return "GNEWS";
    }

    @Override
    public List<NewsArticle> buscarNoticias(String consulta){
        JsonNode resposta = client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("q", consulta)
                        .queryParam("lang", idioma)
                        .queryParam("max", limite)
                        .queryParam("sortby", "publishedAT")
                        .build())
                .retrieve()
                .body(JsonNode.class);

        List<NewsArticle> noticias = new ArrayList<>();

        if(resposta == null){
            return noticias;
        }

        JsonNode artigos = resposta.path("articles");

        if(!artigos.isArray()){
            throw new IllegalArgumentException(
                    "Resposta inesperada da GNews"
            );
        }

        for (JsonNode artigo: artigos){
            String titulo = artigo.path("title").asString("");
            String url = artigo.path("url").asString("");

            if(titulo.isBlank() || url.isBlank()){
                continue;
            }

            String data = artigo.path("publishedAt").asString("");

            OffsetDateTime publicadaEm = data.isBlank() ? null : OffsetDateTime.parse(data);

            noticias.add(new NewsArticle(
                    titulo,
                    artigo.path("description").asString(""),
                    url,
                    artigo.path("source")
                            .path("name").asString(""),
                    publicadaEm
            ));
        }

        return noticias;
    }
}
