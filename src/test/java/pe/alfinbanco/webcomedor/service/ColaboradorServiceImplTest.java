package pe.alfinbanco.webcomedor.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pe.alfinbanco.webcomedor.entity.ColaboradorEntity;
import pe.alfinbanco.webcomedor.repository.ColaboradorRepository;
import pe.alfinbanco.webcomedor.service.impl.ColaboradorServiceImpl;

@ExtendWith(MockitoExtension.class)
class ColaboradorServiceImplTest {

    @Mock
    ColaboradorRepository colaboradorRepository;

    @InjectMocks
    ColaboradorServiceImpl colaboradorService;

    @Test
    void validarColaborador_ok() {
        ColaboradorEntity col = ColaboradorEntity.builder().idColaborador(1).user("aalonso").build();
        when(colaboradorRepository.validar("aalonso", "123456")).thenReturn(Optional.of(col));

        ColaboradorEntity result = colaboradorService.validarColaborador("aalonso", "123456");
        assertNotNull(result);
        assertEquals(1, result.getIdColaborador());
    }

    @Test
    void validarColaborador_fail() {
        when(colaboradorRepository.validar("x", "y")).thenReturn(Optional.empty());
        assertNull(colaboradorService.validarColaborador("x", "y"));
    }
}
