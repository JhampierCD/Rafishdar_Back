package pe.edu.upc.rafishdar_back.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "detalles_captura")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class DetalleCaptura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double volumenKg;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "bitacora_id")
    private BitacoraFaena bitacora;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "especie_id")
    private EspeciePez especie;

}
