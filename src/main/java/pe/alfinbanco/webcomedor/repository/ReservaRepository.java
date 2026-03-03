package pe.alfinbanco.webcomedor.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import pe.alfinbanco.webcomedor.entity.ReservaEntity;

public interface ReservaRepository extends JpaRepository<ReservaEntity, Integer> {

    Page<ReservaEntity> findByColaborador_IdColaboradorOrderByFechaDescHoraDesc(Integer idColaborador, Pageable pageable);

    Optional<ReservaEntity> findByColaborador_IdColaboradorAndFechaAndEstado(Integer idColaborador, LocalDate fecha, Integer estado);

    long countByFechaAndHoraAndEstado(LocalDate fecha, LocalTime hora, Integer estado);
}
