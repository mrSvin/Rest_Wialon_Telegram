package SpringWialonApplication.restController;

import SpringWialonApplication.api.response.WabcoReportResponse;
import SpringWialonApplication.service.ServiceWialon;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {"*"}, maxAge = 3600)
@RestController
@RequestMapping("/api")
public class RestApiWialon {

    private final ServiceWialon serviceWialon;

    public RestApiWialon(ServiceWialon serviceWialon) {
        this.serviceWialon = serviceWialon;
    }


    @PostMapping(value = "/infoData", produces = MediaType.APPLICATION_JSON_VALUE)
    private String info(@RequestHeader("Authorization") String jwtBearer) {
        return serviceWialon.findAllTrailersWialon(jwtBearer);
    }


    @PostMapping("/infoObject/id:{idObject}")
    private String infoObject(@PathVariable Integer idObject, @RequestHeader("Authorization") String jwtBearer) {
        return serviceWialon.infoObject(idObject, jwtBearer);
    }

    @PostMapping("/reportBase/id:{idObject}from:{startTime}to:{lastTime}")
    private String reportBaseObject(@PathVariable Integer idObject,
                                    @PathVariable Integer startTime,
                                    @PathVariable Integer lastTime,
                                    @RequestHeader("Authorization") String jwtBearer) {
        return serviceWialon.reportBase(idObject, startTime, lastTime, jwtBearer);
    }

    @PostMapping("/reportWabco/id:{idObject}from:{startTime}to:{lastTime}")
    private WabcoReportResponse reportWabcoObject(@PathVariable Integer idObject,
                                                  @PathVariable Integer startTime,
                                                  @PathVariable Integer lastTime,
                                                  @RequestHeader("Authorization") String jwtBearer) {
        return serviceWialon.reportWabco(idObject, startTime, lastTime, jwtBearer);
    }

}
