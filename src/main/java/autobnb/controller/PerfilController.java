package autobnb.controller;

import autobnb.authentication.ManagerUserSession;
import autobnb.controller.exception.UsuarioNoLogeadoException;
import autobnb.dto.*;
import autobnb.model.*;
import autobnb.service.*;
import autobnb.service.exception.UsuarioServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Controller
public class PerfilController {
    @Autowired
    private ManagerUserSession managerUserSession;
    @Autowired
    UsuarioService usuarioService;
    @Autowired
    ComentarioService comentarioService;
    @Autowired
    AlquilerService alquilerService;
    @Autowired
    VehiculoService vehiculoService;
    @Autowired
    MarcaService marcaService;
    @Autowired
    ModeloService modeloService;
    @Autowired
    ColorService colorService;
    @Autowired
    TransmisionService transmisionService;
    @Autowired
    CategoriaService categoriaService;

    private void comprobarLogueado(Long idUsuario) {
        if (idUsuario == null) {
            throw new UsuarioNoLogeadoException();
        }
    }

    // Método que devuelve el perfil
    @GetMapping("/perfil/{id}")
    public String perfil(@PathVariable(value = "id") Long idUsuario, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarLogueado(id);

        // Si está logueado, lo buscamos en la base de datos y lo añadimos al atributo "usuario"
        UsuarioData user = usuarioService.findById(id);
        model.addAttribute("logueado", user);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, idUsuario);

        model.addAttribute("usuario", usuario);

        return "perfil/perfil";
    }

    @PostMapping("/cambiarRol/{id}")
    public String cambiarRolUsuario(@PathVariable(value = "id") Long idUsuario, HttpServletRequest request) {
        usuarioService.cambiarRolUsuario(idUsuario);

        // Obtener el valor del encabezado 'Referer'
        String referer = request.getHeader("Referer");

        // Redirigir a la URL del 'Referer' si está presente, de lo contrario, redirigir a la página de perfil
        return referer != null ? "redirect:" + referer : "redirect:/perfil/" + idUsuario;

    }

    @GetMapping("/perfil/{id}/actualizar")
    public String mostrarActualizarPerfil(@PathVariable(value = "id") Long idUsuario, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarLogueado(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, idUsuario);
        model.addAttribute("usuario", usuario);

        RegistroData nuevo = new RegistroData();
        nuevo.setEmail(usuario.getEmail());
        nuevo.setPassword(usuario.getPassword());
        nuevo.setNombre(usuario.getNombre());
        nuevo.setApellidos(usuario.getApellidos());
        nuevo.setTelefono(usuario.getTelefono());
        nuevo.setCodigoPostal(usuario.getCodigoPostal());
        nuevo.setCiudad(usuario.getCiudad());
        nuevo.setDireccion(usuario.getDireccion());
        nuevo.setDni(usuario.getDni());
        nuevo.setFechaCaducidadDni(usuario.getFechaCaducidadDni());
        nuevo.setFechaCarnetConducir(usuario.getFechaCarnetConducir());
        nuevo.setNumeroCuenta(usuario.getCuenta().getNumeroCuenta());

        model.addAttribute("registroData", nuevo);

        return "perfil/actualizarPerfil";
    }

    // Método para manejar la actualización del perfil
    @PostMapping("/perfil/{id}/actualizar")
    public String actualizarPerfil(@PathVariable(value = "id") Long idUsuario, @Valid RegistroData registroData, BindingResult result, Model model) throws IOException {
        Long id = managerUserSession.usuarioLogeado();

        if (result.hasErrors()) {
            System.out.println("Ha ocurrido un error.");
        }
        else{
            comprobarLogueado(id);

            try {
                UsuarioData nuevoUsuarioData = usuarioService.findById(idUsuario);

                if(registroData.getEmail() != null && registroData.getPassword() != null && registroData.getNombre() != null
                        && registroData.getTelefono() != null && registroData.getCodigoPostal() != null
                        && registroData.getCiudad() != null && registroData.getDireccion() != null && registroData.getDni() != null
                        && registroData.getFechaCaducidadDni() != null && registroData.getFechaCarnetConducir() != null) {
                    nuevoUsuarioData.setEmail(registroData.getEmail());
                    nuevoUsuarioData.setPassword(registroData.getPassword());
                    nuevoUsuarioData.setNombre(registroData.getNombre());
                    nuevoUsuarioData.setApellidos(registroData.getApellidos());
                    nuevoUsuarioData.setTelefono(registroData.getTelefono());
                    nuevoUsuarioData.setCiudad(registroData.getCiudad());
                    nuevoUsuarioData.setDireccion(registroData.getDireccion());
                    nuevoUsuarioData.setCodigoPostal(registroData.getCodigoPostal());
                    nuevoUsuarioData.setDni(registroData.getDni());
                    nuevoUsuarioData.setFechaCaducidadDni(registroData.getFechaCaducidadDni());
                    nuevoUsuarioData.setFechaCarnetConducir(registroData.getFechaCarnetConducir());

                    MultipartFile imagen = registroData.getImagen();
                    if (!imagen.isEmpty()) {
                        String contentType = imagen.getContentType();
                        assert contentType != null;
                        if (contentType.equals("image/jpeg") || contentType.equals("image/jpg")) {
                            // Define la ruta del directorio 'uploads'
                            String uploadDir = "uploads";

                            // Formatear la fecha actual para incluirla en el nombre del archivo
                            String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                            String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(imagen.getOriginalFilename()));
                            String fileName = originalFileName.replace(".", dateTime + ".");

                            Path uploadPath = Paths.get(uploadDir);

                            if (!Files.exists(uploadPath)) {
                                Files.createDirectories(uploadPath);
                            }

                            try (InputStream inputStream = imagen.getInputStream()) {
                                Path filePath = uploadPath.resolve(fileName);
                                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            // Guarda solo el nombre del archivo en la base de datos
                            nuevoUsuarioData.setImagen(fileName);
                        } else {
                            // Manejo de error si el archivo no es una imagen JPEG/JPG
                            model.addAttribute("errorActualizar", "Solo se permiten archivos de imagen en formato JPEG y JPG.");
                        }
                    }

                    // Validar y actualizar los datos del usuario en el servicio
                    usuarioService.actualizarUsuarioPorId(idUsuario, nuevoUsuarioData);

                    // Redirigir al perfil del usuario
                    return "redirect:/perfil/" + idUsuario;
                }
                else{
                    model.addAttribute("errorActualizar", "Ho ocurrido un error al intentar actualizar.");
                }

            } catch (UsuarioServiceException e) {
                model.addAttribute("errorActualizar", e.getMessage());
            }
        }

        model.addAttribute("registroData", registroData);

        UsuarioData user = usuarioService.findById(id);
        model.addAttribute("logueado", user);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, idUsuario);

        model.addAttribute("usuario", usuario);

        return "perfil/actualizarPerfil";
    }


    // COMENTARIOS DE USUARIO

    // Método para mostrar los comentarios de un usuario
    @GetMapping("/perfil/{id}/comentarios")
    public String mostrarListadoComentarios(@PathVariable(value = "id") Long idUsuario, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarLogueado(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, idUsuario);
        model.addAttribute("usuario", usuario);

        List<Comentario> comentarios = usuarioService.obtenerComentariosPorUsuarioId(idUsuario);

        model.addAttribute("comentarios", comentarios);

        return "perfil/comentariosUsuario";
    }

    @GetMapping("/perfil/{id}/comentarios/editar/{comentarioId}")
    public String mostrarEditarComentario(@PathVariable(value = "id") Long idUsuario, @PathVariable(value = "comentarioId") Long comentarioId, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, idUsuario);
        model.addAttribute("usuario", usuario);

        comprobarLogueado(id);

        ComentarioData comentario = comentarioService.findById(comentarioId);

        if (comentario != null) {
            List<Comentario> comentarios = comentarioService.listadoCompleto();
            Comentario comentarioBuscado = comentarioService.buscarComentarioPorId(comentarios, comentarioId);
            model.addAttribute("comentario", comentarioBuscado);

            ComentarioData comentarioData = new ComentarioData();
            comentarioData.setDescripcion(comentarioBuscado.getDescripcion());
            comentarioData.setFechaCreacion(comentarioBuscado.getFechaCreacion());
            comentarioData.setIdVehiculo(comentarioBuscado.getVehiculo().getId());
            comentarioData.setIdUsuario(comentarioBuscado.getUsuario().getId());

            model.addAttribute("comentarioData", comentarioData);
            return "perfil/editarComentario";
        }

        return "redirect:/perfil/" + idUsuario + "/comentarios";
    }

    @PostMapping("/perfil/{idUsuario}/comentarios/editar/{comentarioId}")
    public String actualizarComentario(@PathVariable Long idUsuario, @PathVariable Long comentarioId, @Valid ComentarioData comentarioData, BindingResult result, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        if (result.hasErrors()) {
            System.out.println("Ha ocurrido un error.");
        }
        else{
            comprobarLogueado(id);

            try {
                ComentarioData nuevoComentarioData = comentarioService.findById(comentarioId);

                if(comentarioData.getDescripcion() != null) {
                    nuevoComentarioData.setDescripcion(comentarioData.getDescripcion());
                    nuevoComentarioData.setFechaCreacion(comentarioData.getFechaCreacion());
                    nuevoComentarioData.setIdUsuario(comentarioData.getIdUsuario());
                    nuevoComentarioData.setIdVehiculo(comentarioData.getIdVehiculo());

                    comentarioService.actualizarComentario(comentarioId, nuevoComentarioData);

                    return "redirect:/perfil/" + id + "/comentarios";
                }
                else{
                    model.addAttribute("errorActualizar", "Ho ocurrido un error al intentar actualizar.");
                }

            } catch (UsuarioServiceException e) {
                model.addAttribute("errorActualizar", e.getMessage());
            }
        }

        model.addAttribute("comentarioData", comentarioData);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, idUsuario);
        model.addAttribute("usuario", usuario);

        List<Comentario> comentarios = comentarioService.listadoCompleto();
        Comentario comentarioBuscado = comentarioService.buscarComentarioPorId(comentarios, comentarioId);
        model.addAttribute("comentario", comentarioBuscado);

        return "perfil/editarComentario";
    }

    @PostMapping("/perfil/{id}/comentarios/eliminar/{comentarioId}")
    public String eliminarComentario(@PathVariable("id") Long idUsuario, @PathVariable("comentarioId") Long comentarioId) {
        Long id = managerUserSession.usuarioLogeado();
        comprobarLogueado(id);
        comentarioService.eliminarComentario(comentarioId);
        return "redirect:/perfil/" + idUsuario + "/comentarios";
    }


    // ALQUILERES DE USUARIO

    // Método para mostrar los alquileres de un usuario
    @GetMapping("/perfil/{id}/alquileres")
    public String mostrarListadoAlquileres(@PathVariable(value = "id") Long idUsuario, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarLogueado(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, idUsuario);
        model.addAttribute("usuario", usuario);

        List<Pago> pagos = usuarioService.obtenerPagosPorUsuarioId(idUsuario);
        model.addAttribute("pagos", pagos);

        Map<Long, Long> diasDeAlquiler = new HashMap<>();
        for (Alquiler alquiler : alquilerService.listadoCompleto()) {
            if (!(alquiler.getFechaDevolucion().equals(alquiler.getFechaEntrega()))) {
                long diffInMillies = alquiler.getFechaDevolucion().getTime() - alquiler.getFechaEntrega().getTime();
                long diasDeDiferencia = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                diasDeAlquiler.put(alquiler.getId(), diasDeDiferencia + 1);
            }
            else {
                diasDeAlquiler.put(alquiler.getId(), 1L);
            }
        }
        model.addAttribute("diasDeAlquiler", diasDeAlquiler);

        return "perfil/alquileresUsuario";
    }

    @GetMapping("/perfil/{id}/alquileres/arrendador")
    public String mostrarListadoAlquileresArrendador(@PathVariable(value = "id") Long idUsuario, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarLogueado(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, idUsuario);
        model.addAttribute("usuario", usuario);

        List<Vehiculo> vehiculos = vehiculoService.obtenerVehiculosPorPropietario(idUsuario);

        List<Alquiler> alquileres = alquilerService.obtenerAlquileresPorVehiculos(vehiculos);
        model.addAttribute("alquileres", alquileres);

        Map<Long, Long> diasDeAlquiler = new HashMap<>();
        for (Alquiler alquiler : alquilerService.listadoCompleto()) {
            if (!(alquiler.getFechaDevolucion().equals(alquiler.getFechaEntrega()))) {
                long diffInMillies = alquiler.getFechaDevolucion().getTime() - alquiler.getFechaEntrega().getTime();
                long diasDeDiferencia = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                diasDeAlquiler.put(alquiler.getId(), diasDeDiferencia + 1);
            }
            else {
                diasDeAlquiler.put(alquiler.getId(), 1L);
            }
        }
        model.addAttribute("diasDeAlquiler", diasDeAlquiler);

        return "perfil/alquileresUsuarioArrendador";
    }

    @GetMapping("/perfil/{id}/vehiculosAlquiladosArrendatario")
    public String mostrarVehiculosAlquiladosArrendatario(@PathVariable("id") Long idUsuario, Model model, @RequestParam(defaultValue = "0") int page) {
        Long id = managerUserSession.usuarioLogeado();
        comprobarLogueado(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, idUsuario);
        model.addAttribute("usuario", usuario);

        Pageable pageable = PageRequest.of(page, 3, Sort.by("id").ascending());
        Page<Vehiculo> vehiculosPage = vehiculoService.obtenerVehiculosAlquiladosActivosPorUsuario(idUsuario, pageable);
        model.addAttribute("vehiculosPage", vehiculosPage);

        model.addAttribute("cantidad", vehiculosPage.getContent().isEmpty() ? null : vehiculosPage.getContent().size());

        return "perfil/vehiculosAlquiladosArrendatario";
    }

    @GetMapping("/perfil/{id}/vehiculos-alquilados")
    public String mostrarVehiculosAlquiladosArrendador(@PathVariable("id") Long idUsuario, Model model, @RequestParam(defaultValue = "0") int page) {
        Long id = managerUserSession.usuarioLogeado();
        comprobarLogueado(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, idUsuario);
        model.addAttribute("usuario", usuario);

        Pageable pageable = PageRequest.of(page, 3, Sort.by("id").ascending());
        Page<Vehiculo> vehiculosPage = vehiculoService.obtenerVehiculosConAlquileresActivosPorPropietario(idUsuario, pageable);
        model.addAttribute("vehiculosPage", vehiculosPage);

        model.addAttribute("cantidad", vehiculosPage.getContent().isEmpty() ? null : vehiculosPage.getContent().size());

        return "perfil/vehiculosAlquiladosArrendador";
    }

    @GetMapping("/perfil/{usuarioId}/vehiculos-alquilados/detalles/{vehiculoId}")
    public String mostrarDetallesVehiculoAlquilado(@PathVariable(value = "usuarioId") Long idUsuario, @PathVariable(value = "vehiculoId") Long vehiculoId, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarLogueado(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        VehiculoData vehiculo = vehiculoService.findById(vehiculoId);

        if (vehiculo != null) {
            List<Vehiculo> vehiculos = vehiculoService.listadoCompleto();
            Vehiculo vehiculoBuscado = vehiculoService.buscarVehiculoPorId(vehiculos, vehiculoId);
            model.addAttribute("vehiculo", vehiculoBuscado);

            if (vehiculoBuscado.getOferta() != null) {
                BigDecimal precioOriginal = vehiculoBuscado.getPrecioPorDia();
                BigDecimal porcentajeOferta = BigDecimal.valueOf(vehiculoBuscado.getOferta());
                BigDecimal descuento = porcentajeOferta.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
                BigDecimal precioOferta = precioOriginal.multiply(BigDecimal.ONE.subtract(descuento));
                precioOferta = precioOferta.setScale(0, RoundingMode.HALF_UP);
                model.addAttribute("precioOferta", precioOferta);
            }

            if (vehiculoBuscado.getComentarios() != null) {
                model.addAttribute("comentarios", vehiculoService.obtenerComentariosPorVehiculoId(vehiculoBuscado.getId()));

                if (!vehiculoService.obtenerComentariosPorVehiculoId(vehiculoBuscado.getId()).isEmpty()) {
                    model.addAttribute("cantidadComentarios", vehiculoService.obtenerComentariosPorVehiculoId(vehiculoBuscado.getId()).size());
                }
                else {
                    model.addAttribute("cantidadComentarios", null);
                }
            }

            // Obtener alquileres activos para el vehículo
            List<Alquiler> alquileresActivos = alquilerService.obtenerAlquileresActivosPorVehiculoId(vehiculoId);
            model.addAttribute("alquileresActivos", alquileresActivos);

            return "perfil/detallesVehiculoAlquiladoUsuario";
        }

        return "redirect:/perfil/" + idUsuario + "/vehiculosAlquiladosArrendatario";
    }

    @GetMapping("/perfil/{usuarioId}/vehiculos-alquilados/arrendador/detalles/{vehiculoId}")
    public String mostrarDetallesVehiculoAlquiladoArrendador(@PathVariable(value = "usuarioId") Long idUsuario, @PathVariable(value = "vehiculoId") Long vehiculoId, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarLogueado(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        VehiculoData vehiculo = vehiculoService.findById(vehiculoId);

        if (vehiculo != null) {
            List<Vehiculo> vehiculos = vehiculoService.listadoCompleto();
            Vehiculo vehiculoBuscado = vehiculoService.buscarVehiculoPorId(vehiculos, vehiculoId);
            model.addAttribute("vehiculo", vehiculoBuscado);

            if (vehiculoBuscado.getOferta() != null) {
                BigDecimal precioOriginal = vehiculoBuscado.getPrecioPorDia();
                BigDecimal porcentajeOferta = BigDecimal.valueOf(vehiculoBuscado.getOferta());
                BigDecimal descuento = porcentajeOferta.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
                BigDecimal precioOferta = precioOriginal.multiply(BigDecimal.ONE.subtract(descuento));
                precioOferta = precioOferta.setScale(0, RoundingMode.HALF_UP);
                model.addAttribute("precioOferta", precioOferta);
            }

            if (vehiculoBuscado.getComentarios() != null) {
                model.addAttribute("comentarios", vehiculoService.obtenerComentariosPorVehiculoId(vehiculoBuscado.getId()));

                if (!vehiculoService.obtenerComentariosPorVehiculoId(vehiculoBuscado.getId()).isEmpty()) {
                    model.addAttribute("cantidadComentarios", vehiculoService.obtenerComentariosPorVehiculoId(vehiculoBuscado.getId()).size());
                }
                else {
                    model.addAttribute("cantidadComentarios", null);
                }
            }

            // Obtener alquileres activos para el vehículo
            List<Alquiler> alquileresActivos = alquilerService.obtenerAlquileresActivosPorVehiculoId(vehiculoId);
            model.addAttribute("alquileresActivos", alquileresActivos);

            return "perfil/detallesVehiculoAlquiladoUsuarioArrendador";
        }

        return "redirect:/perfil/" + idUsuario + "/vehiculos-alquilados";
    }

    @PostMapping("/perfil/{id}/alquileres/{vehiculoId}/arrendatario/eliminar/{alquilerId}")
    public String eliminarAlquilerArrendatario(@PathVariable("id") Long idUsuario, @PathVariable("alquilerId") Long alquilerId, @PathVariable("vehiculoId") Long vehiculoId) {
        Long id = managerUserSession.usuarioLogeado();
        comprobarLogueado(id);
        alquilerService.eliminarAlquiler(alquilerId);
        return "redirect:/perfil/" + idUsuario + "/vehiculos-alquilados/detalles/" + vehiculoId;
    }

    @PostMapping("/perfil/{id}/alquileres/{vehiculoId}/arrendador/eliminar/{alquilerId}")
    public String eliminarAlquilerArrendador(@PathVariable("id") Long idUsuario, @PathVariable("alquilerId") Long alquilerId, @PathVariable("vehiculoId") Long vehiculoId) {
        Long id = managerUserSession.usuarioLogeado();
        comprobarLogueado(id);
        alquilerService.eliminarAlquiler(alquilerId);
        return "redirect:/perfil/" + idUsuario + "/vehiculos-alquilados/arrendador/detalles/" + vehiculoId;
    }

    @PostMapping("/perfil/{id}/alquileres/eliminar/{alquilerId}")
    public String eliminarAlquiler(@PathVariable("id") Long idUsuario, @PathVariable("alquilerId") Long alquilerId) {
        Long id = managerUserSession.usuarioLogeado();
        comprobarLogueado(id);
        alquilerService.eliminarAlquiler(alquilerId);
        return "redirect:/perfil/" + idUsuario + "/alquileres";
    }


    // VEHÍCULOS DE USUARIO

    @GetMapping("/perfil/{id}/añadir-vehiculo")
    public String mostrarAñadirVehiculo(Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarLogueado(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        RegistroVehiculoData registroVehiculoData = new RegistroVehiculoData();

        model.addAttribute("registroVehiculoData", registroVehiculoData);

        List<Marca> marcas = marcaService.listadoCompleto();
        model.addAttribute("marcas", marcas);
        List<Modelo> modelos = modeloService.listadoCompleto();
        model.addAttribute("modelos", modelos);
        List<Categoria> categorias = categoriaService.listadoCompleto();
        model.addAttribute("categorias", categorias);
        List<Color> colores = colorService.listadoCompleto();
        model.addAttribute("colores", colores);
        List<Transmision> transmisiones = transmisionService.listadoCompleto();
        model.addAttribute("transmisiones", transmisiones);

        return "perfil/añadirVehiculoUsuario";
    }

    @GetMapping("/modelosPorMarca/{marcaId}")
    public @ResponseBody Map<Long, String> getModelosPorMarca(@PathVariable Long marcaId) {
        List<ModeloData> modelos = modeloService.findByMarcaId(marcaId);
        Map<Long, String> modeloMap = new HashMap<>();
        for (ModeloData modelo : modelos) {
            modeloMap.put(modelo.getId(), modelo.getNombre());
        }
        return modeloMap;
    }

    @PostMapping("/perfil/{usuarioId}/añadir-vehiculo")
    public String registrarVehiculo(@PathVariable("usuarioId") Long idUsuario, @Valid RegistroVehiculoData registroVehiculoData, BindingResult result, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarLogueado(id);

        model.addAttribute("registroVehiculoData", registroVehiculoData);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        List<Marca> marcas = marcaService.listadoCompleto();
        model.addAttribute("marcas", marcas);
        List<Modelo> modelos = modeloService.listadoCompleto();
        model.addAttribute("modelos", modelos);
        List<Categoria> categorias = categoriaService.listadoCompleto();
        model.addAttribute("categorias", categorias);
        List<Color> colores = colorService.listadoCompleto();
        model.addAttribute("colores", colores);
        List<Transmision> transmisiones = transmisionService.listadoCompleto();
        model.addAttribute("transmisiones", transmisiones);

        if (result.hasErrors()) {
            return "perfil/añadirVehiculoUsuario";
        }
        else{
            try {
                if (vehiculoService.findByMatricula(registroVehiculoData.getMatricula()) != null) {
                    model.addAttribute("errorActualizar", "El vehículo con matrícula (" + registroVehiculoData.getMatricula() + ") ya existe.");
                    return "perfil/añadirVehiculoUsuario";
                }

                registroVehiculoData.setIdUsuario(idUsuario);

                VehiculoData vehiculoData = new VehiculoData();
                vehiculoData.setMatricula(registroVehiculoData.getMatricula());
                vehiculoData.setDescripcion(registroVehiculoData.getDescripcion());
                vehiculoData.setKilometraje(registroVehiculoData.getKilometraje());
                vehiculoData.setAnyoFabricacion(registroVehiculoData.getAnyoFabricacion());
                vehiculoData.setCapacidadPasajeros(registroVehiculoData.getCapacidadPasajeros());
                vehiculoData.setCapacidadMaletero(registroVehiculoData.getCapacidadMaletero());
                vehiculoData.setNumeroPuertas(registroVehiculoData.getNumeroPuertas());
                vehiculoData.setNumeroMarchas(registroVehiculoData.getNumeroMarchas());
                vehiculoData.setAireAcondicionado(registroVehiculoData.isAireAcondicionado());
                vehiculoData.setEnMantenimiento(registroVehiculoData.isEnMantenimiento());
                vehiculoData.setOferta(registroVehiculoData.getOferta());
                vehiculoData.setPrecioPorDia(registroVehiculoData.getPrecioPorDia());
                vehiculoData.setPrecioPorMedioDia(registroVehiculoData.getPrecioPorMedioDia());
                vehiculoData.setPrecioCombustible(registroVehiculoData.getPrecioCombustible());
                vehiculoData.setIdMarca(registroVehiculoData.getIdMarca());
                vehiculoData.setIdModelo(registroVehiculoData.getIdModelo());
                vehiculoData.setIdCategoria(registroVehiculoData.getIdCategoria());
                vehiculoData.setIdTransmision(registroVehiculoData.getIdTransmision());
                vehiculoData.setIdColor(registroVehiculoData.getIdColor());
                vehiculoData.setIdUsuario(registroVehiculoData.getIdUsuario());

                MultipartFile imagen = registroVehiculoData.getImagen();
                if (!imagen.isEmpty()) {
                    String contentType = imagen.getContentType();
                    assert contentType != null;
                    if (contentType.equals("image/jpeg") || contentType.equals("image/jpg")) {
                        // Define la ruta del directorio 'uploads'
                        String uploadDir = "uploads";

                        // Formatear la fecha actual para incluirla en el nombre del archivo
                        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                        String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(imagen.getOriginalFilename()));
                        String fileName = originalFileName.replace(".", dateTime + ".");

                        Path uploadPath = Paths.get(uploadDir);

                        if (!Files.exists(uploadPath)) {
                            Files.createDirectories(uploadPath);
                        }

                        try (InputStream inputStream = imagen.getInputStream()) {
                            Path filePath = uploadPath.resolve(fileName);
                            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        // Guarda solo el nombre del archivo en la base de datos
                        vehiculoData.setImagen(fileName);
                    } else {
                        // Manejo de error si el archivo no es una imagen JPEG/JPG
                        model.addAttribute("errorActualizar", "Solo se permiten archivos de imagen en formato JPEG y JPG.");
                        return "perfil/añadirVehiculoUsuario";
                    }
                } else {
                    // Manejo de error si no se subió ninguna imagen
                    model.addAttribute("errorActualizar", "Ha ocurrido un error en la subida de la imagen.");
                    return "perfil/añadirVehiculoUsuario";
                }

                vehiculoService.registrarVehiculo(vehiculoData);

                return "redirect:/perfil/" + idUsuario;
            } catch (Exception e) {
                model.addAttribute("errorActualizar", e.getMessage());
            }
        }

        return "perfil/añadirVehiculoUsuario";
    }

    @GetMapping("/perfil/{id}/vehiculos")
    public String mostrarListadoVehiculos(@PathVariable(value = "id") Long idUsuario, Model model, @RequestParam(defaultValue = "0") int page) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarLogueado(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, idUsuario);
        model.addAttribute("usuario", usuario);

        Pageable pageable = PageRequest.of(page, 6, Sort.by("id").ascending());
        Page<Vehiculo> vehiculosPage = vehiculoService.listadoPaginadoVehiculosDeUsuario(idUsuario, pageable);
        model.addAttribute("vehiculosPage", vehiculosPage);

        if (vehiculosPage.getContent().isEmpty()) {
            model.addAttribute("cantidad", null);
        }
        else {
            model.addAttribute("cantidad", vehiculosPage.getContent().size());
        }

        return "perfil/vehiculosUsuario";
    }

    @GetMapping("/perfil/{usuarioId}/vehiculos/editar/{vehiculoId}")
    public String mostrarEditarVehiculo(@PathVariable(value = "usuarioId") Long idUsuario, @PathVariable(value = "vehiculoId") Long vehiculoId, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarLogueado(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        VehiculoData vehiculo = vehiculoService.findById(vehiculoId);

        if (vehiculo != null) {
            List<Vehiculo> vehiculos = vehiculoService.listadoCompleto();
            Vehiculo vehiculoBuscado = vehiculoService.buscarVehiculoPorId(vehiculos, vehiculoId);
            model.addAttribute("vehiculo", vehiculoBuscado);

            RegistroVehiculoData registroVehiculoData = new RegistroVehiculoData();
            registroVehiculoData.setMatricula(vehiculoBuscado.getMatricula());
            registroVehiculoData.setDescripcion(vehiculoBuscado.getDescripcion());
            registroVehiculoData.setKilometraje(vehiculoBuscado.getKilometraje());
            registroVehiculoData.setAnyoFabricacion(vehiculoBuscado.getAnyoFabricacion());
            registroVehiculoData.setCapacidadPasajeros(vehiculoBuscado.getCapacidadPasajeros());
            registroVehiculoData.setCapacidadMaletero(vehiculoBuscado.getCapacidadMaletero());
            registroVehiculoData.setNumeroPuertas(vehiculoBuscado.getNumeroPuertas());
            registroVehiculoData.setNumeroMarchas(vehiculoBuscado.getNumeroMarchas());
            registroVehiculoData.setAireAcondicionado(vehiculoBuscado.isAireAcondicionado());
            registroVehiculoData.setEnMantenimiento(vehiculoBuscado.isEnMantenimiento());
            registroVehiculoData.setOferta(vehiculoBuscado.getOferta());
            registroVehiculoData.setPrecioPorDia(vehiculoBuscado.getPrecioPorDia());
            registroVehiculoData.setPrecioPorMedioDia(vehiculoBuscado.getPrecioPorMedioDia());
            registroVehiculoData.setPrecioCombustible(vehiculoBuscado.getPrecioCombustible());
            registroVehiculoData.setIdMarca(vehiculoBuscado.getMarca().getId());
            registroVehiculoData.setIdModelo(vehiculoBuscado.getModelo().getId());
            registroVehiculoData.setIdCategoria(vehiculoBuscado.getCategoria().getId());
            registroVehiculoData.setIdTransmision(vehiculoBuscado.getTransmision().getId());
            registroVehiculoData.setIdColor(vehiculoBuscado.getColor().getId());

            model.addAttribute("registroVehiculoData", registroVehiculoData);

            model.addAttribute("marcas", marcaService.listadoCompleto());
            model.addAttribute("modelos", modeloService.listadoCompleto());
            model.addAttribute("categorias", categoriaService.listadoCompleto());
            model.addAttribute("colores", colorService.listadoCompleto());
            model.addAttribute("transmisiones", transmisionService.listadoCompleto());

            return "perfil/editarVehiculoUsuario";
        }

        return "redirect:/perfil/" + idUsuario + "/vehiculos";
    }

    @PostMapping("/perfil/{usuarioId}/vehiculos/editar/{vehiculoId}")
    public String actualizarVehiculo(@PathVariable(value = "usuarioId") Long idUsuario, @PathVariable Long vehiculoId, @Valid RegistroVehiculoData registroVehiculoData, BindingResult result, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarLogueado(id);

        model.addAttribute("registroVehiculoData", registroVehiculoData);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        model.addAttribute("marcas", marcaService.listadoCompleto());
        model.addAttribute("modelos", modeloService.listadoCompleto());
        model.addAttribute("categorias", categoriaService.listadoCompleto());
        model.addAttribute("colores", colorService.listadoCompleto());
        model.addAttribute("transmisiones", transmisionService.listadoCompleto());

        List<Vehiculo> vehiculos = vehiculoService.listadoCompleto();
        Vehiculo vehiculoBuscado = vehiculoService.buscarVehiculoPorId(vehiculos, vehiculoId);
        model.addAttribute("vehiculo", vehiculoBuscado);

        if (result.hasErrors()) {
            return "perfil/editarVehiculoUsuario";
        }
        else{
            try {
                VehiculoData nuevoVehiculoData = vehiculoService.findById(vehiculoId);

                if(nuevoVehiculoData.getMatricula() != null) {
                    if ((vehiculoService.findByMatricula(registroVehiculoData.getMatricula()) != null) && !registroVehiculoData.getMatricula().equals(nuevoVehiculoData.getMatricula())) {
                        model.addAttribute("errorActualizar", "El vehículo con matrícula (" + registroVehiculoData.getMatricula() + ") ya existe.");
                        return "perfil/editarVehiculoUsuario";
                    }

                    nuevoVehiculoData.setMatricula(registroVehiculoData.getMatricula());
                    nuevoVehiculoData.setDescripcion(registroVehiculoData.getDescripcion());
                    nuevoVehiculoData.setKilometraje(registroVehiculoData.getKilometraje());
                    nuevoVehiculoData.setAnyoFabricacion(registroVehiculoData.getAnyoFabricacion());
                    nuevoVehiculoData.setCapacidadPasajeros(registroVehiculoData.getCapacidadPasajeros());
                    nuevoVehiculoData.setCapacidadMaletero(registroVehiculoData.getCapacidadMaletero());
                    nuevoVehiculoData.setNumeroPuertas(registroVehiculoData.getNumeroPuertas());
                    nuevoVehiculoData.setNumeroMarchas(registroVehiculoData.getNumeroMarchas());
                    nuevoVehiculoData.setAireAcondicionado(registroVehiculoData.isAireAcondicionado());
                    nuevoVehiculoData.setEnMantenimiento(registroVehiculoData.isEnMantenimiento());
                    nuevoVehiculoData.setOferta(registroVehiculoData.getOferta());
                    nuevoVehiculoData.setPrecioPorDia(registroVehiculoData.getPrecioPorDia());
                    nuevoVehiculoData.setPrecioPorMedioDia(registroVehiculoData.getPrecioPorMedioDia());
                    nuevoVehiculoData.setPrecioCombustible(registroVehiculoData.getPrecioCombustible());
                    nuevoVehiculoData.setIdMarca(registroVehiculoData.getIdMarca());
                    nuevoVehiculoData.setIdModelo(registroVehiculoData.getIdModelo());
                    nuevoVehiculoData.setIdCategoria(registroVehiculoData.getIdCategoria());
                    nuevoVehiculoData.setIdTransmision(registroVehiculoData.getIdTransmision());
                    nuevoVehiculoData.setIdColor(registroVehiculoData.getIdColor());

                    MultipartFile imagen = registroVehiculoData.getImagen();
                    if (!imagen.isEmpty()) {
                        String contentType = imagen.getContentType();
                        assert contentType != null;
                        if (contentType.equals("image/jpeg") || contentType.equals("image/jpg")) {
                            // Define la ruta del directorio 'uploads'
                            String uploadDir = "uploads";

                            // Formatear la fecha actual para incluirla en el nombre del archivo
                            String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                            String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(imagen.getOriginalFilename()));
                            String fileName = originalFileName.replace(".", dateTime + ".");

                            Path uploadPath = Paths.get(uploadDir);

                            if (!Files.exists(uploadPath)) {
                                Files.createDirectories(uploadPath);
                            }

                            try (InputStream inputStream = imagen.getInputStream()) {
                                Path filePath = uploadPath.resolve(fileName);
                                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            // Guarda solo el nombre del archivo en la base de datos
                            nuevoVehiculoData.setImagen(fileName);
                        } else {
                            // Manejo de error si el archivo no es una imagen JPEG/JPG
                            model.addAttribute("errorActualizar", "Solo se permiten archivos de imagen en formato JPEG y JPG.");
                            return "perfil/editarVehiculoUsuario";
                        }
                    }

                    vehiculoService.actualizarVehiculo(vehiculoId, nuevoVehiculoData);

                    return "redirect:/perfil/" + idUsuario + "/vehiculos";
                }
                else{
                    model.addAttribute("errorActualizar", "Ha ocurrido un error al intentar actualizar.");
                }
            } catch (Exception e) {
                model.addAttribute("errorActualizar", e.getMessage());
            }
        }

        return "perfil/editarVehiculoUsuario";
    }

    @PostMapping("/perfil/{usuarioId}/vehiculos/eliminar/{vehiculoId}")
    public String eliminarVehiculo(@PathVariable(value = "usuarioId") Long idUsuario, @PathVariable("vehiculoId") Long vehiculoId, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarLogueado(id);

        try {
            vehiculoService.eliminarVehiculo(vehiculoId);
            model.addAttribute("success", "Vehículo eliminado con éxito.");
        } catch (Exception e) {
            model.addAttribute("error", "No se puede eliminar el vehículo debido a: " + e.getMessage());
        }

        return "redirect:/perfil/" + idUsuario + "/vehiculos";
    }

    @PostMapping("/perfil/{id}/vehiculos/{vehiculoId}/eliminar-comentario/{comentarioId}")
    public String eliminarComentario(@PathVariable("id") Long idUsuario, @PathVariable("comentarioId") Long comentarioId, @PathVariable("vehiculoId") Long vehiculoId, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarLogueado(id);

        try {
            comentarioService.eliminarComentario(comentarioId);
        } catch (Exception e) {
            model.addAttribute("error", "No se puede eliminar el vehículo debido a: " + e.getMessage());
        }
        return "redirect:/perfil/" + idUsuario + "/vehiculos/detalles/" + vehiculoId;
    }

    @GetMapping("/perfil/{usuarioId}/vehiculos/detalles/{vehiculoId}")
    public String mostrarDetallesVehiculo(@PathVariable(value = "usuarioId") Long idUsuario, @PathVariable(value = "vehiculoId") Long vehiculoId, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarLogueado(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        VehiculoData vehiculo = vehiculoService.findById(vehiculoId);

        if (vehiculo != null) {
            List<Vehiculo> vehiculos = vehiculoService.listadoCompleto();
            Vehiculo vehiculoBuscado = vehiculoService.buscarVehiculoPorId(vehiculos, vehiculoId);
            model.addAttribute("vehiculo", vehiculoBuscado);

            if (vehiculoBuscado.getOferta() != null) {
                BigDecimal precioOriginal = vehiculoBuscado.getPrecioPorDia();
                BigDecimal porcentajeOferta = BigDecimal.valueOf(vehiculoBuscado.getOferta());
                BigDecimal descuento = porcentajeOferta.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
                BigDecimal precioOferta = precioOriginal.multiply(BigDecimal.ONE.subtract(descuento));
                precioOferta = precioOferta.setScale(0, RoundingMode.HALF_UP);
                model.addAttribute("precioOferta", precioOferta);
            }

            if (vehiculoBuscado.getComentarios() != null) {
                model.addAttribute("comentarios", vehiculoService.obtenerComentariosPorVehiculoId(vehiculoBuscado.getId()));

                if (!vehiculoService.obtenerComentariosPorVehiculoId(vehiculoBuscado.getId()).isEmpty()) {
                    model.addAttribute("cantidadComentarios", vehiculoService.obtenerComentariosPorVehiculoId(vehiculoBuscado.getId()).size());
                }
                else {
                    model.addAttribute("cantidadComentarios", null);
                }
            }

            return "perfil/detallesVehiculoUsuario";
        }

        return "redirect:/perfil/" + idUsuario + "/vehiculos";
    }


    // CUENTA DE USUARIO

    @GetMapping("/perfil/{id}/cuenta")
    public String mostrarCuenta(@PathVariable(value = "id") Long idUsuario, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarLogueado(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, idUsuario);
        List<Vehiculo> vehiculos = usuarioService.obtenerVehiculosPorUsuarioId(idUsuario);
        model.addAttribute("vehiculos", vehiculos);
        model.addAttribute("usuario", usuario);

        List<Pago> pagos = usuarioService.obtenerPagosPorUsuarioId(idUsuario);
        model.addAttribute("pagos", pagos);

        List<Alquiler> alquileres = new ArrayList<>();

        // Recorrer cada vehículo y obtener sus alquileres
        for (Vehiculo vehiculo : vehiculos) {
            List<Alquiler> alquileresDeVehiculo = usuarioService.obtenerAlquileresPorVehiculoId(vehiculo.getId());
            alquileres.addAll(alquileresDeVehiculo);
        }

        model.addAttribute("alquileres", alquileres);

        return "perfil/cuentaUsuario";
    }

    @PostMapping("/perfil/{id}/añadirSaldo")
    public String añadirSaldo(@PathVariable(value = "id") Long idUsuario, Model model) {
        Usuario usuario = usuarioService.añadirSaldo(idUsuario, BigDecimal.valueOf(100));
        return "redirect:/perfil/" + idUsuario + "/cuenta";
    }
}
