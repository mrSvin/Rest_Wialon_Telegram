package SpringWialonApplication.api.response;

import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.Data;

@Data
public class WabcoReportResponse {

    private String id_ppc;
    private String mileage;
    private String name;
    private String pos_x;
    private String pos_y;
    private String speed;
    private String timestamp;
    private String voltage;
    @JsonRawValue
    private String wabco;

    private String error;

}
