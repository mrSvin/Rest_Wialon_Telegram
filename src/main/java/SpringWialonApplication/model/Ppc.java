package SpringWialonApplication.model;

import javax.persistence.*;

@Entity
@Table(name = "ppc")
public class Ppc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "timestamp")
    private Integer timestamp;

    @Column(name = "id_ppc")
    private Integer idPpc;

    @Column(name = "name", columnDefinition = "VARCHAR(100)")
    private String name;

    @Column(name = "pos_X", columnDefinition = "VARCHAR(12)")
    private String posX;

    @Column(name = "pos_Y", columnDefinition = "VARCHAR(12)")
    private String posY;

    @Column(name = "speed")
    private Integer speed;

    @Column(name = "mileage")
    private Integer mileage;

    @Column(name = "voltage", columnDefinition = "VARCHAR(8)")
    private String voltage;

    @Column(name = "wabco", columnDefinition = "VARCHAR(600)")
    private String wabco;

}


