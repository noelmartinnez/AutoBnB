package autobnb.dto;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;
import java.util.Date;

public class RegistroData {

    @NotEmpty(message = "El nombre no puede ser nulo.")
    private String nombre;

    private String apellidos;

    @NotEmpty(message = "El email no puede ser nulo.")
    @Email(message = "Por favor, introduce una dirección de correo electrónico válida.")
    private String email;

    @NotEmpty(message = "La contraseña no puede ser nula.")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres.")
    private String password;

    @NotNull(message = "La contraseña no puede ser nula.")
    @Min(value = 100000000, message = "El teléfono debe tener 9 dígitos.")
    @Max(value = 999999999, message = "El teléfono debe tener 9 dígitos.")
    private Integer telefono;

    @NotEmpty(message = "El DNI no puede ser nulo.")
    @Pattern(regexp = "\\d{8}[A-HJ-NP-TV-Z]", message = "DNI no válido.")
    private String dni;

    @NotNull(message = "La fecha de caducidad del DNI no puede ser nula.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Future(message = "La fecha de caducidad del DNI debe ser en el futuro.")
    private Date fechaCaducidadDni;

    @NotEmpty(message = "La dirección no puede ser nula.")
    @Size(max = 100, message = "La dirección no puede superar los 100 caracteres.")
    private String direccion;

    @NotEmpty(message = "La ciudad no puede ser nula.")
    @Size(max = 50, message = "La ciudad no debe superar los 50 caracteres.")
    private String ciudad;

    @NotNull(message = "El código postal no puede ser nulo.")
    @Min(value = 0, message = "El código postal debe ser mayor o igual a 0.")
    private Integer codigoPostal;

    @NotNull(message = "La fecha de expedición del carnet de conducir no puede ser nula.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Past(message = "La fecha del carnet de conducir debe ser en el pasado.")
    private Date fechaCarnetConducir;

    @NotEmpty(message = "El número de cuenta no puede ser nulo.")
    @Pattern(regexp = "ES\\d{22}", message = "Número de cuenta no válido. Debe comenzar por ES seguido de 22 dígitos.")
    private String numeroCuenta;

    private MultipartFile imagen;

    private boolean administrador;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getTelefono() {
        return telefono;
    }

    public void setTelefono(Integer telefono) {
        this.telefono = telefono;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
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

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
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

    public MultipartFile getImagen() {
        return imagen;
    }

    public void setImagen(MultipartFile imagen) {
        this.imagen = imagen;
    }

    public boolean isAdministrador() {
        return administrador;
    }

    public void setAdministrador(boolean administrador) {
        this.administrador = administrador;
    }
}
