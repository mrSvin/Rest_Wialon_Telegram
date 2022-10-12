package SpringWialonApplication.service;

import SpringWialonApplication.repository.TrailersRepository;
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
    private final TrailersRepository trailersRepository;
    private final ServiceWialon serviceWialon;
    private final ServiceParcer serviceParcer;

    public ServiceTelegramBot(
            TelegramBotsApi telegramBotsApi,
            @Value("${botName}") String botUsername,
            @Value("${telegramToken}") String botToken, TrailersRepository trailersRepository, ServiceWialon serviceWialon, ServiceParcer serviceParcer) throws TelegramApiException {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.trailersRepository = trailersRepository;
        this.serviceWialon = serviceWialon;
        this.serviceParcer = serviceParcer;
        telegramBotsApi.registerBot(this);
    }

    @Override
    public void onUpdateReceived(Update update) {
        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {

            String inputText = update.getMessage().getText();

            System.out.println(update.getMessage().getFrom().getFirstName() + " боту написал: " + inputText);

            if (inputText.startsWith("help")) {
                sendMassage(update.getMessage().getChatId(), "Пример использования: report 24860811");
            }

            if (inputText.startsWith("report") ) {
                if (inputText.length()>14) {
                    int objectId = Integer.parseInt(inputText.substring(7,15));
                    if (trailersRepository.countWialonId(objectId)>0) {
                        StringBuffer message = serviceParcer.objectInfo(serviceWialon.infoObject(objectId));
                        sendMassage(update.getMessage().getChatId(), String.valueOf(message));
                    }
                }

            }


        }
    }

    public void sendMassage(long idChat, String textMessage) {
//        System.out.println("Send message: " + textMessage);
        SendMessage message = new SendMessage();
        message.enableMarkdown(true);
        message.setChatId(idChat);
        message.setText(textMessage);

        try {
            execute(message); // Call method to send the message
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }



}
