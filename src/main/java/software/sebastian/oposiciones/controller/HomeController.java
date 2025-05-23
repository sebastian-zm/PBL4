package software.sebastian.oposiciones.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "index";  // Thymeleaf buscará templates/index.html
    }
}
