package autobnb.dto;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;

public class VehiculoData {

    private Long id;
    @NotNull
    private String descripcion;
    @NotNull
    private String imagen;
    @NotNull
    private String matricula;
    @NotNull
    private Integer kilometraje;
    @NotNull
    private Integer anyoFabricacion;
    @NotNull
    private Integer capacidadPasajeros;
    @NotNull
    private Integer capacidadMaletero;
    @NotNull
    private Integer numeroPuertas;
    @NotNull
    private Integer numeroMarchas;
    @NotNull
    private boolean aireAcondicionado;
    @NotNull
    private boolean enMantenimiento;
    private Integer oferta;
    @NotNull
    private BigDecimal precioPorDia;
    @NotNull
    private BigDecimal precioPorMedioDia;
    @NotNull
    private BigDecimal precioCombustible;
    private Long idUsuario;
    private Long idMarca;
    private Long idModelo;
    private Long idCategoria;
    private Long idTransmision;
    private Long idColor;

    // Getters y setters

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

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VehiculoData)) return false;
        VehiculoData that = (VehiculoData) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
