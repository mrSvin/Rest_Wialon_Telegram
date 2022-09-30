package SpringWialonApplication.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Getter
public class ServiceTelegramBot extends TelegramLongPollingBot {

    private String botUsername;
    private String botToken;

    public ServiceTelegramBot(
            TelegramBotsApi telegramBotsApi,
            @Value("${botName}") String botUsername,
            @Value("${telegramToken}") String botToken) throws TelegramApiException {
        this.botUsername = botUsername;
        this.botToken = botToken;
        telegramBotsApi.registerBot(this);
    }

    @Override
    public void onUpdateReceived(Update update) {
        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {

            sendMassage(update.getMessage().getChatId(), "Hi");
            System.out.println(update.getMessage().getFrom().getFirstName() + " боту написал: " + update.getMessage().getText());

        }
    }

    public void sendMassage(long idChat, String textMessage) {
        System.out.println("Send message: " + textMessage);
        SendMessage message = new SendMessage();
        message.setChatId(idChat);
        message.setText(textMessage);

        try {
            execute(message); // Call method to send the message
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


}
