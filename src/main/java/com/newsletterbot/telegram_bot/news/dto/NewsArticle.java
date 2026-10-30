package com.newsletterbot.telegram_bot.news.dto;

import java.time.OffsetDateTime;

public record NewsArticle(
        String titulo,
        String descricao,
        String url,
        String fonte,
        OffsetDateTime publicadaEm
) {
}
