package pe.alfinbanco.webcomedor.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import pe.alfinbanco.webcomedor.entity.ColaboradorEntity;
import pe.alfinbanco.webcomedor.service.ColaboradorService;

@Controller
public class LoginController {

    private final ColaboradorService colaboradorService;

    public LoginController(ColaboradorService colaboradorService) {
        this.colaboradorService = colaboradorService;
    }

    @GetMapping("/")
    public String login() {
        return "login";
    }

    @PostMapping("/actionLogin")
    public String actionLogin(
            @RequestParam("user") String user,
            @RequestParam("password") String password,
            HttpServletRequest request,
            Model model
    ) {
        ColaboradorEntity col = colaboradorService.validarColaborador(user, password);
        if (col == null) {
            model.addAttribute("msgError", "Usuario o contraseña incorrectos");
            return "login";
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("colaborador", col);
        return "redirect:/home";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }
}
