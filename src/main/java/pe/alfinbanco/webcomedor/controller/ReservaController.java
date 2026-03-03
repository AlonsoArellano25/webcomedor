package pe.alfinbanco.webcomedor.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pe.alfinbanco.webcomedor.entity.ColaboradorEntity;
import pe.alfinbanco.webcomedor.entity.ReservaEntity;
import pe.alfinbanco.webcomedor.service.ReservaException;
import pe.alfinbanco.webcomedor.service.ReservaService;

@Controller
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @GetMapping("/misReservas")
    public String misReservas(
            @RequestParam(name = "pag", defaultValue = "1") int pag,
            HttpSession session,
            Model model
    ) {
        ColaboradorEntity col = (ColaboradorEntity) session.getAttribute("colaborador");

        Page<ReservaEntity> page = reservaService.listarMisReservas(col.getIdColaborador(), pag, 5);

        model.addAttribute("colaborador", col);
        model.addAttribute("reservas", page.getContent());
        model.addAttribute("pagActual", pag);
        model.addAttribute("cantidadPaginas", page.getTotalPages());
        model.addAttribute("horas", buildHoras());

        return "misReservas";
    }

    @GetMapping("/reservar")
    public String reservar(HttpSession session, Model model) {
        ColaboradorEntity col = (ColaboradorEntity) session.getAttribute("colaborador");
        model.addAttribute("colaborador", col);
        model.addAttribute("horas", buildHoras());
        model.addAttribute("hoy", LocalDate.now());
        return "reservar";
    }

    @PostMapping("/reservas/crear")
    public String crear(
            @RequestParam("fecha") String fecha,
            @RequestParam("hora") String hora,
            HttpSession session,
            RedirectAttributes ra
    ) {
        ColaboradorEntity col = (ColaboradorEntity) session.getAttribute("colaborador");
        try {
            reservaService.crearReserva(col.getIdColaborador(), parseFecha(fecha), parseHora(hora));
            ra.addFlashAttribute("msgOk", "Reserva creada correctamente");
        } catch (ReservaException ex) {
            ra.addFlashAttribute("msgError", ex.getMessage());
            ra.addFlashAttribute("fecha", fecha);
            ra.addFlashAttribute("hora", hora);
            return "redirect:/reservar";
        }
        return "redirect:/misReservas";
    }

    @GetMapping("/reservas/editar/{id}")
    public String editar(@PathVariable("id") Integer id, HttpSession session, Model model, RedirectAttributes ra) {
        ColaboradorEntity col = (ColaboradorEntity) session.getAttribute("colaborador");
        try {
            ReservaEntity r = reservaService.obtenerPorId(id);
            if (!r.getColaborador().getIdColaborador().equals(col.getIdColaborador())) {
                ra.addFlashAttribute("msgError", "No tienes permisos para editar esa reserva");
                return "redirect:/misReservas";
            }
            model.addAttribute("colaborador", col);
            model.addAttribute("reserva", r);
            model.addAttribute("horas", buildHoras());
            return "editarReserva";
        } catch (ReservaException ex) {
            ra.addFlashAttribute("msgError", ex.getMessage());
            return "redirect:/misReservas";
        }
    }

    @PostMapping("/reservas/actualizar")
    public String actualizar(
            @RequestParam("idReserva") Integer idReserva,
            @RequestParam("fecha") String fecha,
            @RequestParam("hora") String hora,
            HttpSession session,
            RedirectAttributes ra
    ) {
        ColaboradorEntity col = (ColaboradorEntity) session.getAttribute("colaborador");
        try {
            reservaService.actualizarReserva(idReserva, col.getIdColaborador(), parseFecha(fecha), parseHora(hora));
            ra.addFlashAttribute("msgOk", "Reserva actualizada correctamente");
        } catch (ReservaException ex) {
            ra.addFlashAttribute("msgError", ex.getMessage());
            return "redirect:/reservas/editar/" + idReserva;
        }
        return "redirect:/misReservas";
    }

    @GetMapping("/reservas/cancelar/{id}")
    public String cancelar(@PathVariable("id") Integer id, HttpSession session, RedirectAttributes ra) {
        ColaboradorEntity col = (ColaboradorEntity) session.getAttribute("colaborador");
        try {
            reservaService.cancelarReserva(id, col.getIdColaborador());
            ra.addFlashAttribute("msgOk", "Reserva cancelada");
        } catch (ReservaException ex) {
            ra.addFlashAttribute("msgError", ex.getMessage());
        }
        return "redirect:/misReservas";
    }

    private static LocalDate parseFecha(String fecha) {
        return LocalDate.parse(fecha, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    private static LocalTime parseHora(String hora) {
        return LocalTime.parse(hora);
    }

    private static List<String> buildHoras() {
        List<String> horas = new ArrayList<>();
        LocalTime t = LocalTime.of(11, 0);
        LocalTime end = LocalTime.of(15, 0);
        while (!t.isAfter(end)) {
            horas.add(t.toString()); // HH:mm
            t = t.plusMinutes(30);
        }
        return horas;
    }
}
