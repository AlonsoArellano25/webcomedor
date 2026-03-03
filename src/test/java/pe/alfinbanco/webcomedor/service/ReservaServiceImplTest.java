package pe.alfinbanco.webcomedor.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pe.alfinbanco.webcomedor.entity.ColaboradorEntity;
import pe.alfinbanco.webcomedor.entity.ReservaEntity;
import pe.alfinbanco.webcomedor.repository.ColaboradorRepository;
import pe.alfinbanco.webcomedor.repository.ReservaRepository;
import pe.alfinbanco.webcomedor.service.impl.ReservaServiceImpl;

@ExtendWith(MockitoExtension.class)
class ReservaServiceImplTest {

    @Mock
    ReservaRepository reservaRepository;

    @Mock
    ColaboradorRepository colaboradorRepository;

    @InjectMocks
    ReservaServiceImpl reservaService;

    @Test
    void crearReserva_ok() {
        Integer idCol = 1;
        LocalDate fecha = LocalDate.of(2026, 3, 1);
        LocalTime hora = LocalTime.of(11, 0);

        when(reservaRepository.findByColaborador_IdColaboradorAndFechaAndEstado(idCol, fecha, 1))
                .thenReturn(Optional.empty());
        when(reservaRepository.countByFechaAndHoraAndEstado(fecha, hora, 1)).thenReturn(3L);
        when(colaboradorRepository.findById(idCol)).thenReturn(Optional.of(ColaboradorEntity.builder().idColaborador(idCol).build()));
        when(reservaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ReservaEntity r = reservaService.crearReserva(idCol, fecha, hora);
        assertNotNull(r);
        assertEquals(1, r.getEstado());
    }

    @Test
    void crearReserva_fail_por_regla_uno_por_dia() {
        Integer idCol = 1;
        LocalDate fecha = LocalDate.of(2026, 3, 1);
        LocalTime hora = LocalTime.of(11, 0);

        when(reservaRepository.findByColaborador_IdColaboradorAndFechaAndEstado(idCol, fecha, 1))
                .thenReturn(Optional.of(ReservaEntity.builder().idReserva(10).build()));

        ReservaException ex = assertThrows(ReservaException.class, () ->
                reservaService.crearReserva(idCol, fecha, hora)
        );

        assertTrue(ex.getMessage().toLowerCase().contains("1 vez"));
    }

    @Test
    void crearReserva_fail_por_slots() {
        Integer idCol = 1;
        LocalDate fecha = LocalDate.of(2026, 3, 1);
        LocalTime hora = LocalTime.of(11, 0);

        when(reservaRepository.findByColaborador_IdColaboradorAndFechaAndEstado(idCol, fecha, 1))
                .thenReturn(Optional.empty());
        when(reservaRepository.countByFechaAndHoraAndEstado(fecha, hora, 1)).thenReturn(10L);

        ReservaException ex = assertThrows(ReservaException.class, () ->
                reservaService.crearReserva(idCol, fecha, hora)
        );

        assertTrue(ex.getMessage().toLowerCase().contains("cupos"));
    }

    @Test
    void cancelarReserva_ok() {
        Integer idCol = 1;
        ReservaEntity r = ReservaEntity.builder()
                .idReserva(7)
                .estado(1)
                .colaborador(ColaboradorEntity.builder().idColaborador(idCol).build())
                .build();

        when(reservaRepository.findById(7)).thenReturn(Optional.of(r));
        when(reservaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        reservaService.cancelarReserva(7, idCol);
        assertEquals(0, r.getEstado());
    }
}
