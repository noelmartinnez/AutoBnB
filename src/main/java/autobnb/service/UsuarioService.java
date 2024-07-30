package autobnb.service;

import autobnb.dto.UsuarioData;
import autobnb.model.*;
import autobnb.repository.*;
import autobnb.service.exception.UsuarioServiceException;
import de.mkammerer.argon2.Argon2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioService {
    public enum LoginStatus {LOGIN_OK, USER_NOT_FOUND, ERROR_PASSWORD}

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private CuentaRepository cuentaRepository;
    @Autowired
    private ComentarioRepository comentarioRepository;
    @Autowired
    private PagoRepository pagoRepository;
    @Autowired
    private VehiculoRepository vehiculoRepository;
    @Autowired
    private AlquilerRepository alquilerRepository;
    @Autowired
    private ComentarioService comentarioService;
    @Autowired
    private AlquilerService alquilerService;
    @Autowired
    private VehiculoService vehiculoService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private Argon2 argon2;

    private boolean isArgon2Hashed(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }

        return password.startsWith("$argon2i$");
    }

    @Transactional
    public LoginStatus login(String email, String password) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);

        char[] passwordChars = password.toCharArray();

        try {
            if (!usuario.isPresent()) {
                return LoginStatus.USER_NOT_FOUND;
            } else {
                // Verificar si la contraseña ya está hasheada con Argon2
                if (isArgon2Hashed(usuario.get().getPassword())) {
                    if (!argon2.verify(usuario.get().getPassword(), passwordChars)) {
                        return LoginStatus.ERROR_PASSWORD;
                    } else {
                        return LoginStatus.LOGIN_OK;
                    }
                } else {
                    // La contraseña no está hasheada con Argon2
                    if (!usuario.get().getPassword().equals(password)) {
                        return LoginStatus.ERROR_PASSWORD;
                    } else {
                        // Hash de la contraseña con Argon2
                        String hashedPassword = argon2.hash(2, 65536, 1, passwordChars);
                        usuario.get().setPassword(hashedPassword);
                        usuarioRepository.save(usuario.get());

                        return LoginStatus.LOGIN_OK;
                    }
                }
            }
        } finally {
            argon2.wipeArray(passwordChars);
        }
    }

    @Transactional
    public UsuarioData registrar(UsuarioData usuario) {
        Optional<Usuario> usuarioBD = usuarioRepository.findByEmail(usuario.getEmail());
        if (usuarioBD.isPresent())
            throw new UsuarioServiceException("El usuario " + usuario.getEmail() + " ya está registrado");
        else {
            Usuario usuarioNuevo = modelMapper.map(usuario, Usuario.class);

            // Hash de la contraseña con Argon2
            String hashedPassword = argon2.hash(2, 65536, 1, usuario.getPassword().toCharArray());
            usuarioNuevo.setPassword(hashedPassword);

            usuarioNuevo = usuarioRepository.save(usuarioNuevo);
            return modelMapper.map(usuarioNuevo, UsuarioData.class);
        }
    }

    @Transactional(readOnly = true)
    public UsuarioData findByEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        if (usuario == null) return null;
        else {
            return modelMapper.map(usuario, UsuarioData.class);
        }
    }

    @Transactional(readOnly = true)
    public UsuarioData findByDni(String dni) {
        Usuario usuario = usuarioRepository.findByDni(dni).orElse(null);

        if (usuario == null) return null;
        else {
            return modelMapper.map(usuario, UsuarioData.class);
        }
    }

    @Transactional(readOnly = true)
    public UsuarioData findById(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);

        if (usuario == null) return null;
        else {
            return modelMapper.map(usuario, UsuarioData.class);
        }
    }

    // Método que devuelve el listado completo de objetos Usuario que hay en la base de datos.
    @Transactional(readOnly = true)
    public List<Usuario> listadoCompleto() {
        return ((List<Usuario>) usuarioRepository.findAll())
                .stream()
                .sorted(Comparator.comparingLong(Usuario::getId))
                .collect(Collectors.toList());
    }

    // Método que busca un Usuario en una lista de Usuarios pasado por parámetro y un id concreto a buscar
    @Transactional(readOnly = true)
    public Usuario buscarUsuarioPorId(List<Usuario> usuarios, Long idBuscado) {
        for (Usuario usuario : usuarios) {
            if (usuario.getId().equals(idBuscado)) {
                return usuario; // Devuelve el usuario si se encuentra
            }
        }
        return null; // Devuelve null si no se encuentra el usuario
    }

    // Método que añade una cuenta a un usuario
    @Transactional
    public UsuarioData añadirCuenta(Long usuarioId, UsuarioData nuevosDatos) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findById(usuarioId);

        if (!usuarioExistente.isPresent()) {
            throw new UsuarioServiceException("El usuario con ID " + usuarioId + " no existe en la base de datos");
        }

        Usuario usuarioActualizado = usuarioExistente.get();

        // Actualiza los campos con los nuevos datos proporcionados
        if (nuevosDatos.getIdCuenta() != null) {
            Optional<Cuenta> cuenta = cuentaRepository.findById(nuevosDatos.getIdCuenta());
            usuarioActualizado.setCuenta(cuenta.get());
        }
        else{
            throw new UsuarioServiceException("Se ha recibido una cuenta NULL");
        }

        usuarioActualizado = usuarioRepository.save(usuarioActualizado);

        return modelMapper.map(usuarioActualizado, UsuarioData.class);
    }

    @Transactional
    public UsuarioData cambiarRolUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioServiceException("El usuario con ID " + usuarioId + " no se encuentra."));

        // Cambiar roles
        usuario.setEsArrendador(!usuario.isEsArrendador());
        usuario.setEsArrendatario(!usuario.isEsArrendatario());

        // Guardar el usuario actualizado
        usuario = usuarioRepository.save(usuario);

        // Convertir el usuario a UsuarioData para devolver
        return modelMapper.map(usuario, UsuarioData.class);
    }

    @Transactional
    public UsuarioData actualizarUsuarioPorId(Long usuarioId, UsuarioData nuevosDatos) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findById(usuarioId);

        if (!usuarioExistente.isPresent()) {
            throw new UsuarioServiceException("El usuario con ID " + usuarioId + " no existe en la base de datos");
        }

        Usuario usuarioActualizado = usuarioExistente.get();

        // Actualiza los campos con los nuevos datos proporcionados
        if (nuevosDatos.getNombre() != null) {
            usuarioActualizado.setNombre(nuevosDatos.getNombre());
        }
        else{
            throw new UsuarioServiceException("Se ha recibido un nombre NULL");
        }

        if (nuevosDatos.getPassword() != null) {
            // Hash de la contraseña con Argon2
            String hashedPassword = argon2.hash(2, 65536, 1, nuevosDatos.getPassword().toCharArray());
            usuarioActualizado.setPassword(hashedPassword);
        }
        else{
            throw new UsuarioServiceException("Se ha recibido un password NULL");
        }

        if (nuevosDatos.getEmail() != null) {
            usuarioActualizado.setEmail(nuevosDatos.getEmail());
        }
        else{
            throw new UsuarioServiceException("Se ha recibido un email NULL");
        }

        if (nuevosDatos.getTelefono() != null) {
            usuarioActualizado.setTelefono(nuevosDatos.getTelefono());
        }
        else{
            throw new UsuarioServiceException("Se ha recibido un telefono NULL");
        }

        if (nuevosDatos.getCodigoPostal() != null) {
            usuarioActualizado.setCodigoPostal(nuevosDatos.getCodigoPostal());
        }
        else{
            throw new UsuarioServiceException("Se ha recibido un codigo postal NULL");
        }

        if (nuevosDatos.getCiudad() != null) {
            usuarioActualizado.setCiudad(nuevosDatos.getCiudad());
        }
        else{
            throw new UsuarioServiceException("Se ha recibido una ciudad NULL");
        }

        if (nuevosDatos.getDireccion() != null) {
            usuarioActualizado.setDireccion(nuevosDatos.getDireccion());
        }
        else{
            throw new UsuarioServiceException("Se ha recibido una direccion NULL");
        }

        if (nuevosDatos.getDni() != null) {
            usuarioActualizado.setDni(nuevosDatos.getDni());
        }
        else{
            throw new UsuarioServiceException("Se ha recibido un dni NULL");
        }

        if (nuevosDatos.getFechaCaducidadDni() != null) {
            usuarioActualizado.setFechaCaducidadDni(nuevosDatos.getFechaCaducidadDni());
        }
        else{
            throw new UsuarioServiceException("Se ha recibido una fecha de caducidad de dni NULL");
        }

        if (nuevosDatos.getFechaCarnetConducir() != null) {
            usuarioActualizado.setFechaCarnetConducir(nuevosDatos.getFechaCarnetConducir());
        }
        else{
            throw new UsuarioServiceException("Se ha recibido una fecha de carnet de conducir NULL");
        }

        if (nuevosDatos.isAdministrador()) {
            usuarioActualizado.setAdministrador(nuevosDatos.isAdministrador());
        }

        usuarioActualizado.setApellidos(nuevosDatos.getApellidos());
        usuarioActualizado.setImagen(nuevosDatos.getImagen());

        usuarioActualizado = usuarioRepository.save(usuarioActualizado);

        return modelMapper.map(usuarioActualizado, UsuarioData.class);
    }

    // Método para obtener todos los comentarios de un usuario específico
    @Transactional(readOnly = true)
    public List<Comentario> obtenerComentariosPorUsuarioId(Long usuarioId) {
        return comentarioRepository.findByUsuarioId(usuarioId);
    }

    @Transactional(readOnly = true)
    public List<Pago> obtenerPagosPorUsuarioId(Long usuarioId) {
        return pagoRepository.findByUsuarioId(usuarioId);
    }

    @Transactional(readOnly = true)
    public List<Vehiculo> obtenerVehiculosPorUsuarioId(Long usuarioId) {
        return vehiculoRepository.findByUsuarioId(usuarioId);
    }

    @Transactional(readOnly = true)
    public List<Alquiler> obtenerAlquileresPorVehiculoId(Long vehiculoId) {
        return alquilerRepository.findByVehiculoId(vehiculoId);
    }

    @Transactional
    public Usuario añadirSaldo(Long usuarioId, BigDecimal cantidad) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioServiceException("El usuario con ID " + usuarioId + " no se encuentra."));

        Cuenta cuenta = usuario.getCuenta();
        cuenta.setSaldo(cuenta.getSaldo().add(cantidad));
        cuentaRepository.save(cuenta);

        return usuario;
    }

    @Transactional
    public void eliminarUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);

        if (usuario != null) {
            for (Vehiculo vehiculo : usuario.getVehiculos()) {
                vehiculoService.eliminarVehiculo(vehiculo.getId());
            }

            usuario.getComentarios().forEach(comentario -> comentarioService.eliminarComentario(comentario.getId()));
            usuario.getPagos().forEach(pago -> alquilerService.eliminarAlquiler(pago.getAlquiler().getId()));

            usuarioRepository.delete(usuario);
        } else {
            throw new EntityNotFoundException("No se encontró el usuario con ID: " + usuario);
        }
    }

    @Transactional(readOnly = true)
    public Page<Usuario> listadoPaginado(Pageable pageable) {
        return usuarioRepository.findAll(pageable);
    }
}
