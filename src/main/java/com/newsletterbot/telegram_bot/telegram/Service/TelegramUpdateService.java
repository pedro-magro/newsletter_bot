package com.newsletterbot.telegram_bot.telegram.Service;

import com.newsletterbot.telegram_bot.telegram.client.TelegramClient;
import org.springframework.boot.jackson.autoconfigure.JacksonProperties;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TelegramUpdateService {

    private final TelegramClient telegram;

    private final Map<Long, Set<String>> preferencias = new ConcurrentHashMap<>();

    private static final Set<String> ASSUNTOS_VALIDOS = Set.of("tecnologia", "cibersegurança", "economia");

    public TelegramUpdateService(TelegramClient telegram){
            this.telegram = telegram;
    }

    public void processar(JsonNode update) {
        JsonNode message = update.path("message");

        if(!message.isMissingNode()){
            processarMensagem(message);
            return;
        }
        JsonNode callback = update.path("callback_query");
        if(!callback.isMissingNode()){
            processarCallback(callback);
        }
    }

    private void processarMensagem(JsonNode message) {
        if(!"private".equals
                (message.path("chat").path("type").asText(""))){
            return;
        }

        String texto = message.path("text").asText("");
        Long chatId = message.path("chat")
                .path("id").asLong();

        if(texto.equals("/start")){
            preferencias.putIfAbsent(
                    chatId,
                    ConcurrentHashMap.newKeySet()
            );

            telegram.sendMessage(chatId,
                    "Bem vindo ao NewsBot!");

            telegram.sendTopicMenu(chatId);
        }
    }

    private void processarCallback(JsonNode callback) {
        String callbackId = callback.path("id").asText();
        String data = callback.path("data").asText("");

        if(!data.startsWith("topic:")){
            telegram.answerCallback(callbackId);
            return;
        }

        if(!"private".equals(
                callback.path("message")
                        .path("chat")
                        .path("type").asText()
        )) {
            telegram.answerCallback(callbackId);
            return;
        }

        String slug = data.substring(6);
        if(!ASSUNTOS_VALIDOS.contains(slug)) {
            telegram.answerCallback(callbackId);
            return;
        }

        Long usuarioId = callback.path("form")
                .path("id").asLong();

        Long chatId = callback.path("message")
                .path("chat").path("id").asLong();

        Set <String> assuntos = preferencias.computeIfAbsent(usuarioId, id-> ConcurrentHashMap.newKeySet());

        boolean adicionado;

        if(assuntos.contains(slug)){
            assuntos.remove(slug);
            adicionado = false;
        }
        else{
            assuntos.add(slug);
            adicionado = true;
        }

        telegram.answerCallback(callbackId);

        telegram.sendMessage(
                chatId,
                adicionado
                        ? "Assunto adicionado:" + slug
                        : "Assunto removido:" + slug
        );
    }
}
