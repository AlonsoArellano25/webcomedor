package pe.alfinbanco.webcomedor.service.impl;

import org.springframework.stereotype.Service;

import pe.alfinbanco.webcomedor.entity.ColaboradorEntity;
import pe.alfinbanco.webcomedor.repository.ColaboradorRepository;
import pe.alfinbanco.webcomedor.service.ColaboradorService;

@Service
public class ColaboradorServiceImpl implements ColaboradorService {

    private final ColaboradorRepository colaboradorRepository;

    public ColaboradorServiceImpl(ColaboradorRepository colaboradorRepository) {
        this.colaboradorRepository = colaboradorRepository;
    }

    @Override
    public ColaboradorEntity validarColaborador(String user, String password) {
        return colaboradorRepository.validar(user, password).orElse(null);
    }
}
