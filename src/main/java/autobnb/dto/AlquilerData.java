package autobnb.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

public class AlquilerData {

    private Long id;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fechaCreacion;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fechaEntrega;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fechaDevolucion;
    private BigDecimal precioFinal;
    private BigDecimal litrosCombustible;
    private Long idVehiculo; // Usamos un Long para representar el ID del Vehículo asociado
    private Long idPago; // Usamos un Long para representar el ID del Pago asociado

    // Getters y Setters
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

    public Long getIdVehiculo() {
        return idVehiculo;
    }

    public void setIdVehiculo(Long idVehiculo) {
        this.idVehiculo = idVehiculo;
    }

    public Long getIdPago() {
        return idPago;
    }

    public void setIdPago(Long idPago) {
        this.idPago = idPago;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AlquilerData)) return false;
        AlquilerData that = (AlquilerData) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
