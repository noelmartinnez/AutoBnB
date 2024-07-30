package autobnb.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "comentario")
public class Comentario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String descripcion;

    @NotNull
    @Temporal(TemporalType.DATE)
    private Date fechaCreacion;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "idVehiculo")
    private Vehiculo vehiculo;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "idUsuario")
    private Usuario usuario;

    public Comentario() {}

    public Comentario(String descripcion, Date fechaCreacion, Vehiculo vehiculo, Usuario usuario) {
        this.descripcion = descripcion;
        this.fechaCreacion = fechaCreacion;
        this.vehiculo = vehiculo;
        this.usuario = usuario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comentario comentario = (Comentario) o;

        // Si tenemos los ID, comparamos por ID
        if (id != null && comentario.id != null) {
            return Objects.equals(id, comentario.id);
        }

        // Si no tenemos ID, comparamos por campos obligatorios
        return descripcion.equals(comentario.descripcion) &&
                fechaCreacion.equals(comentario.fechaCreacion) &&
                vehiculo.equals(comentario.vehiculo) &&
                usuario.equals(comentario.usuario);
    }

    @Override
    public int hashCode() {
        // Generamos un hash basado en los campos obligatorios
        return Objects.hash(descripcion, fechaCreacion, vehiculo, usuario);
    }
}
