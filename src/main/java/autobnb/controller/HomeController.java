package autobnb.controller;

import autobnb.authentication.ManagerUserSession;
import autobnb.dto.BusquedaData;
import autobnb.dto.BusquedaHomeData;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Controller
public class HomeController {
    @Autowired
    UsuarioService usuarioService;
    @Autowired
    VehiculoService vehiculoService;
    @Autowired
    MarcaService marcaService;
    @Autowired
    CategoriaService categoriaService;
    @Autowired
    TransmisionService transmisionService;
    @Autowired
    ColorService colorService;
    @Autowired
    ManagerUserSession managerUserSession;

    @GetMapping("/")
    public String init(Model model) {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home(Model model) {
        Long id = managerUserSession.usuarioLogeado();

        List<Vehiculo> vehiculos = vehiculoService.listadoVehiculosConOferta();
        model.addAttribute("vehiculos", vehiculos);

        Map<Long, BigDecimal> preciosOferta = new HashMap<>();

        for (Vehiculo vehiculo : vehiculos) {
            BigDecimal precioOriginal = vehiculo.getPrecioPorDia();
            BigDecimal porcentajeOferta = BigDecimal.valueOf(vehiculo.getOferta());
            BigDecimal descuento = porcentajeOferta.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
            BigDecimal precioOferta = precioOriginal.multiply(BigDecimal.ONE.subtract(descuento));
            precioOferta = precioOferta.setScale(0, RoundingMode.HALF_UP);
            preciosOferta.put(vehiculo.getId(), precioOferta);
        }

        model.addAttribute("vehiculos", vehiculos);
        model.addAttribute("preciosOferta", preciosOferta);

        model.addAttribute("marcas", marcaService.listadoCompleto());
        model.addAttribute("categorias", categoriaService.listadoCompleto());

        if(id != null){
            UsuarioData user = usuarioService.findById(id);
            model.addAttribute("usuario", user);
        }
        else {
            model.addAttribute("usuario", null);
        }

        return "home";
    }

    @GetMapping("/about")
    public String about(Model model) {
        Long id = managerUserSession.usuarioLogeado();

        if(id != null){
            UsuarioData user = usuarioService.findById(id);
            model.addAttribute("usuario", user);
        }
        else {
            model.addAttribute("usuario", null);
        }

        return "about";
    }

    @GetMapping("/home/detalles-vehiculo/{vehiculoId}")
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

            return "detallesVehiculoHome";
        }

        return "redirect:/home";
    }

    @GetMapping("/listado-vehiculos/home/busqueda")
    public String buscarVehiculo(@ModelAttribute BusquedaHomeData busquedaHomeData, Model model, @RequestParam(defaultValue = "0") int page) {
        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        today = cal.getTime();

        if (busquedaHomeData.getIdMarca() == null || busquedaHomeData.getIdCategoria() == null || busquedaHomeData.getFechaInicial() == null || busquedaHomeData.getFechaFinal() == null || busquedaHomeData.getFechaInicial().before(today) || busquedaHomeData.getFechaFinal().before(today) || busquedaHomeData.getFechaFinal().before(busquedaHomeData.getFechaInicial())){
            if (busquedaHomeData.getFechaFinal() != null && busquedaHomeData.getFechaInicial() != null) {
                if (busquedaHomeData.getFechaInicial().before(today) || busquedaHomeData.getFechaFinal().before(today)) {
                    model.addAttribute("error", "La fecha de inicio y la fecha de fin deben ser posteriores a la fecha actual.");
                }
                else if (busquedaHomeData.getFechaFinal().before(busquedaHomeData.getFechaInicial())) {
                    model.addAttribute("error", "La fecha de fin debe ser posterior a la fecha de inicio.");
                }
            }
            else {
                model.addAttribute("error", "Debe completar todos los campos.");
            }

            Long id = managerUserSession.usuarioLogeado();

            List<Vehiculo> vehiculos = vehiculoService.listadoVehiculosConOferta();
            model.addAttribute("vehiculos", vehiculos);

            Map<Long, BigDecimal> preciosOferta = new HashMap<>();

            for (Vehiculo vehiculo : vehiculos) {
                BigDecimal precioOriginal = vehiculo.getPrecioPorDia();
                BigDecimal porcentajeOferta = BigDecimal.valueOf(vehiculo.getOferta());
                BigDecimal descuento = porcentajeOferta.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
                BigDecimal precioOferta = precioOriginal.multiply(BigDecimal.ONE.subtract(descuento));
                precioOferta = precioOferta.setScale(0, RoundingMode.HALF_UP);
                preciosOferta.put(vehiculo.getId(), precioOferta);
            }

            model.addAttribute("vehiculos", vehiculos);
            model.addAttribute("preciosOferta", preciosOferta);

            model.addAttribute("marcas", marcaService.listadoCompleto());
            model.addAttribute("categorias", categoriaService.listadoCompleto());

            if(id != null){
                UsuarioData user = usuarioService.findById(id);
                model.addAttribute("usuario", user);
            }
            else {
                model.addAttribute("usuario", null);
            }

            return "home";
        }

        Long id = managerUserSession.usuarioLogeado();

        if (id != null) {
            List<Usuario> usuarios = usuarioService.listadoCompleto();
            Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
            model.addAttribute("usuario", usuario);
        } else {
            model.addAttribute("usuario", null);
        }

        Pageable pageable = PageRequest.of(page, 6, Sort.by("id").ascending());

        Page<Vehiculo> vehiculosPage = vehiculoService.buscarVehiculosDisponibles(
                busquedaHomeData.getIdMarca(),
                busquedaHomeData.getIdCategoria(),
                busquedaHomeData.getFechaInicial(),
                busquedaHomeData.getFechaFinal(),
                pageable);

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
        model.addAttribute("colores", colorService.listadoCompleto());
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
}
