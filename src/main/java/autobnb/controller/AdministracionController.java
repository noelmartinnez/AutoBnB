package autobnb.controller;

import autobnb.authentication.ManagerUserSession;
import autobnb.controller.exception.UsuarioNoAutorizadoException;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Controller
public class AdministracionController {
    @Autowired
    private ManagerUserSession managerUserSession;
    @Autowired
    UsuarioService usuarioService;
    @Autowired
    ComentarioService comentarioService;
    @Autowired
    AlquilerService alquilerService;
    @Autowired
    CuentaService cuentaService;
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
    @Autowired
    VehiculoService vehiculoService;

    private void comprobarAdmin(Long idUsuario) {
        if (idUsuario != null) {
            UsuarioData user = usuarioService.findById(idUsuario);
            if (user != null && !user.isAdministrador()) {
                throw new UsuarioNoAutorizadoException();
            }
        } else {
            throw new UsuarioNoLogeadoException();
        }
    }

    @GetMapping("/administracion")
    public String panelAdministracion(Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        UsuarioData user = usuarioService.findById(id);
        model.addAttribute("usuario", user);

        return "administracion/panelAdministrador";
    }


    // COMENTARIOS

    @GetMapping("/administracion/comentarios")
    public String mostrarListadoComentarios(Model model, @RequestParam(defaultValue = "0") int page) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        Pageable pageable = PageRequest.of(page, 8, Sort.by("id"));
        Page<Comentario> comentarios = comentarioService.listadoPaginado(pageable);
        model.addAttribute("comentarios", comentarios);

        if (comentarios.getContent().isEmpty()) {
            model.addAttribute("cantidad", null);
        }
        else {
            model.addAttribute("cantidad", comentarios.getContent().size());
        }

        return "administracion/listar/administracionComentarios";
    }

    @GetMapping("/administracion/comentarios/editar/{comentarioId}")
    public String mostrarEditarComentario(@PathVariable(value = "comentarioId") Long comentarioId, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

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
            return "administracion/editar/editarComentarioAdministrador";
        }


        return "redirect:/administracion/comentarios";
    }

    @PostMapping("/administracion/comentarios/editar/{comentarioId}")
    public String actualizarComentario(@PathVariable Long comentarioId, @Valid ComentarioData comentarioData, BindingResult result, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        if (result.hasErrors()) {
            System.out.println("Ha ocurrido un error.");
        }
        else{
            try {
                ComentarioData nuevoComentarioData = comentarioService.findById(comentarioId);

                if(comentarioData.getDescripcion() != null && !Objects.equals(comentarioData.getDescripcion(), "")) {
                    nuevoComentarioData.setDescripcion(comentarioData.getDescripcion());
                    nuevoComentarioData.setFechaCreacion(comentarioData.getFechaCreacion());
                    nuevoComentarioData.setIdUsuario(comentarioData.getIdUsuario());
                    nuevoComentarioData.setIdVehiculo(comentarioData.getIdVehiculo());

                    comentarioService.actualizarComentario(comentarioId, nuevoComentarioData);

                    return "redirect:/administracion/comentarios";
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
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        List<Comentario> comentarios = comentarioService.listadoCompleto();
        Comentario comentarioBuscado = comentarioService.buscarComentarioPorId(comentarios, comentarioId);
        model.addAttribute("comentario", comentarioBuscado);

        return "administracion/editar/editarComentarioAdministrador";
    }

    @PostMapping("/administracion/comentarios/eliminar/{comentarioId}")
    public String eliminarComentario(@PathVariable("comentarioId") Long comentarioId) {
        Long id = managerUserSession.usuarioLogeado();
        comprobarAdmin(id);
        comentarioService.eliminarComentario(comentarioId);
        return "redirect:/administracion/comentarios";
    }


    // ALQUILERES

    @GetMapping("/administracion/alquileres")
    public String mostrarListadoAlquileres(Model model, @RequestParam(defaultValue = "0") int page) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        Map<Long, Long> diasDeAlquiler = new HashMap<>();
        for (Alquiler alquiler : alquilerService.listadoCompleto()) {
            if (!(alquiler.getFechaDevolucion().equals(alquiler.getFechaEntrega()))) {
                long diffInMillies = alquiler.getFechaDevolucion().getTime() - alquiler.getFechaEntrega().getTime();
                long diasDeDiferencia = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                diasDeAlquiler.put(alquiler.getId(), diasDeDiferencia + 1);
            }
        }
        model.addAttribute("diasDeAlquiler", diasDeAlquiler);

        Pageable pageable = PageRequest.of(page, 8, Sort.by("id"));
        Page<Alquiler> alquileres = alquilerService.listadoPaginado(pageable);
        model.addAttribute("alquileres", alquileres);

        if (alquileres.getContent().isEmpty()) {
            model.addAttribute("cantidad", null);
        }
        else {
            model.addAttribute("cantidad", alquileres.getContent().size());
        }

        return "administracion/listar/administracionAlquileres";
    }

    @PostMapping("/administracion/alquileres/eliminar/{alquilerId}")
    public String eliminarAlquiler(@PathVariable("alquilerId") Long alquilerId) {
        Long id = managerUserSession.usuarioLogeado();
        comprobarAdmin(id);
        alquilerService.eliminarAlquiler(alquilerId);
        return "redirect:/administracion/alquileres";
    }


    // CUENTAS

    @GetMapping("/administracion/cuentas")
    public String mostrarListadoCuentas(Model model, @RequestParam(defaultValue = "0") int page) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        Pageable pageable = PageRequest.of(page, 8, Sort.by("id"));
        Page<Cuenta> cuentas = cuentaService.listadoPaginado(pageable);
        model.addAttribute("cuentas", cuentas);

        if (cuentas.getContent().isEmpty()) {
            model.addAttribute("cantidad", null);
        }
        else {
            model.addAttribute("cantidad", cuentas.getContent().size());
        }

        return "administracion/listar/administracionCuentas";
    }


    // MARCAS

    @GetMapping("/administracion/marcas")
    public String mostrarListadoMarcas(Model model, @RequestParam(defaultValue = "0") int page) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        Pageable pageable = PageRequest.of(page, 8, Sort.by("nombre"));
        Page<Marca> marcas = marcaService.listadoPaginado(pageable);
        model.addAttribute("marcas", marcas);

        if (marcas.getContent().isEmpty()) {
            model.addAttribute("cantidad", null);
        }
        else {
            model.addAttribute("cantidad", marcas.getContent().size());
        }

        return "administracion/listar/administracionMarcas";
    }

    @GetMapping("/administracion/marcas/editar/{marcaId}")
    public String mostrarEditarMarca(@PathVariable(value = "marcaId") Long marcaId, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        MarcaData marca = marcaService.findById(marcaId);

        if (marca != null) {
            List<Marca> marcas = marcaService.listadoCompleto();
            Marca marcaBuscada = marcaService.buscarMarcaPorId(marcas, marcaId);
            model.addAttribute("marca", marcaBuscada);

            MarcaData marcaData = new MarcaData();
            marcaData.setNombre(marcaBuscada.getNombre());

            model.addAttribute("marcaData", marcaData);
            return "administracion/editar/editarMarcaAdministrador";
        }

        return "redirect:/administracion/marcas";
    }

    @PostMapping("/administracion/marcas/editar/{marcaId}")
    public String actualizarMarca(@PathVariable Long marcaId, @Valid MarcaData marcaData, BindingResult result, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        model.addAttribute("marcaData", marcaData);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        List<Marca> marcas = marcaService.listadoCompleto();
        Marca marcaBuscada = marcaService.buscarMarcaPorId(marcas, marcaId);
        model.addAttribute("marca", marcaBuscada);

        if (result.hasErrors() || marcaData.getNombre().trim().isEmpty()) {
            if(marcaData.getNombre().trim().isEmpty()) {
                model.addAttribute("errorActualizar", "El nombre de la marca no puede estar vacío.");
            } else {
                System.out.println("Ha ocurrido un error.");
            }
            return "administracion/editar/editarMarcaAdministrador";
        }
        else{
            try {
                MarcaData nuevaMarcaData = marcaService.findById(marcaId);

                if(nuevaMarcaData.getNombre() != null) {
                    if (marcaService.findByNombre(marcaData.getNombre()) != null) {
                        model.addAttribute("errorActualizar", "La marca con nombre (" + marcaData.getNombre() + ") ya existe.");
                        return "administracion/editar/editarMarcaAdministrador";
                    }

                    nuevaMarcaData.setNombre(marcaData.getNombre());

                    marcaService.actualizarMarca(marcaId, nuevaMarcaData);

                    return "redirect:/administracion/marcas";
                }
                else{
                    model.addAttribute("errorActualizar", "Ha ocurrido un error al intentar actualizar.");
                }

            } catch (UsuarioServiceException e) {
                model.addAttribute("errorActualizar", e.getMessage());
            }
        }

        return "administracion/editar/editarMarcaAdministrador";
    }

    @PostMapping("/administracion/marcas/eliminar/{marcaId}")
    public String eliminarMarca(@PathVariable("marcaId") Long marcaId, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        if (marcaService.tieneVehiculosAsociados(marcaId)) {
            model.addAttribute("error", "No se puede eliminar la marca porque tiene vehículos asociados.");
        } else {
            marcaService.eliminarMarca(marcaId);
            model.addAttribute("success", "Marca eliminada con éxito.");
        }

        return "redirect:/administracion/marcas";
    }

    @GetMapping("/administracion/marcas/crear")
    public String mostrarCrearMarca(Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        MarcaData marcaData = new MarcaData();
        model.addAttribute("marcaData", marcaData);
        return "administracion/crear/crearMarca";
    }

    @PostMapping("/administracion/marcas/crear")
    public String crearMarca(@Valid MarcaData marcaData, BindingResult result, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        model.addAttribute("marcaData", marcaData);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        if (result.hasErrors() || marcaData.getNombre().trim().isEmpty()) {
            if(marcaData.getNombre().trim().isEmpty()) {
                model.addAttribute("errorCrear", "El nombre de la marca no puede estar vacío.");
            } else {
                System.out.println("Ha ocurrido un error.");
            }
            return "administracion/crear/crearMarca";
        }
        else{
            try {
                if (marcaService.findByNombre(marcaData.getNombre()) != null) {
                    model.addAttribute("errorCrear", "La marca con nombre (" + marcaData.getNombre() + ") ya existe.");
                    return "administracion/crear/crearMarca";
                }

                marcaService.crearMarca(marcaData);

                return "redirect:/administracion/marcas";

            } catch (UsuarioServiceException e) {
                model.addAttribute("errorCrear", e.getMessage());
            }
        }

        return "administracion/crear/crearMarca";
    }


    // MODELOS

    @GetMapping("/administracion/modelos")
    public String mostrarListadoModelos(Model model, @RequestParam(defaultValue = "0") int page) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        Pageable pageable = PageRequest.of(page, 8, Sort.by("nombre"));
        Page<Modelo> modelos = modeloService.listadoPaginado(pageable);
        model.addAttribute("modelos", modelos);

        if (modelos.getContent().isEmpty()) {
            model.addAttribute("cantidad", null);
        }
        else {
            model.addAttribute("cantidad", modelos.getContent().size());
        }

        int totalPages = modelos.getTotalPages();
        int currentPage = modelos.getNumber();
        int startPage = Math.max(0, currentPage - 3);
        int endPage = Math.min(currentPage + 3, totalPages - 1);

        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "administracion/listar/administracionModelos";
    }

    @GetMapping("/administracion/modelos/editar/{modeloId}")
    public String mostrarEditarModelo(@PathVariable(value = "modeloId") Long modeloId, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        ModeloData modelo = modeloService.findById(modeloId);

        if (modelo != null) {
            List<Modelo> modelos = modeloService.listadoCompleto();
            Modelo modeloBuscado = modeloService.buscarModeloPorId(modelos, modeloId);
            model.addAttribute("modelo", modeloBuscado);

            ModeloData modeloData = new ModeloData();
            modeloData.setNombre(modeloBuscado.getNombre());

            model.addAttribute("modeloData", modeloData);
            return "administracion/editar/editarModeloAdministrador";
        }

        return "redirect:/administracion/modelos";
    }

    @PostMapping("/administracion/modelos/editar/{modeloId}")
    public String actualizarModelo(@PathVariable Long modeloId, @Valid ModeloData modeloData, BindingResult result, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        model.addAttribute("modeloData", modeloData);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        List<Modelo> modelos = modeloService.listadoCompleto();
        Modelo modeloBuscado = modeloService.buscarModeloPorId(modelos, modeloId);
        model.addAttribute("modelo", modeloBuscado);

        if (result.hasErrors() || modeloData.getNombre().trim().isEmpty()) {
            if(modeloData.getNombre().trim().isEmpty()) {
                model.addAttribute("errorActualizar", "El nombre del modelo no puede estar vacío.");
            } else {
                System.out.println("Ha ocurrido un error.");
            }
            return "administracion/editar/editarModeloAdministrador";
        }
        else{
            try {
                ModeloData nuevoModeloData = modeloService.findById(modeloId);

                if(nuevoModeloData.getNombre() != null) {
                    if (modeloService.findByNombre(modeloData.getNombre()) != null) {
                        model.addAttribute("errorActualizar", "El modelo con nombre (" + modeloData.getNombre() + ") ya existe.");
                        return "administracion/editar/editarModeloAdministrador";
                    }

                    nuevoModeloData.setNombre(modeloData.getNombre());

                    modeloService.actualizarModelo(modeloId, nuevoModeloData);

                    return "redirect:/administracion/modelos";
                }
                else{
                    model.addAttribute("errorActualizar", "Ha ocurrido un error al intentar actualizar.");
                }

            } catch (UsuarioServiceException e) {
                model.addAttribute("errorActualizar", e.getMessage());
            }
        }

        return "administracion/editar/editarModeloAdministrador";
    }

    @PostMapping("/administracion/modelos/eliminar/{modeloId}")
    public String eliminarModelo(@PathVariable("modeloId") Long modeloId, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        if (modeloService.tieneVehiculosAsociados(modeloId)) {
            model.addAttribute("error", "No se puede eliminar el modelo porque tiene vehículos asociados.");
        } else {
            modeloService.eliminarModelo(modeloId);
            model.addAttribute("success", "Modelo eliminada con éxito.");
        }

        return "redirect:/administracion/modelos";
    }

    @GetMapping("/administracion/modelos/crear")
    public String mostrarCrearModelo(Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        ModeloData modeloData = new ModeloData();
        model.addAttribute("modeloData", modeloData);

        List<Marca> marcas = marcaService.listadoCompleto();
        model.addAttribute("marcas", marcas);

        return "administracion/crear/crearModelo";
    }

    @PostMapping("/administracion/modelos/crear")
    public String crearModelo(@Valid ModeloData modeloData, BindingResult result, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        model.addAttribute("modeloData", modeloData);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        List<Marca> marcas = marcaService.listadoCompleto();
        model.addAttribute("marcas", marcas);

        if (result.hasErrors() || modeloData.getNombre().trim().isEmpty() || modeloData.getIdMarca() == null) {
            if(modeloData.getNombre().trim().isEmpty()) {
                model.addAttribute("errorCrear", "El nombre del modelo no puede estar vacío.");
            } else if(modeloData.getIdMarca() == null) {
                model.addAttribute("errorCrear", "Debe seleccionar una marca.");
            } else {
                System.out.println("Ha ocurrido un error.");
            }
            return "administracion/crear/crearModelo";
        }
        else{
            try {
                if (modeloService.findByNombre(modeloData.getNombre()) != null) {
                    model.addAttribute("errorCrear", "El modelo con nombre (" + modeloData.getNombre() + ") ya existe.");
                    return "administracion/crear/crearModelo";
                }

                modeloService.crearModelo(modeloData);

                return "redirect:/administracion/modelos";

            } catch (UsuarioServiceException e) {
                model.addAttribute("errorCrear", e.getMessage());
            }
        }

        return "administracion/crear/crearModelo";
    }


    // COLORES

    @GetMapping("/administracion/colores")
    public String mostrarListadoColores(Model model, @RequestParam(defaultValue = "0") int page) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        Pageable pageable = PageRequest.of(page, 8, Sort.by("nombre"));
        Page<Color> colores = colorService.listadoPaginado(pageable);
        model.addAttribute("colores", colores);

        if (colores.getContent().isEmpty()) {
            model.addAttribute("cantidad", null);
        }
        else {
            model.addAttribute("cantidad", colores.getContent().size());
        }

        return "administracion/listar/administracionColores";
    }

    @GetMapping("/administracion/colores/editar/{colorId}")
    public String mostrarEditarColor(@PathVariable(value = "colorId") Long colorId, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        ColorData color = colorService.findById(colorId);

        if (color != null) {
            List<Color> colores = colorService.listadoCompleto();
            Color colorBuscado = colorService.buscarColorPorId(colores, colorId);
            model.addAttribute("color", colorBuscado);

            ColorData colorData = new ColorData();
            colorData.setNombre(colorBuscado.getNombre());

            model.addAttribute("colorData", colorData);
            return "administracion/editar/editarColorAdministrador";
        }

        return "redirect:/administracion/colores";
    }

    @PostMapping("/administracion/colores/editar/{colorId}")
    public String actualizarColor(@PathVariable Long colorId, @Valid ColorData colorData, BindingResult result, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        model.addAttribute("colorData", colorData);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        List<Color> colores = colorService.listadoCompleto();
        Color colorBuscado = colorService.buscarColorPorId(colores, colorId);
        model.addAttribute("color", colorBuscado);

        if (result.hasErrors() || colorData.getNombre().trim().isEmpty()) {
            if(colorData.getNombre().trim().isEmpty()) {
                model.addAttribute("errorActualizar", "El nombre del color no puede estar vacío.");
            } else {
                System.out.println("Ha ocurrido un error.");
            }
            return "administracion/editar/editarColorAdministrador";
        }
        else{
            try {
                ColorData nuevoColorData = colorService.findById(colorId);

                if(nuevoColorData.getNombre() != null) {
                    if (colorService.findByNombre(colorData.getNombre()) != null) {
                        model.addAttribute("errorActualizar", "El color con nombre (" + colorData.getNombre() + ") ya existe.");
                        return "administracion/editar/editarColorAdministrador";
                    }

                    nuevoColorData.setNombre(colorData.getNombre());

                    colorService.actualizarColor(colorId, nuevoColorData);

                    return "redirect:/administracion/colores";
                }
                else{
                    model.addAttribute("errorActualizar", "Ha ocurrido un error al intentar actualizar.");
                }

            } catch (UsuarioServiceException e) {
                model.addAttribute("errorActualizar", e.getMessage());
            }
        }

        return "administracion/editar/editarColorAdministrador";
    }

    @PostMapping("/administracion/colores/eliminar/{colorId}")
    public String eliminarColor(@PathVariable("colorId") Long colorId, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        if (colorService.tieneVehiculosAsociados(colorId)) {
            model.addAttribute("error", "No se puede eliminar el color porque tiene vehículos asociados.");
        } else {
            colorService.eliminarColor(colorId);
            model.addAttribute("success", "Color eliminado con éxito.");
        }

        return "redirect:/administracion/colores";
    }

    @GetMapping("/administracion/colores/crear")
    public String mostrarCrearColor(Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        ColorData colorData = new ColorData();
        model.addAttribute("colorData", colorData);

        return "administracion/crear/crearColor";
    }

    @PostMapping("/administracion/colores/crear")
    public String crearColor(@Valid ColorData colorData, BindingResult result, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        model.addAttribute("colorData", colorData);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        if (result.hasErrors() || colorData.getNombre().trim().isEmpty()) {
            if(colorData.getNombre().trim().isEmpty()) {
                model.addAttribute("errorCrear", "El nombre del color no puede estar vacío.");
            } else {
                System.out.println("Ha ocurrido un error.");
            }
            return "administracion/crear/crearColor";
        }
        else{
            try {
                if (colorService.findByNombre(colorData.getNombre()) != null) {
                    model.addAttribute("errorCrear", "El color con nombre (" + colorData.getNombre() + ") ya existe.");
                    return "administracion/crear/crearColor";
                }

                colorService.crearColor(colorData);

                return "redirect:/administracion/colores";

            } catch (UsuarioServiceException e) {
                model.addAttribute("errorCrear", e.getMessage());
            }
        }

        return "administracion/crear/crearColor";
    }


    // TRANSMISIONES

    @GetMapping("/administracion/transmisiones")
    public String mostrarListadoTransmisiones(Model model, @RequestParam(defaultValue = "0") int page) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        Pageable pageable = PageRequest.of(page, 8, Sort.by("nombre"));
        Page<Transmision> transmisiones = transmisionService.listadoPaginado(pageable);
        model.addAttribute("transmisiones", transmisiones);

        if (transmisiones.getContent().isEmpty()) {
            model.addAttribute("cantidad", null);
        }
        else {
            model.addAttribute("cantidad", transmisiones.getContent().size());
        }

        return "administracion/listar/administracionTransmisiones";
    }

    @GetMapping("/administracion/transmisiones/editar/{transmisionId}")
    public String mostrarEditarTransmision(@PathVariable(value = "transmisionId") Long transmisionId, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        TransmisionData transmision = transmisionService.findById(transmisionId);

        if (transmision != null) {
            List<Transmision> transmisiones = transmisionService.listadoCompleto();
            Transmision transmisionBuscada = transmisionService.buscarTransmisionPorId(transmisiones, transmisionId);
            model.addAttribute("transmision", transmisionBuscada);

            TransmisionData transmisionData = new TransmisionData();
            transmisionData.setNombre(transmisionBuscada.getNombre());

            model.addAttribute("transmisionData", transmisionData);
            return "administracion/editar/editarTransmisionAdministrador";
        }

        return "redirect:/administracion/transmisiones";
    }

    @PostMapping("/administracion/transmisiones/editar/{transmisionId}")
    public String actualizarTransmision(@PathVariable Long transmisionId, @Valid TransmisionData transmisionData, BindingResult result, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        model.addAttribute("transmisionData", transmisionData);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        List<Transmision> transmisiones = transmisionService.listadoCompleto();
        Transmision transmisionBuscada = transmisionService.buscarTransmisionPorId(transmisiones, transmisionId);
        model.addAttribute("transmision", transmisionBuscada);

        if (result.hasErrors() || transmisionData.getNombre().trim().isEmpty()) {
            if(transmisionData.getNombre().trim().isEmpty()) {
                model.addAttribute("errorActualizar", "El nombre de la transmisión no puede estar vacío.");
            } else {
                System.out.println("Ha ocurrido un error.");
            }
            return "administracion/editar/editarTransmisionAdministrador";
        }
        else{
            try {
                TransmisionData nuevoTransmisionData = transmisionService.findById(transmisionId);

                if(nuevoTransmisionData.getNombre() != null) {
                    if (transmisionService.findByNombre(transmisionData.getNombre()) != null) {
                        model.addAttribute("errorActualizar", "La transmisión con nombre (" + transmisionData.getNombre() + ") ya existe.");
                        return "administracion/editar/editarTransmisionAdministrador";
                    }

                    nuevoTransmisionData.setNombre(transmisionData.getNombre());

                    transmisionService.actualizarTransmision(transmisionId, nuevoTransmisionData);

                    return "redirect:/administracion/transmisiones";
                }
                else{
                    model.addAttribute("errorActualizar", "Ha ocurrido un error al intentar actualizar.");
                }

            } catch (UsuarioServiceException e) {
                model.addAttribute("errorActualizar", e.getMessage());
            }
        }

        return "administracion/editar/editarTransmisionAdministrador";
    }

    @PostMapping("/administracion/transmisiones/eliminar/{transmisionId}")
    public String eliminarTransmision(@PathVariable("transmisionId") Long transmisionId, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        if (transmisionService.tieneVehiculosAsociados(transmisionId)) {
            model.addAttribute("error", "No se puede eliminar la transmisión porque tiene vehículos asociados.");
        } else {
            transmisionService.eliminarTransmision(transmisionId);
            model.addAttribute("success", "Transmisión eliminada con éxito.");
        }

        return "redirect:/administracion/transmisiones";
    }

    @GetMapping("/administracion/transmisiones/crear")
    public String mostrarCrearTransmision(Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        TransmisionData transmisionData = new TransmisionData();
        model.addAttribute("transmisionData", transmisionData);

        return "administracion/crear/crearTransmision";
    }

    @PostMapping("/administracion/transmisiones/crear")
    public String crearTransmision(@Valid TransmisionData transmisionData, BindingResult result, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        model.addAttribute("transmisionData", transmisionData);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        if (result.hasErrors() || transmisionData.getNombre().trim().isEmpty()) {
            if(transmisionData.getNombre().trim().isEmpty()) {
                model.addAttribute("errorCrear", "El nombre de la transmisión no puede estar vacío.");
            } else {
                System.out.println("Ha ocurrido un error.");
            }
            return "administracion/crear/crearTransmision";
        }
        else{
            try {
                if (transmisionService.findByNombre(transmisionData.getNombre()) != null) {
                    model.addAttribute("errorCrear", "La transmisión con nombre (" + transmisionData.getNombre() + ") ya existe.");
                    return "administracion/crear/crearTransmision";
                }

                transmisionService.crearTransmision(transmisionData);

                return "redirect:/administracion/transmisiones";

            } catch (UsuarioServiceException e) {
                model.addAttribute("errorCrear", e.getMessage());
            }
        }

        return "administracion/crear/crearTransmision";
    }


    // CATETORIAS

    @GetMapping("/administracion/categorias")
    public String mostrarListadoCategorias(Model model, @RequestParam(defaultValue = "0") int page) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        Pageable pageable = PageRequest.of(page, 8, Sort.by("nombre"));
        Page<Categoria> categorias = categoriaService.listadoPaginado(pageable);
        model.addAttribute("categorias", categorias);

        if (categorias.getContent().isEmpty()) {
            model.addAttribute("cantidad", null);
        }
        else {
            model.addAttribute("cantidad", categorias.getContent().size());
        }

        return "administracion/listar/administracionCategorias";
    }

    @GetMapping("/administracion/categorias/editar/{categoriaId}")
    public String mostrarEditarCategoria(@PathVariable(value = "categoriaId") Long categoriaId, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        CategoriaData categoria = categoriaService.findById(categoriaId);

        if (categoria != null) {
            List<Categoria> categorias = categoriaService.listadoCompleto();
            Categoria categoriaBuscada = categoriaService.buscarCategoriaPorId(categorias, categoriaId);
            model.addAttribute("categoria", categoriaBuscada);

            CategoriaData categoriaData = new CategoriaData();
            categoriaData.setNombre(categoriaBuscada.getNombre());
            categoriaData.setDescripcion(categoriaBuscada.getDescripcion());

            model.addAttribute("categoriaData", categoriaData);
            return "administracion/editar/editarCategoriaAdministrador";
        }

        return "redirect:/administracion/categorias";
    }

    @PostMapping("/administracion/categorias/editar/{categoriaId}")
    public String actualizarCategoria(@PathVariable Long categoriaId, @Valid CategoriaData categoriaData, BindingResult result, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        model.addAttribute("categoriaData", categoriaData);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        List<Categoria> categorias = categoriaService.listadoCompleto();
        Categoria categoriaBuscada = categoriaService.buscarCategoriaPorId(categorias, categoriaId);
        model.addAttribute("categoria", categoriaBuscada);

        if (result.hasErrors() || categoriaData.getNombre().trim().isEmpty()) {
            if(categoriaData.getNombre().trim().isEmpty()) {
                model.addAttribute("errorActualizar", "El nombre de la categoria no puede estar vacío.");
            } else {
                System.out.println("Ha ocurrido un error.");
            }
            return "administracion/editar/editarCategoriaAdministrador";
        }
        else{
            try {
                CategoriaData nuevaCategoriaData = categoriaService.findById(categoriaId);

                if(nuevaCategoriaData.getNombre() != null) {
                    if ((categoriaService.findByNombre(categoriaData.getNombre()) != null) && Objects.equals(categoriaData.getDescripcion(), nuevaCategoriaData.getDescripcion())) {
                        model.addAttribute("errorActualizar", "La categoria con nombre (" + categoriaData.getNombre() + ") ya existe.");
                        return "administracion/editar/editarCategoriaAdministrador";
                    }

                    nuevaCategoriaData.setNombre(categoriaData.getNombre());
                    nuevaCategoriaData.setDescripcion(categoriaData.getDescripcion());

                    categoriaService.actualizarCategoria(categoriaId, nuevaCategoriaData);

                    return "redirect:/administracion/categorias";
                }
                else{
                    model.addAttribute("errorActualizar", "Ha ocurrido un error al intentar actualizar.");
                }

            } catch (UsuarioServiceException e) {
                model.addAttribute("errorActualizar", e.getMessage());
            }
        }

        return "administracion/editar/editarCategoriaAdministrador";
    }

    @PostMapping("/administracion/categorias/eliminar/{categoriaId}")
    public String eliminarCategoria(@PathVariable("categoriaId") Long categoriaId, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        if (categoriaService.tieneVehiculosAsociados(categoriaId)) {
            model.addAttribute("error", "No se puede eliminar la categoria porque tiene vehículos asociados.");
        } else {
            categoriaService.eliminarCategoria(categoriaId);
            model.addAttribute("success", "Categoria eliminada con éxito.");
        }

        return "redirect:/administracion/categorias";
    }

    @GetMapping("/administracion/categorias/crear")
    public String mostrarCrearCategoria(Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        CategoriaData categoriaData = new CategoriaData();
        model.addAttribute("categoriaData", categoriaData);

        return "administracion/crear/crearCategoria";
    }

    @PostMapping("/administracion/categorias/crear")
    public String crearCategoria(@Valid CategoriaData categoriaData, BindingResult result, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        model.addAttribute("categoriaData", categoriaData);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        if (result.hasErrors() || categoriaData.getNombre().trim().isEmpty()) {
            if(categoriaData.getNombre().trim().isEmpty()) {
                model.addAttribute("errorCrear", "El nombre de la categoria no puede estar vacío.");
            } else {
                System.out.println("Ha ocurrido un error.");
            }
            return "administracion/crear/crearCategoria";
        }
        else{
            try {
                if (categoriaService.findByNombre(categoriaData.getNombre()) != null) {
                    model.addAttribute("errorCrear", "La categoria con nombre (" + categoriaData.getNombre() + ") ya existe.");
                    return "administracion/crear/crearCategoria";
                }

                categoriaService.crearCategoria(categoriaData);

                return "redirect:/administracion/categorias";

            } catch (UsuarioServiceException e) {
                model.addAttribute("errorCrear", e.getMessage());
            }
        }

        return "administracion/crear/crearCategoria";
    }


    // VEHICULOS

    @GetMapping("/administracion/vehiculos")
    public String mostrarListadoVehiculos(Model model, @RequestParam(defaultValue = "0") int page) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        Pageable pageable = PageRequest.of(page, 8, Sort.by("matricula"));
        Page<Vehiculo> vehiculos = vehiculoService.listadoPaginadoCompleto(pageable);
        model.addAttribute("vehiculos", vehiculos);

        if (vehiculos.getContent().isEmpty()) {
            model.addAttribute("cantidad", null);
        }
        else {
            model.addAttribute("cantidad", vehiculos.getContent().size());
        }

        return "administracion/listar/administracionVehiculos";
    }

    // Manda un JSON con los modelos de una marca concreta (la que elige el usuario en el listado de marcas)
    @GetMapping("/administracion/modelosPorMarca/{marcaId}")
    public @ResponseBody Map<Long, String> getModelosPorMarca(@PathVariable Long marcaId) {
        List<ModeloData> modelos = modeloService.findByMarcaId(marcaId);
        Map<Long, String> modeloMap = new HashMap<>();
        for (ModeloData modelo : modelos) {
            modeloMap.put(modelo.getId(), modelo.getNombre());
        }
        return modeloMap;
    }

    @GetMapping("/administracion/vehiculos/editar/{vehiculoId}")
    public String mostrarEditarVehiculo(@PathVariable(value = "vehiculoId") Long vehiculoId, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

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

            return "administracion/editar/editarVehiculoAdministrador";
        }

        return "redirect:/administracion/vehiculos";
    }

    @PostMapping("/administracion/vehiculos/editar/{vehiculoId}")
    public String actualizarVehiculo(@PathVariable Long vehiculoId, @Valid RegistroVehiculoData registroVehiculoData, BindingResult result, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

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

        List<Vehiculo> vehiculos = vehiculoService.listadoCompleto();
        Vehiculo vehiculoBuscado = vehiculoService.buscarVehiculoPorId(vehiculos, vehiculoId);
        model.addAttribute("vehiculo", vehiculoBuscado);

        if (result.hasErrors() || registroVehiculoData.getMatricula().trim().isEmpty() || registroVehiculoData.getDescripcion().trim().isEmpty() || registroVehiculoData.getKilometraje() == null || registroVehiculoData.getAnyoFabricacion() == null || registroVehiculoData.getCapacidadPasajeros() == null || registroVehiculoData.getCapacidadMaletero() == null || registroVehiculoData.getNumeroPuertas() == null || registroVehiculoData.getNumeroMarchas() == null || registroVehiculoData.getPrecioPorDia() == null || registroVehiculoData.getPrecioPorMedioDia() == null || registroVehiculoData.getPrecioCombustible() == null || registroVehiculoData.getIdMarca() == null || registroVehiculoData.getIdModelo() == null || registroVehiculoData.getIdCategoria() == null || registroVehiculoData.getIdTransmision() == null || registroVehiculoData.getIdColor() == null) {
            model.addAttribute("errorActualizar", "Unicamente puede estar vacio el campo de la oferta. Todos los demás campos son obligatorios.");
            return "administracion/editar/editarVehiculoAdministrador";
        }
        else{
            try {
                VehiculoData nuevoVehiculoData = vehiculoService.findById(vehiculoId);

                if(nuevoVehiculoData.getMatricula() != null) {
                    if ((vehiculoService.findByMatricula(registroVehiculoData.getMatricula()) != null) && !registroVehiculoData.getMatricula().equals(nuevoVehiculoData.getMatricula())) {
                        model.addAttribute("errorActualizar", "El vehículo con matrícula (" + registroVehiculoData.getMatricula() + ") ya existe.");
                        return "administracion/editar/editarVehiculoAdministrador";
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
                            return "administracion/editar/editarVehiculoAdministrador";
                        }
                    }

                    vehiculoService.actualizarVehiculo(vehiculoId, nuevoVehiculoData);

                    return "redirect:/administracion/vehiculos";
                }
                else{
                    model.addAttribute("errorActualizar", "Ha ocurrido un error al intentar actualizar.");
                }
            } catch (Exception e) {
                model.addAttribute("errorActualizar", e.getMessage());
            }
        }

        return "administracion/editar/editarVehiculoAdministrador";
    }

    @PostMapping("/administracion/vehiculos/eliminar/{vehiculoId}")
    public String eliminarVehiculo(@PathVariable("vehiculoId") Long vehiculoId, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        try {
            vehiculoService.eliminarVehiculo(vehiculoId);
            model.addAttribute("success", "Vehículo eliminado con éxito.");
        } catch (Exception e) {
            model.addAttribute("error", "No se puede eliminar el vehículo debido a: " + e.getMessage());
        }

        return "redirect:/administracion/vehiculos";
    }

    @GetMapping("/administracion/vehiculos/detalles/{vehiculoId}")
    public String mostrarDetallesVehiculo(@PathVariable(value = "vehiculoId") Long vehiculoId, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

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

            return "administracion/detalles/detallesVehiculoAdministrador";
        }

        return "redirect:/administracion/vehiculos";
    }


    // USUARIOS

    @GetMapping("/administracion/usuarios")
    public String mostrarListadoUsuarios(Model model, @RequestParam(defaultValue = "0") int page) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarioService.listadoCompleto(), id);
        model.addAttribute("usuario", usuario);

        Pageable pageable = PageRequest.of(page, 8, Sort.by("email"));
        Page<Usuario> usuarios = usuarioService.listadoPaginado(pageable);
        model.addAttribute("usuarios", usuarios);

        if (usuarios.getContent().isEmpty()) {
            model.addAttribute("cantidad", null);
        }
        else {
            model.addAttribute("cantidad", usuarios.getContent().size());
        }

        return "administracion/listar/administracionUsuarios";
    }

    @GetMapping("/administracion/usuarios/editar/{usuarioId}")
    public String mostrarEditarUsuario(@PathVariable(value = "usuarioId") Long usuarioId, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        UsuarioData usuarioBD = usuarioService.findById(usuarioId);

        if (usuarioBD != null) {
            Usuario usuarioBuscado = usuarioService.buscarUsuarioPorId(usuarios, usuarioId);
            model.addAttribute("usuarioBuscado", usuarioBuscado);

            RegistroData registroData = new RegistroData();

            registroData.setNombre(usuarioBuscado.getNombre());
            registroData.setApellidos(usuarioBuscado.getApellidos());
            registroData.setEmail(usuarioBuscado.getEmail());
            registroData.setPassword(usuarioBuscado.getPassword());
            registroData.setTelefono(usuarioBuscado.getTelefono());
            registroData.setDireccion(usuarioBuscado.getDireccion());
            registroData.setCiudad(usuarioBuscado.getCiudad());
            registroData.setCodigoPostal(usuarioBuscado.getCodigoPostal());
            registroData.setDni(usuarioBuscado.getDni());
            registroData.setFechaCaducidadDni(usuarioBuscado.getFechaCaducidadDni());
            registroData.setFechaCarnetConducir(usuarioBuscado.getFechaCarnetConducir());
            registroData.setAdministrador(usuarioBuscado.isAdministrador());
            registroData.setNumeroCuenta(usuarioBuscado.getCuenta().getNumeroCuenta());

            model.addAttribute("registroData", registroData);

            return "administracion/editar/editarUsuarioAdministrador";
        }

        return "redirect:/administracion/usuarios";
    }

    @PostMapping("/administracion/usuarios/editar/{usuarioId}")
    public String actualizarUsuario(@PathVariable Long usuarioId, @Valid RegistroData registroData, BindingResult result, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        model.addAttribute("registroData", registroData);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        Usuario usuarioBuscado = usuarioService.buscarUsuarioPorId(usuarios, usuarioId);
        model.addAttribute("usuarioBuscado", usuarioBuscado);

        if (result.hasErrors() || registroData.getNombre().trim().isEmpty() || registroData.getEmail().trim().isEmpty() || registroData.getTelefono() == null || registroData.getDireccion().trim().isEmpty() || registroData.getCiudad().trim().isEmpty() || registroData.getCodigoPostal() == null || registroData.getDni().trim().isEmpty() || registroData.getFechaCaducidadDni() == null || registroData.getFechaCarnetConducir() == null) {
            model.addAttribute("errorActualizar", "Unicamente puede estar vacio el campo de los apellidos. Todos los demás campos son obligatorios.");
            return "administracion/editar/editarUsuarioAdministrador";
        }
        else{
            try {
                UsuarioData nuevoUsuarioData = usuarioService.findById(usuarioId);

                if(nuevoUsuarioData.getEmail() != null) {
                    if ((usuarioService.findByEmail(registroData.getEmail()) != null) && !registroData.getEmail().equals(nuevoUsuarioData.getEmail())) {
                        model.addAttribute("errorActualizar", "El vehículo con email (" + registroData.getEmail() + ") ya existe.");
                        return "administracion/editar/editarUsuarioAdministrador";
                    }

                    nuevoUsuarioData.setNombre(registroData.getNombre());
                    nuevoUsuarioData.setApellidos(registroData.getApellidos());
                    nuevoUsuarioData.setEmail(registroData.getEmail());
                    nuevoUsuarioData.setTelefono(registroData.getTelefono());
                    nuevoUsuarioData.setDni(registroData.getDni());
                    nuevoUsuarioData.setFechaCaducidadDni(registroData.getFechaCaducidadDni());
                    nuevoUsuarioData.setAdministrador(registroData.isAdministrador());
                    nuevoUsuarioData.setDireccion(registroData.getDireccion());
                    nuevoUsuarioData.setCiudad(registroData.getCiudad());
                    nuevoUsuarioData.setCodigoPostal(registroData.getCodigoPostal());
                    nuevoUsuarioData.setFechaCarnetConducir(registroData.getFechaCarnetConducir());
                    nuevoUsuarioData.setPassword(registroData.getPassword());

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
                            return "administracion/editar/editarUsuarioAdministrador";
                        }
                    }

                    nuevoUsuarioData.setEsArrendador(usuarioBuscado.isEsArrendador());
                    nuevoUsuarioData.setEsArrendatario(usuarioBuscado.isEsArrendatario());
                    nuevoUsuarioData.setIdCuenta(usuarioBuscado.getCuenta().getId());

                    usuarioService.actualizarUsuarioPorId(usuarioId, nuevoUsuarioData);

                    return "redirect:/administracion/usuarios";
                }
                else{
                    model.addAttribute("errorActualizar", "Ha ocurrido un error al intentar actualizar.");
                }
            } catch (Exception e) {
                model.addAttribute("errorActualizar", e.getMessage());
            }
        }

        return "administracion/editar/editarUsuarioAdministrador";
    }

    @PostMapping("/administracion/usuarios/eliminar/{usuarioId}")
    public String eliminarUsuario(@PathVariable("usuarioId") Long usuarioId, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        try {
            usuarioService.eliminarUsuario(usuarioId);
            model.addAttribute("success", "Usuario eliminado con éxito.");
        } catch (Exception e) {
            model.addAttribute("error", "No se puede eliminar el usuario debido a: " + e.getMessage());
        }

        return "redirect:/administracion/usuarios";
    }

    @GetMapping("/administracion/usuarios/crear")
    public String mostrarCrearUsuario(Model model) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        model.addAttribute("registroData", new RegistroData());

        return "administracion/crear/crearUsuario";
    }

    @PostMapping("/administracion/usuarios/crear")
    public String crearUsuario(@Valid RegistroData registroData, BindingResult result, Model model) throws IOException {
        Long id = managerUserSession.usuarioLogeado();

        comprobarAdmin(id);

        model.addAttribute("registroData", registroData);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        if (result.hasErrors()) {
            return "administracion/crear/crearUsuario";
        }
        else{
            try {
                if (usuarioService.findByEmail(registroData.getEmail()) != null) {
                    model.addAttribute("errorCrear", "El usuario con email (" + registroData.getEmail() + ") ya existe.");
                    return "administracion/crear/crearUsuario";
                }

                UsuarioData usuarioNuevo = new UsuarioData();
                usuarioNuevo.setEmail(registroData.getEmail());
                usuarioNuevo.setPassword(registroData.getPassword());
                usuarioNuevo.setNombre(registroData.getNombre());
                usuarioNuevo.setApellidos(registroData.getApellidos());
                usuarioNuevo.setTelefono(registroData.getTelefono());
                usuarioNuevo.setDireccion(registroData.getDireccion());
                usuarioNuevo.setDni(registroData.getDni());
                usuarioNuevo.setFechaCaducidadDni(registroData.getFechaCaducidadDni());
                usuarioNuevo.setCiudad(registroData.getCiudad());
                usuarioNuevo.setCodigoPostal(registroData.getCodigoPostal());
                usuarioNuevo.setFechaCarnetConducir(registroData.getFechaCarnetConducir());

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
                        usuarioNuevo.setImagen(fileName);
                    } else {
                        // Manejo de error si el archivo no es una imagen JPEG/JPG
                        model.addAttribute("errorCrear", "Solo se permiten archivos de imagen en formato JPEG y JPG.");
                        return "administracion/crear/crearUsuario";
                    }
                } else {
                    // Manejo de error si no se subió ninguna imagen
                    model.addAttribute("errorCrear", "Ha ocurrido un error en la subida de la imagen.");
                    return "administracion/crear/crearUsuario";
                }

                usuarioNuevo.setEsArrendatario(true);

                UsuarioData nuevoUsuario = usuarioService.registrar(usuarioNuevo);

                CuentaData cuenta = new CuentaData();
                cuenta.setSaldo(BigDecimal.valueOf(100.0));
                cuenta.setIdUsuario(nuevoUsuario.getId());
                cuenta.setNumeroCuenta("ES" + registroData.getNumeroCuenta());

                CuentaData nuevaCuenta = cuentaService.crearCuenta(cuenta);

                nuevoUsuario.setIdCuenta(nuevaCuenta.getId());
                usuarioService.añadirCuenta(nuevoUsuario.getId(), nuevoUsuario);

                return "redirect:/administracion/usuarios";

            } catch (UsuarioServiceException e) {
                model.addAttribute("errorCrear", e.getMessage());
            }
        }

        return "administracion/crear/crearUsuario";
    }
}
