package SpringWialonApplication.restController;

import SpringWialonApplication.api.response.WabcoReportResponse;
import SpringWialonApplication.service.ServiceTelegramBot;
import SpringWialonApplication.service.ServiceWialon;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;


@CrossOrigin(origins = {"http://192.168.3.41:8080", "http://192.168.3.148:3000", "http://192.168.3.41:3000", "http://192.168.3.152:8080", "http://iot.sespel.com", "http://89.151.134.234:16080"}, maxAge = 3600)
@RestController
@RequestMapping("/api")
public class RestApiWialon {

    private final ServiceWialon serviceWialon;

    public RestApiWialon(ServiceWialon serviceWialon) {
        this.serviceWialon = serviceWialon;
    }



    @GetMapping(value = "/infoData", produces = MediaType.APPLICATION_JSON_VALUE)
    private String info() {
        return serviceWialon.findALlTrailersWialon();
    }

    @GetMapping("/countData")
    private int countData() {
//        ServiceTelegramBot serviceTelegramBot = new ServiceTelegramBot();
//        serviceTelegramBot.sendMassage(1716365542,"test!");
        return serviceWialon.countData();
    }

    @GetMapping("/infoObject/id:{idObject}")
    private String infoObject(@PathVariable Integer idObject) {
        return serviceWialon.infoObject(idObject);
    }

    @GetMapping("/reportBase/id:{idObject}from:{startTime}to:{lastTime}")
    private String reportBaseObject(@PathVariable Integer idObject, @PathVariable Integer startTime, @PathVariable Integer lastTime) {
        return serviceWialon.reportBase(idObject, startTime, lastTime);
    }

    @GetMapping("/reportWabco/id:{idObject}from:{startTime}to:{lastTime}")
    private WabcoReportResponse reportWabcoObject(@PathVariable Integer idObject, @PathVariable Integer startTime, @PathVariable Integer lastTime) {
        return serviceWialon.reportWabco(idObject, startTime, lastTime);
    }

}
