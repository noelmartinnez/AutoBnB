package autobnb.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "cuenta")
public class Cuenta implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(length = 50, unique = true)
    private String numeroCuenta;

    @NotNull
    @Column(precision = 10, scale = 2)
    private BigDecimal saldo;

    @NotNull
    @OneToOne
    @JoinColumn(name = "idUsuario", unique = true)
    private Usuario usuario;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
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
        Cuenta cuenta = (Cuenta) o;

        // Si tenemos los ID, comparamos por ID
        if (id != null && cuenta.id != null) {
            return Objects.equals(id, cuenta.id);
        }

        // Si no tenemos ID, comparamos por campos obligatorios
        return usuario.equals(cuenta.usuario) &&
                numeroCuenta.equals(cuenta.numeroCuenta) &&
                saldo.equals(cuenta.saldo);
    }

    @Override
    public int hashCode() {
        // Generamos un hash basado en los campos obligatorios
        return Objects.hash(usuario, numeroCuenta, saldo);
    }
}

