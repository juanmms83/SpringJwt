package es.sofftek.jwt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import es.sofftek.jwt.config.JWTAuthorizationFilter;

@SuppressWarnings("deprecation")
@SpringBootApplication
public class EjemploJwtApplication {

	public static void main(String[] args) {
		SpringApplication.run(EjemploJwtApplication.class, args);
	}
	
	/**
	 * nos permite especificar la configuración de acceso a los recursos publicados. 
	 * En este caso se permiten todas las llamadas al controlador /user, 
	 * pero el resto de las llamadas requieren autenticación.
	 * @author Laura
	 *
	 */
	@EnableWebSecurity
	@Configuration
	class WebSecurityConfig extends WebSecurityConfigurerAdapter{
		protected void configure(HttpSecurity httpSecurity) throws Exception {
			httpSecurity.csrf().disable()
			.addFilterAfter(new JWTAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
			.authorizeRequests()
			.antMatchers(HttpMethod.POST,"/user").permitAll()
			.anyRequest()
			.authenticated();
			
		}
	}

}
