package SpringWialonApplication.repository;

import SpringWialonApplication.model.Ppc;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PpcRepository extends CrudRepository<Ppc, Integer> {

    @Query(value = "SELECT * FROM ppc.ppc " +
            "WHERE (id_ppc, id) IN (" +
            "SELECT id_ppc, MAX(id) FROM ppc.ppc GROUP BY id_ppc)", nativeQuery = true)
    List<List<String>> getUniqueIdPpcLastData();

    @Query(value = "SELECT voltage, pos_x, pos_y, speed, timestamp, mileage FROM ppc.ppc " +
            "where id_ppc = ?1 and timestamp between ?2 and ?3  ORDER BY timestamp ASC limit 10000", nativeQuery = true)
    List<List<Object>> getHistoryDataById(Integer id_ppc, Integer startTime, Integer endTime);

    @Query(value = "SELECT max(mileage) - min(mileage) as mileage, CAST(avg(speed) AS INT), max(speed), max(name)as name FROM ppc.ppc " +
            "where id_ppc = ?1 and timestamp between ?2 and ?3", nativeQuery = true)
    List<List<String>> getReport(Integer id_ppc, Integer startTime, Integer endTime);

}
