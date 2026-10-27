package com.newsletterbot.telegram_bot.telegram.Service;

import com.newsletterbot.telegram_bot.telegram.client.TelegramClient;
import com.newsletterbot.telegram_bot.topic.service.AssuntoService;
import com.newsletterbot.telegram_bot.user.service.UsuarioService;
import org.springframework.boot.jackson.autoconfigure.JacksonProperties;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TelegramUpdateService {

    private final TelegramClient telegram;
    private final UsuarioService usuarioService;
    private final AssuntoService assuntoService;

    public TelegramUpdateService(TelegramClient telegram, UsuarioService usuarioService, AssuntoService assuntoService) {
        this.telegram = telegram;
        this.usuarioService = usuarioService;
        this.assuntoService = assuntoService;
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
                (message.path("chat").path("type").asString(""))){
            return;
        }

        if(!message.path("chat").path("id").isNumber()
        || ! message.path("from").path("id").isNumber()){
            return;
        }

        Long telegramId = message.path("from")
                .path("id").asLong();
        Long chatId = message.path("chat")
                .path("id").asLong();
        String nome = message.path("from")
                .path("first_name").asString("");

        String comando = message.path("text").asString("");

        switch(comando) {
            case "/start" -> {
                usuarioService.registrar(telegramId, chatId, nome);
                telegram.sendMessage(
                        chatId,
                        "Bem vindo ao NewsBot!"
                );
                telegram.sendTopicMenu(chatId, assuntoService.listarAtivos());
            }

            case "/assuntos" ->  telegram.sendTopicMenu(chatId, assuntoService.listarAtivos());

            case "/minhas_preferencias" -> {
                var preferencias = usuarioService.consultarPreferencias(telegramId);

                String resposta = preferencias.isEmpty() ? "Você ainda não escolheu assuntos."
                        : "Seus assuntos:\n- "
                        + String.join("\n- ", preferencias);
                telegram.sendMessage(chatId, resposta);
            }

            default -> telegram.sendMessage(chatId,
                    "Comandos disponíveis:\n"
                    + "/start\n"
                    +"/assuntos\n"
                    +"/minhas_preferencias"
            );
        }
    }

    private void processarCallback(JsonNode callback) {
        String callbackId = callback.path("id").asString();
        String data = callback.path("data").asString("");

       JsonNode origem = callback.path("message")
               .path("chat");

       if(!"private".equals(origem.path("type").asString())
               || !origem.path("id").isNumber()
               || !callback.path("from").path("id").isNumber()){
           return;
       }

       Long telegramId = callback.path("from")
               .path("id").asLong();
       Long chatId = origem.path("id").asLong();

       if(data.startsWith("subscribe:")){
           String slug = data.substring("subscribe:".length());
           usuarioService.inscrever(telegramId, slug);

           telegram.answerCallback(callbackId);

           telegram.sendMessage(chatId,
                   "Inscrição realizada: " + slug
                   );
       }
       else if(data.startsWith("unsubscribe:")){

           String slug = data.substring("unsubscribe:".length());
           usuarioService.cancelarInscricao(telegramId, slug);
           telegram.answerCallback(callbackId);
           telegram.sendMessage(chatId,
                   "Inscrição cancelada: " + slug);
       }
       else{
           telegram.answerCallback(callbackId);
       }

    }
}
