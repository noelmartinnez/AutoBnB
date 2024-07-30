package autobnb.controller;

import autobnb.authentication.ManagerUserSession;
import autobnb.model.Usuario;
import autobnb.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class CustomErrorController implements ErrorController {
    @Autowired
    private ManagerUserSession managerUserSession;
    @Autowired
    UsuarioService usuarioService;
    @RequestMapping("/error")
    public String handleError(Model model) {
        List<Usuario> usuarios = usuarioService.listadoCompleto();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarios, managerUserSession.usuarioLogeado());
        model.addAttribute("usuario", usuario);

        return "error";
    }
}

