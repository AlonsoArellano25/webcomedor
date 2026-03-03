DROP DATABASE IF EXISTS comedor;
CREATE DATABASE comedor;
USE comedor;

DROP TABLE IF EXISTS colaborador;
CREATE TABLE colaborador (
  idcolaborador INT NOT NULL AUTO_INCREMENT,
  user VARCHAR(50) NOT NULL,
  password VARCHAR(60) NOT NULL,
  nombre_apellido VARCHAR(80) NOT NULL,
  email VARCHAR(120) NOT NULL,
  estado INT NOT NULL,
  PRIMARY KEY (idcolaborador),
  UNIQUE KEY uk_colaborador_user (user)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS reserva;
CREATE TABLE reserva (
  idreserva INT NOT NULL AUTO_INCREMENT,
  fecha DATE NOT NULL,
  hora TIME NOT NULL,
  estado INT NOT NULL,
  idcolaborador INT NOT NULL,
  PRIMARY KEY (idreserva),
  KEY idx_reserva_fecha_hora (fecha, hora),
  KEY idx_reserva_colaborador (idcolaborador),
  CONSTRAINT fk_reserva_colaborador FOREIGN KEY (idcolaborador) REFERENCES colaborador(idcolaborador)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TRIGGER IF EXISTS trg_reserva_before_insert;
DELIMITER $$
CREATE TRIGGER trg_reserva_before_insert
BEFORE INSERT ON reserva
FOR EACH ROW
BEGIN
  DECLARE v_count_slots INT;
  DECLARE v_count_user_day INT;

  IF NEW.estado IS NULL THEN
    SET NEW.estado = 1;
  END IF;

  IF NEW.estado = 1 THEN

    SELECT COUNT(*) INTO v_count_slots
    FROM reserva
    WHERE fecha = NEW.fecha AND hora = NEW.hora AND estado = 1;

    IF v_count_slots >= 10 THEN
      SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'No hay cupos disponibles (max 10)';
    END IF;

    SELECT COUNT(*) INTO v_count_user_day
    FROM reserva
    WHERE fecha = NEW.fecha AND idcolaborador = NEW.idcolaborador AND estado = 1;

    IF v_count_user_day >= 1 THEN
      SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Solo puedes reservar 1 vez por dia';
    END IF;

  END IF;
END$$
DELIMITER ;

DROP TRIGGER IF EXISTS trg_reserva_before_update;
DELIMITER $$
CREATE TRIGGER trg_reserva_before_update
BEFORE UPDATE ON reserva
FOR EACH ROW
BEGIN
  DECLARE v_count_slots INT;
  DECLARE v_count_user_day INT;

  IF NEW.estado = 1 THEN

    SELECT COUNT(*) INTO v_count_slots
    FROM reserva
    WHERE fecha = NEW.fecha AND hora = NEW.hora AND estado = 1 AND idreserva <> OLD.idreserva;

    IF v_count_slots >= 10 THEN
      SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'No hay cupos disponibles (max 10)';
    END IF;

    SELECT COUNT(*) INTO v_count_user_day
    FROM reserva
    WHERE fecha = NEW.fecha AND idcolaborador = NEW.idcolaborador AND estado = 1 AND idreserva <> OLD.idreserva;

    IF v_count_user_day >= 1 THEN
      SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Solo puedes reservar 1 vez por dia';
    END IF;

  END IF;
END$$
DELIMITER ;

INSERT INTO colaborador(user, password, nombre_apellido, email, estado) VALUES
('aalonso', '123456', 'Alonso Arellano Soto', 'aalonso@correo.com', 1),
('jperez',  '123456', 'Juan Perez',          'jperez@correo.com', 1),
('mlopez',  '123456', 'Maria Lopez',         'mlopez@correo.com', 1);

INSERT INTO reserva(fecha, hora, estado, idcolaborador) VALUES
('2026-03-01', '11:00:00', 1, 1),
('2026-03-02', '11:30:00', 1, 1),
('2026-03-03', '12:00:00', 1, 1),
('2026-03-04', '12:30:00', 1, 1),
('2026-03-05', '13:00:00', 1, 1),
('2026-03-06', '13:30:00', 1, 1),
('2026-03-07', '14:00:00', 1, 1),
('2026-03-08', '14:30:00', 1, 1),
('2026-03-09', '15:00:00', 1, 1),
('2026-03-10', '11:00:00', 0, 1),
('2026-03-11', '11:30:00', 0, 1),
('2026-03-12', '12:00:00', 1, 1);

INSERT INTO reserva(fecha, hora, estado, idcolaborador) VALUES
('2026-03-01', '11:00:00', 1, 2),
('2026-03-02', '11:30:00', 1, 2),
('2026-03-03', '12:00:00', 1, 2);

INSERT INTO reserva(fecha, hora, estado, idcolaborador) VALUES
('2026-03-01', '11:30:00', 1, 3),
('2026-03-02', '12:00:00', 1, 3),
('2026-03-03', '12:30:00', 1, 3);

INSERT INTO reserva(fecha, hora, estado, idcolaborador) VALUES
('2026-03-04', '11:00:00', 1, 2),
('2026-03-04', '11:00:00', 1, 3);
