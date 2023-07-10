package SpringWialonApplication.repository;

import SpringWialonApplication.model.Trailers;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;


@Repository
public interface TrailersRepository extends CrudRepository<Trailers, Long> {

    @Query(value = "SELECT count(*) FROM public.trailers WHERE wialon_id=?1", nativeQuery = true)
    int countWialonId(Object wialon_id);

    @Query(value = "SELECT count(*) FROM public.trailers", nativeQuery = true)
    int countWialonTrailers();

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO public.trailers (name, wialon_id) VALUES (?1, ?2)", nativeQuery = true)
    void addWialonData(Object complex_name, Object info_work);

    @Override
    Iterable<Trailers> findAll();

}
