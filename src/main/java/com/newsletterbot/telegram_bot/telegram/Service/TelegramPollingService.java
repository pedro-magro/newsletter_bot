package com.newsletterbot.telegram_bot.telegram.Service;

import com.newsletterbot.telegram_bot.telegram.client.TelegramClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

@Service
@ConditionalOnProperty(
        name = "telegram.bot.pooling.enabled",
        havingValue = "true"
)
public class TelegramPollingService {

    private final TelegramClient telegram;
    private final TelegramUpdateService updateService;

    private long offset = 0;

    public TelegramPollingService(
            TelegramClient telegram,
            TelegramUpdateService updateService
    ){
        this.telegram = telegram;
        this.updateService = updateService;
    }

    @Scheduled(fixedDelay = 1000)
    public void consultarAtualizacoes(){
        try {
            JsonNode resposta = telegram.getUpdates(offset);

            if(resposta == null || !resposta.path("ok").asBoolean(false)){
                return;
            }

            for(JsonNode update : resposta.path("result")) {
                long updateId = update.path("update_id")
                        .asLong();

                if(updateId < offset){
                    continue;
                }

                updateService.processar(update);

                offset = updateId + 1;
            }
        }
        catch (Exception e){
            System.err.println(
                    "Erro ao consultar Telegram: "
                    + e.getClass().getSimpleName()
            );
        }
    }
}
