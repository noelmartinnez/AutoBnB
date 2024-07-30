package autobnb.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "marca")
public class Marca implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    private String nombre;

    @OneToMany(mappedBy = "marca", cascade = CascadeType.ALL)
    Set<Modelo> modelos = new HashSet<>();

    @OneToMany(mappedBy = "marca")
    Set<Vehiculo> vehiculos = new HashSet<>();

    public Marca() {}

    public Marca(String nombre) {
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

    public Set<Modelo> getModelos() {
        return modelos;
    }

    public void addModelos(Modelo modelo) {
        if (modelos.contains(modelo)) return;
        modelos.add(modelo);
        if (modelo.getMarca() != this) {
            modelo.setMarca(this);
        }
    }

    public Set<Vehiculo> getVehiculos() {
        return vehiculos;
    }

    public void addVehiculos(Vehiculo vehiculo) {
        if (vehiculos.contains(vehiculo)) return;
        vehiculos.add(vehiculo);
        if (vehiculo.getMarca() != this) {
            vehiculo.setMarca(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Marca marca = (Marca) o;

        // Si tenemos los ID, comparamos por ID
        if (id != null && marca.id != null) {
            return Objects.equals(id, marca.id);
        }

        // Si no tenemos ID, comparamos por campos obligatorios
        return nombre.equals(marca.nombre);
    }

    @Override
    public int hashCode() {
        // Generamos un hash basado en los campos obligatorios
        return Objects.hash(nombre);
    }
}
