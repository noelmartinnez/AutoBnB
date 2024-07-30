package autobnb.controller;

import autobnb.authentication.ManagerUserSession;
import autobnb.controller.exception.UsuarioNoLogeadoException;
import autobnb.model.Alquiler;
import autobnb.model.Pago;
import autobnb.model.Usuario;
import autobnb.model.Vehiculo;
import autobnb.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Controller
public class AlquilerController {
    @Autowired
    UsuarioService usuarioService;
    @Autowired
    VehiculoService vehiculoService;
    @Autowired
    MarcaService marcaService;
    @Autowired
    ColorService colorService;
    @Autowired
    PagoService pagoService;
    @Autowired
    AlquilerService alquilerService;
    @Autowired
    TransmisionService transmisionService;
    @Autowired
    CategoriaService categoriaService;
    @Autowired
    ManagerUserSession managerUserSession;

    private static final Logger logger = LoggerFactory.getLogger(AlquilerController.class);

    private void comprobarLogueado(Long idUsuario) {
        if (idUsuario == null) {
            throw new UsuarioNoLogeadoException();
        }
    }

    @GetMapping("/api/alquileres/{vehiculoId}")
    @ResponseBody
    public List<Map<String, Object>> getAlquileres(@PathVariable(value = "vehiculoId") Long vehiculoId) {
        List<Alquiler> alquileres = vehiculoService.obtenerAlquileresPorVehiculoId(vehiculoId);
        List<Map<String, Object>> eventos = new ArrayList<>();
        for (Alquiler alquiler : alquileres) {
            Map<String, Object> evento = new HashMap<>();
            evento.put("title", "ALQUILADO");

            // Formatear fechaEntrega como String
            String fechaEntregaStr = new SimpleDateFormat("yyyy-MM-dd").format(alquiler.getFechaEntrega());
            evento.put("start", fechaEntregaStr);

            // Sumar un día a fechaDevolucion y formatear como String
            Calendar cal = Calendar.getInstance();
            cal.setTime(alquiler.getFechaDevolucion());
            cal.add(Calendar.DATE, 1);
            String fechaDevolucionStr = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
            evento.put("end", fechaDevolucionStr);

            evento.put("allDay", true);
            eventos.add(evento);
        }
        return eventos;
    }

    @GetMapping("/alquilar/{vehiculoId}")
    public String eleccionFechaInicio(@PathVariable(value = "vehiculoId") Long vehiculoId, Model model, HttpServletRequest request) {
        Long id = managerUserSession.usuarioLogeado();

        comprobarLogueado(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        List<Vehiculo> vehiculos = vehiculoService.listadoCompleto();
        model.addAttribute("vehiculo", vehiculoService.buscarVehiculoPorId(vehiculos, vehiculoId));

        String referer = request.getHeader("Referer");
        model.addAttribute("referer", referer);

        return "alquilar/eleccionFechaInicio";
    }

    @GetMapping("/alquilar/{vehiculoId}/guardarFechaInicial")
    public String guardarFechaInicial(@PathVariable(value = "vehiculoId") Long vehiculoId,
                                      @RequestParam("fechaInicial") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaInicial,
                                      Model model) {
        Long id = managerUserSession.usuarioLogeado();

        if (fechaInicial == null) {
            model.addAttribute("error", "La fecha seleccionada no es válida.");

            List<Usuario> usuarios = usuarioService.listadoCompleto();
            Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
            model.addAttribute("usuario", usuario);

            List<Vehiculo> vehiculos = vehiculoService.listadoCompleto();
            model.addAttribute("vehiculo", vehiculoService.buscarVehiculoPorId(vehiculos, vehiculoId));

            return "alquilar/eleccionFechaInicio";
        }

        comprobarLogueado(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        List<Vehiculo> vehiculos = vehiculoService.listadoCompleto();
        model.addAttribute("vehiculo", vehiculoService.buscarVehiculoPorId(vehiculos, vehiculoId));

        // Obtener la fecha actual
        Date fechaActual = new Date();
        // Remover el tiempo de la fecha actual para comparar solo las fechas sin las horas
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fechaActual);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        fechaActual = calendar.getTime();

        // Comprobar si la fechaInicial es anterior al día actual
        if (fechaInicial.before(fechaActual)) {
            model.addAttribute("error", "La fecha seleccionada no puede ser anterior a la fecha actual.");
            return "alquilar/eleccionFechaInicio";
        }

        model.addAttribute("fechaInicioSeleccionada", new SimpleDateFormat("yyyy-MM-dd").format(fechaInicial));

        return "alquilar/eleccionFechaFinal";
    }

    @GetMapping("/alquilar/{vehiculoId}/metodoDePago")
    public String metodoDePago(
            @PathVariable(value = "vehiculoId") Long vehiculoId,
            @RequestParam("fechaInicial") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaInicial,
            @RequestParam("fechaFinal") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaFinal,
            Model model) {

        Long id = managerUserSession.usuarioLogeado();

        comprobarLogueado(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        List<Vehiculo> vehiculos = vehiculoService.listadoCompleto();
        model.addAttribute("vehiculo", vehiculoService.buscarVehiculoPorId(vehiculos, vehiculoId));

        // Se comprueba si el vehículo está alquilado en las fechas seleccionadas
        List<Alquiler> alquileres = vehiculoService.obtenerAlquileresPorVehiculoId(vehiculoId);
        for (Alquiler alquiler : alquileres) {
            if ((alquiler.getFechaEntrega().before(fechaFinal) && alquiler.getFechaDevolucion().after(fechaInicial)) ||
                    alquiler.getFechaEntrega().equals(fechaInicial) ||
                    alquiler.getFechaDevolucion().equals(fechaFinal)) {
                model.addAttribute("fechaInicioSeleccionada", new SimpleDateFormat("yyyy-MM-dd").format(fechaInicial));
                model.addAttribute("error", "El vehículo ya está alquilado en las fechas seleccionadas.");
                return "alquilar/eleccionFechaFinal"; // Devuelve a la página de selección de fechas
            }
        }

        // Comprobación: si la fecha final es anterior a la fecha inicial
        if (fechaFinal.before(fechaInicial)) {
            model.addAttribute("fechaInicioSeleccionada", new SimpleDateFormat("yyyy-MM-dd").format(fechaInicial));
            model.addAttribute("error", "La fecha final no puede ser anterior a la fecha inicial.");
            return "alquilar/eleccionFechaFinal";
        }

        // Comprobación: si la fecha final y la inicial coinciden
        if (fechaFinal.equals(fechaInicial)) {
            model.addAttribute("mismoDia", true);
        } else {
            // Comprobación: si la fecha final es posterior a la inicial
            long diffInMillies = fechaFinal.getTime() - fechaInicial.getTime();
            long diasDeDiferencia = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            if (diasDeDiferencia > 0) {
                model.addAttribute("diasDeAlquiler", diasDeDiferencia + 1);
            } else {
                model.addAttribute("fechaInicioSeleccionada", new SimpleDateFormat("yyyy-MM-dd").format(fechaInicial));
                model.addAttribute("error", "Diferencia de días inválida.");
                return "alquilar/eleccionFechaFinal";
            }
        }

        model.addAttribute("fechaInicial", fechaInicial);
        model.addAttribute("fechaFinal", fechaFinal);

        return "alquilar/metodoDePago";
    }

    @GetMapping("/alquilar/{vehiculoId}/detallesAlquiler")
    public String detallesAlquiler(@PathVariable(value = "vehiculoId") Long vehiculoId,
                               @RequestParam("fechaInicial") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaInicial,
                               @RequestParam("fechaFinal") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaFinal,
                               @RequestParam("tarjeta") String numeroTarjeta,
                                   @RequestParam(value = "litrosCombustible", required = false) BigDecimal litrosCombustible,
                                   @RequestParam("opcionSeleccionada") String opcionSeleccionada,
                                   Model model) {

        Long id = managerUserSession.usuarioLogeado();

        comprobarLogueado(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        List<Vehiculo> vehiculos = vehiculoService.listadoCompleto();
        model.addAttribute("vehiculo", vehiculoService.buscarVehiculoPorId(vehiculos, vehiculoId));
        model.addAttribute("propietario", vehiculoService.obtenerUsuarioPorVehiculoId(vehiculoId));

        if (fechaFinal.equals(fechaInicial)) {
            model.addAttribute("mismoDia", true);
        } else {
            long diffInMillies = fechaFinal.getTime() - fechaInicial.getTime();
            long diasDeDiferencia = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            model.addAttribute("diasDeAlquiler", diasDeDiferencia + 1);
        }

        model.addAttribute("fechaInicial", fechaInicial);
        model.addAttribute("fechaFinal", fechaFinal);

        model.addAttribute("numeroTarjeta", numeroTarjeta);
        model.addAttribute("opcionSeleccionada", opcionSeleccionada);

        if (litrosCombustible != null) {
            model.addAttribute("litrosCombustible", litrosCombustible);
        }

        Map<Long, BigDecimal> preciosOferta = new HashMap<>();
        Map<Long, BigDecimal> preciosOfertaMedioDia = new HashMap<>();

        for (Vehiculo vehiculo : vehiculos) {
            if (vehiculo.getOferta() != null && vehiculo.getOferta() > 0) {
                BigDecimal precioOriginal = vehiculo.getPrecioPorDia();
                BigDecimal precioOriginalMedioDia = vehiculo.getPrecioPorMedioDia();
                BigDecimal porcentajeOferta = BigDecimal.valueOf(vehiculo.getOferta());
                BigDecimal descuento = porcentajeOferta.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
                BigDecimal precioOferta = precioOriginal.multiply(BigDecimal.ONE.subtract(descuento));
                BigDecimal precioOfertaMedioDia = precioOriginalMedioDia.multiply(BigDecimal.ONE.subtract(descuento));
                precioOferta = precioOferta.setScale(0, RoundingMode.HALF_UP);
                precioOfertaMedioDia = precioOfertaMedioDia.setScale(0, RoundingMode.HALF_UP);
                preciosOferta.put(vehiculo.getId(), precioOferta);
                preciosOfertaMedioDia.put(vehiculo.getId(), precioOfertaMedioDia);
            }
        }

        model.addAttribute("preciosOferta", preciosOferta);
        model.addAttribute("preciosOfertaMedioDia", preciosOfertaMedioDia);

        return "alquilar/detallesAlquiler";
    }

    @GetMapping("/alquilar/{vehiculoId}/crearAlquiler")
    public String crearAlquiler(@PathVariable(value = "vehiculoId") Long vehiculoId,
                                   @RequestParam("fechaInicial") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaInicial,
                                   @RequestParam("fechaFinal") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaFinal,
                                   @RequestParam("tarjeta") String numeroTarjeta,
                                   @RequestParam("tarjetaNombre") String nombreTarjeta,
                                   @RequestParam("precioTotal") BigDecimal precioTotal,
                                   @RequestParam(value = "litrosCombustible", required = false) BigDecimal litrosCombustible,
                                   @RequestParam("opcionSeleccionada") String opcionSeleccionada,
                                   Model model) {

        Long id = managerUserSession.usuarioLogeado();

        comprobarLogueado(id);

        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, id);
        model.addAttribute("usuario", usuario);

        List<Vehiculo> vehiculos = vehiculoService.listadoCompleto();
        model.addAttribute("vehiculo", vehiculoService.buscarVehiculoPorId(vehiculos, vehiculoId));
        model.addAttribute("propietario", vehiculoService.obtenerUsuarioPorVehiculoId(vehiculoId));

        if (fechaFinal.equals(fechaInicial)) {
            model.addAttribute("mismoDia", true);
        } else {
            long diffInMillies = fechaFinal.getTime() - fechaInicial.getTime();
            long diasDeDiferencia = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            model.addAttribute("diasDeAlquiler", diasDeDiferencia + 1);
        }

        model.addAttribute("fechaInicial", fechaInicial);
        model.addAttribute("fechaFinal", fechaFinal);

        model.addAttribute("numeroTarjeta", numeroTarjeta);
        model.addAttribute("opcionSeleccionada", opcionSeleccionada);

        if (litrosCombustible != null) {
            model.addAttribute("litrosCombustible", litrosCombustible);
        }

        Map<Long, BigDecimal> preciosOferta = new HashMap<>();

        for (Vehiculo vehiculo : vehiculos) {
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

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date fechaActual = new Date(); // Fecha actual
            String fechaActualStr = dateFormat.format(fechaActual); // Formatear fecha actual a String
            Date fechaCreacion = dateFormat.parse(fechaActualStr); // Convertir fecha actual a Date

            Pago nuevoPago = pagoService.crearPago(nombreTarjeta, numeroTarjeta, precioTotal, usuario.getId());

            if (nuevoPago == null) {
                model.addAttribute("error", "No tienes suficiente saldo en tu cuenta.");
                return "alquilar/detallesAlquiler";
            } else {
                Alquiler nuevoAlquiler = alquilerService.crearAlquiler(fechaCreacion, fechaInicial, fechaFinal, precioTotal, litrosCombustible, vehiculoId, nuevoPago.getId());
                pagoService.asociarAlquilerAPago(nuevoPago.getId(), nuevoAlquiler.getId());
            }
        } catch (Exception e) {
            model.addAttribute("error", "Error al crear alquiler.");
            return "alquilar/detallesAlquiler";
        }

        return "redirect:/perfil/" + id + "/vehiculosAlquiladosArrendatario";
    }
}
