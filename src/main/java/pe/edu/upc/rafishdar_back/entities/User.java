package pe.edu.upc.rafishdar_back.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombres;
    private String apellidos;
    private String correo;
    private String password;
    private String estado;

    @OneToMany(mappedBy = "usuario")
    private List<Embarcacion> embarcaciones;

    @OneToMany(mappedBy = "usuario")
    private List<BitacoraFaena> bitacoras;
}
