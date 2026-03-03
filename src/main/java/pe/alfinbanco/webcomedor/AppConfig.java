package pe.alfinbanco.webcomedor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import pe.alfinbanco.webcomedor.filter.LoginFilter;

@SpringBootApplication
public class AppConfig {

    public static void main(String[] args) {
        SpringApplication.run(AppConfig.class, args);
    }
    
    @Bean
    public FilterRegistrationBean<LoginFilter> loginFilter() {
        FilterRegistrationBean<LoginFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(new LoginFilter());
        reg.addUrlPatterns("/home", "/misReservas", "/reservar", "/reservas/*");
        reg.setOrder(1);
        return reg;
    }
}
