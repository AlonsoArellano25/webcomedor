package pe.alfinbanco.webcomedor.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import pe.alfinbanco.webcomedor.entity.ColaboradorEntity;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        ColaboradorEntity col = (ColaboradorEntity) session.getAttribute("colaborador");
        model.addAttribute("colaborador", col);
        return "home";
    }
}
