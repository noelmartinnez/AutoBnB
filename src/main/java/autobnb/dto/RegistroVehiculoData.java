package autobnb.dto;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

public class RegistroVehiculoData {
    @NotEmpty(message = "La descripción no puede estar vacía.")
    private String descripcion;

    private MultipartFile imagen;

    @NotEmpty(message = "La descripción no puede estar vacía.")
    @Pattern(regexp = "^\\d{4}[\\s\\-]?[a-zA-Z]{3}$", message = "La matrícula no es válida.")
    private String matricula;

    @NotNull(message = "El kilometraje no puede ser nulo.")
    @Min(value = 1, message = "El kilometraje debe ser mayor que 0.")
    private Integer kilometraje;
    @NotNull(message = "El año de fabricación no puede ser nulo.")
    @Min(value = 1, message = "El año de fabricación debe ser mayor que 0.")
    private Integer anyoFabricacion;
    @NotNull(message = "La capacidad de pasajeros no puede ser nula.")
    @Min(value = 1, message = "La capacidad de pasajeros debe ser mayor que 0.")
    private Integer capacidadPasajeros;
    @NotNull(message = "La capacidad del maletero no puede ser nula.")
    @Min(value = 1, message = "La capacidad del maletero debe ser mayor que 0.")
    private Integer capacidadMaletero;
    @NotNull(message = "El número de puertas no puede ser nulo.")
    @Min(value = 1, message = "El número de puertas debe ser mayor que 0.")
    private Integer numeroPuertas;
    @NotNull(message = "El número de marchas no puede ser nulo.")
    @Min(value = 1, message = "El número de marchas debe ser mayor que 0.")
    private Integer numeroMarchas;
    @NotNull(message = "El aire acondicionado no puede ser nulo.")
    private boolean aireAcondicionado;
    @NotNull(message = "El mentenimiento no puede ser nulo.")
    private boolean enMantenimiento;
    @Min(value = 1, message = "La oferta debe ser mayor que 0.")
    private Integer oferta;
    @NotNull(message = "El precio por día no puede ser nulo.")
    private BigDecimal precioPorDia;
    @NotNull(message = "El precio por medio día no puede ser nulo.")
    private BigDecimal precioPorMedioDia;
    @NotNull(message = "El precio del combustible no puede ser nulo.")
    private BigDecimal precioCombustible;
    private Long idUsuario;
    private Long idMarca;
    private Long idModelo;
    private Long idCategoria;
    private Long idTransmision;
    private Long idColor;

    // Getters y setters

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public MultipartFile getImagen() {
        return imagen;
    }

    public void setImagen(MultipartFile imagen) {
        this.imagen = imagen;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public Integer getKilometraje() {
        return kilometraje;
    }

    public void setKilometraje(Integer kilometraje) {
        this.kilometraje = kilometraje;
    }

    public Integer getAnyoFabricacion() {
        return anyoFabricacion;
    }

    public void setAnyoFabricacion(Integer anyoFabricacion) {
        this.anyoFabricacion = anyoFabricacion;
    }

    public Integer getCapacidadPasajeros() {
        return capacidadPasajeros;
    }

    public void setCapacidadPasajeros(Integer capacidadPasajeros) {
        this.capacidadPasajeros = capacidadPasajeros;
    }

    public Integer getCapacidadMaletero() {
        return capacidadMaletero;
    }

    public void setCapacidadMaletero(Integer capacidadMaletero) {
        this.capacidadMaletero = capacidadMaletero;
    }

    public Integer getNumeroPuertas() {
        return numeroPuertas;
    }

    public void setNumeroPuertas(Integer numeroPuertas) {
        this.numeroPuertas = numeroPuertas;
    }

    public Integer getNumeroMarchas() {
        return numeroMarchas;
    }

    public void setNumeroMarchas(Integer numeroMarchas) {
        this.numeroMarchas = numeroMarchas;
    }

    public boolean isAireAcondicionado() {
        return aireAcondicionado;
    }

    public void setAireAcondicionado(boolean aireAcondicionado) {
        this.aireAcondicionado = aireAcondicionado;
    }

    public boolean isEnMantenimiento() {
        return enMantenimiento;
    }

    public void setEnMantenimiento(boolean enMantenimiento) {
        this.enMantenimiento = enMantenimiento;
    }

    public Integer getOferta() {
        return oferta;
    }

    public void setOferta(Integer oferta) {
        this.oferta = oferta;
    }

    public BigDecimal getPrecioPorDia() {
        return precioPorDia;
    }

    public void setPrecioPorDia(BigDecimal precioPorDia) {
        this.precioPorDia = precioPorDia;
    }

    public BigDecimal getPrecioPorMedioDia() {
        return precioPorMedioDia;
    }

    public void setPrecioPorMedioDia(BigDecimal precioPorMedioDia) {
        this.precioPorMedioDia = precioPorMedioDia;
    }

    public BigDecimal getPrecioCombustible() {
        return precioCombustible;
    }

    public void setPrecioCombustible(BigDecimal precioCombustible) {
        this.precioCombustible = precioCombustible;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Long getIdMarca() {
        return idMarca;
    }

    public void setIdMarca(Long idMarca) {
        this.idMarca = idMarca;
    }

    public Long getIdModelo() {
        return idModelo;
    }

    public void setIdModelo(Long idModelo) {
        this.idModelo = idModelo;
    }

    public Long getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Long idCategoria) {
        this.idCategoria = idCategoria;
    }

    public Long getIdTransmision() {
        return idTransmision;
    }

    public void setIdTransmision(Long idTransmision) {
        this.idTransmision = idTransmision;
    }

    public Long getIdColor() {
        return idColor;
    }

    public void setIdColor(Long idColor) {
        this.idColor = idColor;
    }
}
