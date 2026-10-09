package pe.edu.upc.rafishdar_back.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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

    @Column(nullable = false, unique = true)
    private String correo;

    private String password;
    private String estado;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "usuario")
    private List<Embarcacion> embarcaciones;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "usuario")
    private List<BitacoraFaena> bitacoras;

    @JsonIgnore
    @ToString.Exclude
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_authorities",
            joinColumns = {
                    @JoinColumn(
                            name = "user_id",
                            referencedColumnName = "id",
                            nullable = false
                    )
            },
            inverseJoinColumns = {
                    @JoinColumn(
                            name = "authority_id",
                            referencedColumnName = "id",
                            nullable = false
                    )
            }
    )
    private List<Authority> authorities;
}