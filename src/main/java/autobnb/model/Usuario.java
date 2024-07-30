package autobnb.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "usuario")
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(length = 50)
    private String nombre;

    @Column(length = 50)
    private String apellidos;

    @NotNull
    @Column(length = 100, unique = true)
    private String email;

    @NotNull
    @Column(length = 50)
    private String password;

    @NotNull
    private Integer telefono;

    @NotNull
    @Column(length = 100)
    private String direccion;

    @NotNull
    @Column(length = 50)
    private String ciudad;

    @NotNull
    private Integer codigoPostal;

    @NotNull
    @Column(length = 9, unique = true)
    private String dni;

    @NotNull
    @Temporal(TemporalType.DATE)
    private Date fechaCaducidadDni;

    @NotNull
    @Temporal(TemporalType.DATE)
    private Date fechaCarnetConducir;

    @NotNull
    private boolean administrador = false;

    @NotNull
    private boolean esArrendador = false;

    @NotNull
    private boolean esArrendatario = false;

    private String imagen;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
    private Cuenta cuenta;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    Set<Comentario> comentarios = new HashSet<>();

    @OneToMany(mappedBy = "usuario")
    Set<Vehiculo> vehiculos = new HashSet<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    Set<Pago> pagos = new HashSet<>();

    // Constructor vacío necesario para JPA/Hibernate.
    // No debe usarse desde la aplicación.
    public Usuario() {}

    public Usuario(String nombre, String email, String password, Integer telefono, String direccion, String ciudad, Integer codigoPostal, String dni,
                   Date fechaCaducidadDni, Date fechaCarnetConducir, boolean administrador, boolean esArrendador, boolean esArrendatario, String imagen,
                    Cuenta cuenta) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.telefono = telefono;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.codigoPostal = codigoPostal;
        this.dni = dni;
        this.fechaCaducidadDni = fechaCaducidadDni;
        this.fechaCarnetConducir = fechaCarnetConducir;
        this.administrador = administrador;
        this.esArrendador = esArrendador;
        this.esArrendatario = esArrendatario;
        this.cuenta = cuenta;
    }

    // Getters y setters atributos básicos

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

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

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

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
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

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Cuenta getCuenta() {
        return cuenta;
    }

    public void setCuenta(Cuenta cuenta) {
        this.cuenta = cuenta;
    }

    public Set<Comentario> getComentarios() {
        return comentarios;
    }

    public void addComentarios(Comentario comentario) {
        if (comentarios.contains(comentario)) return;
        comentarios.add(comentario);
        if (comentario.getUsuario() != this) {
            comentario.setUsuario(this);
        }
    }

    public Set<Vehiculo> getVehiculos() {
        return vehiculos;
    }

    public void addVehiculos(Vehiculo vehiculo) {
        if (vehiculos.contains(vehiculo)) return;
        vehiculos.add(vehiculo);
        if (vehiculo.getUsuario() != this) {
            vehiculo.setUsuario(this);
        }
    }

    public Set<Pago> getPagos() {
        return pagos;
    }

    public void addPagos(Pago pago) {
        if (pagos.contains(pago)) return;
        pagos.add(pago);
        if (pago.getUsuario() != this) {
            pago.setUsuario(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;

        // Si tenemos los ID, comparamos por ID
        if (id != null && usuario.id != null) {
            return Objects.equals(id, usuario.id);
        }

        // Si no tenemos ID, comparamos por campos obligatorios
        return email.equals(usuario.email) &&
                nombre.equals(usuario.nombre) &&
                password.equals(usuario.password) && direccion.equals(usuario.direccion) &&
                ciudad.equals(usuario.ciudad) && codigoPostal.equals(usuario.codigoPostal) && dni.equals(usuario.dni) &&
                fechaCaducidadDni.equals(usuario.fechaCaducidadDni) && fechaCarnetConducir.equals(usuario.fechaCarnetConducir) &&
                administrador == usuario.administrador && esArrendador == usuario.esArrendador && esArrendatario == usuario.esArrendatario;
    }

    @Override
    public int hashCode() {
        // Generamos un hash basado en los campos obligatorios
        return Objects.hash(email, nombre, password, direccion, ciudad, codigoPostal,
                dni, fechaCaducidadDni, fechaCarnetConducir, administrador, esArrendador, esArrendatario);
    }
}
