package autobnb.controller;

import autobnb.authentication.ManagerUserSession;
import autobnb.dto.CuentaData;
import autobnb.dto.LoginData;
import autobnb.dto.RegistroData;
import autobnb.dto.UsuarioData;
import autobnb.service.CuentaService;
import autobnb.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Controller
public class AutenticacionController {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    CuentaService cuentaService;

    @Autowired
    ManagerUserSession managerUserSession;

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("loginData", new LoginData());
        return "formLogin";
    }

    @PostMapping("/login")
    public String loginSubmit(@ModelAttribute LoginData loginData, Model model) {
        UsuarioService.LoginStatus loginStatus = usuarioService.login(loginData.getEmail(), loginData.getPassword());

        if (loginStatus == UsuarioService.LoginStatus.LOGIN_OK) {
            UsuarioData usuario = usuarioService.findByEmail(loginData.getEmail());

            managerUserSession.logearUsuario(usuario.getId());

            return "redirect:/home";
        } else if (loginStatus == UsuarioService.LoginStatus.USER_NOT_FOUND) {
            model.addAttribute("error", "No existe el usuario introducido.");
            return "formLogin";
        } else if (loginStatus == UsuarioService.LoginStatus.ERROR_PASSWORD) {
            model.addAttribute("error", "Contraseña incorrecta.");
            return "formLogin";
        }

        return "formLogin";
    }

    @GetMapping("/registro")
    public String registroForm(Model model) {
        model.addAttribute("registroData", new RegistroData());
        return "formRegistro";
    }

   @PostMapping("/registro")
   public String registroSubmit(@Valid RegistroData registroData, BindingResult result, Model model) throws IOException {

        if (result.hasErrors()) {
            return "formRegistro";
        }

        if (usuarioService.findByEmail(registroData.getEmail()) != null) {
            model.addAttribute("registroData", registroData);
            model.addAttribute("error", "El usuario con email (" + registroData.getEmail() + ") ya existe.");
            return "formRegistro";
        }

       if (usuarioService.findByDni(registroData.getDni()) != null) {
           model.addAttribute("registroData", registroData);
           model.addAttribute("error", "El usuario con DNI (" + registroData.getDni() + ") ya existe.");
           return "formRegistro";
       }

        UsuarioData usuario = new UsuarioData();
        usuario.setEmail(registroData.getEmail());
        usuario.setPassword(registroData.getPassword());
        usuario.setNombre(registroData.getNombre());
        usuario.setApellidos(registroData.getApellidos());
        usuario.setTelefono(registroData.getTelefono());
        usuario.setDireccion(registroData.getDireccion());
        usuario.setDni(registroData.getDni());
        usuario.setFechaCaducidadDni(registroData.getFechaCaducidadDni());
        usuario.setCiudad(registroData.getCiudad());
        usuario.setCodigoPostal(registroData.getCodigoPostal());
        usuario.setFechaCarnetConducir(registroData.getFechaCarnetConducir());

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
               usuario.setImagen(fileName);
           } else {
               // Manejo de error si el archivo no es una imagen JPEG/JPG
               model.addAttribute("registroData", registroData);
               model.addAttribute("error", "Solo se permiten archivos de imagen en formato JPEG y JPG.");
               return "formRegistro";
           }
       } else {
           // Manejo de error si no se subió ninguna imagen
           model.addAttribute("registroData", registroData);
           model.addAttribute("error", "Ha ocurrido un error en la subida de la imagen.");
           return "formRegistro";
       }

        usuario.setEsArrendatario(true);

        UsuarioData nuevoUsuario = usuarioService.registrar(usuario);

        CuentaData cuenta = new CuentaData();
        cuenta.setSaldo(BigDecimal.valueOf(100.0));
        cuenta.setIdUsuario(nuevoUsuario.getId());
        cuenta.setNumeroCuenta("ES" + registroData.getNumeroCuenta());

        CuentaData nuevaCuenta = cuentaService.crearCuenta(cuenta);

        nuevoUsuario.setIdCuenta(nuevaCuenta.getId());
        usuarioService.añadirCuenta(nuevoUsuario.getId(), nuevoUsuario);

        return "redirect:/login";
   }

   @GetMapping("/logout")
   public String logout() {
        managerUserSession.logout();
        return "redirect:/login";
   }
}
