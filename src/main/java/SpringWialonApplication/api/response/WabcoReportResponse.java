package SpringWialonApplication.api.response;

import lombok.Data;

import java.util.List;

@Data
public class WabcoReportResponse {

    private List<String> loadAxle;
    private String pressuareSystem;
    private List<String> pressuareWheels;
    private String voltage;
    private String possitions;
    private String speed;

}
