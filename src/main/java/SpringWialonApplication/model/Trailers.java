package SpringWialonApplication.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class Trailers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "VARCHAR(90)")
    private String name;
    @Column(columnDefinition = "INT")
    private Integer wialon_id;
    @Column(columnDefinition = "INT")
    private Integer mileage;
    @Column(columnDefinition = "INT")
    private Integer last_connect;


}
