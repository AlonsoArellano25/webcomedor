package pe.alfinbanco.webcomedor.service.impl;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import pe.alfinbanco.webcomedor.entity.ColaboradorEntity;
import pe.alfinbanco.webcomedor.entity.ReservaEntity;
import pe.alfinbanco.webcomedor.repository.ColaboradorRepository;
import pe.alfinbanco.webcomedor.repository.ReservaRepository;
import pe.alfinbanco.webcomedor.service.ReservaException;
import pe.alfinbanco.webcomedor.service.ReservaService;

@Service
public class ReservaServiceImpl implements ReservaService {

    public static final int MAX_SLOTS_POR_HORA = 10;

    private final ReservaRepository reservaRepository;
    private final ColaboradorRepository colaboradorRepository;

    public ReservaServiceImpl(ReservaRepository reservaRepository, ColaboradorRepository colaboradorRepository) {
        this.reservaRepository = reservaRepository;
        this.colaboradorRepository = colaboradorRepository;
    }

    @Override
    public Page<ReservaEntity> listarMisReservas(Integer idColaborador, int pag, int size) {
        return reservaRepository.findByColaborador_IdColaboradorOrderByFechaDescHoraDesc(
                idColaborador,
                PageRequest.of(Math.max(pag, 1) - 1, Math.max(size, 1))
        );
    }

    @Override
    public ReservaEntity crearReserva(Integer idColaborador, LocalDate fecha, LocalTime hora) {
        if (fecha == null || hora == null) {
            throw new ReservaException("Debe seleccionar fecha y hora");
        }
        if (tieneReservaActivaEnFecha(idColaborador, fecha)) {
            throw new ReservaException("Solo puedes reservar 1 vez por día");
        }
        long ocupados = slotsOcupados(fecha, hora);
        if (ocupados >= MAX_SLOTS_POR_HORA) {
            throw new ReservaException("No hay cupos disponibles para esa hora (máximo 10)");
        }

        ColaboradorEntity col = colaboradorRepository.findById(idColaborador)
                .orElseThrow(() -> new ReservaException("Colaborador no existe"));

        ReservaEntity r = ReservaEntity.builder()
                .colaborador(col)
                .fecha(fecha)
                .hora(hora)
                .estado(1)
                .build();

        return reservaRepository.save(r);
    }

    @Override
    public ReservaEntity obtenerPorId(Integer idReserva) {
        return reservaRepository.findById(idReserva)
                .orElseThrow(() -> new ReservaException("Reserva no encontrada"));
    }

    @Override
    public ReservaEntity actualizarReserva(Integer idReserva, Integer idColaborador, LocalDate fecha, LocalTime hora) {
        ReservaEntity actual = obtenerPorId(idReserva);

        if (!actual.getColaborador().getIdColaborador().equals(idColaborador)) {
            throw new ReservaException("No tienes permisos para editar esta reserva");
        }
        if (actual.getEstado() == null || actual.getEstado() != 1) {
            throw new ReservaException("Solo puedes editar reservas activas");
        }

        if (fecha == null || hora == null) {
            throw new ReservaException("Debe seleccionar fecha y hora");
        }

        boolean cambiaFecha = !fecha.equals(actual.getFecha());
        if (cambiaFecha && tieneReservaActivaEnFecha(idColaborador, fecha)) {
            throw new ReservaException("Solo puedes reservar 1 vez por día");
        }

        boolean cambiaFechaHora = cambiaFecha || !hora.equals(actual.getHora());
        if (cambiaFechaHora) {
            long ocupados = slotsOcupados(fecha, hora);
            if (ocupados >= MAX_SLOTS_POR_HORA) {
                throw new ReservaException("No hay cupos disponibles para esa hora (máximo 10)");
            }
        }

        actual.setFecha(fecha);
        actual.setHora(hora);
        return reservaRepository.save(actual);
    }

    @Override
    public void cancelarReserva(Integer idReserva, Integer idColaborador) {
        ReservaEntity r = obtenerPorId(idReserva);
        if (!r.getColaborador().getIdColaborador().equals(idColaborador)) {
            throw new ReservaException("No tienes permisos para cancelar esta reserva");
        }
        if (r.getEstado() != null && r.getEstado() == 0) {
            return;
        }
        r.setEstado(0);
        reservaRepository.save(r);
    }

    @Override
    public boolean tieneReservaActivaEnFecha(Integer idColaborador, LocalDate fecha) {
        return reservaRepository.findByColaborador_IdColaboradorAndFechaAndEstado(idColaborador, fecha, 1).isPresent();
    }

    @Override
    public long slotsOcupados(LocalDate fecha, LocalTime hora) {
        return reservaRepository.countByFechaAndHoraAndEstado(fecha, hora, 1);
    }
}
