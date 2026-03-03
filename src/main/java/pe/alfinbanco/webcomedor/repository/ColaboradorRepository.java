package pe.alfinbanco.webcomedor.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.alfinbanco.webcomedor.entity.ColaboradorEntity;

public interface ColaboradorRepository extends JpaRepository<ColaboradorEntity, Integer> {

    @Query(value = "SELECT * FROM colaborador c WHERE c.user = :user AND c.password = :password AND c.estado = 1 LIMIT 1", nativeQuery = true)
    Optional<ColaboradorEntity> validar(@Param("user") String user, @Param("password") String password);
}
