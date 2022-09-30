package SpringWialonApplication;

import SpringWialonApplication.service.ServiceTelegramBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


@SpringBootApplication
public class SpringWialonApplication {


    public static void main(String[] args) {



        SpringApplication.run(SpringWialonApplication.class, args);

    }

}
