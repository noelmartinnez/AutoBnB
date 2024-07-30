package autobnb.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "color")
public class Color implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    private String nombre;

    @OneToMany(mappedBy = "color")
    Set<Vehiculo> vehiculos = new HashSet<>();

    public Color() {}

    public Color(String nombre) {
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
        if (vehiculo.getColor() != this) {
            vehiculo.setColor(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Color color = (Color) o;

        // Si tenemos los ID, comparamos por ID
        if (id != null && color.id != null) {
            return Objects.equals(id, color.id);
        }

        // Si no tenemos ID, comparamos por campos obligatorios
        return nombre.equals(color.nombre);
    }

    @Override
    public int hashCode() {
        // Generamos un hash basado en los campos obligatorios
        return Objects.hash(nombre);
    }
}
