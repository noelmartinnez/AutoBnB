package autobnb.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "alquiler")
public class Alquiler implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Temporal(TemporalType.DATE)
    private Date fechaCreacion;

    @NotNull
    @Temporal(TemporalType.DATE)
    private Date fechaEntrega;

    @NotNull
    @Temporal(TemporalType.DATE)
    private Date fechaDevolucion;

    @NotNull
    @Column(precision = 10, scale = 2)
    private BigDecimal precioFinal;

    @Column(precision = 10, scale = 2)
    private BigDecimal litrosCombustible;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "idVehiculo")
    private Vehiculo vehiculo;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "idPago", unique = true)
    private Pago pago;

    public Alquiler() {
    }

    public Alquiler(Date fechaCreacion, Date fechaEntrega, Date fechaDevolucion, BigDecimal precioFinal, Vehiculo vehiculo, Pago pago) {
        this.fechaCreacion = fechaCreacion;
        this.fechaEntrega = fechaEntrega;
        this.fechaDevolucion = fechaDevolucion;
        this.precioFinal = precioFinal;
        this.vehiculo = vehiculo;
        this.pago = pago;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Date getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(Date fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public Date getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(Date fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public BigDecimal getPrecioFinal() {
        return precioFinal;
    }

    public void setPrecioFinal(BigDecimal precioFinal) {
        this.precioFinal = precioFinal;
    }

    public BigDecimal getLitrosCombustible() {
        return litrosCombustible;
    }

    public void setLitrosCombustible(BigDecimal litrosCombustible) {
        this.litrosCombustible = litrosCombustible;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public Pago getPago() {
        return pago;
    }

    public void setPago(Pago pago) {
        this.pago = pago;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Alquiler alquiler = (Alquiler) o;

        // Si tenemos los ID, comparamos por ID
        if (id != null && alquiler.id != null) {
            return Objects.equals(id, alquiler.id);
        }

        // Si no tenemos ID, comparamos por campos obligatorios
        return fechaCreacion.equals(alquiler.fechaCreacion) &&
                fechaEntrega.equals(alquiler.fechaEntrega) &&
                fechaDevolucion.equals(alquiler.fechaDevolucion) &&
                precioFinal.equals(alquiler.precioFinal) &&
                vehiculo.equals(alquiler.vehiculo) &&
                pago.equals(alquiler.pago);
    }

    @Override
    public int hashCode() {
        // Generamos un hash basado en los campos obligatorios
        return Objects.hash(fechaCreacion, fechaEntrega, fechaDevolucion, precioFinal, vehiculo, pago);
    }
}
