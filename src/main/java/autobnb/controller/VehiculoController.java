package autobnb.controller;

import autobnb.authentication.ManagerUserSession;
import autobnb.controller.exception.UsuarioNoLogeadoException;
import autobnb.dto.BusquedaData;
import autobnb.dto.UsuarioData;
import autobnb.dto.VehiculoData;
import autobnb.model.Usuario;
import autobnb.model.Vehiculo;
import autobnb.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class VehiculoController {
    @Autowired
    UsuarioService usuarioService;
    @Autowired
    VehiculoService vehiculoService;
    @Autowired
    MarcaService marcaService;
    @Autowired
    ColorService colorService;
    @Autowired
    TransmisionService transmisionService;
    @Autowired
    CategoriaService categoriaService;
    @Autowired
    ManagerUserSession managerUserSession;

    // Método para capitalizar la primera letra de cada palabra en una cadena de texto.
    public static String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return Arrays.stream(input.split("\\s+"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }

    @PostMapping("/comentarios/agregar/{vehiculoId}")
    public String agregarComentarioListado(@PathVariable Long vehiculoId, @RequestParam String descripcion) {
        Long id = managerUserSession.usuarioLogeado();

        if(id != null) {
            vehiculoService.agregarComentarioAVehiculo(vehiculoId, id, descripcion);
        }
        else {
            throw new UsuarioNoLogeadoException();
        }

        return "redirect:/listado-vehiculos/detalles-vehiculo/" + vehiculoId;
    }

    @PostMapping("/comentarios/ofertas/agregar/{vehiculoId}")
    public String agregarComentarioOfertas(@PathVariable Long vehiculoId, @RequestParam String descripcion) {
        Long id = managerUserSession.usuarioLogeado();

        if(id != null) {
            vehiculoService.agregarComentarioAVehiculo(vehiculoId, id, descripcion);
        }
        else {
            throw new UsuarioNoLogeadoException();
        }

        return "redirect:/listado-vehiculos/detalles-vehiculo/oferta/" + vehiculoId;
    }

    @PostMapping("/comentarios/home/agregar/{vehiculoId}")
    public String agregarComentarioHome(@PathVariable Long vehiculoId, @RequestParam String descripcion) {
        Long id = managerUserSession.usuarioLogeado();

        if(id != null) {
            vehiculoService.agregarComentarioAVehiculo(vehiculoId, id, descripcion);
        }
        else {
            throw new UsuarioNoLogeadoException();
        }

        return "redirect:/home/detalles-vehiculo/" + vehiculoId;
    }

    @GetMapping("/listado-vehiculos")
    public String mostrarListadoVehiculos(Model model, @RequestParam(defaultValue = "0") int page) {
        model.addAttribute("marcas", marcaService.listadoCompleto());
        model.addAttribute("categorias", categoriaService.listadoCompleto());
        model.addAttribute("ciudades", vehiculoService.obtenerCiudadesUnicas());
        model.addAttribute("marcas", marcaService.listadoCompleto());
        model.addAttribute("transmisiones", transmisionService.listadoCompleto());

        Map<Long, BigDecimal> preciosOferta = new HashMap<>();

        for (Vehiculo vehiculo : vehiculoService.listadoCompleto()) {
            if (vehiculo.getOferta() != null && vehiculo.getOferta() > 0) {
                BigDecimal precioOriginal = vehiculo.getPrecioPorDia();
                BigDecimal porcentajeOferta = BigDecimal.valueOf(vehiculo.getOferta());
                BigDecimal descuento = porcentajeOferta.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
                BigDecimal precioOferta = precioOriginal.multiply(BigDecimal.ONE.subtract(descuento));
                precioOferta = precioOferta.setScale(0, RoundingMode.HALF_UP);
                preciosOferta.put(vehiculo.getId(), precioOferta);
            }
        }

        model.addAttribute("preciosOferta", preciosOferta);

        Long id = managerUserSession.usuarioLogeado();

        if(id != null){
            UsuarioData user = usuarioService.findById(id);
            model.addAttribute("usuario", user);
        } else {
            model.addAttribute("usuario", null);
        }

        Pageable pageable = PageRequest.of(page, 6, Sort.by("id").ascending());
        Page<Vehiculo> vehiculosPage = vehiculoService.listadoPaginado(pageable);
        model.addAttribute("vehiculosPage", vehiculosPage);

        if (vehiculosPage.getContent().isEmpty()) {
            model.addAttribute("cantidad", null);
        }
        else {
            model.addAttribute("cantidad", vehiculosPage.getContent().size());
        }

        return "listadoVehiculos";
    }

    @GetMapping("/listado-vehiculos/detalles-vehiculo/{vehiculoId}")
    public String mostrarDetallesVehiculo(@PathVariable(value = "vehiculoId") Long vehiculoId, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        if(id != null){
            List<Usuario> usuarios = usuarioService.listadoCompleto();
            Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
            model.addAttribute("usuario", usuario);

            List<Vehiculo> vehiculosUsuario = usuarioService.obtenerVehiculosPorUsuarioId(id);
            for (Vehiculo vehiculo : vehiculosUsuario) {
                if (vehiculo.getId().equals(vehiculoId)) {
                    model.addAttribute("propio", true);
                }
            }
        }
        else {
            model.addAttribute("usuario", null);
        }

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

            return "detallesVehiculo";
        }

        return "redirect:/listado-vehiculos";
    }

    @GetMapping("/listado-vehiculos/ofertas")
    public String mostrarListadoVehiculosOfertas(Model model, @RequestParam(defaultValue = "0") int page) {
        model.addAttribute("marcas", marcaService.listadoCompleto());
        model.addAttribute("categorias", categoriaService.listadoCompleto());
        model.addAttribute("ciudades", vehiculoService.obtenerCiudadesUnicas());
        model.addAttribute("marcas", marcaService.listadoCompleto());
        model.addAttribute("transmisiones", transmisionService.listadoCompleto());

        Map<Long, BigDecimal> preciosOferta = new HashMap<>();

        for (Vehiculo vehiculo : vehiculoService.listadoVehiculosConOfertaCompleto()) {
            if (vehiculo.getOferta() != null && vehiculo.getOferta() > 0) {
                BigDecimal precioOriginal = vehiculo.getPrecioPorDia();
                BigDecimal porcentajeOferta = BigDecimal.valueOf(vehiculo.getOferta());
                BigDecimal descuento = porcentajeOferta.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
                BigDecimal precioOferta = precioOriginal.multiply(BigDecimal.ONE.subtract(descuento));
                precioOferta = precioOferta.setScale(0, RoundingMode.HALF_UP);
                preciosOferta.put(vehiculo.getId(), precioOferta);
            }
        }

        model.addAttribute("preciosOferta", preciosOferta);

        Long id = managerUserSession.usuarioLogeado();

        if(id != null){
            UsuarioData user = usuarioService.findById(id);
            model.addAttribute("usuario", user);
        } else {
            model.addAttribute("usuario", null);
        }

        Pageable pageable = PageRequest.of(page, 6, Sort.by("id").ascending());
        Page<Vehiculo> vehiculosPage = vehiculoService.listadoPaginadoVehiculosConOfertaCompleto(pageable);
        model.addAttribute("vehiculosPage", vehiculosPage);

        if (vehiculosPage.getContent().isEmpty()) {
            model.addAttribute("cantidad", null);
        }
        else {
            model.addAttribute("cantidad", vehiculosPage.getContent().size());
        }

        return "listadoVehiculosOferta";
    }

    @GetMapping("/listado-vehiculos/detalles-vehiculo/oferta/{vehiculoId}")
    public String mostrarDetallesVehiculoOferta(@PathVariable(value = "vehiculoId") Long vehiculoId, Model model) {
        Long id = managerUserSession.usuarioLogeado();

        if(id != null){
            List<Usuario> usuarios = usuarioService.listadoCompleto();
            Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
            model.addAttribute("usuario", usuario);

            List<Vehiculo> vehiculosUsuario = usuarioService.obtenerVehiculosPorUsuarioId(id);
            for (Vehiculo vehiculo : vehiculosUsuario) {
                if (vehiculo.getId().equals(vehiculoId)) {
                    model.addAttribute("propio", true);
                }
            }
        }
        else {
            model.addAttribute("usuario", null);
        }

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

            return "detallesVehiculoOferta";
        }

        return "redirect:/listado-vehiculos/ofertas";
    }

    @GetMapping("/listado-vehiculos/busqueda")
    public String buscarVehiculo(@ModelAttribute BusquedaData busquedaData, Model model, @RequestParam(defaultValue = "0") int page) {
        if (busquedaData.getBusqueda().isEmpty()) {
            return "redirect:/listado-vehiculos";
        }

        Long id = managerUserSession.usuarioLogeado();

        if (id != null) {
            List<Usuario> usuarios = usuarioService.listadoCompleto();
            Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
            model.addAttribute("usuario", usuario);
        } else {
            model.addAttribute("usuario", null);
        }

        String busqueda = busquedaData.getBusqueda();
        String[] partes = busqueda.trim().split("\\s+"); // Usa "\\s+" para dividir por uno o más espacios.

        Pageable pageable = PageRequest.of(page, 6, Sort.by("id").ascending());
        Page<Vehiculo> vehiculosPage;

        if (partes.length == 1) {
            if (Objects.equals(partes[0], "BMW")) {
                vehiculosPage = vehiculoService.listadoPaginadoPorMarca(partes[0], pageable);
            }
            else {
                String marca = capitalizeFirstLetter(partes[0]);
                vehiculosPage = vehiculoService.listadoPaginadoPorMarca(marca, pageable);
            }
        } else {
            if (Objects.equals(partes[0], "BMW")) {
                if (Objects.equals(partes[1], "A4") || Objects.equals(partes[1], "RAV4") ||
                        Objects.equals(partes[1], "F-150") || Objects.equals(partes[1], "CR-V")
                        || Objects.equals(partes[1], "X3") || Objects.equals(partes[1], "X5")
                        || Objects.equals(partes[1], "M4") || Objects.equals(partes[1], "Q5")
                        || Objects.equals(partes[1], "Q7") || Objects.equals(partes[1], "A6")
                        || Objects.equals(partes[1], "TT") || Objects.equals(partes[1], "R8")) {
                    vehiculosPage = vehiculoService.listadoPaginadoPorMarcaYModelo(partes[0], partes[1], pageable);
                }
                else {
                    String modelo = capitalizeFirstLetter(partes[1]);
                    vehiculosPage = vehiculoService.listadoPaginadoPorMarcaYModelo(partes[0], modelo, pageable);
                }
            }
            else {
                if (Objects.equals(partes[1], "A4") || Objects.equals(partes[1], "RAV4") ||
                        Objects.equals(partes[1], "F-150") || Objects.equals(partes[1], "CR-V")
                        || Objects.equals(partes[1], "X3") || Objects.equals(partes[1], "X5")
                        || Objects.equals(partes[1], "M4") || Objects.equals(partes[1], "Q5")
                        || Objects.equals(partes[1], "Q7") || Objects.equals(partes[1], "A6")
                        || Objects.equals(partes[1], "TT") || Objects.equals(partes[1], "R8")) {
                    String marca = capitalizeFirstLetter(partes[0]);
                    vehiculosPage = vehiculoService.listadoPaginadoPorMarcaYModelo(marca, partes[1], pageable);
                }
                else {
                    String marca = capitalizeFirstLetter(partes[0]);
                    String modelo = capitalizeFirstLetter(partes[1]);
                    vehiculosPage = vehiculoService.listadoPaginadoPorMarcaYModelo(marca, modelo, pageable);
                }
            }
        }

        Map<Long, BigDecimal> preciosOferta = new HashMap<>();

        for (Vehiculo vehiculo : vehiculoService.listadoCompleto()) {
            if (vehiculo.getOferta() != null && vehiculo.getOferta() > 0) {
                BigDecimal precioOriginal = vehiculo.getPrecioPorDia();
                BigDecimal porcentajeOferta = BigDecimal.valueOf(vehiculo.getOferta());
                BigDecimal descuento = porcentajeOferta.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
                BigDecimal precioOferta = precioOriginal.multiply(BigDecimal.ONE.subtract(descuento));
                precioOferta = precioOferta.setScale(0, RoundingMode.HALF_UP);
                preciosOferta.put(vehiculo.getId(), precioOferta);
            }
        }

        model.addAttribute("preciosOferta", preciosOferta);

        model.addAttribute("marcas", marcaService.listadoCompleto());
        model.addAttribute("categorias", categoriaService.listadoCompleto());
        model.addAttribute("ciudades", vehiculoService.obtenerCiudadesUnicas());
        model.addAttribute("marcas", marcaService.listadoCompleto());
        model.addAttribute("transmisiones", transmisionService.listadoCompleto());

        model.addAttribute("vehiculosPage", vehiculosPage);

        if (vehiculosPage.getContent().isEmpty()) {
            model.addAttribute("cantidad", null);
        }
        else {
            model.addAttribute("cantidad", vehiculosPage.getContent().size());
        }

        return "listadoVehiculos";
    }

    @GetMapping("/listado-vehiculos/ofertas/busqueda")
    public String buscarVehiculoOfertas(@ModelAttribute BusquedaData busquedaData, Model model, @RequestParam(defaultValue = "0") int page) {
        Long id = managerUserSession.usuarioLogeado();

        if (id != null) {
            List<Usuario> usuarios = usuarioService.listadoCompleto();
            Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
            model.addAttribute("usuario", usuario);
        } else {
            model.addAttribute("usuario", null);
        }

        String busqueda = busquedaData.getBusqueda();
        String[] partes = busqueda.trim().split("\\s+"); // Usa "\\s+" para dividir por uno o más espacios.

        Pageable pageable = PageRequest.of(page, 6, Sort.by("id").ascending());
        Page<Vehiculo> vehiculosPage;

        if (partes.length == 1) {
            if (Objects.equals(partes[0], "BMW")) {
                vehiculosPage = vehiculoService.buscarVehiculosPorMarcaConOferta(partes[0], pageable);
            }
            else {
                String marca = capitalizeFirstLetter(partes[0]);
                vehiculosPage = vehiculoService.buscarVehiculosPorMarcaConOferta(marca, pageable);
            }
        } else {
            if (Objects.equals(partes[0], "BMW")) {
                if (Objects.equals(partes[1], "A4") || Objects.equals(partes[1], "RAV4") ||
                        Objects.equals(partes[1], "F-150") || Objects.equals(partes[1], "CR-V")
                        || Objects.equals(partes[1], "X3") || Objects.equals(partes[1], "X5")
                        || Objects.equals(partes[1], "M4") || Objects.equals(partes[1], "Q5")
                        || Objects.equals(partes[1], "Q7") || Objects.equals(partes[1], "A6")
                        || Objects.equals(partes[1], "TT") || Objects.equals(partes[1], "R8")) {
                    vehiculosPage = vehiculoService.buscarVehiculosPorMarcaYModeloConOferta(partes[0], partes[1], pageable);
                }
                else {
                    String modelo = capitalizeFirstLetter(partes[1]);
                    vehiculosPage = vehiculoService.buscarVehiculosPorMarcaYModeloConOferta(partes[0], modelo, pageable);
                }
            }
            else {
                if (Objects.equals(partes[1], "A4") || Objects.equals(partes[1], "RAV4") ||
                        Objects.equals(partes[1], "F-150") || Objects.equals(partes[1], "CR-V")
                        || Objects.equals(partes[1], "X3") || Objects.equals(partes[1], "X5")
                        || Objects.equals(partes[1], "M4") || Objects.equals(partes[1], "Q5")
                        || Objects.equals(partes[1], "Q7") || Objects.equals(partes[1], "A6")
                        || Objects.equals(partes[1], "TT") || Objects.equals(partes[1], "R8")) {
                    String marca = capitalizeFirstLetter(partes[0]);
                    vehiculosPage = vehiculoService.buscarVehiculosPorMarcaYModeloConOferta(marca, partes[1], pageable);
                }
                else {
                    String marca = capitalizeFirstLetter(partes[0]);
                    String modelo = capitalizeFirstLetter(partes[1]);
                    vehiculosPage = vehiculoService.buscarVehiculosPorMarcaYModeloConOferta(marca, modelo, pageable);
                }
            }
        }

        Map<Long, BigDecimal> preciosOferta = new HashMap<>();

        for (Vehiculo vehiculo : vehiculosPage.getContent()) {
            if (vehiculo.getOferta() != null && vehiculo.getOferta() > 0) {
                BigDecimal precioOriginal = vehiculo.getPrecioPorDia();
                BigDecimal porcentajeOferta = BigDecimal.valueOf(vehiculo.getOferta());
                BigDecimal descuento = porcentajeOferta.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
                BigDecimal precioOferta = precioOriginal.multiply(BigDecimal.ONE.subtract(descuento));
                precioOferta = precioOferta.setScale(0, RoundingMode.HALF_UP);
                preciosOferta.put(vehiculo.getId(), precioOferta);
            }
        }

        model.addAttribute("preciosOferta", preciosOferta);

        model.addAttribute("marcas", marcaService.listadoCompleto());
        model.addAttribute("categorias", categoriaService.listadoCompleto());
        model.addAttribute("ciudades", vehiculoService.obtenerCiudadesUnicas());
        model.addAttribute("marcas", marcaService.listadoCompleto());
        model.addAttribute("transmisiones", transmisionService.listadoCompleto());

        model.addAttribute("vehiculosPage", vehiculosPage);

        if (vehiculosPage.getContent().isEmpty()) {
            model.addAttribute("cantidad", null);
        }
        else {
            model.addAttribute("cantidad", vehiculosPage.getContent().size());
        }

        return "listadoVehiculosOferta";
    }

    @GetMapping("/listado-vehiculos/filtrar-categoria")
    public String filtrarVehiculos(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam Optional<String> categoria,
                                   @RequestParam Optional<String> ciudad,
                                   @RequestParam Optional<String> marca,
                                   @RequestParam Optional<Integer> precioMin,
                                   @RequestParam Optional<Integer> precioMax,
                                   Model model) {

        Pageable pageable = PageRequest.of(page, 6, Sort.by("id").ascending());

        if ((!categoria.isPresent() || categoria.get().equals("Categoria")) &&
                (!ciudad.isPresent() || ciudad.get().equals("Ciudad")) &&
                (!marca.isPresent() || marca.get().equals("Marca")) &&
                (!precioMin.isPresent() || precioMin.get() == 0) &&
                (!precioMax.isPresent() || precioMax.get() == 0)) {
            return "redirect:/listado-vehiculos";
        }

        Page<Vehiculo> vehiculosPage = vehiculoService.filtrarVehiculosConPaginacion(
                categoria.orElse(null),
                ciudad.orElse(null),
                marca.orElse(null),
                precioMin.orElse(null),
                precioMax.orElse(null),
                pageable);

        model.addAttribute("vehiculosPage", vehiculosPage);
        model.addAttribute("marcas", marcaService.listadoCompleto());
        model.addAttribute("categorias", categoriaService.listadoCompleto());
        model.addAttribute("ciudades", vehiculoService.obtenerCiudadesUnicas());
        model.addAttribute("transmisiones", transmisionService.listadoCompleto());

        Map<Long, BigDecimal> preciosOferta = new HashMap<>();

        for (Vehiculo vehiculo : vehiculosPage.getContent()) {
            if (vehiculo.getOferta() != null && vehiculo.getOferta() > 0) {
                BigDecimal precioOriginal = vehiculo.getPrecioPorDia();
                BigDecimal porcentajeOferta = BigDecimal.valueOf(vehiculo.getOferta());
                BigDecimal descuento = porcentajeOferta.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
                BigDecimal precioOferta = precioOriginal.multiply(BigDecimal.ONE.subtract(descuento));
                precioOferta = precioOferta.setScale(0, RoundingMode.HALF_UP);
                preciosOferta.put(vehiculo.getId(), precioOferta);
            }
        }

        model.addAttribute("preciosOferta", preciosOferta);

        Long id = managerUserSession.usuarioLogeado();
        if (id != null) {
            UsuarioData user = usuarioService.findById(id);
            model.addAttribute("usuario", user);
        } else {
            model.addAttribute("usuario", null);
        }

        if (vehiculosPage.getContent().isEmpty()) {
            model.addAttribute("cantidad", null);
        }
        else {
            model.addAttribute("cantidad", vehiculosPage.getContent().size());
        }

        return "listadoVehiculos";
    }

    @GetMapping("/listado-vehiculos/ofertas/filtrar-categoria")
    public String filtrarVehiculosOferta(@RequestParam(defaultValue = "0") int page, @RequestParam Optional<String> categoria,
                                   @RequestParam Optional<String> ciudad,
                                   @RequestParam Optional<String> marca, @RequestParam Optional<Integer> precioMin, @RequestParam Optional<Integer> precioMax,
                                   Model model) {
        Pageable pageable = PageRequest.of(page, 6, Sort.by("id").ascending());

        if ((!categoria.isPresent() || categoria.get().equals("Categoria")) || (!ciudad.isPresent() || ciudad.get().equals("Color")) ||
                (!marca.isPresent() || marca.get().equals("Marca")) || (!precioMin.isPresent() || precioMin.get() == 0) ||
                (!precioMax.isPresent() || precioMax.get() == 0)){
            return "redirect:/listado-vehiculos/ofertas";
        }

        Page<Vehiculo> vehiculosPage = vehiculoService.filtrarVehiculosEnOfertaPaginados(
                categoria.orElse(null),
                ciudad.orElse(null),
                marca.orElse(null),
                precioMin.orElse(null),
                precioMax.orElse(null),
                pageable);

        model.addAttribute("vehiculosPage", vehiculosPage);
        model.addAttribute("marcas", marcaService.listadoCompleto());
        model.addAttribute("categorias", categoriaService.listadoCompleto());
        model.addAttribute("ciudades", vehiculoService.obtenerCiudadesUnicas());
        model.addAttribute("marcas", marcaService.listadoCompleto());
        model.addAttribute("transmisiones", transmisionService.listadoCompleto());

        Map<Long, BigDecimal> preciosOferta = new HashMap<>();

        for (Vehiculo vehiculo : vehiculosPage.getContent()) {
            if (vehiculo.getOferta() != null && vehiculo.getOferta() > 0) {
                BigDecimal precioOriginal = vehiculo.getPrecioPorDia();
                BigDecimal porcentajeOferta = BigDecimal.valueOf(vehiculo.getOferta());
                BigDecimal descuento = porcentajeOferta.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
                BigDecimal precioOferta = precioOriginal.multiply(BigDecimal.ONE.subtract(descuento));
                precioOferta = precioOferta.setScale(0, RoundingMode.HALF_UP);
                preciosOferta.put(vehiculo.getId(), precioOferta);
            }
        }

        model.addAttribute("preciosOferta", preciosOferta);

        Long id = managerUserSession.usuarioLogeado();
        if (id != null) {
            UsuarioData user = usuarioService.findById(id);
            model.addAttribute("usuario", user);
        } else {
            model.addAttribute("usuario", null);
        }

        if (vehiculosPage.getContent().isEmpty()) {
            model.addAttribute("cantidad", null);
        }
        else {
            model.addAttribute("cantidad", vehiculosPage.getContent().size());
        }

        return "listadoVehiculosOferta";
    }
}
