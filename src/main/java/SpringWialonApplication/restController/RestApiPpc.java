package SpringWialonApplication.restController;

import SpringWialonApplication.api.response.WabcoReportResponse;
import SpringWialonApplication.service.DataService;
import com.fasterxml.jackson.annotation.JsonRawValue;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"*"}, maxAge = 3600)
@RestController
@RequestMapping("/api")
public class RestApiPpc {

    private final DataService dataService;

    public RestApiPpc(DataService dataService) {
        this.dataService = dataService;
    }

    @PostMapping(value = "/infoData")
    private List<WabcoReportResponse> info(@RequestHeader("Authorization") String jwtBearer) {
        return dataService.reportNowAllTrailers(jwtBearer);
    }


    @PostMapping("/reportHistory/id:{idObject}from:{startTime}to:{lastTime}")
    private Map<String, Object> reportWabcoObject(@PathVariable Integer idObject,
                                                  @PathVariable Integer startTime,
                                                  @PathVariable Integer lastTime,
                                                  @RequestHeader("Authorization") String jwtBearer) {
        return dataService.reportHistoryTrailer(jwtBearer, idObject, startTime, lastTime);
    }

}
