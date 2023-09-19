package SpringWialonApplication.api.response;

import lombok.Data;

@Data
public class WabcoReportResponse {

    private String voltage;
    private String possitionsX;
    private String possitionsY;
    private String speed;
    private String time;
    private String error;

}
