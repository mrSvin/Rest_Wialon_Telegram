package SpringWialonApplication.service;

import SpringWialonApplication.api.response.WabcoReportResponse;
import SpringWialonApplication.repository.PpcRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DataService {

    private final JwtService jwtService;
    private final PpcRepository ppcRepository;

    public DataService(JwtService jwtService, PpcRepository ppcRepository) {
        this.jwtService = jwtService;
        this.ppcRepository = ppcRepository;
    }

    public List<WabcoReportResponse> reportNowAllTrailers(String jwtToken) {

        List<WabcoReportResponse> result = new ArrayList<>();

        String checkJwt = jwtService.checkJWT(jwtToken, "all");
        WabcoReportResponse mapData = new WabcoReportResponse();
        if (checkJwt.equals("error")) {

            mapData.setError("invalid data");
            result.add(mapData);
            return result;
        } else if (checkJwt.equals("no rights")) {
            mapData.setError("no rights");
            result.add(mapData);
            return result;
        }

        List<List<String>> data = ppcRepository.getUniqueIdPpcLastData();
        for (int i = 0; i < data.size(); i++) {
            mapData.setId_ppc(data.get(i).get(1));
            mapData.setMileage(data.get(i).get(2));
            mapData.setName(data.get(i).get(3));
            mapData.setPos_x(data.get(i).get(4));
            mapData.setPos_y(data.get(i).get(5));
            mapData.setSpeed(data.get(i).get(6));
            mapData.setTimestamp(data.get(i).get(7));
            mapData.setVoltage(data.get(i).get(8));
            mapData.setWabco(data.get(i).get(9));
            result.add(mapData);
        }

        return result;

    }

    public Map<String, Object> reportHistoryTrailer(String jwtToken, int idTrailer, int startTime, int endTime) {

        Map<String, Object> result = new HashMap<>();

        String checkJwt = jwtService.checkJWT(jwtToken, "all");
        if (checkJwt.equals("error")) {
            result.put("error", "invalid data");
            return result;
        } else if (checkJwt.equals("no rights")) {
            result.put("error", "no right");
            return result;
        }

        List<List<Object>> data = ppcRepository.getHistoryDataById(idTrailer, startTime, endTime);
        List<Object> voltage = new ArrayList<>();
        List<Object> posX = new ArrayList<>();
        List<Object> posY = new ArrayList<>();
        List<Object> speed = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            voltage.add(data.get(i).get(0));
            posX.add(data.get(i).get(1));
            posY.add(data.get(i).get(2));
            speed.add(data.get(i).get(3));
        }
        result.put("voltage", voltage);
        result.put("pos_x", posX);
        result.put("pos_y", posY);
        result.put("speed", speed);

        List<List<String>> report = ppcRepository.getReport(idTrailer, startTime, endTime);
        result.put("mileage", report.get(0).get(0));
        result.put("avg_speed", report.get(0).get(1));
        result.put("max_speed", report.get(0).get(2));
        result.put("name", report.get(0).get(3));
        return result;
    }

}
