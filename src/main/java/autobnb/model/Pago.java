package autobnb.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "pago")
public class Pago implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(length = 50)
    private String titular;

    @NotNull
    @Column(length = 16)
    private String numeroTarjeta;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "idUsuario")
    private Usuario usuario;

    @OneToOne(mappedBy = "pago")
    private Alquiler alquiler;

    public Pago() {
    }

    public Pago(String titular, String numeroTarjeta, Usuario usuario) {
        this.titular = titular;
        this.numeroTarjeta = numeroTarjeta;
        this.usuario = usuario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Alquiler getAlquiler() {
        return alquiler;
    }

    public void setAlquiler(Alquiler alquiler) {
        this.alquiler = alquiler;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pago pago = (Pago) o;

        // Si tenemos los ID, comparamos por ID
        if (id != null && pago.id != null) {
            return Objects.equals(id, pago.id);
        }

        // Si no tenemos ID, comparamos por campos obligatorios
        return titular.equals(pago.titular) &&
                numeroTarjeta.equals(pago.numeroTarjeta) &&
                usuario.equals(pago.usuario);
    }

    @Override
    public int hashCode() {
        // Generamos un hash basado en los campos obligatorios
        return Objects.hash(titular, numeroTarjeta, usuario);
    }
}
