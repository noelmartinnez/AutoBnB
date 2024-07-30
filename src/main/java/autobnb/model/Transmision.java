package autobnb.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "transmision")
public class Transmision implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    private String nombre;

    @OneToMany(mappedBy = "transmision")
    Set<Vehiculo> vehiculos = new HashSet<>();

    public Transmision() {}

    public Transmision(String nombre) {
        this.nombre = nombre;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Set<Vehiculo> getVehiculos() {
        return vehiculos;
    }

    public void addVehiculos(Vehiculo vehiculo) {
        if (vehiculos.contains(vehiculo)) return;
        vehiculos.add(vehiculo);
        if (vehiculo.getTransmision() != this) {
            vehiculo.setTransmision(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transmision transmision = (Transmision) o;

        // Si tenemos los ID, comparamos por ID
        if (id != null && transmision.id != null) {
            return Objects.equals(id, transmision.id);
        }

        // Si no tenemos ID, comparamos por campos obligatorios
        return nombre.equals(transmision.nombre);
    }

    @Override
    public int hashCode() {
        // Generamos un hash basado en los campos obligatorios
        return Objects.hash(nombre);
    }
}
