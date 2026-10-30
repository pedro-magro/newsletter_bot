package com.newsletterbot.telegram_bot.news.provider;

import com.newsletterbot.telegram_bot.news.dto.NewsArticle;

import java.util.List;

public interface NewsProvider {
    String getCodigo();
    List<NewsArticle> buscarNoticias(String consulta);
}
