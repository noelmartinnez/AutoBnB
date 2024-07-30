package autobnb.service;

import autobnb.dto.UsuarioData;
import autobnb.model.Usuario;
import autobnb.repository.UsuarioRepository;
import autobnb.service.exception.UsuarioServiceException;
import de.mkammerer.argon2.Argon2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
public class UsuarioServiceTest {

    @Autowired
    private UsuarioService usuarioService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private Argon2 argon2;

    @Autowired
    private ModelMapper modelMapper;

    private Usuario usuario;
    private UsuarioData usuarioData;

    @BeforeEach
    public void setUp() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        usuario = new Usuario("Juan Pérez", "juan.perez@gmail.com", "12345678", 123456789, "Calle Falsa 123",
                "Ciudad Ficticia", 12345, "12345678A", sdf.parse("2025-12-31"), sdf.parse("2025-12-31"), false, false, false, null, null);
        usuario.setId(1L);

        usuarioData = modelMapper.map(usuario, UsuarioData.class);

        Mockito.when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        Mockito.when(usuarioRepository.findByEmail("juan.perez@gmail.com")).thenReturn(Optional.of(usuario));
    }

    @Test
    public void testLoginUsuarioCorrecto() {
        Mockito.when(argon2.verify(any(String.class), any(char[].class))).thenReturn(true);

        UsuarioService.LoginStatus status = usuarioService.login("juan.perez@gmail.com", "12345678");
        assertThat(status).isEqualTo(UsuarioService.LoginStatus.LOGIN_OK);
    }

    @Test
    public void testLoginUsuarioNoEncontrado() {
        UsuarioService.LoginStatus status = usuarioService.login("notfound@gmail.com", "12345678");
        assertThat(status).isEqualTo(UsuarioService.LoginStatus.USER_NOT_FOUND);
    }

    @Test
    public void testLoginUsuarioErrorPassword() {
        Mockito.when(argon2.verify(any(String.class), any(char[].class))).thenReturn(false);

        UsuarioService.LoginStatus status = usuarioService.login("juan.perez@gmail.com", "wrongpassword");
        assertThat(status).isEqualTo(UsuarioService.LoginStatus.ERROR_PASSWORD);
    }

    @Test
    public void testEliminarUsuario() {
        usuarioService.eliminarUsuario(1L);
        Mockito.verify(usuarioRepository, Mockito.times(1)).delete(usuario);
    }

    @Test
    public void testFindById() {
        UsuarioData result = usuarioService.findById(1L);
        assertThat(result).isNotNull();
    }

    @Test
    public void testFindByEmail() {
        UsuarioData result = usuarioService.findByEmail("juan.perez@gmail.com");
        assertThat(result).isNotNull();
    }

    @Test
    public void testRegistrarUsuarioExistente() {
        Mockito.when(usuarioRepository.findByEmail(any(String.class))).thenReturn(Optional.of(usuario));
        assertThatThrownBy(() -> usuarioService.registrar(usuarioData))
                .isInstanceOf(UsuarioServiceException.class)
                .hasMessageContaining("ya está registrado");
    }
}
