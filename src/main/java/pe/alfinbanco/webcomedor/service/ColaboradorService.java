package pe.alfinbanco.webcomedor.service;

import pe.alfinbanco.webcomedor.entity.ColaboradorEntity;

public interface ColaboradorService {
    ColaboradorEntity validarColaborador(String user, String password);
}
