package pe.alfinbanco.webcomedor.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "colaborador")
public class ColaboradorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcolaborador")
    private Integer idColaborador;

    @Column(name = "user", nullable = false, length = 50)
    private String user;

    @Column(name = "password", nullable = false, length = 60)
    private String password;

    @Column(name = "nombre_apellido", nullable = false, length = 80)
    private String nombreApellido;

    @Column(name = "email", nullable = false, length = 120)
    private String email;

    /** 1 = activo, 0 = inactivo */
    @Column(name = "estado", nullable = false)
    private Integer estado;
}
