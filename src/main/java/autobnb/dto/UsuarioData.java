package autobnb.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.Objects;

// Data Transfer Object para la clase Usuario
public class UsuarioData {

    private Long id;
    private String nombre;
    private String apellidos;
    private String email;
    private String password;
    private Integer telefono;
    private String direccion;
    private String ciudad;
    private Integer codigoPostal;
    private String dni;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fechaCaducidadDni;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fechaCarnetConducir;
    private boolean administrador;
    private boolean esArrendador;
    private boolean esArrendatario;
    private String imagen;
    private Long idCuenta;

    // Getters y setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setPassword(String password) { this.password = password; }

    public String getPassword() { return password; }

    public Integer getTelefono() {
        return telefono;
    }

    public void setTelefono(Integer telefono) {
        this.telefono = telefono;
    }

    public Integer getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(Integer codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public boolean isAdministrador() {
        return administrador;
    }

    public void setAdministrador(boolean administrador) {
        this.administrador = administrador;
    }

    public boolean isEsArrendador() {
        return esArrendador;
    }

    public void setEsArrendador(boolean esArrendador) {
        this.esArrendador = esArrendador;
    }

    public boolean isEsArrendatario() {
        return esArrendatario;
    }

    public void setEsArrendatario(boolean esArrendatario) {
        this.esArrendatario = esArrendatario;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public Date getFechaCaducidadDni() {
        return fechaCaducidadDni;
    }

    public void setFechaCaducidadDni(Date fechaCaducidadDni) {
        this.fechaCaducidadDni = fechaCaducidadDni;
    }

    public Date getFechaCarnetConducir() {
        return fechaCarnetConducir;
    }

    public void setFechaCarnetConducir(Date fechaCarnetConducir) {
        this.fechaCarnetConducir = fechaCarnetConducir;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Long getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(Long idCuenta) {
        this.idCuenta = idCuenta;
    }

    // Sobreescribimos equals y hashCode para que dos usuarios sean iguales
    // si tienen el mismo ID (ignoramos el resto de atributos)

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UsuarioData)) return false;
        UsuarioData that = (UsuarioData) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
