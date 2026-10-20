package com.newsletterbot.telegram_bot.telegram.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

@Component
public class TelegramClient {

    private final RestClient client;

    public TelegramClient(
            @Value("${telegram.bot.token}")
            String token
    ){
        this.client = RestClient.builder()
                .baseUrl("https://api.telegram.org/bot" + token)
                .build();
    }

    public JsonNode getUpdates(long offset){
        return client.post()
                .uri("/getUpdates")
                .body(Map.of(
                        "offset", offset,
                        "timeout", 20,
                        "allowed_updates",
                        List.of("message", "callback_query")
                ))
                .retrieve()
                .body(JsonNode.class);
    }

    public void sendMessage(Long chatId, String text){
        client.post()
                .uri("/sendMessage")
                .body(Map.of(
                        "chat_id", chatId,
                        "text", text
                ))
                .retrieve()
                .toBodilessEntity();
    }

    public void sendTopicMenu(Long chatId) {
        var keyboard = List.of(
                List.of(
                        Map.of(
                                "text", "Tecnologia",
                                "callback_data", "topic:tecnologia"
                        )
                ),
                List.of(
                        Map.of(
                                "text", "Cibersegurança",
                                "callback_data", "topic:ciberseguranca"
                        )
                ),
                List.of(
                        Map.of(
                                "text", "Economia",
                                "callback_data", "topic:economia"
                        )
                )
        );

        client.post()
                .uri("/sendMessage")
                .body(Map.of(
                        "chat_id", chatId,
                        "text", "Escolha os assuntos da sua newsletter",
                        "reply_markup", Map.of(
                                "inline_keyboard", keyboard
                        )
                ))
                .retrieve()
                .toBodilessEntity();
    }

    public void answerCallback(String callbackId){
        client.post()
                .uri("/answerCallbackQuery")
                .body(Map.of(
                        "callback_query_id", callbackId
                ))
                .retrieve()
                .toBodilessEntity();
    }


}
