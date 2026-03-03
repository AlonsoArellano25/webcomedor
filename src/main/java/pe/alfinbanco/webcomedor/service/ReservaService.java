package pe.alfinbanco.webcomedor.service;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.domain.Page;

import pe.alfinbanco.webcomedor.entity.ReservaEntity;

public interface ReservaService {

    Page<ReservaEntity> listarMisReservas(Integer idColaborador, int pag, int size);

    ReservaEntity crearReserva(Integer idColaborador, LocalDate fecha, LocalTime hora);

    ReservaEntity obtenerPorId(Integer idReserva);

    ReservaEntity actualizarReserva(Integer idReserva, Integer idColaborador, LocalDate fecha, LocalTime hora);

    void cancelarReserva(Integer idReserva, Integer idColaborador);

    boolean tieneReservaActivaEnFecha(Integer idColaborador, LocalDate fecha);

    long slotsOcupados(LocalDate fecha, LocalTime hora);
}
