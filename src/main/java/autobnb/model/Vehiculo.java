package autobnb.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "vehiculo")
public class Vehiculo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String descripcion;

    @NotNull
    private String imagen;

    @NotNull
    @Column(length = 7, unique = true)
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
    private boolean aireAcondicionado = false;

    @NotNull
    private boolean enMantenimiento = false;

    private Integer oferta;

    @NotNull
    @Column(precision = 10, scale = 2)
    private BigDecimal precioPorDia;

    @NotNull
    @Column(precision = 10, scale = 2)
    private BigDecimal precioPorMedioDia;

    @NotNull
    @Column(precision = 10, scale = 2)
    private BigDecimal precioCombustible;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "idUsuario")
    private Usuario usuario;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "idMarca")
    private Marca marca;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "idModelo")
    private Modelo modelo;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "idCategoria")
    private Categoria categoria;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "idTransmision")
    private Transmision transmision;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "idColor")
    private Color color;

    @OneToMany(mappedBy = "vehiculo", cascade = CascadeType.ALL)
    Set<Comentario> comentarios = new HashSet<>();

    @OneToMany(mappedBy = "vehiculo", cascade = CascadeType.ALL)
    Set<Alquiler> alquileres = new HashSet<>();

    public Vehiculo() {
    }

    public Vehiculo(String descripcion, String imagen, String matricula, Integer kilometraje, Integer anyoFabricacion, Integer capacidadPasajeros, Integer capacidadMaletero, Integer numeroPuertas, Integer numeroMarchas, boolean aireAcondicionado, boolean enMantenimiento,
                    BigDecimal precioPorDia, BigDecimal precioPorMedioDia, BigDecimal precioCombustible, Usuario usuario, Marca marca, Modelo modelo, Categoria categoria, Transmision transmision, Color color) {
        this.descripcion = descripcion;
        this.imagen = imagen;
        this.matricula = matricula;
        this.kilometraje = kilometraje;
        this.anyoFabricacion = anyoFabricacion;
        this.capacidadPasajeros = capacidadPasajeros;
        this.capacidadMaletero = capacidadMaletero;
        this.numeroPuertas = numeroPuertas;
        this.numeroMarchas = numeroMarchas;
        this.aireAcondicionado = aireAcondicionado;
        this.enMantenimiento = enMantenimiento;
        this.precioPorDia = precioPorDia;
        this.precioPorMedioDia = precioPorMedioDia;
        this.precioCombustible = precioCombustible;
        this.usuario = usuario;
        this.marca = marca;
        this.modelo = modelo;
        this.categoria = categoria;
        this.transmision = transmision;
        this.color = color;
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

    public Set<Comentario> getComentarios() {
        return comentarios;
    }

    public void addComentarios(Comentario comentario) {
        if (comentarios.contains(comentario)) return;
        comentarios.add(comentario);
        if (comentario.getVehiculo() != this) {
            comentario.setVehiculo(this);
        }
    }

    public Set<Alquiler> getAlquileres() {
        return alquileres;
    }

    public void addAlquileres(Alquiler alquiler) {
        if (alquileres.contains(alquiler)) return;
        alquileres.add(alquiler);
        if (alquiler.getVehiculo() != this) {
            alquiler.setVehiculo(this);
        }
    }

    public Marca getMarca() {
        return marca;
    }

    public void setMarca(Marca marca) {
        this.marca = marca;
    }

    public Modelo getModelo() {
        return modelo;
    }

    public void setModelo(Modelo modelo) {
        this.modelo = modelo;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Transmision getTransmision() {
        return transmision;
    }

    public void setTransmision (Transmision transmision) {
        this.transmision = transmision;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getPrecioPorDiaSinDecimales() {
        return precioPorDia.setScale(0, RoundingMode.HALF_UP).toString();
    }

    public String getPrecioPorMedioDiaSinDecimales() {
        return precioPorMedioDia.setScale(0, RoundingMode.HALF_UP).toString();
    }

    public String getPrecioCombustibleSinDecimales() {
        return precioCombustible.setScale(0, RoundingMode.HALF_UP).toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehiculo vehiculo = (Vehiculo) o;

        // Si tenemos los ID, comparamos por ID
        if (id != null && vehiculo.id != null) {
            return Objects.equals(id, vehiculo.id);
        }

        // Si no tenemos ID, comparamos por campos obligatorios
        return descripcion.equals(vehiculo.descripcion) &&
                imagen.equals(vehiculo.imagen) &&
                matricula.equals(vehiculo.matricula) &&
                kilometraje.equals(vehiculo.kilometraje) &&
                anyoFabricacion.equals(vehiculo.anyoFabricacion) &&
                capacidadPasajeros.equals(vehiculo.capacidadPasajeros) &&
                capacidadMaletero.equals(vehiculo.capacidadMaletero) &&
                numeroPuertas.equals(vehiculo.numeroPuertas) &&
                numeroMarchas.equals(vehiculo.numeroMarchas) &&
                aireAcondicionado == vehiculo.aireAcondicionado &&
                enMantenimiento == vehiculo.enMantenimiento &&
                precioPorDia.equals(vehiculo.precioPorDia) &&
                precioPorMedioDia.equals(vehiculo.precioPorMedioDia) &&
                precioCombustible.equals(vehiculo.precioCombustible) &&
                usuario.equals(vehiculo.usuario) &&
                marca.equals(vehiculo.marca) &&
                modelo.equals(vehiculo.modelo) &&
                categoria.equals(vehiculo.categoria) &&
                transmision.equals(vehiculo.transmision) &&
                color.equals(vehiculo.color);
    }

    @Override
    public int hashCode() {
        // Generamos un hash basado en los campos obligatorios
        return Objects.hash(descripcion, imagen, matricula, kilometraje, anyoFabricacion, capacidadPasajeros, capacidadMaletero, numeroPuertas,
                numeroMarchas, aireAcondicionado, enMantenimiento, precioPorDia, precioPorMedioDia, precioCombustible, usuario, marca, modelo, categoria, transmision, color);
    }
}
